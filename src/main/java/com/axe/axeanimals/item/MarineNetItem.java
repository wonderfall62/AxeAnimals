package com.axe.axeanimals.item;

import com.axe.axeanimals.entity.StingrayEntity;
import com.axe.axeanimals.entity.WhaleEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.util.ProblemReporter;

import java.util.Optional;
import java.util.function.Consumer;

public class SeaNetItem extends Item {
    public SeaNetItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        CustomData existing = stack.get(DataComponents.CUSTOM_DATA);
        if (existing != null && existing.copyTag().contains("CapturedEntity")) {
            return InteractionResult.PASS;
        }

        boolean canCatch = (target instanceof WhaleEntity w && w.isBaby())
                || target instanceof StingrayEntity
                || target instanceof Dolphin
                || target instanceof Squid
                || target instanceof AbstractFish;

        if (!canCatch) {
            return InteractionResult.PASS;
        }

        if (!player.level().isClientSide()) {
            TagValueOutput out = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, player.registryAccess());
            if (target.saveAsPassenger(out)) {
                CompoundTag root = new CompoundTag();
                root.put("CapturedEntity", out.buildResult());
                root.putString("CapturedName", target.getName().getString());
                stack.set(DataComponents.CUSTOM_DATA, CustomData.of(root));
                target.discard();
                player.level().playSound(null, player.blockPosition(), SoundEvents.FISHING_BOBBER_RETRIEVE, SoundSource.PLAYERS, 1.0F, 0.9F);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null || !data.copyTag().contains("CapturedEntity")) {
            return InteractionResult.PASS;
        }

        BlockHitResult hit = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
        if (hit.getType() == HitResult.Type.BLOCK) {
            return releaseEntity(level, player, stack, hit.getBlockPos().relative(hit.getDirection()));
        }
        return InteractionResult.PASS;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        ItemStack stack = context.getItemInHand();
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null || !data.copyTag().contains("CapturedEntity")) {
            return InteractionResult.PASS;
        }
        BlockPos spawnPos = context.getClickedPos().relative(context.getClickedFace());
        return releaseEntity(context.getLevel(), context.getPlayer(), stack, spawnPos);
    }

    private InteractionResult releaseEntity(Level level, Player player, ItemStack stack, BlockPos pos) {
        if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
            CustomData data = stack.get(DataComponents.CUSTOM_DATA);
            if (data != null) {
                CompoundTag tag = data.copyTag().getCompoundOrEmpty("CapturedEntity");
                Optional<Entity> loaded = EntityType.create(tag, serverLevel, EntitySpawnReason.BUCKET);
                if (loaded.isPresent()) {
                    Entity entity = loaded.get();
                    entity.snapTo(pos.getX() + 0.5D, pos.getY() + 0.1D, pos.getZ() + 0.5D, player != null ? player.getYRot() : 0.0F, 0.0F);
                    serverLevel.addFreshEntity(entity);
                    stack.remove(DataComponents.CUSTOM_DATA);
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
            tooltip.accept(Component.literal("§7ПКМ по детёнышу кита, скату или рыбе — поймать"));
        }
    }
}
