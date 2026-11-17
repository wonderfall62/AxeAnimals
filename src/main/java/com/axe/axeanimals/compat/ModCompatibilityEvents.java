package com.axe.axeanimals.compat;

import com.axe.axeanimals.entity.WhaleEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.dolphin.Dolphin;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.List;

public class ModCompatibilityEvents {

    @SubscribeEvent
    public static void onMobFinalizeSpawn(FinalizeSpawnEvent event) {
        Mob mob = event.getEntity();
        Identifier id = BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType());
        String ns = id.getNamespace();
        String path = id.getPath();

        if (ns.equals("cubeanimals")) {
            if (path.contains("fennec") || path.contains("rattlesnake") || path.contains("snake")) {
                if (Float.compare(mob.getRandom().nextFloat(), 0.80F) == -1) {
                    event.setSpawnCancelled(true);
                }
            }
        } else if (ns.equals("iceandfire") && path.contains("hippocampus")) {
            if (Float.compare(mob.getRandom().nextFloat(), 0.80F) == -1) {
                event.setSpawnCancelled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onDolphinInteract(PlayerInteractEvent.EntityInteract event) {
        if (!(event.getTarget() instanceof Dolphin dolphin) || dolphin.isBaby()) return;
        ItemStack stack = event.getItemStack();
        if (stack.is(Items.COD) || stack.is(Items.SALMON) || stack.is(Items.TROPICAL_FISH)) {
            if (!dolphin.level().isClientSide() && Float.compare(dolphin.getPersistentData().getIntOr("AxeBreedCD", 0), 1) == -1) {
                if (!event.getEntity().getAbilities().instabuild) stack.shrink(1);
                dolphin.getPersistentData().putInt("AxeBreedCD", 6000);
                dolphin.playSound(SoundEvents.DOLPHIN_EAT, 1.0F, 1.2F);
                ((ServerLevel) dolphin.level()).sendParticles(ParticleTypes.HEART, dolphin.getX(), dolphin.getY() + 0.8, dolphin.getZ(), 5, 0.4, 0.3, 0.4, 0.05);

                Entity created = dolphin.getType().create(dolphin.level(), EntitySpawnReason.BREEDING);
                if (created instanceof Dolphin baby) {
                    baby.setBaby(true);
                    baby.snapTo(dolphin.getX(), dolphin.getY(), dolphin.getZ(), dolphin.getYRot(), 0);
                    dolphin.level().addFreshEntity(baby);
                }
            }
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        Entity entity = event.getEntity();
        if (entity.level().isClientSide()) return;

        if (entity instanceof Dolphin dolphin) {
            int cd = dolphin.getPersistentData().getIntOr("AxeBreedCD", 0);
            if (cd != 0) {
                dolphin.getPersistentData().putInt("AxeBreedCD", cd - 1);
            }

            if (dolphin.tickCount % 10 == 0) {
                List<WhaleEntity> veryClose = dolphin.level().getEntitiesOfClass(WhaleEntity.class, dolphin.getBoundingBox().inflate(3.2D));
                if (!veryClose.isEmpty()) {
                    WhaleEntity nearest = veryClose.getFirst();
                    Vec3 away = dolphin.position().subtract(nearest.position());
                    if (Double.compare(away.lengthSqr(), 0.01D) == 1) {
                        Vec3 tangent = new Vec3(-away.z, 0, away.x).normalize().scale(0.06D);
                        Vec3 push = away.normalize().scale(0.05D);
                        dolphin.setDeltaMovement(dolphin.getDeltaMovement().add(push.x + tangent.x, 0, push.z + tangent.z));
                    }
                }
            }
            return;
        }

        Identifier id = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
        String ns = id.getNamespace();
        String path = id.getPath();

        if (ns.equals("iceandfire") && path.contains("hippocampus")) {
            if (!entity.isVehicle() && entity.isInWater()) {
                boolean nearSurface = !entity.level().getFluidState(entity.blockPosition().above()).is(FluidTags.WATER);
                boolean deepBelow = entity.level().getFluidState(entity.blockPosition().below(2)).is(FluidTags.WATER);
                if (nearSurface && deepBelow) {
                    Vec3 m = entity.getDeltaMovement();
                    entity.setDeltaMovement(m.x, Math.min(m.y, -0.06D), m.z);
                }
            }
            return;
        }

        if ((ns.equals("reanimal") && path.contains("butterfly")) || (ns.equals("iceandfire") && path.contains("pixie"))) {
            if (entity.isInWater() || !entity.level().getFluidState(entity.blockPosition().below()).isEmpty()) {
                Vec3 motion = entity.getDeltaMovement();
                entity.setDeltaMovement(motion.x * 1.1, Math.max(0.18D, motion.y + 0.12D), motion.z * 1.1);
            }
            return;
        }

        if (ns.equals("iceandfire") && path.contains("dragon") && !path.contains("egg") && !path.contains("skull")) {
            if (path.contains("ice")) {
                if (entity.isInWater()) {
                    Vec3 m = entity.getDeltaMovement();
                    if (Double.compare(m.horizontalDistanceSqr(), 0.02D) == -1) {
                        Vec3 look = entity.getLookAngle().scale(0.35D);
                        entity.setDeltaMovement(look.x, -0.05D, look.z);
                    }
                }
            } else {
                boolean overWater = entity.isInWater() || !entity.level().getFluidState(entity.blockPosition().below(2)).isEmpty();
                if (overWater && Double.compare(entity.getDeltaMovement().horizontalDistanceSqr(), 0.008D) == -1) {
                    Vec3 look = entity.getLookAngle().scale(0.45D);
                    entity.setDeltaMovement(look.x, 0.35D, look.z);
                }
            }
        }
    }
}
