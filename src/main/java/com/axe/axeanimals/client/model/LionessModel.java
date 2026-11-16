package com.axe.axeanimals.client.model;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class LionessModel {
	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-5.5F, -5.5F, -13.0F, 11.0F, 11.5F, 14.0F, new CubeDeformation(0.0F))
		.texOffs(52, 0).addBox(-4.8F, -4.5F, 1.0F, 9.6F, 9.5F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 7.0F, 0.0F));

		PartDefinition neck = body.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(0, 28).addBox(-4.2F, -4.5F, -5.0F, 8.4F, 8.5F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, -12.0F));

		PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(30, 28).addBox(-4.2F, -4.5F, -8.0F, 8.4F, 7.5F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(64, 28).addBox(-2.3F, -1.8F, -12.0F, 4.6F, 3.3F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(82, 28).addBox(-4.8F, -6.5F, -3.0F, 2.0F, 2.5F, 1.5F, new CubeDeformation(0.0F))
		.texOffs(92, 28).addBox(2.8F, -6.5F, -3.0F, 2.0F, 2.5F, 1.5F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.0F, -3.0F));

		PartDefinition jaw = head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(64, 38).addBox(-1.9F, -0.5F, -4.5F, 3.8F, 1.5F, 4.5F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 2.0F, -7.0F));

		PartDefinition leg_front_left = body.addOrReplaceChild("leg_front_left", CubeListBuilder.create().texOffs(0, 48).addBox(-1.7F, -1.0F, -2.0F, 3.4F, 13.0F, 3.7F, new CubeDeformation(0.0F)), PartPose.offset(-3.7F, 5.0F, -9.5F));

		PartDefinition leg_front_right = body.addOrReplaceChild("leg_front_right", CubeListBuilder.create().texOffs(18, 48).addBox(-1.7F, -1.0F, -2.0F, 3.4F, 13.0F, 3.7F, new CubeDeformation(0.0F)), PartPose.offset(3.7F, 5.0F, -9.5F));

		PartDefinition leg_back_left = body.addOrReplaceChild("leg_back_left", CubeListBuilder.create().texOffs(36, 48).addBox(-1.7F, -1.0F, -2.0F, 3.3F, 13.0F, 3.8F, new CubeDeformation(0.0F)), PartPose.offset(-3.5F, 5.0F, 10.0F));

		PartDefinition leg_back_right = body.addOrReplaceChild("leg_back_right", CubeListBuilder.create().texOffs(54, 48).addBox(-1.6F, -1.0F, -2.0F, 3.3F, 13.0F, 3.8F, new CubeDeformation(0.0F)), PartPose.offset(3.5F, 5.0F, 10.0F));

		PartDefinition tail_base = body.addOrReplaceChild("tail_base", CubeListBuilder.create().texOffs(72, 48).addBox(-0.9F, -0.8F, 0.0F, 1.8F, 1.8F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.5F, 13.0F));

		PartDefinition tail_tip = tail_base.addOrReplaceChild("tail_tip", CubeListBuilder.create().texOffs(92, 48).addBox(-0.9F, -0.8F, 0.0F, 1.8F, 1.8F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(110, 48).addBox(-1.3F, -1.2F, 5.0F, 2.6F, 2.6F, 2.5F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 7.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}
}
