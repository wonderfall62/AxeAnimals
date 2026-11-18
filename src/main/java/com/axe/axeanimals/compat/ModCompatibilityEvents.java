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
    public static void onDragonChangeTarget(net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent event) {
        Entity entity = event.getEntity();
        Identifier id = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
        if (id.getNamespace().equals("iceandfire") && id.getPath().contains("dragon")) {
            net.minecraft.world.entity.LivingEntity newTarget = event.getNewAboutToBeSetTarget();
            if (newTarget != null && (newTarget.isInWater() || newTarget instanceof net.minecraft.world.entity.animal.fish.WaterAnimal)) {
                event.setCanceled(true);
            }
        }
    }
    @SubscribeEvent
    public static void onEntityMount(net.neoforged.neoforge.event.entity.EntityMountEvent event) {
        if (event.isMounting()) {
            Identifier riderId = BuiltInRegistries.ENTITY_TYPE.getKey(event.getEntityMounting().getType());
            Identifier mountId = BuiltInRegistries.ENTITY_TYPE.getKey(event.getEntityBeingMounted().getType());
            if (riderId.getNamespace().equals("reanimal") && mountId.getNamespace().equals("iceandfire")) {
                event.setCanceled(true);
            }
        }
    }
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
        } else if (ns.equals("iceandfire") && path.contains("dragon") && !path.contains("egg")) {
            boolean tooCloseToOtherDragon = !mob.level().getEntities(mob, mob.getBoundingBox().inflate(72.0D),
                    e -> BuiltInRegistries.ENTITY_TYPE.getKey(e.getType()).getPath().contains("dragon")).isEmpty();
            boolean inCrackOrCanyon = !mob.level().canSeeSky(mob.blockPosition().above(2));
            if (tooCloseToOtherDragon || (inCrackOrCanyon && Float.compare(mob.getRandom().nextFloat(), 0.80F) == -1)) {
                event.setSpawnCancelled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        for (ServerLevel level : event.getServer().getAllLevels()) {
            if (level.players().isEmpty() || level.getGameTime() % 200 != 0) continue;
            for (ServerPlayer player : level.players()) {
                if (player.isSpectator() || player.tickCount < 80) continue;
                BlockPos pPos = player.blockPosition();
                var biomeHolder = level.getBiome(pPos);
                if (!biomeHolder.is(BiomeTags.IS_OCEAN)) continue;

                AABB area = player.getBoundingBox().inflate(64.0D);
                AABB wideArea = player.getBoundingBox().inflate(96.0D);
                String biomePath = biomeHolder.unwrapKey().map(k -> k.identifier().getPath()).orElse("");

                long sharksNearby = level.getEntities(player, wideArea, e -> BuiltInRegistries.ENTITY_TYPE.getKey(e.getType()).getPath().contains("shark")).size();
                if (sharksNearby == 0 && level.getRandom().nextInt(3) == 0) {
                    spawnMarineFromOtherMod(level, player, "shark", 1, 28.0D, 56.0D);
                }

                if (!biomePath.contains("frozen")) {
                    long dolphinsNearby = level.getEntities(player, area, e -> e instanceof Dolphin).size();
                    if (Float.compare(dolphinsNearby, 2.5F) == -1) {
                        spawnDolphinPod(level, player, 2 + level.getRandom().nextInt(3), 20.0D, 48.0D);
                    }
                }

                boolean isFrozenOcean = biomePath.contains("frozen");
                if (isFrozenOcean) {
                    long narwhalsNearby = level.getEntities(player, area, e -> BuiltInRegistries.ENTITY_TYPE.getKey(e.getType()).getPath().contains("narwhal")).size();
                    if (Float.compare(narwhalsNearby, 1.5F) == -1) {
                        spawnMarineFromOtherMod(level, player, "narwhal", 2 + level.getRandom().nextInt(2), 18.0D, 44.0D);
                    }
                }

                if (biomePath.contains("deep")) {
                    long serpentsNearby = level.getEntities(player, wideArea, e -> BuiltInRegistries.ENTITY_TYPE.getKey(e.getType()).getPath().contains("sea_serpent")).size();
                    if (serpentsNearby == 0 && level.getRandom().nextInt(5) == 0) {
                        spawnMarineFromOtherMod(level, player, "sea_serpent", 1, 40.0D, 70.0D);
                    }
                }
            }
        }
    }

    private static void spawnDolphinPod(ServerLevel level, ServerPlayer player, int count, double minDist, double maxDist) {
        EntityType<?> dolphinType = BuiltInRegistries.ENTITY_TYPE.getValue(Identifier.withDefaultNamespace("dolphin"));
        if (dolphinType == null) return;
        int seaY = level.getSeaLevel();
        for (int attempt = 0; attempt != 12; attempt++) {
            double angle = level.getRandom().nextDouble() * Math.PI * 2.0D;
            double dist = minDist + level.getRandom().nextDouble() * (maxDist - minDist);
            int sx = (int) (player.getX() + Math.cos(angle) * dist);
            int sz = (int) (player.getZ() + Math.sin(angle) * dist);
            int sy = seaY - 2 - level.getRandom().nextInt(3);
            BlockPos spawnPos = new BlockPos(sx, sy, sz);

            if (level.isLoaded(spawnPos) && level.getFluidState(spawnPos).is(FluidTags.WATER) && level.getFluidState(spawnPos.below(2)).is(FluidTags.WATER)) {
                for (int i = 0; i != count; i++) {
                    Entity dolphin = dolphinType.create(level, EntitySpawnReason.NATURAL);
                    if (dolphin != null) {
                        double ox = (level.getRandom().nextDouble() - 0.5D) * 4.5D;
                        double oz = (level.getRandom().nextDouble() - 0.5D) * 4.5D;
                        dolphin.snapTo(sx + 0.5D + ox, sy, sz + 0.5D + oz, level.getRandom().nextFloat() * 360.0F, 0.0F);
                        level.addFreshEntity(dolphin);
                    }
                }
                break;
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

            if (entity instanceof Mob dragonMob && dragonMob.getTarget() != null
                    && (dragonMob.getTarget().isInWater() || dragonMob.getTarget() instanceof net.minecraft.world.entity.animal.fish.WaterAnimal)) {
                dragonMob.setTarget(null);
            }

            var pData = entity.getPersistentData();
            if (!pData.contains("AxeHomeX")) {
                pData.putDouble("AxeHomeX", entity.getX());
                pData.putDouble("AxeHomeY", entity.getY());
                pData.putDouble("AxeHomeZ", entity.getZ());

                BlockPos center = entity.blockPosition();
                for (int dx = -8; dx <= 8; dx++) {
                    for (int dy = 0; dy <= 6; dy++) {
                        for (int dz = -8; dz <= 8; dz++) {
                            if (Double.compare(dx * dx + dz * dz, 64.0D) == 1) continue;
                            BlockPos bp = center.offset(dx, dy, dz);
                            BlockState bs = entity.level().getBlockState(bp);
                            if (bs.isAir()) continue;
                            String bPath = BuiltInRegistries.BLOCK.getKey(bs.getBlock()).getPath();
                            if (bPath.contains("chest") || bPath.contains("ore") || bPath.contains("bed") || bPath.contains("spawner")) continue;
                            if (bPath.contains("charred") || bPath.contains("frozen") || bPath.contains("crackled") || bPath.contains("ash")
                                    || bPath.contains("spike") || bs.is(BlockTags.ICE) || bs.is(BlockTags.LEAVES) || bs.is(BlockTags.LOGS)) {
                                entity.level().destroyBlock(bp, false);
                            }
                        }
                    }
                }
            }

            // 1. Выход из воды
            boolean inOrNearWater = entity.isInWater() || !entity.level().getFluidState(entity.blockPosition().below(3)).isEmpty();
            if (inOrNearWater) {
                triggerIceAndFireDragonFlight(entity);
                Vec3 look = entity.getLookAngle();
                entity.setDeltaMovement(look.x * 0.55D, 0.58D, look.z * 0.55D);
                entity.fallDistance = 0.0F;
                return;
            }

            // 2. В бою: взлёт против циклопов, на земле против мелкой добычи
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

            // 3. Ночной возврат в центр гнезда, спокойная посадка и сон без дёрганий
            boolean isNight = !entity.level().isBrightOutside();
            if (isNight && entity instanceof Mob dragonMob && dragonMob.getTarget() == null) {
                double hx = pData.getDoubleOr("AxeHomeX", entity.getX());
                double hy = pData.getDoubleOr("AxeHomeY", entity.getY());
                double hz = pData.getDoubleOr("AxeHomeZ", entity.getZ());
                double dx = hx - entity.getX();
                double dz = hz - entity.getZ();
                double horizDistSq = dx * dx + dz * dz;

                if (pData.getBooleanOr("AxeNestSettled", false)) {
                    setDragonBooleanState(entity, "setFlying", false);
                    setDragonBooleanState(entity, "setHovering", false);
                    dragonMob.getNavigation().stop();
                    entity.setDeltaMovement(0.0D, entity.getDeltaMovement().y, 0.0D);
                    setDragonBooleanState(entity, "setSleeping", true);
                    setDragonBooleanState(entity, "setInSittingPose", true);
                    return;
                }

                if (Double.compare(horizDistSq, 25.0D) == 1) {
                    triggerIceAndFireDragonFlight(entity);
                    double dist = Math.sqrt(horizDistSq);
                    double targetY = Math.max(hy + 4.0D, entity.getY());
                    double vy = Float.compare((float) entity.getY(), (float) targetY) == -1 ? 0.25D : -0.05D;
                    entity.setDeltaMovement((dx / dist) * 0.55D, vy, (dz / dist) * 0.55D);
                    entity.setYRot((float) (Math.atan2(dz, dx) * (180.0D / Math.PI)) - 90.0F);
                    entity.fallDistance = 0.0F;
                } else if (!entity.onGround()) {
                    triggerIceAndFireDragonFlight(entity);
                    entity.setDeltaMovement(dx * 0.12D, -0.25D, dz * 0.12D);
                    entity.fallDistance = 0.0F;
                } else {
                    pData.putBoolean("AxeNestSettled", true);
                    setDragonBooleanState(entity, "setFlying", false);
                    setDragonBooleanState(entity, "setHovering", false);
                    dragonMob.getNavigation().stop();
                    entity.setDeltaMovement(Vec3.ZERO);
                    setDragonBooleanState(entity, "setSleeping", true);
                    setDragonBooleanState(entity, "setInSittingPose", true);
                }
                return;
            } else {
                pData.putBoolean("AxeNestSettled", false);
            }

            // 4. Днём: не отпускать дракона дальше 55 блоков от гнезда + плавный взлёт (35 тиков)
            double hx = pData.getDoubleOr("AxeHomeX", entity.getX());
            double hz = pData.getDoubleOr("AxeHomeZ", entity.getZ());
            double toHomeX = hx - entity.getX();
            double toHomeZ = hz - entity.getZ();
            double homeDistSq = toHomeX * toHomeX + toHomeZ * toHomeZ;

            if (!isNight && !entity.onGround() && Double.compare(homeDistSq, 14400.0D) == 1) {
                triggerIceAndFireDragonFlight(entity);
                double dist = Math.sqrt(homeDistSq);
                entity.setDeltaMovement((toHomeX / dist) * 0.50D, 0.05D, (toHomeZ / dist) * 0.50D);
                entity.setYRot((float) (Math.atan2(toHomeZ, toHomeX) * (180.0D / Math.PI)) - 90.0F);
                entity.fallDistance = 0.0F;
                return;
            }

            int takeoff = pData.getIntOr("AxeDragonTakeoff", 0);
            if (!isNight && takeoff != 0) {
                pData.putInt("AxeDragonTakeoff", takeoff - 1);
                triggerIceAndFireDragonFlight(entity);
                Vec3 look = entity.getLookAngle();
                entity.setDeltaMovement(look.x * 0.45D, 0.38D, look.z * 0.45D);
                entity.fallDistance = 0.0F;
                return;
            }

            if (!isNight && entity.onGround() && entity instanceof Mob dragonMob && dragonMob.getTarget() == null) {
                boolean insideNest = Double.compare(homeDistSq, 225.0D) == -1;
                int nestIdle = pData.getIntOr("AxeDragonNestIdle", 0) + 1;

                if (insideNest && nestIdle >= 80) {
                    pData.putInt("AxeDragonNestIdle", 0);
                    pData.putInt("AxeDragonTakeoff", 35);
                    setDragonBooleanState(entity, "setSleeping", false);
                    setDragonBooleanState(entity, "setInSittingPose", false);
                    triggerIceAndFireDragonFlight(entity);
                    return;
                } else if (!insideNest) {
                    nestIdle = 0;
                    if (entity.tickCount % 220 == 0 && Float.compare(entity.getRandom().nextFloat(), 0.45F) == -1) {
                        pData.putInt("AxeDragonTakeoff", 35);
                        setDragonBooleanState(entity, "setSleeping", false);
                        setDragonBooleanState(entity, "setInSittingPose", false);
                        triggerIceAndFireDragonFlight(entity);
                        return;
                    }
                }
                pData.putInt("AxeDragonNestIdle", nestIdle);
            }

            // 5. Ломка льда и препятствий при застревании (ТОЛЬКО ДНЁМ)
            int stuck = pData.getIntOr("AxeDragonStuck", 0);
            if (!isNight && Double.compare(entity.getDeltaMovement().horizontalDistanceSqr(), 0.003D) == -1) {
                stuck++;
                if (stuck >= 60) {
                    stuck = 0;
                    BlockPos center = entity.blockPosition();
                    for (int dx = -4; dx <= 4; dx++) {
                        for (int dy = 0; dy <= 7; dy++) {
                            for (int dz = -4; dz <= 4; dz++) {
                                BlockPos bp = center.offset(dx, dy, dz);
                                BlockState bs = entity.level().getBlockState(bp);
                                if (bs.isAir()) continue;
                                String bPath = BuiltInRegistries.BLOCK.getKey(bs.getBlock()).getPath();
                                if (bPath.contains("chest") || bPath.contains("ore") || bPath.contains("bed") || bPath.contains("spawner")) continue;
                                if (bs.is(BlockTags.LEAVES) || bs.is(BlockTags.LOGS) || bs.is(BlockTags.SNOW) || bs.is(BlockTags.ICE)
                                        || bPath.contains("ice") || bPath.contains("spike") || bPath.contains("charred")
                                        || bPath.contains("frozen") || bPath.contains("crackled") || bPath.contains("ash")) {
                                    entity.level().destroyBlock(bp, false);
                                }
                            }
                        }
                    }
                    pData.putInt("AxeDragonTakeoff", 35);
                    setDragonBooleanState(entity, "setSleeping", false);
                    setDragonBooleanState(entity, "setInSittingPose", false);
                    triggerIceAndFireDragonFlight(entity);
                }
            } else {
                stuck = 0;
            }
            pData.putInt("AxeDragonStuck", stuck);
        }
    }

    @SubscribeEvent
    public static void onAnimalDrops(net.neoforged.neoforge.event.entity.living.LivingDropsEvent event) {
        Entity entity = event.getEntity();
        if (entity.level().isClientSide()) return;

        if (entity instanceof WhaleEntity whale && !whale.isBaby()) {
            ItemStack drop = new ItemStack(com.axe.axeanimals.registry.ModItems.WHALE_MEAT.get(), 6);
            event.getDrops().add(new net.minecraft.world.entity.item.ItemEntity(entity.level(), entity.getX(), entity.getY(), entity.getZ(), drop));
        } else if (entity instanceof com.axe.axeanimals.entity.OrcaEntity orca && !orca.isBaby()) {
            ItemStack drop = new ItemStack(com.axe.axeanimals.registry.ModItems.ORCA_MEAT.get(), 4);
            event.getDrops().add(new net.minecraft.world.entity.item.ItemEntity(entity.level(), entity.getX(), entity.getY(), entity.getZ(), drop));
        } else if (entity instanceof com.axe.axeanimals.entity.StingrayEntity) {
            ItemStack drop = new ItemStack(com.axe.axeanimals.registry.ModItems.STINGRAY_MEAT.get(), 1);
            event.getDrops().add(new net.minecraft.world.entity.item.ItemEntity(entity.level(), entity.getX(), entity.getY(), entity.getZ(), drop));
        }
    }
}
