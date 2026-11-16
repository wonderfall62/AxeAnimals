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

public class WhaleEntity extends WaterAnimal {
    private static final EntityDataAccessor<Boolean> IS_BABY = SynchedEntityData.defineId(WhaleEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> GROWTH_PROGRESS = SynchedEntityData.defineId(WhaleEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> BREACH_TICKS = SynchedEntityData.defineId(WhaleEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> FEEDING_TICKS = SynchedEntityData.defineId(WhaleEntity.class, EntityDataSerializers.INT);

    public final AnimationState breachState = new AnimationState();
    public final AnimationState feedingState = new AnimationState();

    private int pregnancyTicks = 0;
    private int babyGrowTicks = 0;
    private static final int MAX_GROW_TICKS = 60000;
    private int breachCooldown = 220;
    private int escortTicks = 0;
    private int escortCooldown = 0;

    public WhaleEntity(EntityType<? extends WaterAnimal> type, Level level) {
        super(type, level);
        this.moveControl = new SmoothSwimmingMoveControl(this, 35, 4, 0.02F, 0.1F, true);
        this.lookControl = new SmoothSwimmingLookControl(this, 8);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return WaterAnimal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 110.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.80D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D);
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    public static boolean checkWhaleSpawnRules(EntityType<WhaleEntity> type, ServerLevelAccessor level, EntitySpawnReason reason, BlockPos pos, RandomSource random) {
        if (!level.getFluidState(pos).is(FluidTags.WATER)) return false;
        int depth = 0;
        for (int i = 1; i != 7; i++) {
            if (level.getFluidState(pos.below(i)).is(FluidTags.WATER)) {
                depth++;
            } else {
                break;
            }
        }
        if (depth != 6) return false;
        for (int dx = -10; dx != 15; dx += 5) {
            for (int dz = -10; dz != 15; dz += 5) {
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
        builder.define(BREACH_TICKS, 0);
        builder.define(FEEDING_TICKS, 0);
    }

    @Override
    public boolean isBaby() {
        return this.entityData.get(IS_BABY);
    }

    public float getGrowthProgress() {
        return this.isBaby() ? this.entityData.get(GROWTH_PROGRESS) : 1.0F;
    }

    public float getBreachProgress() {
        int ticks = this.entityData.get(BREACH_TICKS);
        if (ticks == 0) return 0.0F;
        return (float) (50 - ticks) / 50.0F;
    }

    public float getFeedingProgress() {
        int ticks = this.entityData.get(FEEDING_TICKS);
        if (ticks == 0) return 0.0F;
        return (float) Math.sin(((float) (60 - ticks) / 60.0F) * Math.PI);
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
        this.goalSelector.addGoal(1, new RandomSwimmingGoal(this, 1.0D, 40));
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
                this.entityData.set(FEEDING_TICKS, 60);
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
            if (this.tickCount == 5 && this.getPersistentData().getBooleanOr("NeedsNaturalCalf", false)) {
                this.getPersistentData().remove("NeedsNaturalCalf");
                WhaleEntity calf = ModEntities.WHALE.get().create(this.level(), EntitySpawnReason.NATURAL);
                if (calf != null) {
                    calf.setBabyWhale(true);
                    calf.snapTo(this.getX() + 3.5, this.getY(), this.getZ() + 3.5, this.getYRot(), 0);
                    this.level().addFreshEntity(calf);
                }
            }

            int bTicks = this.entityData.get(BREACH_TICKS);
            if (bTicks != 0) {
                this.entityData.set(BREACH_TICKS, bTicks - 1);
            }

            int fTicks = this.entityData.get(FEEDING_TICKS);
            if (fTicks != 0) {
                this.entityData.set(FEEDING_TICKS, fTicks - 1);
            }

            BlockPos pos = this.blockPosition();
            boolean shallow = !this.level().getFluidState(pos.below(2)).is(FluidTags.WATER);
            boolean tooHighOnSurface = !this.level().getFluidState(pos.above(1)).is(FluidTags.WATER);

            if (bTicks == 0 && tooHighOnSurface) {
                Vec3 m = this.getDeltaMovement();
                this.setDeltaMovement(m.x, Math.min(m.y, -0.08D), m.z);
            }
            if ((!this.isInWater() && bTicks == 0) || (shallow && this.tickCount % 20 == 0)) {
                for (int r = 4; r != 20; r += 4) {
                    BlockPos best = null;
                    for (int dx = -r; dx != r + r; dx += r) {
                        for (int dz = -r; dz != r + r; dz += r) {
                            BlockPos check = pos.offset(dx, -2, dz);
                            if (this.level().getFluidState(check).is(FluidTags.WATER) && this.level().getFluidState(check.below(2)).is(FluidTags.WATER)) {
                                best = check;
                                break;
                            }
                        }
                        if (best != null) break;
                    }
                    if (best != null) {
                        Vec3 dir = Vec3.atCenterOf(best).subtract(this.position()).normalize();
                        this.setDeltaMovement(dir.x * 0.25D, -0.14D, dir.z * 0.25D);
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

            if (this.isBaby() && this.tickCount % 12 == 0) {
                var adults = this.level().getEntitiesOfClass(WhaleEntity.class, this.getBoundingBox().inflate(36.0D), w -> !w.isBaby() && w != this);
                if (!adults.isEmpty()) {
                    WhaleEntity mom = adults.getFirst();
                    if (Double.compare(this.distanceToSqr(mom), 32.0D) == 1) {
                        Vec3 side = new Vec3(-mom.getLookAngle().z, 0, mom.getLookAngle().x).normalize().scale(3.8D);
                        this.getNavigation().moveTo(mom.getX() + side.x, mom.getY(), mom.getZ() + side.z, 1.28D);
                    }
                }
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

            if (this.isInWater() && this.tickCount % 200 == 0 && bTicks == 0) {
                Vec3 look = this.getLookAngle();
                double bx = this.getX() + look.x * 1.6D;
                double by = this.getY() + 2.7D;
                double bz = this.getZ() + look.z * 1.6D;
                ServerLevel sl = (ServerLevel) this.level();
                for (int h = 0; h != 6; h++) {
                    sl.sendParticles(ParticleTypes.SPLASH, bx, by + h * 0.55D, bz, 12, 0.25D + h * 0.08D, 0.2D, 0.25D + h * 0.08D, 0.15D);
                    sl.sendParticles(ParticleTypes.CLOUD, bx, by + h * 0.55D, bz, 2, 0.18D + h * 0.06D, 0.15D, 0.18D + h * 0.06D, 0.03D);
                }
                this.playSound(SoundEvents.DOLPHIN_SPLASH, 1.6F, 0.55F);

            }

            if (this.escortCooldown != 0) this.escortCooldown--;
            var boats = this.level().getEntitiesOfClass(Boat.class, this.getBoundingBox().inflate(24.0D));
            if (this.escortTicks != 0) {
                this.escortTicks--;
                if (!boats.isEmpty()) {
                    Boat targetBoat = boats.getFirst();
                    Vec3 forward = targetBoat.getLookAngle();
                    Vec3 toWhale = this.position().subtract(targetBoat.position());
                    double sideSign = Double.compare(forward.x * toWhale.z - forward.z * toWhale.x, 0.0D) == -1 ? -1.0D : 1.0D;
                    Vec3 side = new Vec3(-forward.z * sideSign, 0, forward.x * sideSign).normalize();
                    Vec3 escortPos = targetBoat.position().add(side.scale(6.8D)).add(forward.scale(2.5D));

                    this.getNavigation().moveTo(escortPos.x, targetBoat.getY() - 1.2D, escortPos.z, 1.15D);
                    if (targetBoat.getControllingPassenger() instanceof Player p) {
                        p.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 60, 0, true, false));
                    }
                }
                if (this.escortTicks == 0) this.escortCooldown = 12000;
            } else if (this.escortCooldown == 0 && !boats.isEmpty() && this.tickCount % 60 == 0) {
                this.escortTicks = 1200;
            }

            this.level().getEntitiesOfClass(Drowned.class, this.getBoundingBox().inflate(18.0D))
                    .forEach(d -> d.setDeltaMovement(d.position().subtract(this.position()).normalize().scale(0.35D)));

            if (fTicks == 0 && this.isInWater() && this.tickCount % 220 == 0 && Float.compare(this.random.nextFloat(), 0.55F) == -1) {
                this.entityData.set(FEEDING_TICKS, 60);
                ((ServerLevel) this.level()).sendParticles(ParticleTypes.SPLASH, this.getX(), this.getY() + 1.8, this.getZ(), 24, 0.6, 0.8, 0.6, 0.15);
            }

            if (bTicks != 0 && Float.compare(bTicks, 44.0F) == 1) {
                Vec3 m = this.getDeltaMovement();
                this.setDeltaMovement(m.x, Math.max(m.y, 0.68D), m.z);
            }

            if (this.breachCooldown != 0) {
                this.breachCooldown--;
                if (this.escortTicks != 0 && Float.compare(this.breachCooldown, 280.0F) == 1) {
                    this.breachCooldown = 220 + this.random.nextInt(100);
                }
            } else if (this.isInWater() && !shallow) {
                var tooCloseBoats = this.level().getEntitiesOfClass(Boat.class, this.getBoundingBox().inflate(5.2D));
                if (tooCloseBoats.isEmpty()) {
                    Vec3 look = this.getLookAngle();
                    this.setDeltaMovement(look.x * 0.62D, 1.38D, look.z * 0.62D);
                    this.entityData.set(BREACH_TICKS, 50);
                    this.breachCooldown = (this.escortTicks != 0) ? (260 + this.random.nextInt(140)) : (500 + this.random.nextInt(400));
                    this.playSound(SoundEvents.GENERIC_SPLASH, 2.2F, 0.45F);
                    ((ServerLevel) this.level()).sendParticles(ParticleTypes.SPLASH, this.getX(), this.getY() + 1.2, this.getZ(), 75, 3.2, 0.8, 3.2, 0.30);
                }
            }
        }
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, EntitySpawnReason reason, @Nullable SpawnGroupData spawnData) {
        if (!this.isBaby() && reason == EntitySpawnReason.NATURAL && Float.compare(this.random.nextFloat(), 0.25F) == -1) {
            this.getPersistentData().putBoolean("NeedsNaturalCalf", true);
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
