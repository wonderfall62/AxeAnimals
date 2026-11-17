package com.axe.axeanimals.compat;

import com.axe.axeanimals.entity.OrcaEntity;
import com.axe.axeanimals.entity.WhaleEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.dolphin.Dolphin;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.List;

public class ModCompatibilityEvents {

    @SubscribeEvent
    public static void onMobFinalizeSpawn(FinalizeSpawnEvent event) {
        Mob mob = event.getEntity();
        Identifier id = BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType());
        String ns = id.getNamespace();
        String path = id.getPath();

        if (event.getSpawnType() == EntitySpawnReason.NATURAL) {
            if (mob instanceof Dolphin) {
                int count = mob.level().getEntitiesOfClass(Dolphin.class, mob.getBoundingBox().inflate(48.0D)).size();
                if (Float.compare(count, 5.5F) == 1) {
                    event.setSpawnCancelled(true);
                    return;
                }
            } else if (mob instanceof WhaleEntity) {
                int count = mob.level().getEntitiesOfClass(WhaleEntity.class, mob.getBoundingBox().inflate(64.0D)).size();
                if (Float.compare(count, 1.5F) == 1) {
                    event.setSpawnCancelled(true);
                    return;
                }
            } else if (mob instanceof OrcaEntity) {
                int count = mob.level().getEntitiesOfClass(OrcaEntity.class, mob.getBoundingBox().inflate(56.0D)).size();
                if (Float.compare(count, 3.5F) == 1) {
                    event.setSpawnCancelled(true);
                    return;
                }
            }
        }

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
    public static void onServerTick(ServerTickEvent.Post event) {
        for (ServerLevel level : event.getServer().getAllLevels()) {
            if (level.getGameTime() % 240 != 0) continue;
            for (ServerPlayer player : level.players()) {
                if (player.isSpectator()) continue;
                BlockPos pPos = player.blockPosition();
                if (!level.getBiome(pPos).is(BiomeTags.IS_OCEAN)) continue;

                AABB area = player.getBoundingBox().inflate(52.0D);
                long sharksNearby = level.getEntities(player, area, e -> BuiltInRegistries.ENTITY_TYPE.getKey(e.getType()).getPath().contains("shark")).size();
                if (Float.compare(sharksNearby, 1.5F) == 1) continue;

                List<EntityType<?>> sharkTypes = BuiltInRegistries.ENTITY_TYPE.stream()
                        .filter(t -> {
                            Identifier tid = BuiltInRegistries.ENTITY_TYPE.getKey(t);
                            return !tid.getNamespace().equals("minecraft") && tid.getPath().contains("shark") && !tid.getPath().contains("egg");
                        })
                        .toList();
                if (sharkTypes.isEmpty()) continue;

                for (int attempt = 0; attempt != 6; attempt++) {
                    double angle = level.getRandom().nextDouble() * Math.PI * 2.0D;
                    double dist = 24.0D + level.getRandom().nextDouble() * 24.0D;
                    int sx = (int) (player.getX() + Math.cos(angle) * dist);
                    int sz = (int) (player.getZ() + Math.sin(angle) * dist);
                    int sy = Math.max(level.getMinY() + 5, (int) player.getY() - 4 + level.getRandom().nextInt(8));
                    BlockPos spawnPos = new BlockPos(sx, sy, sz);
                    if (level.getFluidState(spawnPos).is(FluidTags.WATER) && level.getFluidState(spawnPos.below(2)).is(FluidTags.WATER)) {
                        EntityType<?> chosen = sharkTypes.get(level.getRandom().nextInt(sharkTypes.size()));
                        Entity shark = chosen.create(level, EntitySpawnReason.NATURAL);
                        if (shark != null) {
                            shark.snapTo(sx + 0.5D, sy, sz + 0.5D, level.getRandom().nextFloat() * 360.0F, 0.0F);
                            level.addFreshEntity(shark);
                        }
                        break;
                    }
                }
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
            if (dolphin.tickCount == 10 && !dolphin.isBaby() && !dolphin.getPersistentData().getBooleanOr("AxePodChecked", false)) {
                dolphin.getPersistentData().putBoolean("AxePodChecked", true);
                List<Dolphin> nearby = dolphin.level().getEntitiesOfClass(Dolphin.class, dolphin.getBoundingBox().inflate(32.0D));
                if (nearby.size() == 1 || nearby.size() == 2) {
                    int extra = 4 - nearby.size() + dolphin.getRandom().nextInt(2);
                    for (int i = 0; i != extra; i++) {
                        Entity friend = dolphin.getType().create(dolphin.level(), EntitySpawnReason.NATURAL);
                        if (friend instanceof Dolphin d) {
                            d.getPersistentData().putBoolean("AxePodChecked", true);
                            double ox = (dolphin.getRandom().nextDouble() - 0.5D) * 8.0D;
                            double oz = (dolphin.getRandom().nextDouble() - 0.5D) * 8.0D;
                            d.snapTo(dolphin.getX() + ox, dolphin.getY(), dolphin.getZ() + oz, dolphin.getRandom().nextFloat() * 360.0F, 0.0F);
                            dolphin.level().addFreshEntity(d);
                        }
                    }
                }
            }

            int cd = dolphin.getPersistentData().getIntOr("AxeBreedCD", 0);
            if (cd != 0) {
                dolphin.getPersistentData().putInt("AxeBreedCD", cd - 1);
            }

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

            int whalePlay = dolphin.getPersistentData().getIntOr("AxeWhalePlay", 0);
            int whaleCD = dolphin.getPersistentData().getIntOr("AxeWhaleCD", 0);
            if (whaleCD != 0) {
                dolphin.getPersistentData().putInt("AxeWhaleCD", whaleCD - 1);
            }

            if (whalePlay != 0) {
                dolphin.getPersistentData().putInt("AxeWhalePlay", whalePlay - 1);
                List<WhaleEntity> whales = dolphin.level().getEntitiesOfClass(WhaleEntity.class, dolphin.getBoundingBox().inflate(24.0D));
                if (!whales.isEmpty()) {
                    WhaleEntity w = whales.getFirst();
                    double distSq = dolphin.distanceToSqr(w);
                    if (Double.compare(distSq, 64.0D) == 1) {
                        Vec3 side = new Vec3(-w.getLookAngle().z, 0, w.getLookAngle().x).normalize().scale(7.5D);
                        dolphin.getNavigation().moveTo(w.getX() + side.x, w.getY(), w.getZ() + side.z, 1.15D);
                    }
                }
                if (whalePlay - 1 == 0) {
                    dolphin.getPersistentData().putInt("AxeWhaleCD", 18000);
                }
            } else if (whaleCD == 0 && dolphin.tickCount % 120 == 0) {
                List<WhaleEntity> whales = dolphin.level().getEntitiesOfClass(WhaleEntity.class, dolphin.getBoundingBox().inflate(24.0D));
                if (!whales.isEmpty()) {
                    dolphin.getPersistentData().putInt("AxeWhalePlay", 300);
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
