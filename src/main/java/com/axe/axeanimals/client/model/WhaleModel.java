package com.axe.axeanimals.client.model;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class WhaleModel {
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

		PartDefinition jaw = head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(118, 56).addBox(-10.5F, 0.0F, -24.0F, 21.0F, 7.5F, 25.0F, new CubeDeformation(0.0F))
		.texOffs(118, 90).addBox(-8.0F, 0.0F, -31.0F, 16.0F, 6.5F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 3.5F, 0.0F));

		PartDefinition left_fin = body.addOrReplaceChild("left_fin", CubeListBuilder.create().texOffs(30, 96).addBox(-14.0F, -1.0F, -4.0F, 14.0F, 3.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(-14.0F, 5.0F, -8.0F));

		PartDefinition left_fin_tip = left_fin.addOrReplaceChild("left_fin_tip", CubeListBuilder.create().texOffs(30, 114).addBox(-12.0F, -1.0F, -4.0F, 12.0F, 2.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(-14.0F, 0.5F, 2.0F));

		PartDefinition right_fin = body.addOrReplaceChild("right_fin", CubeListBuilder.create().texOffs(82, 96).addBox(0.0F, -1.0F, -4.0F, 14.0F, 3.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(14.0F, 5.0F, -8.0F));

		PartDefinition right_fin_tip = right_fin.addOrReplaceChild("right_fin_tip", CubeListBuilder.create().texOffs(82, 114).addBox(0.0F, -1.0F, -4.0F, 12.0F, 2.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(14.0F, 0.5F, 2.0F));

		PartDefinition tail_1 = body.addOrReplaceChild("tail_1", CubeListBuilder.create().texOffs(0, 130).addBox(-10.0F, -9.0F, 0.0F, 20.0F, 18.0F, 22.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 18.0F));

		PartDefinition tail_2 = tail_1.addOrReplaceChild("tail_2", CubeListBuilder.create().texOffs(86, 130).addBox(-6.0F, -6.0F, 0.0F, 12.0F, 12.0F, 20.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 22.0F));

		PartDefinition tail_fluke = tail_2.addOrReplaceChild("tail_fluke", CubeListBuilder.create().texOffs(0, 174).addBox(-8.0F, -1.5F, 0.0F, 16.0F, 3.0F, 12.0F, new CubeDeformation(0.0F))
		.texOffs(46, 174).addBox(-24.0F, -1.0F, 2.0F, 16.0F, 2.0F, 12.0F, new CubeDeformation(0.0F))
		.texOffs(104, 174).addBox(8.0F, -1.0F, 2.0F, 16.0F, 2.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 20.0F));

		return LayerDefinition.create(meshdefinition, 256, 256);
	}
}
