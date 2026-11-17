package com.axe.axeanimals.client.model;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class FerretModel {
    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        // 100% оригинальная геометрия, иерархия и UV-координаты ZAWA BlackFootedFerretModel$Adult (64x64)
        PartDefinition body = root.addOrReplaceChild("Body", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-3.0F, -2.5F, -3.5F, 6.0F, 5.0F, 7.0F),
                PartPose.offsetAndRotation(0.0F, 18.1F, 0.0F, 0.18325958F, 0.0F, 0.0F));

        PartDefinition chest = body.addOrReplaceChild("Chest", CubeListBuilder.create()
                .texOffs(29, 17).addBox(-2.5F, -2.0F, -4.0F, 5.0F, 4.0F, 4.0F),
                PartPose.offsetAndRotation(0.0F, 0.6F, -2.2F, -0.54105204F, 0.0F, 0.0F));

        PartDefinition neck = chest.addOrReplaceChild("Neck", CubeListBuilder.create()
                .texOffs(48, 26).addBox(-1.5F, -2.0F, -4.0F, 3.0F, 2.0F, 4.0F),
                PartPose.offsetAndRotation(0.0F, 0.1F, -4.0F, -0.715585F, 0.0F, 0.0F));

        PartDefinition neckLower = neck.addOrReplaceChild("NeckLower", CubeListBuilder.create()
                .texOffs(27, 47).addBox(-1.5F, 0.0F, -3.0F, 3.0F, 3.0F, 2.0F),
                PartPose.offsetAndRotation(0.0F, -1.8F, 1.6F, 0.2443461F, 0.0F, 0.0F));

        neckLower.addOrReplaceChild("NeckLower2", CubeListBuilder.create()
                .texOffs(40, 44).addBox(-1.0F, -1.0F, -3.0F, 2.0F, 2.0F, 3.0F),
                PartPose.offsetAndRotation(0.0F, 2.0F, -2.5F, -0.2617994F, 0.0F, 0.0F));

        PartDefinition head = neck.addOrReplaceChild("Head", CubeListBuilder.create()
                .texOffs(31, 28).addBox(-2.0F, -2.0F, -4.0F, 4.0F, 4.0F, 4.0F),
                PartPose.offsetAndRotation(0.0F, -1.5F, -2.4F, 1.2217305F, 0.0F, 0.0F));

        head.addOrReplaceChild("EarLeft", CubeListBuilder.create()
                .texOffs(37, 39).mirror().addBox(-1.0F, -2.0F, 0.0F, 2.0F, 2.0F, 1.0F),
                PartPose.offsetAndRotation(1.3F, -0.7F, -1.5F, -0.33161256F, -0.43633232F, 0.87266463F));

        head.addOrReplaceChild("EarRight", CubeListBuilder.create()
                .texOffs(37, 39).addBox(-1.0F, -2.0F, 0.0F, 2.0F, 2.0F, 1.0F),
                PartPose.offsetAndRotation(-1.3F, -0.7F, -1.5F, -0.33161256F, 0.43633232F, -0.87266463F));

        PartDefinition snout = head.addOrReplaceChild("Snout", CubeListBuilder.create()
                .texOffs(51, 34).mirror().addBox(-0.3F, 0.0F, 0.0F, 2.0F, 2.0F, 2.0F),
                PartPose.offsetAndRotation(0.0F, -0.5F, -5.7F, 0.1134464F, 0.0F, 0.1308997F));

        snout.addOrReplaceChild("SnoutRight", CubeListBuilder.create()
                .texOffs(51, 34).addBox(-1.7F, 0.0F, 0.02F, 2.0F, 2.0F, 2.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.2617994F));

        PartDefinition nose = snout.addOrReplaceChild("Nose", CubeListBuilder.create()
                .texOffs(53, 39).addBox(-1.5F, 0.0F, -2.0F, 3.0F, 1.0F, 2.0F),
                PartPose.offsetAndRotation(0.0F, -0.45F, 2.0F, 0.17453292F, 0.0F, -0.1308997F));

        nose.addOrReplaceChild("NoseTip", CubeListBuilder.create()
                .texOffs(46, 38).addBox(-0.5F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F),
                PartPose.offsetAndRotation(0.0F, 0.05F, -2.3F, (float)Math.PI / 90.0F, 0.0F, 0.0F));

        snout.addOrReplaceChild("Mouth", CubeListBuilder.create()
                .texOffs(54, 45).addBox(-1.0F, 0.0F, -2.0F, 2.0F, 1.0F, 2.0F),
                PartPose.offsetAndRotation(0.2F, 1.7F, 2.2F, -0.16580628F, 0.0F, -0.1308997F));

        PartDefinition armBaseLeft = chest.addOrReplaceChild("ArmBaseLeft", CubeListBuilder.create()
                .texOffs(37, 53).mirror().addBox(-1.5F, 0.0F, -1.0F, 3.0F, 4.0F, 2.0F),
                PartPose.offsetAndRotation(1.8F, -0.8F, -1.5F, 0.61086524F, 0.0F, 0.0F));

        PartDefinition armLeft = armBaseLeft.addOrReplaceChild("ArmLeft", CubeListBuilder.create()
                .texOffs(28, 53).mirror().addBox(-1.0F, 0.0F, -2.0F, 2.0F, 3.0F, 2.0F),
                PartPose.offsetAndRotation(0.2F, 3.6F, 1.0F, -0.4712389F, 0.0F, 0.0F));

        armLeft.addOrReplaceChild("HandLeft", CubeListBuilder.create()
                .texOffs(19, 54).mirror().addBox(-1.0F, -0.5F, -1.3F, 2.0F, 1.0F, 2.0F),
                PartPose.offsetAndRotation(-0.1F, 2.8F, -1.1F, 0.18325958F, 0.0F, 0.0F));

        PartDefinition armBaseRight = chest.addOrReplaceChild("ArmBaseRight", CubeListBuilder.create()
                .texOffs(37, 53).addBox(-1.5F, 0.0F, -1.0F, 3.0F, 4.0F, 2.0F),
                PartPose.offsetAndRotation(-1.8F, -0.8F, -1.5F, 0.61086524F, 0.0F, 0.0F));

        PartDefinition armRight = armBaseRight.addOrReplaceChild("ArmRight", CubeListBuilder.create()
                .texOffs(28, 53).addBox(-1.0F, 0.0F, -2.0F, 2.0F, 3.0F, 2.0F),
                PartPose.offsetAndRotation(-0.2F, 3.6F, 1.0F, -0.4712389F, 0.0F, 0.0F));

        armRight.addOrReplaceChild("HandRight", CubeListBuilder.create()
                .texOffs(19, 54).addBox(-1.0F, -0.5F, -1.3F, 2.0F, 1.0F, 2.0F),
                PartPose.offsetAndRotation(0.1F, 2.8F, -1.1F, 0.18325958F, 0.0F, 0.0F));

        PartDefinition hips = body.addOrReplaceChild("Hips", CubeListBuilder.create()
                .texOffs(0, 15).addBox(-3.5F, -2.0F, 0.0F, 7.0F, 6.0F, 6.0F),
                PartPose.offsetAndRotation(0.0F, -0.9F, 2.2F, -0.43633232F, 0.0F, 0.0F));

        PartDefinition tail1 = hips.addOrReplaceChild("Tail1", CubeListBuilder.create()
                .texOffs(15, 29).addBox(-1.5F, -1.5F, 0.0F, 3.0F, 3.0F, 5.0F),
                PartPose.offsetAndRotation(0.0F, 0.6F, 5.5F, (float)Math.PI / 90.0F, 0.0F, 0.0F));

        tail1.addOrReplaceChild("Tail2", CubeListBuilder.create()
                .texOffs(0, 36).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 6.0F),
                PartPose.offsetAndRotation(0.0F, 0.1F, 4.2F, 0.16580628F, 0.0F, 0.0F));

        PartDefinition thighLeft = hips.addOrReplaceChild("ThighLeft", CubeListBuilder.create()
                .texOffs(28, 0).mirror().addBox(-1.5F, 0.0F, -2.0F, 3.0F, 5.0F, 4.0F),
                PartPose.offsetAndRotation(2.5F, -0.1F, 4.0F, -0.10471976F, 0.0F, 0.0F));

        PartDefinition legLeft = thighLeft.addOrReplaceChild("LegLeft", CubeListBuilder.create()
                .texOffs(42, 0).mirror().addBox(-1.0F, 0.0F, 0.0F, 2.0F, 3.0F, 3.0F),
                PartPose.offsetAndRotation(0.0F, 4.5F, -2.0F, 0.6981317F, 0.0F, 0.0F));

        legLeft.addOrReplaceChild("FootLeft", CubeListBuilder.create()
                .texOffs(47, 7).mirror().addBox(-1.0F, -0.5F, -3.0F, 2.0F, 1.0F, 4.0F),
                PartPose.offsetAndRotation(-0.1F, 3.0F, 1.7F, -0.33161256F, 0.0F, 0.0F));

        PartDefinition thighRight = hips.addOrReplaceChild("ThighRight", CubeListBuilder.create()
                .texOffs(28, 0).addBox(-1.5F, 0.0F, -2.0F, 3.0F, 5.0F, 4.0F),
                PartPose.offsetAndRotation(-2.5F, -0.1F, 4.0F, -0.10471976F, 0.0F, 0.0F));

        PartDefinition legRight = thighRight.addOrReplaceChild("LegRight", CubeListBuilder.create()
                .texOffs(42, 0).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 3.0F, 3.0F),
                PartPose.offsetAndRotation(0.0F, 4.5F, -2.0F, 0.6981317F, 0.0F, 0.0F));

        legRight.addOrReplaceChild("FootRight", CubeListBuilder.create()
                .texOffs(47, 7).addBox(-1.0F, -0.5F, -3.0F, 2.0F, 1.0F, 4.0F),
                PartPose.offsetAndRotation(0.1F, 3.0F, 1.7F, -0.33161256F, 0.0F, 0.0F));

        return LayerDefinition.create(mesh, 64, 64);
    }
}
