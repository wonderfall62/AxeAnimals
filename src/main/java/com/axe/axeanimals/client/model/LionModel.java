package com.axe.axeanimals.client.model;

import com.axe.axeanimals.client.renderer.state.LionRenderState;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

public class LionModel extends EntityModel<LionRenderState> {
    private final ModelPart body;
    private final ModelPart neck;
    private final ModelPart head;
    private final ModelPart jaw;
    private final ModelPart legFrontLeft;
    private final ModelPart legFrontRight;
    private final ModelPart legBackLeft;
    private final ModelPart legBackRight;
    private final ModelPart tailBase;
    private final ModelPart tailTip;

    public LionModel(ModelPart root) {
        super(root);
        this.body = root.getChild("body");
        this.neck = this.body.getChild("neck");
        this.head = this.neck.getChild("head");
        this.jaw = this.head.getChild("jaw");
        this.legFrontLeft = this.body.getChild("leg_front_left");
        this.legFrontRight = this.body.getChild("leg_front_right");
        this.legBackLeft = this.body.getChild("leg_back_left");
        this.legBackRight = this.body.getChild("leg_back_right");
        this.tailBase = this.body.getChild("tail_base");
        this.tailTip = this.tailBase.getChild("tail_tip");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-6.0F, -6.0F, -13.0F, 12.0F, 12.0F, 14.0F, new CubeDeformation(0.0F))
                .texOffs(52, 0).addBox(-5.0F, -5.0F, 1.0F, 10.0F, 10.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 7.0F, 0.0F));

        PartDefinition neck = body.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(0, 28).addBox(-4.5F, -5.0F, -5.0F, 9.0F, 9.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(42, 68).addBox(-7.0F, -6.5F, -4.0F, 14.0F, 16.5F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, -12.0F));

        PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 68).addBox(-6.5F, -7.5F, -5.5F, 13.0F, 13.5F, 6.5F, new CubeDeformation(0.0F))
                .texOffs(30, 28).addBox(-4.5F, -5.0F, -8.0F, 9.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(64, 28).addBox(-2.5F, -2.0F, -12.0F, 5.0F, 3.5F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(82, 28).addBox(-5.0F, -7.0F, -3.0F, 2.0F, 2.5F, 1.5F, new CubeDeformation(0.0F))
                .texOffs(92, 28).addBox(3.0F, -7.0F, -3.0F, 2.0F, 2.5F, 1.5F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.0F, -3.0F));

        head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(64, 38).addBox(-2.0F, -0.5F, -4.5F, 4.0F, 1.5F, 4.5F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 2.0F, -7.0F));

        body.addOrReplaceChild("leg_front_left", CubeListBuilder.create().texOffs(0, 48).addBox(-1.8F, -1.0F, -2.0F, 3.6F, 13.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.0F, 5.0F, -9.5F));

        body.addOrReplaceChild("leg_front_right", CubeListBuilder.create().texOffs(18, 48).addBox(-1.8F, -1.0F, -2.0F, 3.6F, 13.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, 5.0F, -9.5F));

        body.addOrReplaceChild("leg_back_left", CubeListBuilder.create().texOffs(36, 48).addBox(-1.7F, -1.0F, -2.0F, 3.5F, 13.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.8F, 5.0F, 10.0F));

        body.addOrReplaceChild("leg_back_right", CubeListBuilder.create().texOffs(54, 48).addBox(-1.8F, -1.0F, -2.0F, 3.5F, 13.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(3.8F, 5.0F, 10.0F));

        PartDefinition tail_base = body.addOrReplaceChild("tail_base", CubeListBuilder.create().texOffs(72, 48).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.5F, 13.0F));

        tail_base.addOrReplaceChild("tail_tip", CubeListBuilder.create().texOffs(92, 48).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(110, 48).addBox(-1.5F, -1.5F, 5.0F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 7.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(LionRenderState state) {
        super.setupAnim(state);
        float walkSpeed = Math.min(state.walkAnimationSpeed * 1.4F, 1.1F);
        float walkPos = state.walkAnimationPos * 1.1F;

        this.head.xRot = state.xRot * ((float) Math.PI / 180F) * 0.6F;
        this.head.yRot = state.yRot * ((float) Math.PI / 180F) * 0.6F;
        this.neck.yRot = state.yRot * ((float) Math.PI / 180F) * 0.4F;
        this.jaw.xRot = 0.05F + Math.max(0.0F, Mth.sin(state.ageInTicks * 0.08F)) * 0.12F;

        if (state.isSitting) {
            this.body.y = 10.5F;
            this.body.xRot = -0.35F;
            this.neck.xRot = 0.25F;
            this.legFrontLeft.xRot = 0.35F;
            this.legFrontRight.xRot = 0.35F;
            this.legBackLeft.xRot = -1.15F;
            this.legBackRight.xRot = -1.15F;
            this.tailBase.yRot = Mth.cos(state.ageInTicks * 0.14F) * 0.35F;
            this.tailTip.yRot = Mth.sin(state.ageInTicks * 0.14F) * 0.45F;
        } else {
            this.body.y = 7.0F - Math.abs(Mth.sin(walkPos * 0.66F)) * 0.6F * walkSpeed;
            this.body.xRot = 0.0F;
            this.neck.xRot = Mth.cos(walkPos * 0.66F) * 0.08F * walkSpeed;
            this.legFrontLeft.xRot = Mth.cos(walkPos * 0.66F) * 1.05F * walkSpeed;
            this.legFrontRight.xRot = Mth.cos(walkPos * 0.66F + (float) Math.PI) * 1.05F * walkSpeed;
            this.legBackLeft.xRot = Mth.cos(walkPos * 0.66F + (float) Math.PI) * 1.05F * walkSpeed;
            this.legBackRight.xRot = Mth.cos(walkPos * 0.66F) * 1.05F * walkSpeed;
            this.tailBase.xRot = -0.55F + Mth.cos(walkPos * 0.66F) * 0.15F * walkSpeed;
            this.tailBase.yRot = Mth.sin(state.ageInTicks * 0.15F + walkPos * 0.4F) * 0.28F;
            this.tailTip.yRot = Mth.sin(state.ageInTicks * 0.15F + walkPos * 0.4F - 0.7F) * 0.42F;
        }
    }
}
