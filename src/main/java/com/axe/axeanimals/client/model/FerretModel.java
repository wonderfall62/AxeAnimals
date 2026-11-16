package com.axe.axeanimals.client.model;

import com.axe.axeanimals.client.renderer.state.FerretRenderState;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

public class FerretModel extends EntityModel<FerretRenderState> {
    private final ModelPart body;
    private final ModelPart hip;
    private final ModelPart legBl;
    private final ModelPart legBr;
    private final ModelPart tailBase;
    private final ModelPart tailTip;
    private final ModelPart neck;
    private final ModelPart head;
    private final ModelPart legFl;
    private final ModelPart legFr;

    public FerretModel(ModelPart root) {
        super(root);
        this.body = root.getChild("body");
        this.hip = this.body.getChild("hip");
        this.legBl = this.hip.getChild("leg_bl");
        this.legBr = this.hip.getChild("leg_br");
        this.tailBase = this.hip.getChild("tail_base");
        this.tailTip = this.tailBase.getChild("tail_tip");
        this.neck = this.body.getChild("neck");
        this.head = this.neck.getChild("head");
        this.legFl = this.body.getChild("leg_fl");
        this.legFr = this.body.getChild("leg_fr");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 11).addBox(-2.0F, -3.0F, -11.0F, 4.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 21.0F, 5.0F));

        PartDefinition hip = body.addOrReplaceChild("hip", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -2.0F, 0.0F, 4.0F, 4.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.0F, -5.0F));

        hip.addOrReplaceChild("leg_bl", CubeListBuilder.create().texOffs(14, 21).addBox(-0.5F, 0.0F, -1.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.5F, 1.0F, 5.0F));

        hip.addOrReplaceChild("leg_br", CubeListBuilder.create().texOffs(14, 26).addBox(-0.5F, 0.0F, -1.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(1.5F, 1.0F, 5.0F));

        PartDefinition tail_base = hip.addOrReplaceChild("tail_base", CubeListBuilder.create().texOffs(20, 19).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 7.0F));

        tail_base.addOrReplaceChild("tail_tip", CubeListBuilder.create().texOffs(0, 21).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 5.0F));

        PartDefinition neck = body.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(22, 0).addBox(-1.5F, -2.0F, -3.0F, 3.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, -11.0F));

        neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(20, 11).addBox(-2.0F, -2.0F, -4.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(22, 7).addBox(-1.0F, 0.0F, -6.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(20, 26).addBox(-3.0F, -3.0F, -2.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(26, 26).addBox(1.0F, -3.0F, -2.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.0F, -3.0F));

        body.addOrReplaceChild("leg_fl", CubeListBuilder.create().texOffs(0, 28).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.5F, 0.0F, -9.5F));

        body.addOrReplaceChild("leg_fr", CubeListBuilder.create().texOffs(4, 28).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(1.5F, 0.0F, -9.5F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(FerretRenderState state) {
        super.setupAnim(state);
        float walkSpeed = Math.min(state.walkAnimationSpeed * 1.6F, 1.2F);
        float walkPos = state.walkAnimationPos * 1.35F;

        this.head.xRot = state.xRot * ((float) Math.PI / 180F) * 0.65F;
        this.head.yRot = state.yRot * ((float) Math.PI / 180F) * 0.65F;
        this.neck.yRot = state.yRot * ((float) Math.PI / 180F) * 0.35F;

        if (state.isSitting) {
            this.body.y = 21.5F;
            this.body.xRot = -0.35F;
            this.hip.xRot = 0.30F;
            this.neck.xRot = 0.25F;
            this.legFl.xRot = 0.35F;
            this.legFr.xRot = 0.35F;
            this.legBl.xRot = -1.1F;
            this.legBr.xRot = -1.1F;
            this.tailBase.yRot = Mth.cos(state.ageInTicks * 0.15F) * 0.35F;
            this.tailTip.yRot = Mth.sin(state.ageInTicks * 0.15F) * 0.45F;
        } else if (state.standState.isStarted()) {
            float p = Math.min(state.standState.getTimeInMillis(state.ageInTicks) / 250.0F, 1.0F);
            this.body.y = 21.0F - 1.5F * p;
            this.body.xRot = -1.15F * p;
            this.hip.xRot = 0.25F * p;
            this.neck.xRot = 0.65F * p;
            this.head.xRot = 0.45F * p + state.xRot * ((float) Math.PI / 180F) * 0.5F;
            this.head.yRot = Mth.sin(state.ageInTicks * 0.22F) * 0.35F * p;
            this.legFl.xRot = 1.15F * p;
            this.legFr.xRot = 1.15F * p;
            this.legBl.xRot = 1.05F * p;
            this.legBr.xRot = 1.05F * p;
            this.tailBase.xRot = 0.90F * p;
            this.tailTip.xRot = 0.25F * p;
        } else {
            float wave = Mth.sin(walkPos * 0.85F);
            this.body.y = 21.0F - Math.abs(wave) * 0.65F * walkSpeed;
            this.body.xRot = wave * 0.18F * walkSpeed;
            this.hip.xRot = -wave * 0.26F * walkSpeed;
            this.neck.xRot = -wave * 0.14F * walkSpeed;

            this.legFl.xRot = Mth.cos(walkPos * 0.85F) * 1.15F * walkSpeed;
            this.legFr.xRot = Mth.cos(walkPos * 0.85F + 0.45F) * 1.15F * walkSpeed;
            this.legBl.xRot = Mth.cos(walkPos * 0.85F + (float) Math.PI) * 1.15F * walkSpeed;
            this.legBr.xRot = Mth.cos(walkPos * 0.85F + (float) Math.PI + 0.45F) * 1.15F * walkSpeed;

            this.tailBase.xRot = -0.15F + wave * 0.22F * walkSpeed;
            this.tailBase.yRot = Mth.sin(state.ageInTicks * 0.18F + walkPos * 0.5F) * 0.30F;
            this.tailTip.yRot = Mth.sin(state.ageInTicks * 0.18F + walkPos * 0.5F - 0.7F) * 0.45F;
        }
    }
}
