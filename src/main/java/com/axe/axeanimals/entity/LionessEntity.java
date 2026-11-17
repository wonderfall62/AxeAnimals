package com.axe.axeanimals.entity;

import com.axe.axeanimals.registry.ModEntities;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.cow.Cow;
import net.minecraft.world.entity.animal.pig.Pig;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.entity.animal.rabbit.Rabbit;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class LionessEntity extends TamableAnimal {
    private static final EntityDataAccessor<Integer> STRETCH_TICKS = SynchedEntityData.defineId(LionessEntity.class, EntityDataSerializers.INT);
    public final AnimationState stretchState = new AnimationState();
    private int sprintTicks = 0;

    public LionessEntity(EntityType<? extends TamableAnimal> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createAnimalAttributes()
                .add(Attributes.MAX_HEALTH, 32.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.33D)
                .add(Attributes.ATTACK_DAMAGE, 7.0D);
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(STRETCH_TICKS, 0);
    }

    @Override
    protected void registerGoals() {
                this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Animal.class, 220, true, false,
                (target, level) -> !this.isTame() && !this.isBaby()
                        && !(target instanceof LionEntity)
                        && !(target instanceof LionessEntity)
                        && !(target instanceof FerretEntity)
                        && !(target instanceof TamableAnimal t && t.isTame())));

        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.35D, true));
        this.goalSelector.addGoal(4, new FollowOwnerGoal(this, 1.15D, 10.0F, 2.0F));
        this.goalSelector.addGoal(5, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.95D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 10.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this).setAlertOthers());
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (this.isBaby() && !this.isTame() && stack.is(Items.BEEF)) {
            if (!this.level().isClientSide()) {
                if (!player.getAbilities().instabuild) stack.shrink(1);
                if (this.random.nextInt(3) == 0) {
                    this.tame(player);
                    this.level().broadcastEntityEvent(this, (byte) 7);
                } else {
                    this.level().broadcastEntityEvent(this, (byte) 6);
                }
            }
            return InteractionResult.SUCCESS;
        }
        if (this.isTame() && this.isOwnedBy(player) && !this.isFood(stack)) {
            if (!this.level().isClientSide()) {
                this.setOrderedToSit(!this.isOrderedToSit());
            }
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide()) {
            int st = this.entityData.get(STRETCH_TICKS);
            boolean moving = Double.compare(this.getDeltaMovement().horizontalDistanceSqr(), 0.002D) == 1;

            if (st != 0) {
                if (moving || this.isInWater() || this.isOrderedToSit()) {
                    this.entityData.set(STRETCH_TICKS, 0);
                } else {
                    this.getNavigation().stop();
                    this.entityData.set(STRETCH_TICKS, st - 1);
                }
            } else if (!this.isBaby() && !this.isInWater() && this.onGround() && !this.isOrderedToSit()
                    && !moving && this.sprintTicks == 0 && this.random.nextInt(260) == 0) {
                this.entityData.set(STRETCH_TICKS, 50);
            }

            if (this.sprintTicks != 0) {
                this.sprintTicks--;
                this.setSprinting(true);
                if (this.getNavigation().isDone() && this.tickCount % 20 == 0) {
                    double tx = this.getX() + (this.random.nextDouble() - 0.5D) * 28.0D;
                    double tz = this.getZ() + (this.random.nextDouble() - 0.5D) * 28.0D;
                    this.getNavigation().moveTo(tx, this.getY(), tz, 1.45D);
                }
            } else {
                this.setSprinting(this.getTarget() != null);
                if (!this.isInWater() && this.onGround() && !this.isOrderedToSit()
                        && this.entityData.get(STRETCH_TICKS) == 0 && this.random.nextInt(320) == 0) {
                    this.sprintTicks = 130 + this.random.nextInt(70);
                }
            }
        }
    }

    public boolean isStretchingNow() {
        return this.entityData.get(STRETCH_TICKS) != 0;
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(Items.BEEF) || stack.is(Items.MUTTON);
    }

    @Override
    public boolean canMate(Animal otherAnimal) {
        return otherAnimal instanceof LionEntity && super.canMate(otherAnimal);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return level.getRandom().nextBoolean()
                ? ModEntities.LION.get().create(level, EntitySpawnReason.BREEDING)
                : ModEntities.LIONESS.get().create(level, EntitySpawnReason.BREEDING);
    }

    @Override
    public int getAmbientSoundInterval() {
        return 480;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.OCELOT_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.POLAR_BEAR_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.POLAR_BEAR_DEATH;
    }

    @Override
    public float getSoundVolume() {
        return 0.30F;
    }

    @Override
    public float getVoicePitch() {
        return this.isBaby() ? 1.35F : 0.65F;
    }
}
