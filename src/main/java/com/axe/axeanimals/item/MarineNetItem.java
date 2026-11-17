package com.axe.axeanimals.item;

import com.axe.axeanimals.entity.OrcaEntity;
import com.axe.axeanimals.entity.StingrayEntity;
import com.axe.axeanimals.entity.WhaleEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.dolphin.Dolphin;
import net.minecraft.world.entity.animal.fish.AbstractFish;
import net.minecraft.world.entity.animal.squid.Squid;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

public class MarineNetItem extends Item {
    public MarineNetItem(Properties properties) {
        super(properties);
    }

    private boolean isCatchable(Entity target) {
        return target instanceof WhaleEntity
                || target instanceof OrcaEntity
                || target instanceof StingrayEntity
                || target instanceof Dolphin
                || target instanceof Squid
                || target instanceof AbstractFish;
    }

    private boolean catchEntity(Player player, InteractionHand hand, ItemStack stack, LivingEntity target) {
        if (!player.level().isClientSide()) {
            TagValueOutput out = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, player.registryAccess());
            if (target.saveAsPassenger(out)) {
                CompoundTag root = new CompoundTag();
                root.put("CapturedEntity", out.buildResult());
                root.putString("CapturedName", target.getName().getString());
                ItemStack updated = stack.copy();
                updated.set(DataComponents.CUSTOM_DATA, CustomData.of(root));
                player.setItemInHand(hand, updated);
                target.discard();
                player.level().playSound(null, player.blockPosition(), SoundEvents.FISHING_BOBBER_RETRIEVE, SoundSource.PLAYERS, 1.0F, 0.9F);
                return true;
            }
        }
        return true;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        CustomData existing = stack.get(DataComponents.CUSTOM_DATA);
        if (existing != null && existing.copyTag().contains("CapturedEntity")) {
            return InteractionResult.PASS;
        }
        if (!isCatchable(target)) {
            return InteractionResult.PASS;
        }
        catchEntity(player, hand, stack, target);
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);

        if (data == null || !data.copyTag().contains("CapturedEntity")) {
            Vec3 eye = player.getEyePosition();
            Vec3 look = player.getLookAngle();
            Vec3 end = eye.add(look.scale(12.0D));
            AABB searchBox = player.getBoundingBox().expandTowards(look.scale(12.0D)).inflate(3.5D);
            List<LivingEntity> candidates = level.getEntitiesOfClass(LivingEntity.class, searchBox, e -> e != player && isCatchable(e));
            LivingEntity best = candidates.stream()
                    .filter(e -> e.getBoundingBox().inflate(2.5D).clip(eye, end).isPresent())
                    .min(Comparator.comparingDouble(e -> e.distanceToSqr(player)))
                    .orElse(null);
            if (best != null) {
                catchEntity(player, hand, stack, best);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        }

        BlockHitResult hit = getPlayerPOVHitResult(level, player, ClipContext.Fluid.ANY);
        if (hit.getType() == HitResult.Type.BLOCK) {
            return releaseEntity(level, player, hand, stack, hit.getBlockPos().relative(hit.getDirection()));
        }
        return InteractionResult.PASS;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null) return InteractionResult.PASS;
        ItemStack stack = context.getItemInHand();
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null || !data.copyTag().contains("CapturedEntity")) {
            return InteractionResult.PASS;
        }
        BlockPos spawnPos = context.getClickedPos().relative(context.getClickedFace());
        return releaseEntity(context.getLevel(), player, context.getHand(), stack, spawnPos);
    }

    private InteractionResult releaseEntity(Level level, Player player, InteractionHand hand, ItemStack stack, BlockPos pos) {
        if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
            CustomData data = stack.get(DataComponents.CUSTOM_DATA);
            if (data != null) {
                CompoundTag tag = data.copyTag().getCompoundOrEmpty("CapturedEntity");
                ValueInput input = TagValueInput.create(ProblemReporter.DISCARDING, serverLevel.registryAccess(), tag);
                Entity entity = EntityType.loadEntityRecursive(input, serverLevel, EntitySpawnReason.BUCKET, e -> e);
                if (entity != null) {
                    entity.snapTo(pos.getX() + 0.5D, pos.getY() + 0.15D, pos.getZ() + 0.5D, player.getYRot(), 0.0F);
                    serverLevel.addFreshEntity(entity);
                    ItemStack cleared = stack.copy();
                    cleared.remove(DataComponents.CUSTOM_DATA);
                    player.setItemInHand(hand, cleared);
                    serverLevel.playSound(null, pos, SoundEvents.BUCKET_EMPTY_FISH, SoundSource.PLAYERS, 1.0F, 0.9F);
                }
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data != null && data.copyTag().contains("CapturedName")) {
            String name = data.copyTag().getStringOr("CapturedName", "Морской житель");
            tooltip.accept(Component.literal("§bПойман: §f" + name + " §7(ПКМ по воде/земле — выпустить)"));
        } else {
            tooltip.accept(Component.literal("§7ПКМ по киту, косатке, скату, дельфину или рыбе — поймать"));
        }
    }
}
