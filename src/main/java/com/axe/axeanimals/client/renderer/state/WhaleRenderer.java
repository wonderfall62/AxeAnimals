package com.axe.axeanimals.client.renderer;

import com.axe.axeanimals.AxeAnimalsMod;
import com.axe.axeanimals.client.model.ModModelLayers;
import com.axe.axeanimals.client.model.WhaleModel;
import com.axe.axeanimals.client.renderer.state.WhaleRenderState;
import com.axe.axeanimals.entity.WhaleEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;

public class WhaleRenderer extends MobRenderer<WhaleEntity, WhaleRenderState, WhaleModel> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(AxeAnimalsMod.MOD_ID, "textures/entity/whale/whale.png");

    public WhaleRenderer(EntityRendererProvider.Context context) {
        super(context, new WhaleModel(context.bakeLayer(ModModelLayers.WHALE)), 1.6F);
    }

    @Override
    public WhaleRenderState createRenderState() {
        return new WhaleRenderState();
    }

    @Override
    public void extractRenderState(WhaleEntity entity, WhaleRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.isBaby = entity.isBaby();
        state.growthProgress = entity.getGrowthProgress();
        state.breachState.copyFrom(entity.breachState);
        state.feedingState.copyFrom(entity.feedingState);
    }

    @Override
    protected void scale(WhaleRenderState state, PoseStack poseStack) {
        float s = state.isBaby ? (0.60F + 1.25F * state.growthProgress) : 1.85F;
        poseStack.scale(s, s, s);
    }

    @Override
    public Identifier getTextureLocation(WhaleRenderState state) {
        return TEXTURE;
    }
}
