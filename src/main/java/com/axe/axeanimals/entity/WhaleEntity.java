package com.axe.axeanimals.entity;

import com.axe.axeanimals.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
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
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WhaleEntity extends WaterAnimal {
    private static final EntityDataAccessor<Boolean> IS_BABY = SynchedEntityData.defineId(WhaleEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> GROWTH_PROGRESS = SynchedEntityData.defineId(WhaleEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> IS_BREACHING = SynchedEntityData.defineId(WhaleEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_FEEDING = SynchedEntityData.defineId(WhaleEntity.class, EntityDataSerializers.BOOLEAN);

    public final AnimationState breachState = new AnimationState();
    public final AnimationState feedingState = new AnimationState();

    private int pregnancyTicks = 0;
    private int babyGrowTicks = 0;
    private static final int MAX_GROW_TICKS = 60000;
    private int breachCooldown = 1800;
    private int feedingTicks = 0;
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

    public static boolean checkWhaleSpawnRules(EntityType<WhaleEntity> type, ServerLevelAccessor level, EntitySpawnReason reason, BlockPos pos, RandomSource random) {
        if (!level.getFluidState(pos).is(FluidTags.WATER)) return false;
        int depth = 0;
        for (int i = 1; i <= 6; i++) {
            if (level.getFluidState(pos.below(i)).is(FluidTags.WATER)) {
                depth++;
            } else {
                break;
            }
        }
        if (depth < 5) return false;
        for (int dx = -8; dx <= 8; dx += 4) {
            for (int dz = -8; dz <= 8; dz += 4) {
                if (!level.getFluidState(pos.offset(dx, -1, dz)).is(FluidTags.WATER)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(IS_BABY, false);
        builder.define(GROWTH_PROGRESS, 1.0F);
        builder.define(IS_BREACHING, false);
        builder.define(IS_FEEDING, false);
    }

    @Override
    public boolean isBaby() {
        return this.entityData.get(IS_BABY);
    }

    public float getGrowthProgress() {
        return this.isBaby() ? this.entityData.get(GROWTH_PROGRESS) : 1.0F;
    }

    public boolean isBreachingNow() {
        return this.entityData.get(IS_BREACHING);
    }

    public boolean isFeedingNow() {
        return this.entityData.get(IS_FEEDING);
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
        return 1.8F;
    }
    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!this.isBaby() && stack.is(Items.TROPICAL_FISH) && this.pregnancyTicks == 0) {
            if (!this.level().isClientSide()) {
                if (!player.getAbilities().instabuild) stack.shrink(1);
                this.pregnancyTicks = 30000;
                this.feedingTicks = 45;
                this.entityData.set(IS_FEEDING, true);
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
            // Защита от застревания на мелководье или над водой
            BlockPos pos = this.blockPosition();
            boolean shallow = !this.level().getFluidState(pos.below(2)).is(FluidTags.WATER);
                        if ((!this.isInWater() && !this.isBreachingNow()) || (shallow && this.tickCount % 20 == 0)) {
                for (int r = 3; r != 15; r += 3) {
                    BlockPos best = null;
                    for (int dx = -r; dx != r + r; dx += r) {
                        for (int dz = -r; dz != r + r; dz += r) {
                            BlockPos check = pos.offset(dx, -1, dz);
                            if (this.level().getFluidState(check).is(FluidTags.WATER) && this.level().getFluidState(check.below(2)).is(FluidTags.WATER)) {
                                best = check;
                                break;
                            }
                        }
                        if (best != null) break;
                    }
                    if (best != null) {
                        Vec3 dir = Vec3.atCenterOf(best).subtract(this.position()).normalize();
                        this.setDeltaMovement(dir.x * 0.25D, -0.12D, dir.z * 0.25D);
                        this.getNavigation().moveTo(best.getX(), best.getY(), best.getZ(), 1.15D);
                        break;
                    }
                }
            }

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
                        calf.snapTo(this.getX() + 3.0, this.getY(), this.getZ() + 3.0, this.getYRot(), 0);
                        this.level().addFreshEntity(calf);
                    }
                }
            }

            if (this.escortCooldown != 0) this.escortCooldown--;
            if (this.escortTicks != 0) {
                this.escortTicks--;
                var boats = this.level().getEntitiesOfClass(Boat.class, this.getBoundingBox().inflate(24.0D));
                if (!boats.isEmpty()) {
                    Boat targetBoat = boats.getFirst();
                    Vec3 forward = targetBoat.getLookAngle();
                    Vec3 side = new Vec3(-forward.z, 0, forward.x).normalize();
                    Vec3 sideTarget = targetBoat.position().add(side.scale(5.8D)).add(forward.scale(2.0D));

                    if (Double.compare(this.distanceToSqr(targetBoat), 18.0D) == -1) {
                        Vec3 away = this.position().subtract(targetBoat.position()).normalize();
                        this.setDeltaMovement(away.x * 0.22D, this.getDeltaMovement().y, away.z * 0.22D);
                    } else {
                        this.getNavigation().moveTo(sideTarget.x, targetBoat.getY() - 0.5D, sideTarget.z, 1.15D);
                    }
                    if (targetBoat.getControllingPassenger() instanceof Player p) {
                        p.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 60, 0, true, false));
                    }
                }
                this.level().getEntitiesOfClass(Drowned.class, this.getBoundingBox().inflate(18.0D))
                        .forEach(d -> d.setDeltaMovement(d.position().subtract(this.position()).normalize().scale(0.35D)));
                if (this.escortTicks == 0) this.escortCooldown = 14000;
            } else if (this.escortCooldown == 0 && this.tickCount % 60 == 0) {
                var boats = this.level().getEntitiesOfClass(Boat.class, this.getBoundingBox().inflate(26.0D));
                if (!boats.isEmpty()) {
                    this.escortTicks = 1800;
                }
            }


            // Анимация 2: кормление / фонтан
            if (this.feedingTicks != 0) {
                this.feedingTicks--;
                if (this.feedingTicks == 0) {
                    this.entityData.set(IS_FEEDING, false);
                }
            } else if (this.isInWater() && this.tickCount % 320 == 0 && Float.compare(this.random.nextFloat(), 0.45F) == -1) {
                this.feedingTicks = 40;
                this.entityData.set(IS_FEEDING, true);
                ((ServerLevel) this.level()).sendParticles(ParticleTypes.SPLASH, this.getX(), this.getY() + 1.8, this.getZ(), 20, 0.6, 0.8, 0.6, 0.15);
            }

            // Анимация 3: выпрыгивание из воды (breach) — сбоку от лодки или в открытом океане
            if (this.breachCooldown != 0) {
                this.breachCooldown--;
                if (this.escortTicks != 0 && Float.compare(this.breachCooldown, 450.0F) == 1) {
                    this.breachCooldown = 350 + this.random.nextInt(150);
                }
                if (this.isInWater() && this.isBreachingNow() && Double.compare(this.getDeltaMovement().y, 0.05D) == -1) {
                    this.entityData.set(IS_BREACHING, false);
                }
            } else if (this.isInWater() && !shallow && this.level().isEmptyBlock(this.blockPosition().above(2))) {
                List<Boat> nearBoats = this.level().getEntitiesOfClass(Boat.class, this.getBoundingBox().inflate(4.5D));
                if (nearBoats.isEmpty()) {
                    this.setDeltaMovement(this.getDeltaMovement().add(0, 0.90D, 0));
                    this.entityData.set(IS_BREACHING, true);
                    this.breachCooldown = (this.escortTicks != 0) ? (400 + this.random.nextInt(250)) : (1800 + this.random.nextInt(1800));
                    this.playSound(SoundEvents.GENERIC_SPLASH, 1.8F, 0.45F);
                    ((ServerLevel) this.level()).sendParticles(ParticleTypes.SPLASH, this.getX(), this.getY() + 1.2, this.getZ(), 45, 2.5, 0.5, 2.5, 0.25);
                }
            }
        } else {
            if (this.isBreachingNow()) {
                this.breachState.startIfStopped(this.tickCount);
            } else {
                this.breachState.stop();
            }
            if (this.isFeedingNow()) {
                this.feedingState.startIfStopped(this.tickCount);
            } else {
                this.feedingState.stop();
            }
        }
    }


    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, EntitySpawnReason reason, @Nullable SpawnGroupData spawnData) {
        if (!this.isBaby() && Float.compare(this.random.nextFloat(), 0.22F) == -1) {
            WhaleEntity calf = ModEntities.WHALE.get().create(level.getLevel(), EntitySpawnReason.NATURAL);
            if (calf != null) {
                calf.setBabyWhale(true);
                calf.snapTo(this.getX() + 3.5, this.getY(), this.getZ() + 3.5, this.getYRot(), 0);
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
