package com.axe.axeanimals.client.model;

import com.axe.axeanimals.client.renderer.state.WhaleRenderState;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

public class WhaleModel extends EntityModel<WhaleRenderState> {
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart jaw;
    private final ModelPart leftFin;
    private final ModelPart leftFinTip;
    private final ModelPart rightFin;
    private final ModelPart rightFinTip;
    private final ModelPart tail1;
    private final ModelPart tail2;
    private final ModelPart tailFluke;

    public WhaleModel(ModelPart root) {
        super(root);
        this.body = root.getChild("body");
        this.head = this.body.getChild("head");
        this.jaw = this.head.getChild("jaw");
        this.leftFin = this.body.getChild("left_fin");
        this.leftFinTip = this.leftFin.getChild("left_fin_tip");
        this.rightFin = this.body.getChild("right_fin");
        this.rightFinTip = this.rightFin.getChild("right_fin_tip");
        this.tail1 = this.body.getChild("tail_1");
        this.tail2 = this.tail1.getChild("tail_2");
        this.tailFluke = this.tail2.getChild("tail_fluke");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-14.0F, -12.0F, -16.0F, 28.0F, 24.0F, 34.0F, new CubeDeformation(0.0F))
                .texOffs(0, 62).addBox(-12.0F, 11.0F, -14.0F, 24.0F, 3.0F, 30.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 10.0F, -10.0F));

        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(118, 0).addBox(-12.0F, -9.0F, -24.0F, 24.0F, 12.0F, 25.0F, new CubeDeformation(0.0F))
                .texOffs(118, 38).addBox(-9.0F, -7.0F, -32.0F, 18.0F, 9.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(180, 0).addBox(-10.5F, 2.5F, -24.0F, 2.0F, 2.0F, 22.0F, new CubeDeformation(0.0F))
                .texOffs(180, 32).addBox(8.5F, 2.5F, -24.0F, 2.0F, 2.0F, 22.0F, new CubeDeformation(0.0F))
                .texOffs(180, 64).addBox(-8.0F, 1.8F, -31.0F, 16.0F, 1.7F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -16.0F));

        head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(118, 56).addBox(-10.5F, 0.0F, -24.0F, 21.0F, 7.5F, 25.0F, new CubeDeformation(0.0F))
                .texOffs(118, 90).addBox(-8.0F, 0.0F, -31.0F, 16.0F, 6.5F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 3.5F, 0.0F));
        PartDefinition left_fin = body.addOrReplaceChild("left_fin", CubeListBuilder.create().texOffs(30, 96).addBox(-14.0F, -1.0F, -4.0F, 14.0F, 3.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(-14.0F, 5.0F, -8.0F));

        left_fin.addOrReplaceChild("left_fin_tip", CubeListBuilder.create().texOffs(30, 114).addBox(-12.0F, -1.0F, -4.0F, 12.0F, 2.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(-14.0F, 0.5F, 2.0F));

        PartDefinition right_fin = body.addOrReplaceChild("right_fin", CubeListBuilder.create().texOffs(82, 96).addBox(0.0F, -1.0F, -4.0F, 14.0F, 3.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(14.0F, 5.0F, -8.0F));

        right_fin.addOrReplaceChild("right_fin_tip", CubeListBuilder.create().texOffs(82, 114).addBox(0.0F, -1.0F, -4.0F, 12.0F, 2.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(14.0F, 0.5F, 2.0F));

        PartDefinition tail_1 = body.addOrReplaceChild("tail_1", CubeListBuilder.create().texOffs(0, 130).addBox(-10.0F, -9.0F, 0.0F, 20.0F, 18.0F, 22.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 18.0F));

        PartDefinition tail_2 = tail_1.addOrReplaceChild("tail_2", CubeListBuilder.create().texOffs(86, 130).addBox(-6.0F, -6.0F, 0.0F, 12.0F, 12.0F, 20.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 22.0F));

        tail_2.addOrReplaceChild("tail_fluke", CubeListBuilder.create().texOffs(0, 174).addBox(-8.0F, -1.5F, 0.0F, 16.0F, 3.0F, 12.0F, new CubeDeformation(0.0F))
                .texOffs(46, 174).addBox(-24.0F, -1.0F, 2.0F, 16.0F, 2.0F, 12.0F, new CubeDeformation(0.0F))
                .texOffs(104, 174).addBox(8.0F, -1.0F, 2.0F, 16.0F, 2.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 20.0F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    @Override
    public void setupAnim(WhaleRenderState state) {
        super.setupAnim(state);
        float t = state.ageInTicks * 0.13F + state.walkAnimationPos * 0.45F;
        float speedFactor = 0.55F + Math.min(state.walkAnimationSpeed, 1.0F) * 0.65F;

        this.body.xRot = state.xRot * ((float) Math.PI / 180F) * 0.45F;
        this.body.y = 10.0F + Mth.sin(t) * 0.85F;
        this.head.xRot = -Mth.sin(t) * 0.04F;

        this.tail1.xRot = Mth.sin(t - 0.4F) * 0.16F * speedFactor;
        this.tail2.xRot = Mth.sin(t - 1.0F) * 0.24F * speedFactor;
        this.tailFluke.xRot = Mth.sin(t - 1.7F) * 0.34F * speedFactor;

        this.leftFin.zRot = -0.22F - Mth.cos(t * 0.8F) * 0.16F;
        this.leftFinTip.zRot = -0.10F - Mth.cos(t * 0.8F - 0.6F) * 0.20F;

        this.rightFin.zRot = 0.22F + Mth.cos(t * 0.8F) * 0.16F;
        this.rightFinTip.zRot = 0.10F + Mth.cos(t * 0.8F - 0.6F) * 0.20F;

        if (state.feedingState.isStarted()) {
            float p = Mth.sin(Math.min(state.feedingState.getTimeInMillis(state.ageInTicks) / 1500.0F, 1.0F) * (float) Math.PI);
            this.jaw.xRot = 0.42F * p;
        } else {
            this.jaw.xRot = 0.03F + Mth.sin(t * 0.5F) * 0.02F;
        }
    }
}
