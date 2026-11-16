package com.axe.axeanimals.item;

import com.axe.axeanimals.entity.StingrayEntity;
import com.axe.axeanimals.entity.WhaleEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.dolphin.Dolphin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.util.ProblemReporter;

public class MarineNetItem extends Item {
    public MarineNetItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        if (!(target instanceof WhaleEntity || target instanceof StingrayEntity || target instanceof Dolphin)) {
            return InteractionResult.PASS;
        }
        if (hasCapturedEntity(stack)) return InteractionResult.FAIL;

        Level level = player.level();
        if (!level.isClientSide()) {
            TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, level.registryAccess());
            target.saveWithoutId(output);
            CompoundTag tag = output.buildResult();
            tag.putString("CapturedEntityId", BuiltInRegistries.ENTITY_TYPE.getKey(target.getType()).toString());
            tag.putString("CapturedEntityName", target.getDisplayName().getString());

            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
            level.playSound(null, target.blockPosition(), SoundEvents.FISHING_BOBBER_RETRIEVE, SoundSource.PLAYERS, 1.0F, 0.8F);
            target.discard();
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        ItemStack stack = context.getItemInHand();
        if (!hasCapturedEntity(stack)) return InteractionResult.PASS;

        Level level = context.getLevel();
        if (!level.isClientSide()) {
            CustomData data = stack.get(DataComponents.CUSTOM_DATA);
            if (data != null) {
                CompoundTag tag = data.copyTag();
                String idStr = tag.getStringOr("CapturedEntityId", "");
                EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.getValue(Identifier.parse(idStr));
                if (type != null) {
                    BlockPos pos = context.getClickedPos().relative(context.getClickedFace());
                    Entity entity = type.create(level, EntitySpawnReason.BUCKET);
                    if (entity != null) {
                        entity.load(TagValueInput.create(ProblemReporter.DISCARDING, level.registryAccess(), tag));
                        entity.snapTo(pos.getX() + 0.5, pos.getY() + 0.2, pos.getZ() + 0.5, context.getRotation(), 0.0F);
                        level.addFreshEntity(entity);
                        level.playSound(null, pos, SoundEvents.BUCKET_EMPTY_FISH, SoundSource.PLAYERS, 1.0F, 0.9F);
                        stack.remove(DataComponents.CUSTOM_DATA);
                    }
                }
            }
        }
        return InteractionResult.SUCCESS;
    }

    public static boolean hasCapturedEntity(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        return data != null && data.copyTag().contains("CapturedEntityId");
    }

    @Override
    public Component getName(ItemStack stack) {
        if (hasCapturedEntity(stack)) {
            String mobName = stack.get(DataComponents.CUSTOM_DATA).copyTag().getStringOr("CapturedEntityName", "");
            return Component.translatable(this.getDescriptionId()).append(" (" + mobName + ")");
        }
        return super.getName(stack);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return hasCapturedEntity(stack);
    }
}
