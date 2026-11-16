package com.axe.axeanimals.compat;

import com.axe.axeanimals.entity.WhaleEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
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

    // 1. Уменьшаем в 5 раз спавн фенеков и гремучих змей из Cube Animals
    @SubscribeEvent
    public static void onMobFinalizeSpawn(FinalizeSpawnEvent event) {
        Mob mob = event.getEntity();
        Identifier id = BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType());
        if (id.getNamespace().equals("cubeanimals")) {
            String path = id.getPath();
            if (path.contains("fennec") || path.contains("rattlesnake") || path.contains("snake")) {
                if (mob.getRandom().nextFloat()  0) dolphin.getPersistentData().putInt("AxeBreedCD", cd - 1);

            int whalePlay = dolphin.getPersistentData().getIntOr("AxeWhalePlay", 0);
            int whaleCD = dolphin.getPersistentData().getIntOr("AxeWhaleCD", 0);
            if (whaleCD > 0) dolphin.getPersistentData().putInt("AxeWhaleCD", whaleCD - 1);

            if (whalePlay > 0) {
                dolphin.getPersistentData().putInt("AxeWhalePlay", whalePlay - 1);
                List<WhaleEntity> whales = dolphin.level().getEntitiesOfClass(WhaleEntity.class, dolphin.getBoundingBox().inflate(24.0D));
                if (!whales.isEmpty()) {
                    WhaleEntity w = whales.getFirst();
                    if (dolphin.distanceToSqr(w) > 25.0D) {
                        dolphin.getNavigation().moveTo(w, 1.25D);
                    }
                }
                if (whalePlay - 1 == 0) dolphin.getPersistentData().putInt("AxeWhaleCD", 12000);
            } else if (whaleCD == 0 && dolphin.tickCount % 80 == 0) {
                List<WhaleEntity> whales = dolphin.level().getEntitiesOfClass(WhaleEntity.class, dolphin.getBoundingBox().inflate(28.0D));
                if (!whales.isEmpty()) {
                    dolphin.getPersistentData().putInt("AxeWhalePlay", 1500); // Играют 1.25 минуты и уплывают
                }
            }
            return;
        }

        Identifier id = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
        String ns = id.getNamespace();
        String path = id.getPath();

        // Б) Фикс бабочек (Re:Animal и Ice and Fire) от зависания над водой
        if ((ns.equals("reanimal") && path.contains("butterfly")) || (ns.equals("iceandfire") && path.contains("pixie"))) {
            if (entity.isInWater() || !entity.level().getFluidState(entity.blockPosition().below()).isEmpty()) {
                Vec3 motion = entity.getDeltaMovement();
                entity.setDeltaMovement(motion.x * 1.1, Math.max(0.18D, motion.y + 0.12D), motion.z * 1.1);
                entity.hurtMarked = true;
            }
            return;
        }

        // В) Драконы Ice and Fire: огненные/грозовые не застревают над водой, ледяные плавают под водой как водные виверны
        if (ns.equals("iceandfire") && path.contains("dragon") && !path.contains("egg") && !path.contains("skull")) {
            if (path.contains("ice")) {
                // Ледяной дракон уверенно патрулирует под водой
                if (entity.isInWater()) {
                    Vec3 m = entity.getDeltaMovement();
                    if (m.horizontalDistanceSqr() < 0.02D) {
                        Vec3 look = entity.getLookAngle().scale(0.35D);
                        entity.setDeltaMovement(look.x, -0.05D, look.z);
                        entity.hurtMarked = true;
                    }
                }
            } else {
                // Огненный и грозовой драконы не зависают над волнами
                boolean overWater = entity.isInWater() || !entity.level().getFluidState(entity.blockPosition().below(2)).isEmpty();
                if (overWater && entity.getDeltaMovement().horizontalDistanceSqr() < 0.008D) {
                    Vec3 look = entity.getLookAngle().scale(0.45D);
                    entity.setDeltaMovement(look.x, 0.35D, look.z);
                    entity.hurtMarked = true;
                }
            }
        }
    }
}
