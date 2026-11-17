package com.axe.axeanimals.compat;

import com.axe.axeanimals.entity.WhaleEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.dolphin.Dolphin;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
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
            if (level.players().isEmpty() || level.getGameTime() % 200 != 0) continue;
            for (ServerPlayer player : level.players()) {
                if (player.isSpectator() || player.tickCount < 100) continue;
                BlockPos pPos = player.blockPosition();
                var biomeHolder = level.getBiome(pPos);
                if (!biomeHolder.is(BiomeTags.IS_OCEAN)) continue;

                AABB area = player.getBoundingBox().inflate(56.0D);
                String biomePath = biomeHolder.unwrapKey().map(k -> k.identifier().getPath()).orElse("");

                // 1. Акулы (в любых океанах)
                long sharksNearby = level.getEntities(player, area, e -> BuiltInRegistries.ENTITY_TYPE.getKey(e.getType()).getPath().contains("shark")).size();
                if (Float.compare(sharksNearby, 1.5F) == -1) {
                    spawnMarineFromOtherMod(level, player, "shark", 1, 26.0D, 52.0D);
                }

                // 2. Нарвалы (ТОЛЬКО в холодных и ледяных океанах)
                boolean isColdOcean = biomePath.contains("cold") || biomePath.contains("frozen");
                if (isColdOcean) {
                    long narwhalsNearby = level.getEntities(player, area, e -> BuiltInRegistries.ENTITY_TYPE.getKey(e.getType()).getPath().contains("narwhal")).size();
                    if (Float.compare(narwhalsNearby, 1.5F) == -1 && level.getRandom().nextBoolean()) {
                        spawnMarineFromOtherMod(level, player, "narwhal", 2, 26.0D, 52.0D);
                    }
                }

                // 3. Морские змеи / Левиафаны (редко в глубоких океанах)
                if (biomePath.contains("deep")) {
                    AABB wideArea = player.getBoundingBox().inflate(96.0D);
                    long serpentsNearby = level.getEntities(player, wideArea, e -> BuiltInRegistries.ENTITY_TYPE.getKey(e.getType()).getPath().contains("sea_serpent")).size();
                    if (serpentsNearby == 0 && level.getRandom().nextInt(6) == 0) {
                        spawnMarineFromOtherMod(level, player, "sea_serpent", 1, 45.0D, 75.0D);
                    }
                }
            }
        }
    }

    private static void spawnMarineFromOtherMod(ServerLevel level, ServerPlayer player, String keyword, int count, double minDist, double maxDist) {
        List<EntityType<?>> types = BuiltInRegistries.ENTITY_TYPE.stream()
                .filter(t -> {
                    Identifier tid = BuiltInRegistries.ENTITY_TYPE.getKey(t);
                    return !tid.getNamespace().equals("minecraft") && tid.getPath().contains(keyword) && !tid.getPath().contains("egg");
                })
                .toList();
        if (types.isEmpty()) return;

        for (int attempt = 0; attempt != 8; attempt++) {
            double angle = level.getRandom().nextDouble() * Math.PI * 2.0D;
            double dist = minDist + level.getRandom().nextDouble() * (maxDist - minDist);
            int sx = (int) (player.getX() + Math.cos(angle) * dist);
            int sz = (int) (player.getZ() + Math.sin(angle) * dist);
            int sy = Math.max(level.getMinY() + 6, (int) player.getY() - 5 + level.getRandom().nextInt(9));
            BlockPos spawnPos = new BlockPos(sx, sy, sz);

            if (level.isLoaded(spawnPos) && level.getFluidState(spawnPos).is(FluidTags.WATER) && level.getFluidState(spawnPos.below(2)).is(FluidTags.WATER)) {
                EntityType<?> chosen = types.get(level.getRandom().nextInt(types.size()));
                for (int i = 0; i != count; i++) {
                    Entity mob = chosen.create(level, EntitySpawnReason.NATURAL);
                    if (mob != null) {
                        double ox = (level.getRandom().nextDouble() - 0.5D) * 4.0D;
                        double oz = (level.getRandom().nextDouble() - 0.5D) * 4.0D;
                        mob.snapTo(sx + 0.5D + ox, sy, sz + 0.5D + oz, level.getRandom().nextFloat() * 360.0F, 0.0F);
                        level.addFreshEntity(mob);
                    }
                }
                break;
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
        if (entity.level().isClientSide() || entity.level().players().isEmpty()) return;

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
            if (entity.isVehicle()) return;

            if (path.contains("ice") && entity.isInWater()) {
                Vec3 m = entity.getDeltaMovement();
                if (Double.compare(m.horizontalDistanceSqr(), 0.02D) == -1) {
                    Vec3 look = entity.getLookAngle().scale(0.35D);
                    entity.setDeltaMovement(look.x, -0.05D, look.z);
                }
                return;
            }

            boolean overWater = entity.isInWater() || !entity.level().getFluidState(entity.blockPosition().below(2)).isEmpty();
            if (overWater && Double.compare(entity.getDeltaMovement().horizontalDistanceSqr(), 0.008D) == -1) {
                Vec3 look = entity.getLookAngle().scale(0.45D);
                entity.setDeltaMovement(look.x, 0.35D, look.z);
                return;
            }

            // Периодический взлёт диких драконов в небо (чтобы не ходили постоянно пешком)
            int flightTimer = entity.getPersistentData().getIntOr("AxeDragonFlight", 0);
            if (flightTimer != 0) {
                entity.getPersistentData().putInt("AxeDragonFlight", flightTimer - 1);
                Vec3 look = entity.getLookAngle();
                double vy = Float.compare(flightTimer, 70.0F) == 1 ? 0.28D : 0.04D;
                entity.setDeltaMovement(look.x * 0.52D, vy, look.z * 0.52D);
                entity.fallDistance = 0.0F;
                return;
            } else if (entity.onGround() && !entity.isInWater() && entity.tickCount % 260 == 0 && Float.compare(entity.getRandom().nextFloat(), 0.45F) == -1) {
                entity.getPersistentData().putInt("AxeDragonFlight", 110);
                Vec3 look = entity.getLookAngle();
                entity.setDeltaMovement(look.x * 0.48D, 0.62D, look.z * 0.48D);
                return;
            }

            int stuck = entity.getPersistentData().getIntOr("AxeDragonStuck", 0);
            boolean barelyMoving = Double.compare(entity.getDeltaMovement().horizontalDistanceSqr(), 0.004D) == -1;
            if (barelyMoving) {
                stuck++;
                if (stuck >= 80) {
                    stuck = 0;
                    BlockPos center = entity.blockPosition();
                    for (int dx = -4; dx <= 4; dx++) {
                        for (int dy = 1; dy <= 7; dy++) {
                            for (int dz = -4; dz <= 4; dz++) {
                                BlockPos bp = center.offset(dx, dy, dz);
                                BlockState bs = entity.level().getBlockState(bp);
                                if (bs.isAir()) continue;
                                String bPath = BuiltInRegistries.BLOCK.getKey(bs.getBlock()).getPath();
                                if (bs.is(BlockTags.LEAVES) || bs.is(BlockTags.LOGS) || bs.is(BlockTags.SNOW)
                                        || bPath.contains("charred") || bPath.contains("frozen") || bPath.contains("crackled") || bPath.contains("ash")) {
                                    entity.level().destroyBlock(bp, false);
                                }
                            }
                        }
                    }
                    Vec3 look = entity.getLookAngle().scale(0.55D);
                    entity.setDeltaMovement(look.x, 0.55D, look.z);
                }
            } else {
                stuck = 0;
            }
            entity.getPersistentData().putInt("AxeDragonStuck", stuck);
        }
    }
}
