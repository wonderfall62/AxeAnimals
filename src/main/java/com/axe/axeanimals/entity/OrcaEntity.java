package com.axe.axeanimals.entity;

import com.axe.axeanimals.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.animal.dolphin.Dolphin;
import net.minecraft.world.entity.animal.fish.AbstractFish;
import net.minecraft.world.entity.animal.fish.WaterAnimal;
import net.minecraft.world.entity.animal.squid.Squid;
import net.minecraft.world.entity.monster.Monster;
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
import java.util.UUID;

public class OrcaEntity extends WaterAnimal {
    private static final EntityDataAccessor<Boolean> IS_BABY = SynchedEntityData.defineId(OrcaEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> BREACH_TICKS = SynchedEntityData.defineId(OrcaEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> IS_TAMED = SynchedEntityData.defineId(OrcaEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> FIN_CRUISE_TICKS = SynchedEntityData.defineId(OrcaEntity.class, EntityDataSerializers.INT);

    @Nullable
    private UUID ownerUUID;
    private int breachCooldown = 700;
    private int finCruiseCooldown = 400;
    private int breedCooldown = 0;
    private int loveTicks = 0;
    private int babyGrowTicks = 0;

    public OrcaEntity(EntityType<? extends WaterAnimal> type, Level level) {
        super(type, level);
        this.moveControl = new SmoothSwimmingMoveControl(this, 80, 12, 0.055F, 0.16F, true);
        this.lookControl = new SmoothSwimmingLookControl(this, 10);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return WaterAnimal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 60.0D)
                .add(Attributes.MOVEMENT_SPEED, 1.45D)
                .add(Attributes.ATTACK_DAMAGE, 8.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.65D);
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    public static boolean checkOrcaSpawnRules(EntityType<OrcaEntity> type, ServerLevelAccessor level, EntitySpawnReason reason, BlockPos pos, RandomSource random) {
        return level.getFluidState(pos).is(FluidTags.WATER);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(IS_BABY, false);
        builder.define(BREACH_TICKS, 0);
        builder.define(IS_TAMED, false);
        builder.define(FIN_CRUISE_TICKS, 0);
    }

    @Override
    public boolean isBaby() {
        return this.entityData.get(IS_BABY);
    }

    public boolean isTamedOrca() {
        return this.entityData.get(IS_TAMED);
    }

    public boolean isFinCruisingNow() {
        return this.entityData.get(FIN_CRUISE_TICKS) != 0;
    }

    public void startFinCruise(int ticks) {
        this.entityData.set(FIN_CRUISE_TICKS, ticks);
        this.finCruiseCooldown = 900 + this.random.nextInt(500);
    }

    public void setTamedOrca(boolean tamed, @Nullable Player owner) {
        this.entityData.set(IS_TAMED, tamed);
        this.ownerUUID = (tamed && owner != null) ? owner.getUUID() : null;
        if (tamed) {
            this.setTarget(null);
            this.setHealth(this.getMaxHealth());
        }
    }

    @Nullable
    public Player getOwnerPlayer() {
        if (this.ownerUUID == null) return null;
        return this.level().getPlayerByUUID(this.ownerUUID);
    }

    public void setBabyOrca(boolean baby) {
        this.entityData.set(IS_BABY, baby);
        this.babyGrowTicks = baby ? 36000 : 0;
        this.refreshDimensions();
    }

    public float getBreachProgress() {
        int ticks = this.entityData.get(BREACH_TICKS);
        if (ticks == 0) return 0.0F;
        return (float) (36 - ticks) / 36.0F;
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new WaterBoundPathNavigation(this, level);
    }
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.75D, true));
        this.goalSelector.addGoal(2, new RandomSwimmingGoal(this, 1.40D, 20));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this) {
            @Override
            public boolean canUse() {
                return super.canUse() && !(this.mob.getLastHurtByMob() instanceof Player p && OrcaEntity.this.ownerUUID != null && OrcaEntity.this.ownerUUID.equals(p.getUUID()));
            }
        }.setAlertOthers());

        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Monster.class, 20, true, false,
                (target, lvl) -> this.isTamedOrca() && target.isInWater()));

        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, 50, true, false,
                (target, lvl) -> !this.isTamedOrca() && !this.isBaby() && target.isInWater()
                        && target instanceof Player p && !p.isCreative() && !p.isSpectator()
                        && !p.getMainHandItem().is(Items.NAUTILUS_SHELL) && !p.getOffhandItem().is(Items.NAUTILUS_SHELL)));

        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 150, true, false,
                (target, lvl) -> !this.isTamedOrca() && !this.isBaby() && target != this && !(target instanceof OrcaEntity)
                        && (target instanceof Dolphin
                        || target instanceof StingrayEntity
                        || (target instanceof WhaleEntity w && w.isBaby())
                        || BuiltInRegistries.ENTITY_TYPE.getKey(target.getType()).getPath().contains("seal"))));

        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Drowned.class, 120, true, false,
                (target, lvl) -> target.isInWater()));
        this.targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(this, Squid.class, 180, true, false, null));
        this.targetSelector.addGoal(7, new NearestAttackableTargetGoal<>(this, AbstractFish.class, 260, true, false,
                (target, lvl) -> !this.isBaby() && !this.isTamedOrca() && !(target instanceof StingrayEntity)));
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!this.isTamedOrca() && stack.is(Items.NAUTILUS_SHELL)) {
            if (!this.level().isClientSide()) {
                if (!player.getAbilities().instabuild) stack.shrink(1);
                if (this.random.nextInt(3) == 0) {
                    this.setTamedOrca(true, player);
                    this.playSound(SoundEvents.DOLPHIN_PLAY, 1.3F, 0.85F);
                    ((ServerLevel) this.level()).sendParticles(ParticleTypes.HEART, this.getX(), this.getY() + 1.2, this.getZ(), 10, 0.9, 0.5, 0.9, 0.05);
                } else {
                    this.playSound(SoundEvents.DOLPHIN_EAT, 1.1F, 0.7F);
                    ((ServerLevel) this.level()).sendParticles(ParticleTypes.SMOKE, this.getX(), this.getY() + 1.2, this.getZ(), 6, 0.6, 0.4, 0.6, 0.03);
                }
            }
            return InteractionResult.SUCCESS;
        }

        if (this.isTamedOrca() && !this.isBaby() && stack.is(Items.NAUTILUS_SHELL) && this.breedCooldown == 0 && this.loveTicks == 0) {
            if (!this.level().isClientSide()) {
                if (!player.getAbilities().instabuild) stack.shrink(1);
                this.heal(12.0F);
                this.loveTicks = 600;
                this.playSound(SoundEvents.DOLPHIN_EAT, 1.1F, 0.8F);
                ((ServerLevel) this.level()).sendParticles(ParticleTypes.HEART, this.getX(), this.getY() + 1.2, this.getZ(), 8, 0.8, 0.5, 0.8, 0.05);
            }
            return InteractionResult.SUCCESS;
        }

        if (this.isTamedOrca() && (stack.is(Items.SALMON) || stack.is(Items.COD)) && Float.compare(this.getHealth(), this.getMaxHealth()) == -1) {
            if (!this.level().isClientSide()) {
                if (!player.getAbilities().instabuild) stack.shrink(1);
                this.heal(8.0F);
                this.playSound(SoundEvents.DOLPHIN_EAT, 1.0F, 0.85F);
                ((ServerLevel) this.level()).sendParticles(ParticleTypes.HEART, this.getX(), this.getY() + 1.1, this.getZ(), 4, 0.5, 0.3, 0.5, 0.03);
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
                if (this.babyGrowTicks == 0) this.setBabyOrca(false);
            }

            if (this.loveTicks != 0) {
                this.loveTicks--;
                if (this.tickCount % 10 == 0) {
                    ((ServerLevel) this.level()).sendParticles(ParticleTypes.HEART, this.getX(), this.getY() + 1.1, this.getZ(), 2, 0.5, 0.3, 0.5, 0.02);
                    List<OrcaEntity> mates = this.level().getEntitiesOfClass(OrcaEntity.class, this.getBoundingBox().inflate(24.0D),
                            o -> o != this && !o.isBaby() && o.loveTicks != 0);
                    if (!mates.isEmpty()) {
                        OrcaEntity partner = mates.getFirst();
                        this.getNavigation().moveTo(partner, 1.55D);
                        if (Double.compare(this.distanceToSqr(partner), 16.0D) == -1) {
                            this.loveTicks = 0;
                            partner.loveTicks = 0;
                            this.breedCooldown = 6000;
                            partner.breedCooldown = 6000;
                            OrcaEntity calf = ModEntities.ORCA.get().create(this.level(), EntitySpawnReason.BREEDING);
                            if (calf != null) {
                                calf.setBabyOrca(true);
                                if (this.isTamedOrca()) {
                                    calf.setTamedOrca(true, this.getOwnerPlayer());
                                }
                                calf.snapTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0);
                                this.level().addFreshEntity(calf);
                                ((ServerLevel) this.level()).sendParticles(ParticleTypes.HEART, this.getX(), this.getY() + 1.3, this.getZ(), 12, 1.0, 0.6, 1.0, 0.05);
                            }
                        }
                    }
                }
            }

            // Спокойное волнообразное патрулирование стаи у поверхности (плавники регулярно выныривают из воды и уходят обратно)
            int fcTicks = this.entityData.get(FIN_CRUISE_TICKS);
            if (fcTicks != 0) {
                this.entityData.set(FIN_CRUISE_TICKS, fcTicks - 1);
                if (this.isInWater()) {
                    this.getNavigation().stop();
                    Vec3 look = this.getLookAngle();
                    boolean airRightAbove = !this.level().getFluidState(this.blockPosition().above()).is(FluidTags.WATER);
                    boolean deepUnderwater = this.level().getFluidState(this.blockPosition().above(2)).is(FluidTags.WATER);

                    double vy;
                    if (deepUnderwater) {
                        // Если косатка глубоко — плавно всплывает к поверхности
                        vy = 0.22D;
                    } else {
                        // У самой поверхности: волной то показывает плавник над водой, то опускается на блок ниже
                        double wave = Math.sin(fcTicks * 0.09D);
                        if (airRightAbove && Double.compare(wave, 0.0D) == 1) {
                            vy = -0.02D;
                        } else {
                            vy = wave * 0.14D;
                        }
                    }
                    // Спокойная, величественная скорость патрулирования
                    this.setDeltaMovement(look.x * 0.28D, vy, look.z * 0.28D);
                    if (airRightAbove && this.tickCount % 6 == 0) {
                        ((ServerLevel) this.level()).sendParticles(ParticleTypes.SPLASH, this.getX(), this.getY() + 0.65, this.getZ(), 3, 0.3, 0.08, 0.3, 0.03);
                    }
                }
            } else {
                if (this.finCruiseCooldown != 0) {
                    this.finCruiseCooldown--;
                } else if (this.isInWater() && this.entityData.get(BREACH_TICKS) == 0 && this.getTarget() == null && this.random.nextInt(120) == 0) {
                    int dur = 280;
                    this.startFinCruise(dur);
                    List<OrcaEntity> pod = this.level().getEntitiesOfClass(OrcaEntity.class, this.getBoundingBox().inflate(36.0D), o -> o != this);
                    for (OrcaEntity mate : pod) {
                        mate.startFinCruise(dur);
                    }
                }

                if (this.isInWater() && this.entityData.get(BREACH_TICKS) == 0) {
                    Vec3 m = this.getDeltaMovement();
                    double hSpeedSq = m.x * m.x + m.z * m.z;
                    if (Double.compare(hSpeedSq, 0.002D) == 1 && Double.compare(hSpeedSq, 0.16D) == -1) {
                        double boost = (this.getTarget() != null) ? 1.12D : 1.06D;
                        this.setDeltaMovement(m.x * boost, m.y, m.z * boost);
                    }
                }
            }

            int bTicks = this.entityData.get(BREACH_TICKS);
            if (bTicks != 0) {
                this.entityData.set(BREACH_TICKS, bTicks - 1);
            }

            if (this.isTamedOrca()) {
                Player owner = this.getOwnerPlayer();
                boolean ownerAquatic = owner != null && (owner.isInWater() || owner.getVehicle() instanceof Boat);
                if (ownerAquatic && this.getTarget() == null && this.loveTicks == 0 && this.tickCount % 12 == 0) {
                    double distSq = this.distanceToSqr(owner);
                    if (Double.compare(distSq, 64.0D) == 1 && Double.compare(distSq, 2500.0D) == -1) {
                        this.getNavigation().moveTo(owner.getX(), owner.getY() - 0.8D, owner.getZ(), 1.65D);
                    }
                } else if (!ownerAquatic && this.getTarget() != null && !this.getTarget().isInWater()) {
                    this.setTarget(null);
                }
            } else if (this.tickCount % 25 == 0 && fcTicks == 0) {
                List<OrcaEntity> pod = this.level().getEntitiesOfClass(OrcaEntity.class, this.getBoundingBox().inflate(32.0D), o -> o != this && !o.isBaby() && !o.isTamedOrca());
                if (!pod.isEmpty()) {
                    OrcaEntity leader = pod.getFirst();
                    if (Double.compare(this.distanceToSqr(leader), 120.0D) == 1) {
                        this.getNavigation().moveTo(leader, 1.45D);
                    }
                }
            }

            boolean nearSurface = !this.level().getFluidState(this.blockPosition().above(2)).is(FluidTags.WATER);
            boolean deepEnough = this.level().getFluidState(this.blockPosition().below(3)).is(FluidTags.WATER);
            if (this.breachCooldown != 0) {
                this.breachCooldown--;
            } else if (this.isInWater() && nearSurface && deepEnough && this.getTarget() == null && fcTicks == 0) {
                Vec3 look = this.getLookAngle();
                this.setDeltaMovement(look.x * 1.15D, 0.85D, look.z * 1.15D);
                this.entityData.set(BREACH_TICKS, 36);
                this.breachCooldown = 1400 + this.random.nextInt(1200);
                this.playSound(SoundEvents.DOLPHIN_SPLASH, 1.5F, 0.65F);
                ((ServerLevel) this.level()).sendParticles(ParticleTypes.SPLASH, this.getX(), this.getY() + 0.8, this.getZ(), 45, 2.0, 0.5, 2.0, 0.25);
            }
        }
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
        return this.isBaby() ? 0.95F : 0.62F;
    }

    @Override
    public float getSoundVolume() {
        return 1.3F;
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putBoolean("IsBabyOrca", this.isBaby());
        output.putBoolean("IsTamedOrca", this.isTamedOrca());
        output.putInt("BabyGrowTicks", this.babyGrowTicks);
        if (this.ownerUUID != null) {
            output.putString("OwnerUUID", this.ownerUUID.toString());
        }
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        boolean baby = input.getBooleanOr("IsBabyOrca", false);
        this.entityData.set(IS_BABY, baby);
        this.entityData.set(IS_TAMED, input.getBooleanOr("IsTamedOrca", false));
        this.babyGrowTicks = input.getIntOr("BabyGrowTicks", 0);
        String uuidStr = input.getStringOr("OwnerUUID", "");
        if (!uuidStr.isEmpty()) {
            try {
                this.ownerUUID = UUID.fromString(uuidStr);
            } catch (IllegalArgumentException ignored) {
            }
        }
    }
}
