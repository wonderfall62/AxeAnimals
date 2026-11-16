package com.axe.axeanimals.entity;

import com.axe.axeanimals.registry.ModEntities;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.animal.fish.WaterAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

public class StingrayEntity extends WaterAnimal {
    private static final EntityDataAccessor<Boolean> IS_BABY = SynchedEntityData.defineId(StingrayEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> GROWTH_PROGRESS = SynchedEntityData.defineId(StingrayEntity.class, EntityDataSerializers.FLOAT);
    private int breedCooldown = 0;
    private int babyGrowTicks = 0;
    private static final int MAX_GROW_TICKS = 24000;

    public StingrayEntity(EntityType<? extends WaterAnimal> type, Level level) {
        super(type, level);
        this.moveControl = new SmoothSwimmingMoveControl(this, 85, 10, 0.02F, 0.1F, true);
        this.lookControl = new SmoothSwimmingLookControl(this, 10);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return WaterAnimal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 14.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.9D);
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

    public void setBabyRay(boolean baby) {
        this.entityData.set(IS_BABY, baby);
        this.babyGrowTicks = baby ? MAX_GROW_TICKS : 0;
        this.entityData.set(GROWTH_PROGRESS, baby ? 0.0F : 1.0F);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new WaterBoundPathNavigation(this, level);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new RandomSwimmingGoal(this, 1.0D, 25));
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.FISH_SWIM;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.SALMON_HURT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SALMON_DEATH;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!this.isBaby() && stack.is(Items.TROPICAL_FISH) && this.breedCooldown == 0) {
            if (!this.level().isClientSide()) {
                if (!player.getAbilities().instabuild) stack.shrink(1);
                this.breedCooldown = 6000;
                this.playSound(SoundEvents.FISH_SWIM, 1.0F, 1.2F);
                ((ServerLevel) this.level()).sendParticles(ParticleTypes.HEART, this.getX(), this.getY() + 0.5, this.getZ(), 5, 0.4, 0.2, 0.4, 0.05);
                StingrayEntity baby = ModEntities.STINGRAY.get().create(this.level(), EntitySpawnReason.BREEDING);
                if (baby != null) {
                    baby.setBabyRay(true);
                    baby.snapTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0);
                    this.level().addFreshEntity(baby);
                }
            }
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide()) {
            if (this.breedCooldown != 0) this.breedCooldown--;
            if (this.isBaby() && this.babyGrowTicks != 0) {
                this.babyGrowTicks--;
                if (this.babyGrowTicks % 200 == 0) {
                    float progress = 1.0F - ((float) this.babyGrowTicks / (float) MAX_GROW_TICKS);
                    this.entityData.set(GROWTH_PROGRESS, Math.max(0.0F, Math.min(1.0F, progress)));
                }
                if (this.babyGrowTicks == 0) this.setBabyRay(false);
            }
        }
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putBoolean("IsBabyRay", this.isBaby());
        output.putInt("BabyGrowTicks", this.babyGrowTicks);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        boolean baby = input.getBooleanOr("IsBabyRay", false);
        this.entityData.set(IS_BABY, baby);
        this.babyGrowTicks = input.getIntOr("BabyGrowTicks", 0);
        float progress = baby ? 1.0F - ((float) this.babyGrowTicks / (float) MAX_GROW_TICKS) : 1.0F;
        this.entityData.set(GROWTH_PROGRESS, Math.max(0.0F, Math.min(1.0F, progress)));
    }
}
