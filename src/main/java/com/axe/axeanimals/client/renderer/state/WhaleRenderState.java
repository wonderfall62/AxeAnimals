package com.axe.axeanimals.client.renderer.state;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.AnimationState;

public class WhaleRenderState extends LivingEntityRenderState {
    public boolean isBaby;
    public float growthProgress = 1.0F;
    public final AnimationState breachState = new AnimationState();
    public final AnimationState feedingState = new AnimationState();
}
