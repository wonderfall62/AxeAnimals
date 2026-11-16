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
    private final ModelPart tailBase;
    private final ModelPart tailFluke;
    private final ModelPart leftFlipper;
    private final ModelPart rightFlipper;

    public WhaleModel(ModelPart root) {
        super(root);
        this.body = root.getChild("body");
        this.head = this.body.getChild("head");
        this.jaw = this.head.getChild("jaw");
        this.tailBase = this.body.getChild("tail_base");
        this.tailFluke = this.tailBase.getChild("tail_fluke");
        this.leftFlipper = this.body.getChild("left_flipper");
        this.rightFlipper = this.body.getChild("right_flipper");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition body = root.addOrReplaceChild("body",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-11.0F, -10.0F, -18.0F, 22.0F, 20.0F, 36.0F, CubeDeformation.NONE)
                        .texOffs(0, 56).addBox(-1.0F, -14.0F, 6.0F, 2.0F, 4.0F, 6.0F, CubeDeformation.NONE),
                PartPose.offset(0.0F, 14.0F, 0.0F));

        PartDefinition head = body.addOrReplaceChild("head",
                CubeListBuilder.create()
                        .texOffs(80, 0).addBox(-10.0F, -8.0F, -16.0F, 20.0F, 11.0F, 16.0F, CubeDeformation.NONE),
                PartPose.offset(0.0F, -1.0F, -18.0F));

        head.addOrReplaceChild("jaw",
                CubeListBuilder.create()
                        .texOffs(80, 28).addBox(-9.5F, 0.0F, -15.5F, 19.0F, 6.0F, 15.5F, CubeDeformation.NONE),
                PartPose.offset(0.0F, 3.0F, 0.0F));
        PartDefinition tailBase = body.addOrReplaceChild("tail_base",
                CubeListBuilder.create()
                        .texOffs(0, 68).addBox(-6.5F, -6.5F, 0.0F, 13.0F, 13.0F, 18.0F, CubeDeformation.NONE),
                PartPose.offset(0.0F, -1.0F, 18.0F));

        tailBase.addOrReplaceChild("tail_fluke",
                CubeListBuilder.create()
                        .texOffs(64, 68).addBox(-16.0F, -1.5F, 0.0F, 32.0F, 3.0F, 10.0F, CubeDeformation.NONE),
                PartPose.offset(0.0F, 0.0F, 17.0F));

        body.addOrReplaceChild("left_flipper",
                CubeListBuilder.create()
                        .texOffs(64, 84).addBox(0.0F, -1.0F, -3.0F, 16.0F, 2.0F, 7.0F, CubeDeformation.NONE),
                PartPose.offset(11.0F, 4.0F, -8.0F));

        body.addOrReplaceChild("right_flipper",
                CubeListBuilder.create()
                        .texOffs(64, 94).addBox(-16.0F, -1.0F, -3.0F, 16.0F, 2.0F, 7.0F, CubeDeformation.NONE),
                PartPose.offset(-11.0F, 4.0F, -8.0F));

        return LayerDefinition.create(mesh, 160, 128);
    }

    @Override
    public void setupAnim(WhaleRenderState state) {
        super.setupAnim(state);
        float t = state.ageInTicks * 0.14F + state.walkAnimationPos * 0.5F;
        float speedFactor = 0.55F + Math.min(state.walkAnimationSpeed, 1.0F) * 0.65F;

        this.body.xRot = state.xRot * ((float) Math.PI / 180F) * 0.45F;
        this.body.y = 14.0F + Mth.sin(t) * 0.8F;

        this.tailBase.xRot = Mth.sin(t) * 0.25F * speedFactor;
        this.tailFluke.xRot = Mth.sin(t - 0.8F) * 0.36F * speedFactor;

        this.leftFlipper.zRot = 0.25F + Mth.cos(t * 0.8F) * 0.2F;
        this.rightFlipper.zRot = -0.25F - Mth.cos(t * 0.8F) * 0.2F;
        this.leftFlipper.yRot = -0.25F;
        this.rightFlipper.yRot = 0.25F;

        if (state.feedingState.isStarted()) {
            float p = Mth.sin(Math.min(state.feedingState.getTimeInMillis(state.ageInTicks) / 1500.0F, 1.0F) * (float) Math.PI);
            this.jaw.xRot = 0.42F * p;
        } else {
            this.jaw.xRot = 0.03F + Mth.sin(t * 0.5F) * 0.02F;
        }
    }
}
