package com.axe.axeanimals.entity;

import com.axe.axeanimals.registry.ModEntities;
import net.minecraft.network.chat.Component;
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
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class LionessEntity extends TamableAnimal {
    private static final EntityDataAccessor<Boolean> IS_SLEEPING = SynchedEntityData.defineId(LionessEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> SLEEP_PROGRESS = SynchedEntityData.defineId(LionessEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> COMMAND_MODE = SynchedEntityData.defineId(LionessEntity.class, EntityDataSerializers.INT);
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
        builder.define(IS_SLEEPING, false);
        builder.define(SLEEP_PROGRESS, 0.0F);
        builder.define(COMMAND_MODE, 0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.35D, true));
        this.goalSelector.addGoal(4, new FollowOwnerGoal(this, 1.15D, 10.0F, 2.0F) {
            @Override public boolean canUse() { return !LionessEntity.this.isSleepingNow() && LionessEntity.this.entityData.get(COMMAND_MODE) == 0 && super.canUse(); }
            @Override public boolean canContinueToUse() { return !LionessEntity.this.isSleepingNow() && LionessEntity.this.entityData.get(COMMAND_MODE) == 0 && super.canContinueToUse(); }
        });
        this.goalSelector.addGoal(5, new BreedGoal(this, 1.0D, LionEntity.class));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.95D) {
            @Override public boolean canUse() { return !LionessEntity.this.isSleepingNow() && super.canUse(); }
            @Override public boolean canContinueToUse() { return !LionessEntity.this.isSleepingNow() && super.canContinueToUse(); }
        });
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 10.0F) {
            @Override public boolean canUse() { return !LionessEntity.this.isSleepingNow() && super.canUse(); }
        });
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this) {
            @Override public boolean canUse() { return !LionessEntity.this.isSleepingNow() && super.canUse(); }
        });

        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this).setAlertOthers());
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Animal.class, 200, true, false,
                (target, level) -> !this.isTame() && !this.isBaby() && !this.isSleepingNow()
                        && !(target instanceof LionEntity)
                        && !(target instanceof LionessEntity)
                        && !(target instanceof FerretEntity)
                        && !(target instanceof TamableAnimal t && t.isTame())));
    }
    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        this.entityData.set(IS_SLEEPING, false);

        if (this.isBaby() && !this.isTame() && this.isFood(stack)) {
            if (!this.level().isClientSide()) {
                if (!player.getAbilities().instabuild) stack.shrink(1);
                if (this.random.nextInt(3) == 0) {
                    this.tame(player);
                    this.entityData.set(COMMAND_MODE, 0);
                    this.setOrderedToSit(false);
                    this.level().broadcastEntityEvent(this, (byte) 7);
                } else {
                    this.level().broadcastEntityEvent(this, (byte) 6);
                }
            }
            return InteractionResult.SUCCESS;
        }

        if (!this.isBaby() && this.isFood(stack)) {
            if (!this.level().isClientSide()) {
                if (Float.compare(this.getHealth(), this.getMaxHealth()) == -1) {
                    if (!player.getAbilities().instabuild) stack.shrink(1);
                    this.heal(6.0F);
                    this.level().broadcastEntityEvent(this, (byte) 7);
                    return InteractionResult.SUCCESS;
                }
                if (this.getAge() == 0 && this.canFallInLove()) {
                    if (!player.getAbilities().instabuild) stack.shrink(1);
                    this.setInLove(player);
                    return InteractionResult.SUCCESS;
                }
            }
            return InteractionResult.SUCCESS;
        }

        if (this.isTame() && this.isOwnedBy(player)) {
            if (!this.level().isClientSide()) {
                int nextMode = (this.entityData.get(COMMAND_MODE) + 1) % 3;
                this.entityData.set(COMMAND_MODE, nextMode);
                this.setOrderedToSit(nextMode == 1);
                String msg = nextMode == 0 ? "Режим: Следовать" : (nextMode == 1 ? "Режим: Сидеть" : "Режим: Свободный выгул");
                if (player instanceof net.minecraft.server.level.ServerPlayer sp) {
                    sp.sendSystemMessage(Component.literal(msg), true);
                }
            }
            return InteractionResult.SUCCESS;
        }


        return super.mobInteract(player, hand);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide()) return;

        boolean isNight = !this.level().isBrightOutside();
        boolean canSleep = isNight && this.onGround() && !this.isInWater() && this.getTarget() == null && !this.isInLove() && !this.isOrderedToSit();

        if (canSleep) {
            if (!this.isSleepingNow() && this.tickCount % 40 == 0 && this.random.nextInt(2) == 0) {
                this.entityData.set(IS_SLEEPING, true);
                this.getNavigation().stop();
            }
            if (this.isSleepingNow()) {
                this.getNavigation().stop();
                this.setDeltaMovement(0.0D, this.getDeltaMovement().y, 0.0D);
            }
        } else if (this.isSleepingNow()) {
            this.entityData.set(IS_SLEEPING, false);
        }

        float curProgress = this.entityData.get(SLEEP_PROGRESS);
        if (this.isSleepingNow() && Float.compare(curProgress, 1.0F) == -1) {
            this.entityData.set(SLEEP_PROGRESS, Math.min(1.0F, curProgress + 0.04F));
        } else if (!this.isSleepingNow() && Float.compare(curProgress, 0.0F) == 1) {
            this.entityData.set(SLEEP_PROGRESS, Math.max(0.0F, curProgress - 0.06F));
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
            if (!isNight && !this.isSleepingNow() && !this.isInWater() && this.onGround() && !this.isOrderedToSit()
                    && this.random.nextInt(1500) == 0) {
                this.sprintTicks = 75 + this.random.nextInt(35);
            }
        }
    }

    public boolean isSleepingNow() {
        return this.entityData.get(IS_SLEEPING);
    }

    public float getSleepProgress() {
        return this.entityData.get(SLEEP_PROGRESS);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(Items.BEEF) || stack.is(Items.MUTTON) || stack.is(Items.PORKCHOP) || stack.is(Items.CHICKEN);
    }

    @Override
    public boolean canMate(Animal otherAnimal) {
        if (otherAnimal == this) return false;
        if (!(otherAnimal instanceof LionEntity other)) return false;
        return this.isInLove() && other.isInLove();
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        TamableAnimal cub = level.getRandom().nextBoolean()
                ? ModEntities.LION.get().create(level, EntitySpawnReason.BREEDING)
                : ModEntities.LIONESS.get().create(level, EntitySpawnReason.BREEDING);
        if (cub != null) {
            cub.setBaby(true);
            if (this.isTame() && this.getOwner() instanceof Player owner) {
                cub.tame(owner);
            }
        }
        return cub;
    }

    @Override
    public int getAmbientSoundInterval() {
        return 480;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return this.isSleepingNow() ? null : SoundEvents.OCELOT_AMBIENT;
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
