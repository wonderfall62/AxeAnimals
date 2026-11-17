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

import java.lang.reflect.Method;
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
            if (level.players().isEmpty() || level.getGameTime() % 160 != 0) continue;
            for (ServerPlayer player : level.players()) {
                if (player.isSpectator() || player.tickCount < 80) continue;
                BlockPos pPos = player.blockPosition();
                var biomeHolder = level.getBiome(pPos);
                if (!biomeHolder.is(BiomeTags.IS_OCEAN)) continue;

                AABB area = player.getBoundingBox().inflate(64.0D);
                String biomePath = biomeHolder.unwrapKey().map(k -> k.identifier().getPath()).orElse("");

                long sharksNearby = level.getEntities(player, area, e -> BuiltInRegistries.ENTITY_TYPE.getKey(e.getType()).getPath().contains("shark")).size();
                if (Float.compare(sharksNearby, 1.5F) == -1) {
                    spawnMarineFromOtherMod(level, player, "shark", 1, 22.0D, 48.0D);
                }

                boolean isColdOcean = biomePath.contains("cold") || biomePath.contains("frozen");
                if (isColdOcean) {
                    long narwhalsNearby = level.getEntities(player, area, e -> BuiltInRegistries.ENTITY_TYPE.getKey(e.getType()).getPath().contains("narwhal")).size();
                    if (Float.compare(narwhalsNearby, 1.5F) == -1) {
                        spawnMarineFromOtherMod(level, player, "narwhal", 2 + level.getRandom().nextInt(2), 18.0D, 44.0D);
                    }
                }

                if (biomePath.contains("deep")) {
                    AABB wideArea = player.getBoundingBox().inflate(96.0D);
                    long serpentsNearby = level.getEntities(player, wideArea, e -> BuiltInRegistries.ENTITY_TYPE.getKey(e.getType()).getPath().contains("sea_serpent")).size();
                    if (serpentsNearby == 0 && level.getRandom().nextInt(5) == 0) {
                        spawnMarineFromOtherMod(level, player, "sea_serpent", 1, 40.0D, 70.0D);
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

        int seaY = level.getSeaLevel();
        for (int attempt = 0; attempt != 12; attempt++) {
            double angle = level.getRandom().nextDouble() * Math.PI * 2.0D;
            double dist = minDist + level.getRandom().nextDouble() * (maxDist - minDist);
            int sx = (int) (player.getX() + Math.cos(angle) * dist);
            int sz = (int) (player.getZ() + Math.sin(angle) * dist);
            int sy = seaY - 2 - level.getRandom().nextInt(4);
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
    private static void setDragonBooleanState(Entity dragon, String methodName, boolean value) {
        try {
            for (Method m : dragon.getClass().getMethods()) {
                if (m.getName().equals(methodName) && m.getParameterCount() == 1 && m.getParameterTypes()[0] == boolean.class) {
                    m.invoke(dragon, value);
                }
            }
        } catch (Exception ignored) {
        }
    }

    private static void triggerIceAndFireDragonFlight(Entity dragon) {
        setDragonBooleanState(dragon, "setSleeping", false);
        setDragonBooleanState(dragon, "setOrderedToSit", false);
        setDragonBooleanState(dragon, "setModelDead", false);
        setDragonBooleanState(dragon, "setFlying", true);
        setDragonBooleanState(dragon, "setHovering", true);
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

            if (entity instanceof Mob dragonMob && dragonMob.getTarget() != null && dragonMob.getTarget().isInWater()) {
                dragonMob.setTarget(null);
            }

            var pData = entity.getPersistentData();
            if (!pData.contains("AxeHomeX")) {
                pData.putDouble("AxeHomeX", entity.getX());
                pData.putDouble("AxeHomeY", entity.getY());
                pData.putDouble("AxeHomeZ", entity.getZ());
            }

            // 1. Вытаскиваем дракона из воды и поднимаем выше поверхности океана
            boolean inOrNearWater = entity.isInWater() || !entity.level().getFluidState(entity.blockPosition().below(3)).isEmpty();
            if (inOrNearWater) {
                triggerIceAndFireDragonFlight(entity);
                Vec3 look = entity.getLookAngle();
                entity.setDeltaMovement(look.x * 0.55D, 0.58D, look.z * 0.55D);
                entity.fallDistance = 0.0F;
                return;
            }

            // 2. В бою: против крупных врагов (циклопов и т.п.) дракон взлетает в воздух, а мелкую добычу кусает на земле!
            if (entity instanceof Mob dragonMob && dragonMob.getTarget() != null) {
                Entity target = dragonMob.getTarget();
                String tPath = BuiltInRegistries.ENTITY_TYPE.getKey(target.getType()).getPath();
                boolean bigEnemy = Float.compare(target.getBbHeight(), 2.2F) == 1
                        || tPath.contains("cyclops") || tPath.contains("troll") || tPath.contains("golem") || tPath.contains("dragon");

                if (bigEnemy && entity.onGround() && entity.tickCount % 25 == 0) {
                    triggerIceAndFireDragonFlight(entity);
                    Vec3 away = entity.position().subtract(target.position()).normalize();
                    entity.setDeltaMovement(away.x * 0.35D, 0.55D, away.z * 0.35D);
                }
                return;
            }


            // 3. Ночной возврат дикого дракона в родное гнездо, посадка и сон
            boolean isNight = !entity.level().isBrightOutside();
            if (isNight && entity instanceof Mob dragonMob && dragonMob.getTarget() == null) {
                double hx = pData.getDoubleOr("AxeHomeX", entity.getX());
                double hy = pData.getDoubleOr("AxeHomeY", entity.getY());
                double hz = pData.getDoubleOr("AxeHomeZ", entity.getZ());
                double distSq = entity.distanceToSqr(hx, hy, hz);

                if (Double.compare(distSq, 196.0D) == 1) {
                    if (entity.tickCount % 25 == 0) {
                        triggerIceAndFireDragonFlight(entity);
                        dragonMob.getNavigation().moveTo(hx, hy + 2.0D, hz, 1.35D);
                    }
                } else {
                    setDragonBooleanState(entity, "setFlying", false);
                    setDragonBooleanState(entity, "setHovering", false);
                    if (!entity.onGround()) {
                        Vec3 m = entity.getDeltaMovement();
                        entity.setDeltaMovement(m.x * 0.4D, -0.25D, m.z * 0.4D);
                        entity.fallDistance = 0.0F;
                    } else {
                        dragonMob.getNavigation().stop();
                        entity.setDeltaMovement(Vec3.ZERO);
                        setDragonBooleanState(entity, "setSleeping", true);
                        setDragonBooleanState(entity, "setInSittingPose", true);
                    }
                }
                return;
            }

            // 4. Днём: пробуждение и периодический естественный взлёт
            if (!isNight && entity.onGround() && entity.tickCount % 220 == 0 && Float.compare(entity.getRandom().nextFloat(), 0.45F) == -1) {
                setDragonBooleanState(entity, "setInSittingPose", false);
                triggerIceAndFireDragonFlight(entity);
                return;
            }

            // 5. Расчистка листвы/веток, если дракон застрял в кронах деревьев
            int stuck = pData.getIntOr("AxeDragonStuck", 0);
            boolean barelyMoving = entity.onGround() && Double.compare(entity.getDeltaMovement().horizontalDistanceSqr(), 0.003D) == -1;
            if (barelyMoving) {
                stuck++;
                if (stuck >= 100) {
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
                    triggerIceAndFireDragonFlight(entity);
                }
            } else {
                stuck = 0;
            }
            pData.putInt("AxeDragonStuck", stuck);

        }
    }
}
