package com.axe.axeanimals.entity;

import com.axe.axeanimals.registry.ModEntities;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WhaleEntity extends WaterAnimal {
    private static final EntityDataAccessor<Boolean> IS_BABY = SynchedEntityData.defineId(WhaleEntity.class, EntityDataSerializers.BOOLEAN);
    public final AnimationState breachState = new AnimationState();
    public final AnimationState feedingState = new AnimationState();

    private int pregnancyTicks = 0; // Долгая беременность (~25 минут = 30000 тиков)
    private int babyGrowTicks = 0;  // Долгое взросление (~50 минут = 60000 тиков)
    private int breachCooldown = 3600;
    private int escortTicks = 0;
    private int escortCooldown = 0;

    public WhaleEntity(EntityType<? extends WaterAnimal> type, Level level) {
        super(type, level);
        this.moveControl = new SmoothSwimmingMoveControl(this, 45, 5, 0.02F, 0.1F, true);
        this.lookControl = new SmoothSwimmingLookControl(this, 10);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return WaterAnimal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 90.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.85D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(IS_BABY, false);
    }

    @Override
    public boolean isBaby() {
        return this.entityData.get(IS_BABY);
    }

    public void setBabyWhale(boolean baby) {
        this.entityData.set(IS_BABY, baby);
        this.babyGrowTicks = baby ? 60000 : 0;
        this.refreshDimensions();
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new WaterBoundPathNavigation(this, level);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new RandomSwimmingGoal(this, 1.0D, 30));
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!this.isBaby() && stack.is(Items.TROPICAL_FISH) && this.pregnancyTicks <= 0) {
            if (!this.level().isClientSide()) {
                if (!player.getAbilities().instabuild) stack.shrink(1);
                this.pregnancyTicks = 30000; // 25 минут вынашивания
                this.playSound(SoundEvents.DOLPHIN_EAT, 1.0F, 0.6F);
                ((ServerLevel) this.level()).sendParticles(ParticleTypes.HEART, this.getX(), this.getY() + 1.5, this.getZ(), 7, 1.0, 0.5, 1.0, 0.05);
            }
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide()) {
            // 1. Взросление китёнка
            if (this.isBaby() && this.babyGrowTicks > 0) {
                this.babyGrowTicks--;
                if (this.babyGrowTicks <= 0) this.setBabyWhale(false);
            }
            // 2. Беременность и рождение
            if (this.pregnancyTicks > 0) {
                this.pregnancyTicks--;
                if (this.pregnancyTicks == 0) {
                    WhaleEntity calf = ModEntities.WHALE.get().create(this.level(), EntitySpawnReason.BREEDING);
                    if (calf != null) {
                        calf.setBabyWhale(true);
                        calf.snapTo(this.getX() + 2.0, this.getY(), this.getZ() + 2.0, this.getYRot(), 0);
                        this.level().addFreshEntity(calf);
                    }
                }
            }
            // 3. Временное сопровождение лодок (1.5 минуты) и отпугивание утопленников
            if (this.escortCooldown > 0) this.escortCooldown--;
            if (this.escortTicks > 0) {
                this.escortTicks--;
                List<Boat> boats = this.level().getEntitiesOfClass(Boat.class, this.getBoundingBox().inflate(18.0D));
                if (!boats.isEmpty()) {
                    Boat targetBoat = boats.getFirst();
                    if (this.distanceToSqr(targetBoat) > 36.0D) {
                        this.getNavigation().moveTo(targetBoat, 1.15D);
                    }
                    if (targetBoat.getControllingPassenger() instanceof Player p) {
                        p.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 60, 0, true, false));
                    }
                }
                // Отпугиваем утопленников рядом с китом
                this.level().getEntitiesOfClass(Drowned.class, this.getBoundingBox().inflate(16.0D))
                        .forEach(d -> d.setDeltaMovement(d.position().subtract(this.position()).normalize().scale(0.35D)));
                if (this.escortTicks == 0) this.escortCooldown = 14000; // Кулдаун ~12 минут
            } else if (this.escortCooldown == 0 && this.tickCount % 60 == 0) {
                List<Boat> boats = this.level().getEntitiesOfClass(Boat.class, this.getBoundingBox().inflate(24.0D));
                if (!boats.isEmpty()) {
                    this.escortTicks = 1800; // Сопровождает 1.5 минуты
                }
            }
            // 4. Редкое выпрыгивание из воды
            if (this.breachCooldown > 0) {
                this.breachCooldown--;
            } else if (this.isInWater() && this.level().isEmptyBlock(this.blockPosition().above(2))) {
                this.setDeltaMovement(this.getDeltaMovement().add(0, 0.85D, 0));
                this.breachCooldown = 3600 + this.random.nextInt(2400); // Раз в 3-5 минут
                this.playSound(SoundEvents.GENERIC_SPLASH, 1.5F, 0.5F);
                ((ServerLevel) this.level()).sendParticles(ParticleTypes.SPLASH, this.getX(), this.getY() + 1.0, this.getZ(), 35, 2.0, 0.5, 2.0, 0.2);
            }
        } else {
            if (!this.isInWater() && this.getDeltaMovement().y > 0.1D) {
                this.breachState.startIfStopped(this.tickCount);
            } else if (this.onGround() || (this.isInWater() && this.getDeltaMovement().y < -0.2D)) {
                this.breachState.stop();
            }
            if (this.tickCount % 400 == 0) this.feedingState.start(this.tickCount);
        }
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, EntitySpawnReason reason, @Nullable SpawnGroupData spawnData) {
        if (!this.isBaby() && this.random.nextFloat() < 0.22F) {
            WhaleEntity calf = ModEntities.WHALE.get().create(level.getLevel(), EntitySpawnReason.NATURAL);
            if (calf != null) {
                calf.setBabyWhale(true);
                calf.snapTo(this.getX() + 2.5, this.getY(), this.getZ() + 2.5, this.getYRot(), 0);
                level.addFreshEntity(calf);
            }
        }
        return super.finalizeSpawn(level, difficulty, reason, spawnData);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putBoolean("IsBabyWhale", this.isBaby());
        output.putInt("PregnancyTicks", this.pregnancyTicks);
        output.putInt("BabyGrowTicks", this.babyGrowTicks);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.entityData.set(IS_BABY, input.getBooleanOr("IsBabyWhale", false));
        this.pregnancyTicks = input.getIntOr("PregnancyTicks", 0);
        this.babyGrowTicks = input.getIntOr("BabyGrowTicks", 0);
    }
}
