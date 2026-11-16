package com.axe.axeanimals.entity;

import com.axe.axeanimals.registry.ModEntities;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.animal.fish.WaterAnimal;
import net.minecraft.world.entity.monster.zombie.Drowned;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.boat.Boat;
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
    private static final EntityDataAccessor<Float> GROWTH_PROGRESS = SynchedEntityData.defineId(WhaleEntity.class, EntityDataSerializers.FLOAT);
    public final AnimationState breachState = new AnimationState();
    public final AnimationState feedingState = new AnimationState();

    private int pregnancyTicks = 0;
    private int babyGrowTicks = 0;
    private static final int MAX_GROW_TICKS = 60000;
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
                .add(Attributes.MAX_HEALTH, 110.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.85D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(IS_BABY, false);
        builder.define(GROWTH_PROGRESS, 1.0F);
    }

    @Override
    public boolean isBaby() {
        return this.entityData.get(IS_BABY);
    }

    public float getGrowthProgress() {
        return this.isBaby() ? this.entityData.get(GROWTH_PROGRESS) : 1.0F;
    }

    public void setBabyWhale(boolean baby) {
        this.entityData.set(IS_BABY, baby);
        this.babyGrowTicks = baby ? MAX_GROW_TICKS : 0;
        this.entityData.set(GROWTH_PROGRESS, baby ? 0.0F : 1.0F);
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

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.DOLPHIN_AMBIENT_WATER;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.DOLPHIN_HURT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.DOLPHIN_DEATH;
    }

    @Override
    public float getVoicePitch() {
        return this.isBaby() ? 0.75F : 0.35F;
    }

    @Override
    public float getSoundVolume() {
        return 2.0F;
    }
    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!this.isBaby() && stack.is(Items.TROPICAL_FISH) && this.pregnancyTicks == 0) {
            if (!this.level().isClientSide()) {
                if (!player.getAbilities().instabuild) stack.shrink(1);
                this.pregnancyTicks = 30000;
                this.playSound(SoundEvents.DOLPHIN_EAT, 1.2F, 0.45F);
                ((ServerLevel) this.level()).sendParticles(ParticleTypes.HEART, this.getX(), this.getY() + 2.0, this.getZ(), 7, 1.2, 0.6, 1.2, 0.05);
            }
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide()) {
            if (this.isBaby() && this.babyGrowTicks != 0) {
                this.babyGrowTicks--;
                if (this.babyGrowTicks % 200 == 0) {
                    float progress = 1.0F - ((float) this.babyGrowTicks / (float) MAX_GROW_TICKS);
                    this.entityData.set(GROWTH_PROGRESS, Math.max(0.0F, Math.min(1.0F, progress)));
                }
                if (this.babyGrowTicks == 0) this.setBabyWhale(false);
            }
            if (this.pregnancyTicks != 0) {
                this.pregnancyTicks--;
                if (this.pregnancyTicks == 0) {
                    WhaleEntity calf = ModEntities.WHALE.get().create(this.level(), EntitySpawnReason.BREEDING);
                    if (calf != null) {
                        calf.setBabyWhale(true);
                        calf.snapTo(this.getX() + 2.5, this.getY(), this.getZ() + 2.5, this.getYRot(), 0);
                        this.level().addFreshEntity(calf);
                    }
                }
            }
            if (this.escortCooldown != 0) this.escortCooldown--;
            if (this.escortTicks != 0) {
                this.escortTicks--;
                List<Boat> boats = this.level().getEntitiesOfClass(Boat.class, this.getBoundingBox().inflate(22.0D));
                if (!boats.isEmpty()) {
                    Boat targetBoat = boats.getFirst();
                    if (Double.compare(this.distanceToSqr(targetBoat), 49.0D) == 1) {
                        this.getNavigation().moveTo(targetBoat, 1.15D);
                    }
                    if (targetBoat.getControllingPassenger() instanceof Player p) {
                        p.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 60, 0, true, false));
                    }
                }
                this.level().getEntitiesOfClass(Drowned.class, this.getBoundingBox().inflate(18.0D))
                        .forEach(d -> d.setDeltaMovement(d.position().subtract(this.position()).normalize().scale(0.35D)));
                if (this.escortTicks == 0) this.escortCooldown = 14000;
            } else if (this.escortCooldown == 0 && this.tickCount % 60 == 0) {
                List<Boat> boats = this.level().getEntitiesOfClass(Boat.class, this.getBoundingBox().inflate(26.0D));
                if (!boats.isEmpty()) {
                    this.escortTicks = 1800;
                }
            }
            if (this.breachCooldown != 0) {
                this.breachCooldown--;
            } else if (this.isInWater() && this.level().isEmptyBlock(this.blockPosition().above(2))) {
                this.setDeltaMovement(this.getDeltaMovement().add(0, 0.85D, 0));
                this.breachCooldown = 3600 + this.random.nextInt(2400);
                this.playSound(SoundEvents.GENERIC_SPLASH, 1.8F, 0.45F);
                ((ServerLevel) this.level()).sendParticles(ParticleTypes.SPLASH, this.getX(), this.getY() + 1.2, this.getZ(), 40, 2.5, 0.5, 2.5, 0.2);
            }
        } else {
            if (!this.isInWater() && Double.compare(this.getDeltaMovement().y, 0.1D) == 1) {
                this.breachState.startIfStopped(this.tickCount);
            } else if (this.onGround() || (this.isInWater() && Double.compare(this.getDeltaMovement().y, -0.2D) == -1)) {
                this.breachState.stop();
            }
            if (this.tickCount % 400 == 0) this.feedingState.start(this.tickCount);
        }
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, EntitySpawnReason reason, @Nullable SpawnGroupData spawnData) {
        if (!this.isBaby() && Float.compare(this.random.nextFloat(), 0.22F) == -1) {
            WhaleEntity calf = ModEntities.WHALE.get().create(level.getLevel(), EntitySpawnReason.NATURAL);
            if (calf != null) {
                calf.setBabyWhale(true);
                calf.snapTo(this.getX() + 3.0, this.getY(), this.getZ() + 3.0, this.getYRot(), 0);
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
        boolean baby = input.getBooleanOr("IsBabyWhale", false);
        this.entityData.set(IS_BABY, baby);
        this.pregnancyTicks = input.getIntOr("PregnancyTicks", 0);
        this.babyGrowTicks = input.getIntOr("BabyGrowTicks", 0);
        float progress = baby ? 1.0F - ((float) this.babyGrowTicks / (float) MAX_GROW_TICKS) : 1.0F;
        this.entityData.set(GROWTH_PROGRESS, Math.max(0.0F, Math.min(1.0F, progress)));
    }
}
