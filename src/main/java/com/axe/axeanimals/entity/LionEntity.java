package com.axe.axeanimals.entity;

import com.axe.axeanimals.registry.ModEntities;
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
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;

public class LionEntity extends TamableAnimal {
    private static final EntityDataAccessor<Integer> ROAR_TICKS = SynchedEntityData.defineId(LionEntity.class, EntityDataSerializers.INT);
    public final AnimationState roarState = new AnimationState();

    public LionEntity(EntityType<? extends TamableAnimal> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createAnimalAttributes()
                .add(Attributes.MAX_HEALTH, 36.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.30D)
                .add(Attributes.ATTACK_DAMAGE, 8.0D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ROAR_TICKS, 0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.25D, true));
        this.goalSelector.addGoal(4, new FollowOwnerGoal(this, 1.1D, 10.0F, 2.0F));
        this.goalSelector.addGoal(5, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.9D));
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
            int rt = this.entityData.get(ROAR_TICKS);
            if (rt != 0) {
                this.entityData.set(ROAR_TICKS, rt - 1);
            } else if (!this.isBaby() && !this.isInWater() && this.onGround() && !this.isOrderedToSit() && this.random.nextInt(360) == 0) {
                this.entityData.set(ROAR_TICKS, 45);
                this.playSound(SoundEvents.RAVAGER_ROAR, 0.95F, 0.72F);
            }
        }
    }

    public boolean isRoaringNow() {
        return this.entityData.get(ROAR_TICKS) != 0;
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(Items.BEEF) || stack.is(Items.MUTTON);
    }

    @Override
    public boolean canMate(Animal otherAnimal) {
        return otherAnimal instanceof LionessEntity && super.canMate(otherAnimal);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return level.getRandom().nextBoolean()
                ? ModEntities.LION.get().create(level, EntitySpawnReason.BREEDING)
                : ModEntities.LIONESS.get().create(level, EntitySpawnReason.BREEDING);
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, EntitySpawnReason reason, @Nullable SpawnGroupData spawnData) {
        if (reason == EntitySpawnReason.NATURAL && !this.isBaby()) {
            for (int i = 0; i != 2; i++) {
                LionessEntity lioness = ModEntities.LIONESS.get().create(level.getLevel(), EntitySpawnReason.NATURAL);
                if (lioness != null) {
                    lioness.snapTo(this.getX() + (i + 1) * 1.5, this.getY(), this.getZ() + 1.5, this.getYRot(), 0);
                    level.addFreshEntity(lioness);
                }
            }
            if (Float.compare(this.random.nextFloat(), 0.18F) == -1) {
                int cubs = 2 + this.random.nextInt(2);
                for (int i = 0; i != cubs; i++) {
                    TamableAnimal cub = this.random.nextBoolean()
                            ? ModEntities.LION.get().create(level.getLevel(), EntitySpawnReason.NATURAL)
                            : ModEntities.LIONESS.get().create(level.getLevel(), EntitySpawnReason.NATURAL);
                    if (cub != null) {
                        cub.setBaby(true);
                        cub.snapTo(this.getX() - (i + 1), this.getY(), this.getZ() + 2.0, this.getYRot(), 0);
                        level.addFreshEntity(cub);
                    }
                }
            }
        }
        return super.finalizeSpawn(level, difficulty, reason, spawnData);
    }

    @Override
    public int getAmbientSoundInterval() {
        return 420;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.POLAR_BEAR_AMBIENT;
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
        return 0.50F;
    }

    @Override
    public float getVoicePitch() {
        return this.isBaby() ? 1.4F : 0.65F;
    }
}
