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
    private final ModelPart rightWing;
    private final ModelPart tail;

    public StingrayModel(ModelPart root) {
        super(root);
        this.body = root.getChild("body");
        this.leftWing = this.body.getChild("left_wing");
        this.rightWing = this.body.getChild("right_wing");
        this.tail = this.body.getChild("tail");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition body = root.addOrReplaceChild("body",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-4.5F, -1.5F, -6.0F, 9.0F, 3.0F, 12.0F, CubeDeformation.NONE)
                        .texOffs(0, 16).addBox(-3.0F, -1.0F, -8.5F, 6.0F, 2.0F, 2.5F, CubeDeformation.NONE),
                PartPose.offset(0.0F, 22.0F, 0.0F));

        body.addOrReplaceChild("left_wing",
                CubeListBuilder.create()
                        .texOffs(20, 16).addBox(0.0F, -0.7F, -5.0F, 8.0F, 1.4F, 10.0F, CubeDeformation.NONE),
                PartPose.offset(4.5F, 0.0F, 0.0F));

        body.addOrReplaceChild("right_wing",
                CubeListBuilder.create()
                        .texOffs(20, 28).addBox(-8.0F, -0.7F, -5.0F, 8.0F, 1.4F, 10.0F, CubeDeformation.NONE),
                PartPose.offset(-4.5F, 0.0F, 0.0F));

        body.addOrReplaceChild("tail",
                CubeListBuilder.create()
                        .texOffs(0, 22).addBox(-0.8F, -0.8F, 0.0F, 1.6F, 1.6F, 13.0F, CubeDeformation.NONE),
                PartPose.offset(0.0F, 0.0F, 6.0F));

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(StingrayRenderState state) {
        super.setupAnim(state);
        float t = state.ageInTicks * 0.25F + state.walkAnimationPos * 0.6F;
        float amp = 0.30F + Math.min(state.walkAnimationSpeed, 1.0F) * 0.25F;

        this.body.y = 22.0F + Mth.sin(t) * 0.5F;
        this.leftWing.zRot = Mth.sin(t) * amp;
        this.rightWing.zRot = -Mth.sin(t) * amp;
        this.tail.yRot = Mth.cos(t * 0.7F) * 0.30F;
        this.tail.xRot = Mth.sin(t * 0.7F) * 0.14F;
    }
}
