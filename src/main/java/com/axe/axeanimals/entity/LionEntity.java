package com.axe.axeanimals.entity;

import com.axe.axeanimals.registry.ModEntities;
import net.minecraft.core.particles.ParticleTypes;
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

import java.util.List;

public class LionEntity extends TamableAnimal {
    private static final EntityDataAccessor<Integer> ROAR_TICKS = SynchedEntityData.defineId(LionEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> COMMAND_MODE = SynchedEntityData.defineId(LionEntity.class, EntityDataSerializers.INT);
    private int roarCooldown = 240;
    private int sprintTicks = 0;

    public LionEntity(EntityType<? extends TamableAnimal> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createAnimalAttributes()
                .add(Attributes.MAX_HEALTH, 36.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.31D)
                .add(Attributes.ATTACK_DAMAGE, 8.0D);
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ROAR_TICKS, 0);
        builder.define(COMMAND_MODE, 0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.30D, true));
        this.goalSelector.addGoal(4, new FollowOwnerGoal(this, 1.15D, 10.0F, 2.0F) {
            @Override public boolean canUse() { return LionEntity.this.entityData.get(COMMAND_MODE) == 0 && super.canUse(); }
            @Override public boolean canContinueToUse() { return LionEntity.this.entityData.get(COMMAND_MODE) == 0 && super.canContinueToUse(); }
        });
        this.goalSelector.addGoal(5, new BreedGoal(this, 1.0D, LionessEntity.class));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.90D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 10.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this).setAlertOthers());
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Animal.class, 260, true, false,
                (target, level) -> !this.isTame() && !this.isBaby()
                        && !(target instanceof LionEntity)
                        && !(target instanceof LionessEntity)
                        && !(target instanceof FerretEntity)
                        && !(target instanceof TamableAnimal t && t.isTame())));
    }
    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

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
                player.displayClientMessage(Component.literal(msg), true);
            }
            return InteractionResult.SUCCESS;
        }

        return super.mobInteract(player, hand);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide()) {
            int rTicks = this.entityData.get(ROAR_TICKS);
            if (rTicks != 0) {
                this.entityData.set(ROAR_TICKS, rTicks - 1);
            }

            if (this.sprintTicks != 0) {
                this.sprintTicks--;
                this.setSprinting(true);
                if (this.getNavigation().isDone() && this.tickCount % 20 == 0) {
                    double tx = this.getX() + (this.random.nextDouble() - 0.5D) * 28.0D;
                    double tz = this.getZ() + (this.random.nextDouble() - 0.5D) * 28.0D;
                    this.getNavigation().moveTo(tx, this.getY(), tz, 1.40D);
                }
            } else {
                this.setSprinting(this.getTarget() != null);
                if (!this.isInWater() && this.onGround() && !this.isOrderedToSit() && this.random.nextInt(1600) == 0) {
                    this.sprintTicks = 70 + this.random.nextInt(30);
                }
            }

            if (this.roarCooldown != 0) {
                this.roarCooldown--;
            } else if (!this.isBaby() && this.onGround() && !this.isInWater()) {
                this.entityData.set(ROAR_TICKS, 55);
                this.roarCooldown = 1400 + this.random.nextInt(1000);
                this.playSound(SoundEvents.ENDER_DRAGON_GROWL, 0.90F, 0.72F);

                List<LionessEntity> pride = this.level().getEntitiesOfClass(LionessEntity.class, this.getBoundingBox().inflate(24.0D));
                for (LionessEntity lioness : pride) {
                    if (Double.compare(lioness.distanceToSqr(this), 49.0D) == 1) {
                        lioness.getNavigation().moveTo(this, 1.15D);
                    }
                    ((ServerLevel) this.level()).sendParticles(ParticleTypes.NOTE, lioness.getX(), lioness.getY() + 1.6, lioness.getZ(), 3, 0.4, 0.2, 0.4, 0.05);
                }
            }
        }
    }

    public boolean isRoaringNow() {
        return this.entityData.get(ROAR_TICKS) != 0;
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
        return 0.35F;
    }

    @Override
    public float getVoicePitch() {
        return this.isBaby() ? 1.30F : 0.52F;
    }
}
