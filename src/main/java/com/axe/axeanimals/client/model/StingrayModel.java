package com.axe.axeanimals.client.model;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class StingrayModel {
	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -1.0F, -6.0F, 8.0F, 2.0F, 12.0F, new CubeDeformation(0.0F))
		.texOffs(24, 0).addBox(-3.0F, -0.5F, -8.0F, 6.0F, 1.5F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 22.0F, 0.0F));

		PartDefinition left_wing = body.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(0, 14).addBox(-5.0F, -0.5F, -5.0F, 5.0F, 1.3F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.0F, 0.0F, 0.0F));

		PartDefinition left_wing_tip = left_wing.addOrReplaceChild("left_wing_tip", CubeListBuilder.create().texOffs(28, 14).addBox(-5.0F, -0.4F, -3.5F, 5.0F, 0.8F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 0.2F, 0.0F));

		PartDefinition right_wing = body.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(0, 26).addBox(0.0F, -0.5F, -5.0F, 5.0F, 1.3F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, 0.0F, 0.0F));

		PartDefinition right_wing_tip = right_wing.addOrReplaceChild("right_wing_tip", CubeListBuilder.create().texOffs(28, 26).addBox(0.0F, -0.4F, -3.5F, 5.0F, 0.8F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, 0.2F, 0.0F));

		PartDefinition tail_base = body.addOrReplaceChild("tail_base", CubeListBuilder.create().texOffs(0, 38).addBox(-1.0F, -0.5F, 0.0F, 2.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.2F, 6.0F));

		PartDefinition tail_tip = tail_base.addOrReplaceChild("tail_tip", CubeListBuilder.create().texOffs(16, 38).addBox(-0.5F, -0.3F, 0.0F, 1.0F, 0.6F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 6.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}
}
