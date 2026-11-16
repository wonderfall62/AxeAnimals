package com.axe.axeanimals.client.model;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class LionModel {
    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        // 100% оригинальная геометрия, иерархия и UV-координаты ZAWA AfricanLionModel$Adult (128x96)
        PartDefinition chest = root.addOrReplaceChild("Chest", CubeListBuilder.create()
                .texOffs(1, 0).addBox(-4.5F, -6.0F, -1.5F, 9.0F, 11.0F, 11.0F),
                PartPose.offsetAndRotation(0.0F, 8.0F, -7.0F, -0.08726646F, 0.0F, 0.0F));

        PartDefinition backPoint = chest.addOrReplaceChild("BackPoint", CubeListBuilder.create(),
                PartPose.offset(0.0F, -6.0F, 9.0F));

        PartDefinition body = backPoint.addOrReplaceChild("Body", CubeListBuilder.create()
                .texOffs(43, 0).addBox(-4.0F, -11.0F, 0.0F, 8.0F, 11.0F, 11.0F),
                PartPose.offsetAndRotation(0.0F, 11.0F, 0.5F, 0.08726646F, 0.0F, 0.0F));

        PartDefinition hips = body.addOrReplaceChild("Hips", CubeListBuilder.create()
                .texOffs(98, 20).addBox(-3.5F, 0.0F, 0.0F, 7.0F, 10.0F, 7.0F),
                PartPose.offsetAndRotation(0.0F, -11.0F, 11.0F, -0.34906584F, 0.0F, 0.0F));

        PartDefinition tail1 = hips.addOrReplaceChild("Tail1", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-0.99F, 0.0F, -1.0F, 2.0F, 7.0F, 2.0F),
                PartPose.offsetAndRotation(0.0F, 1.0F, 6.2F, 0.55850536F, 0.0F, 0.0F));

        PartDefinition tail2 = tail1.addOrReplaceChild("Tail2", CubeListBuilder.create()
                .texOffs(31, 0).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 5.0F, 2.0F),
                PartPose.offsetAndRotation(0.0F, 7.0F, -1.0F, 0.20943952F, 0.0F, 0.0F));

        tail2.addOrReplaceChild("TailTuft", CubeListBuilder.create()
                .texOffs(39, 0).addBox(-1.5F, -2.0F, -1.5F, 3.0F, 4.0F, 3.0F),
                PartPose.offset(0.0F, 6.5F, 1.0F));

        PartDefinition neck = chest.addOrReplaceChild("Neck", CubeListBuilder.create()
                .texOffs(68, 24).addBox(-3.0F, -3.5F, -8.0F, 6.0F, 7.0F, 8.0F),
                PartPose.offsetAndRotation(0.0F, -2.4F, 3.0F, (float)(-Math.PI) / 180.0F, 0.0F, 0.0F));

        neck.addOrReplaceChild("NeckLower", CubeListBuilder.create()
                .texOffs(0, 38).addBox(-2.5F, -1.5F, -9.0F, 5.0F, 3.0F, 9.0F),
                PartPose.offsetAndRotation(0.0F, 4.5F, -1.5F, -0.27925268F, 0.0F, 0.0F));

        PartDefinition head = neck.addOrReplaceChild("Head", CubeListBuilder.create()
                .texOffs(90, 37).addBox(-3.5F, -3.5F, -7.0F, 7.0F, 7.0F, 7.0F),
                PartPose.offsetAndRotation(0.0F, -0.3F, -6.7F, 0.12217305F, 0.0F, 0.0F));

        PartDefinition snout = head.addOrReplaceChild("Snout", CubeListBuilder.create()
                .texOffs(111, 37).addBox(-2.0F, -2.0F, -4.5F, 4.0F, 3.0F, 4.0F),
                PartPose.offsetAndRotation(0.0F, 1.3F, -5.7F, 0.17453292F, 0.0F, 0.0F));

        snout.addOrReplaceChild("TopSnout", CubeListBuilder.create()
                .texOffs(70, 39).addBox(-1.5F, 0.0F, -5.5F, 3.0F, 2.0F, 6.0F),
                PartPose.offsetAndRotation(0.0F, -3.5F, 0.5F, 0.2443461F, 0.0F, 0.0F));

        snout.addOrReplaceChild("Mouth", CubeListBuilder.create()
                .texOffs(104, 15).addBox(-1.5F, 0.0F, -3.0F, 3.0F, 1.0F, 4.0F),
                PartPose.offset(0.0F, 1.0F, -1.4F));

        head.addOrReplaceChild("EarLeft", CubeListBuilder.create()
                .texOffs(71, 0).addBox(-1.5F, -3.0F, -1.0F, 3.0F, 3.0F, 1.0F),
                PartPose.offsetAndRotation(1.8F, -2.0F, -3.0F, 0.0F, -0.17453292F, 0.2617994F));

        head.addOrReplaceChild("EarRight", CubeListBuilder.create()
                .texOffs(71, 0).mirror().addBox(-1.5F, -3.0F, -1.0F, 3.0F, 3.0F, 1.0F),
                PartPose.offsetAndRotation(-1.8F, -2.0F, -3.0F, 0.0F, 0.17453292F, -0.2617994F));

        PartDefinition maneTop2 = head.addOrReplaceChild("ManeTop2", CubeListBuilder.create()
                .texOffs(49, 70).addBox(-2.0F, 0.0F, -4.0F, 4.0F, 3.0F, 4.0F),
                PartPose.offsetAndRotation(0.0F, -5.1F, -2.5F, 0.10471976F, 0.0F, 0.0F));

        maneTop2.addOrReplaceChild("ManeTopLeft", CubeListBuilder.create()
                .texOffs(20, 61).addBox(0.0F, 0.0F, -2.0F, 3.0F, 5.0F, 4.0F),
                PartPose.offsetAndRotation(1.7F, 0.8F, -1.3F, -0.13962634F, 0.0F, 0.0F));

        maneTop2.addOrReplaceChild("ManeTopRight", CubeListBuilder.create()
                .texOffs(20, 61).mirror().addBox(-3.0F, 0.0F, -2.0F, 3.0F, 5.0F, 4.0F),
                PartPose.offsetAndRotation(-1.7F, 0.8F, -1.3F, -0.13962634F, 0.0F, 0.0F));

        PartDefinition mane = neck.addOrReplaceChild("Mane", CubeListBuilder.create()
                .texOffs(15, 41).addBox(-4.0F, -5.0F, 0.0F, 8.0F, 7.0F, 13.0F),
                PartPose.offsetAndRotation(0.0F, 3.8F, -12.3F, -0.075049154F, 0.0F, 0.0F));

        mane.addOrReplaceChild("Mane2", CubeListBuilder.create()
                .texOffs(71, 48).addBox(-3.0F, -5.0F, 0.0F, 6.0F, 5.0F, 5.0F),
                PartPose.offsetAndRotation(0.0F, 2.0F, 0.5F, -0.43633232F, 0.0F, 0.0F));

        mane.addOrReplaceChild("ManeTop", CubeListBuilder.create()
                .texOffs(46, 53).addBox(-2.5F, -2.5F, 0.0F, 5.0F, 3.0F, 13.0F),
                PartPose.offsetAndRotation(0.0F, -6.8F, 0.0F, (float)(-Math.PI) / 90.0F, 0.0F, 0.0F));

        PartDefinition maneBottom = mane.addOrReplaceChild("ManeBottom", CubeListBuilder.create()
                .texOffs(46, 41).addBox(-3.5F, -1.5F, 0.0F, 7.0F, 4.0F, 8.0F),
                PartPose.offsetAndRotation(0.0F, 2.0F, 4.5F, 0.04363323F, 0.0F, 0.0F));

        maneBottom.addOrReplaceChild("ManeBottom2", CubeListBuilder.create()
                .texOffs(99, 51).addBox(-2.5F, -3.0F, 0.0F, 5.0F, 3.0F, 3.0F),
                PartPose.offsetAndRotation(0.0F, 2.0F, 8.0F, 0.13962634F, 0.0F, 0.0F));
        PartDefinition maneLeft = mane.addOrReplaceChild("ManeLeft", CubeListBuilder.create()
                .texOffs(83, 51).addBox(-1.0F, -5.0F, 0.0F, 3.0F, 7.0F, 10.0F),
                PartPose.offsetAndRotation(3.5F, -1.0F, 0.5F, -0.04363323F, 0.05235988F, -0.06981317F));

        PartDefinition maneLeft2 = maneLeft.addOrReplaceChild("ManeLeft2", CubeListBuilder.create()
                .texOffs(1, 62).addBox(-3.0F, -4.0F, 0.0F, 4.0F, 9.0F, 11.0F),
                PartPose.offset(0.2F, -4.0F, 1.0F));

        maneLeft2.addOrReplaceChild("ManeLeft3", CubeListBuilder.create()
                .texOffs(34, 66).addBox(-4.0F, -3.0F, 0.0F, 4.0F, 6.0F, 3.0F),
                PartPose.offsetAndRotation(0.5F, 0.5F, 11.0F, 0.0F, -0.61086524F, 0.0F));

        PartDefinition maneRight = mane.addOrReplaceChild("ManeRight", CubeListBuilder.create()
                .texOffs(83, 51).mirror().addBox(-2.0F, -5.0F, 0.0F, 3.0F, 7.0F, 10.0F),
                PartPose.offsetAndRotation(-3.5F, -1.0F, 0.5F, -0.04363323F, -0.05235988F, 0.06981317F));

        PartDefinition maneRight2 = maneRight.addOrReplaceChild("ManeRight2", CubeListBuilder.create()
                .texOffs(1, 62).mirror().addBox(-1.0F, -4.0F, 0.0F, 4.0F, 9.0F, 11.0F),
                PartPose.offset(-0.2F, -4.0F, 1.0F));

        maneRight2.addOrReplaceChild("ManeRight3", CubeListBuilder.create()
                .texOffs(34, 66).mirror().addBox(0.0F, -3.0F, 0.0F, 4.0F, 6.0F, 3.0F),
                PartPose.offsetAndRotation(-0.5F, 0.5F, 11.0F, 0.0F, 0.61086524F, 0.0F));

        PartDefinition armBaseLeft = chest.addOrReplaceChild("ArmBaseLeft", CubeListBuilder.create()
                .texOffs(82, 0).addBox(-3.5F, 0.0F, -3.0F, 5.0F, 9.0F, 6.0F),
                PartPose.offsetAndRotation(4.0F, -2.4F, 2.5F, 0.17453292F, 0.0F, 0.0F));

        PartDefinition armLeft = armBaseLeft.addOrReplaceChild("ArmLeft", CubeListBuilder.create()
                .texOffs(104, 0).addBox(-2.0F, -1.0F, -4.5F, 4.0F, 5.0F, 5.0F),
                PartPose.offsetAndRotation(-1.0F, 9.0F, 2.0F, -0.08726646F, 0.0F, 0.0F));

        PartDefinition armFurLeft = armLeft.addOrReplaceChild("ArmFurLeft", CubeListBuilder.create()
                .texOffs(98, 0).addBox(-1.0F, -1.5F, -0.9F, 2.0F, 3.0F, 1.0F),
                PartPose.offsetAndRotation(0.0F, 4.0F, 0.5F, -0.17453292F, 0.0F, 0.0F));

        armFurLeft.addOrReplaceChild("ArmFurLeft2", CubeListBuilder.create()
                .texOffs(105, 0).addBox(-0.5F, -3.0F, -0.5F, 1.0F, 3.0F, 1.0F),
                PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.08726646F, 0.0F, 0.0F));

        PartDefinition forearmLeft = armLeft.addOrReplaceChild("ForearmLeft", CubeListBuilder.create()
                .texOffs(89, 15).addBox(-2.0F, 0.0F, -2.0F, 3.0F, 6.0F, 4.0F),
                PartPose.offsetAndRotation(0.5F, 3.0F, -2.0F, -0.08726646F, 0.0F, 0.0F));

        forearmLeft.addOrReplaceChild("HandLeft", CubeListBuilder.create()
                .texOffs(57, 22).addBox(-2.0F, -1.0F, -3.5F, 4.0F, 2.0F, 5.0F),
                PartPose.offsetAndRotation(-0.5F, 5.5F, 0.0F, 0.08726646F, 0.0F, 0.0F));

        PartDefinition armBaseRight = chest.addOrReplaceChild("ArmBaseRight", CubeListBuilder.create()
                .texOffs(82, 0).mirror().addBox(-1.5F, 0.0F, -3.0F, 5.0F, 9.0F, 6.0F),
                PartPose.offsetAndRotation(-4.0F, -2.4F, 2.5F, 0.17453292F, 0.0F, 0.0F));

        PartDefinition armRight = armBaseRight.addOrReplaceChild("ArmRight", CubeListBuilder.create()
                .texOffs(104, 0).mirror().addBox(-2.0F, -1.0F, -4.5F, 4.0F, 5.0F, 5.0F),
                PartPose.offsetAndRotation(1.0F, 9.0F, 2.0F, -0.08726646F, 0.0F, 0.0F));

        PartDefinition armFurRight = armRight.addOrReplaceChild("ArmFurRight", CubeListBuilder.create()
                .texOffs(98, 0).addBox(-1.0F, -1.5F, -0.9F, 2.0F, 3.0F, 1.0F),
                PartPose.offsetAndRotation(0.0F, 4.0F, 0.5F, -0.17453292F, 0.0F, 0.0F));

        armFurRight.addOrReplaceChild("ArmFurRight2", CubeListBuilder.create()
                .texOffs(105, 0).addBox(-0.5F, -3.0F, -0.5F, 1.0F, 3.0F, 1.0F),
                PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.08726646F, 0.0F, 0.0F));

        PartDefinition forearmRight = armRight.addOrReplaceChild("ForearmRight", CubeListBuilder.create()
                .texOffs(89, 15).mirror().addBox(-2.0F, 0.0F, -2.0F, 3.0F, 6.0F, 4.0F),
                PartPose.offsetAndRotation(0.5F, 3.0F, -2.0F, -0.08726646F, 0.0F, 0.0F));

        forearmRight.addOrReplaceChild("HandRight", CubeListBuilder.create()
                .texOffs(57, 22).mirror().addBox(-2.0F, -1.0F, -3.5F, 4.0F, 2.0F, 5.0F),
                PartPose.offsetAndRotation(-0.5F, 5.5F, 0.0F, 0.08726646F, 0.0F, 0.0F));

        PartDefinition thighLeft = body.addOrReplaceChild("ThighLeft", CubeListBuilder.create()
                .texOffs(0, 22).addBox(-2.5F, 0.0F, -3.0F, 5.0F, 9.0F, 7.0F),
                PartPose.offsetAndRotation(2.5F, -8.3F, 10.5F, -0.17453292F, 0.0F, 0.0F));

        PartDefinition upperLegLeft = thighLeft.addOrReplaceChild("UpperLegLeft", CubeListBuilder.create()
                .texOffs(24, 22).addBox(-2.0F, 0.0F, 0.0F, 4.0F, 7.0F, 5.0F),
                PartPose.offsetAndRotation(0.0F, 9.0F, -3.0F, 0.7853982F, 0.0F, 0.0F));

        PartDefinition lowerLegLeft = upperLegLeft.addOrReplaceChild("LowerLegLeft", CubeListBuilder.create()
                .texOffs(42, 22).addBox(-2.0F, 0.0F, -4.0F, 3.0F, 7.0F, 4.0F),
                PartPose.offsetAndRotation(0.5F, 6.8F, 4.7F, -0.7853982F, 0.0F, 0.0F));

        lowerLegLeft.addOrReplaceChild("FootLeft", CubeListBuilder.create()
                .texOffs(57, 22).addBox(-2.0F, -1.0F, -4.0F, 4.0F, 2.0F, 5.0F),
                PartPose.offsetAndRotation(-0.5F, 6.7F, -1.5F, 0.17453292F, 0.0F, 0.0F));

        PartDefinition thighRight = body.addOrReplaceChild("ThighRight", CubeListBuilder.create()
                .texOffs(0, 22).mirror().addBox(-2.5F, 0.0F, -3.0F, 5.0F, 9.0F, 7.0F),
                PartPose.offsetAndRotation(-2.5F, -8.3F, 10.5F, -0.17453292F, 0.0F, 0.0F));

        PartDefinition upperLegRight = thighRight.addOrReplaceChild("UpperLegRight", CubeListBuilder.create()
                .texOffs(24, 22).mirror().addBox(-2.0F, 0.0F, 0.0F, 4.0F, 7.0F, 5.0F),
                PartPose.offsetAndRotation(0.0F, 9.0F, -3.0F, 0.7853982F, 0.0F, 0.0F));

        PartDefinition lowerLegRight = upperLegRight.addOrReplaceChild("LowerLegRight", CubeListBuilder.create()
                .texOffs(42, 22).mirror().addBox(-2.0F, 0.0F, -4.0F, 3.0F, 7.0F, 4.0F),
                PartPose.offsetAndRotation(0.5F, 6.8F, 4.7F, -0.7853982F, 0.0F, 0.0F));

        lowerLegRight.addOrReplaceChild("FootRight", CubeListBuilder.create()
                .texOffs(57, 22).mirror().addBox(-2.0F, -1.0F, -4.0F, 4.0F, 2.0F, 5.0F),
                PartPose.offsetAndRotation(-0.5F, 6.7F, -1.5F, 0.17453292F, 0.0F, 0.0F));

        return LayerDefinition.create(mesh, 128, 96);
    }
}
