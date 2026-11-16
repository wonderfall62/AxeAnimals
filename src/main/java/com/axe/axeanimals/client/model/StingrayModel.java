package com.axe.axeanimals.client.model;

import com.axe.axeanimals.client.renderer.state.StingrayRenderState;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

public class StingrayModel extends EntityModel<StingrayRenderState> {
    private final ModelPart body;
    private final ModelPart leftWing;
    private final ModelPart leftWingTip;
    private final ModelPart rightWing;
    private final ModelPart rightWingTip;
    private final ModelPart tailBase;
    private final ModelPart tailTip;

    public StingrayModel(ModelPart root) {
        super(root);
        this.body = root.getChild("body");
        this.leftWing = this.body.getChild("left_wing");
        this.leftWingTip = this.leftWing.getChild("left_wing_tip");
        this.rightWing = this.body.getChild("right_wing");
        this.rightWingTip = this.rightWing.getChild("right_wing_tip");
        this.tailBase = this.body.getChild("tail_base");
        this.tailTip = this.tailBase.getChild("tail_tip");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -1.0F, -6.0F, 8.0F, 2.0F, 12.0F, new CubeDeformation(0.0F))
                .texOffs(24, 0).addBox(-3.0F, -0.5F, -8.0F, 6.0F, 1.5F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 22.0F, 0.0F));

        PartDefinition left_wing = body.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(0, 14).addBox(-5.0F, -0.5F, -5.0F, 5.0F, 1.3F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.0F, 0.0F, 0.0F));

        left_wing.addOrReplaceChild("left_wing_tip", CubeListBuilder.create().texOffs(28, 14).addBox(-5.0F, -0.4F, -3.5F, 5.0F, 0.8F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 0.2F, 0.0F));

        PartDefinition right_wing = body.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(0, 26).addBox(0.0F, -0.5F, -5.0F, 5.0F, 1.3F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, 0.0F, 0.0F));

        right_wing.addOrReplaceChild("right_wing_tip", CubeListBuilder.create().texOffs(28, 26).addBox(0.0F, -0.4F, -3.5F, 5.0F, 0.8F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, 0.2F, 0.0F));

        PartDefinition tail_base = body.addOrReplaceChild("tail_base", CubeListBuilder.create().texOffs(0, 38).addBox(-1.0F, -0.5F, 0.0F, 2.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.2F, 6.0F));

        tail_base.addOrReplaceChild("tail_tip", CubeListBuilder.create().texOffs(16, 38).addBox(-0.5F, -0.3F, 0.0F, 1.0F, 0.6F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 6.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(StingrayRenderState state) {
        super.setupAnim(state);
        float t = state.ageInTicks * 0.25F + state.walkAnimationPos * 0.6F;
        float amp = 0.28F + Math.min(state.walkAnimationSpeed, 1.0F) * 0.22F;

        this.body.y = 22.0F + Mth.sin(t) * 0.45F;

        this.leftWing.zRot = Mth.sin(t) * amp;
        this.leftWingTip.zRot = Mth.sin(t - 0.8F) * amp * 1.25F;

        this.rightWing.zRot = -Mth.sin(t) * amp;
        this.rightWingTip.zRot = -Mth.sin(t - 0.8F) * amp * 1.25F;

        this.tailBase.yRot = Mth.cos(t * 0.7F) * 0.24F;
        this.tailTip.yRot = Mth.cos(t * 0.7F - 0.7F) * 0.34F;
        this.tailBase.xRot = Mth.sin(t * 0.7F) * 0.12F;
    }
}
