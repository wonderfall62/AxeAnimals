package com.axe.axeanimals.client.model;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class LionModel {
    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        // Корпус и грудь (попадают на тёмную грудную гриву и золотистую спину)
        PartDefinition chest = root.addOrReplaceChild("Chest", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-4.5F, -4.5F, -8.5F, 9.0F, 9.0F, 9.0F),
                PartPose.offsetAndRotation(0.0F, 11.4F, -4.0F, -0.0873F, 0.0F, 0.0F));

        PartDefinition body = chest.addOrReplaceChild("Body", CubeListBuilder.create()
                .texOffs(36, 0).addBox(-4.0F, -4.0F, 0.0F, 8.0F, 8.0F, 11.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.5F, 0.0873F, 0.0F, 0.0F));

        PartDefinition hips = body.addOrReplaceChild("Hips", CubeListBuilder.create()
                .texOffs(74, 0).addBox(-3.5F, -3.5F, 0.0F, 7.0F, 8.0F, 7.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 11.0F, -0.1047F, 0.0F, 0.0F));

        // Хвост и тёмная кисточка
        PartDefinition tail1 = hips.addOrReplaceChild("Tail1", CubeListBuilder.create()
                .texOffs(102, 0).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 8.0F),
                PartPose.offsetAndRotation(0.0F, -2.2F, 6.5F, -1.0472F, 0.0F, 0.0F));

        PartDefinition tail2 = tail1.addOrReplaceChild("Tail2", CubeListBuilder.create()
                .texOffs(102, 10).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 8.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 8.0F, 0.3491F, 0.0F, 0.0F));

        tail2.addOrReplaceChild("TailTuft", CubeListBuilder.create()
                .texOffs(100, 0).addBox(-1.5F, -1.5F, 0.0F, 3.0F, 3.0F, 4.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 7.5F, 0.1745F, 0.0F, 0.0F));

        // Шея, голова (глаза на UV 86,33), морда (чёрный нос на UV 68,33) и уши
        PartDefinition neck = chest.addOrReplaceChild("Neck", CubeListBuilder.create()
                .texOffs(80, 18).addBox(-3.5F, -3.5F, -7.0F, 7.0F, 7.0F, 8.0F),
                PartPose.offsetAndRotation(0.0F, -0.8F, -6.5F, -0.2618F, 0.0F, 0.0F));

        neck.addOrReplaceChild("NeckLower", CubeListBuilder.create()
                .texOffs(54, 19).addBox(-2.5F, -2.0F, -6.5F, 5.0F, 4.0F, 7.0F),
                PartPose.offsetAndRotation(0.0F, 2.8F, -5.5F, -0.4363F, 0.0F, 0.0F));

        PartDefinition head = neck.addOrReplaceChild("Head", CubeListBuilder.create()
                .texOffs(86, 33).addBox(-3.5F, -3.5F, -6.0F, 7.0F, 7.0F, 6.0F),
                PartPose.offsetAndRotation(0.0F, -0.5F, -6.5F, 0.3665F, 0.0F, 0.0F));

        PartDefinition snout = head.addOrReplaceChild("Snout", CubeListBuilder.create()
                .texOffs(68, 33).addBox(-2.0F, -1.5F, -4.0F, 4.0F, 3.0F, 4.0F),
                PartPose.offsetAndRotation(0.0F, 0.6F, -5.8F, 0.0873F, 0.0F, 0.0F));

        snout.addOrReplaceChild("TopSnout", CubeListBuilder.create()
                .texOffs(68, 25).addBox(-1.5F, -1.0F, -3.8F, 3.0F, 2.0F, 4.0F),
                PartPose.offsetAndRotation(0.0F, -1.3F, 0.2F, 0.2269F, 0.0F, 0.0F));

        head.addOrReplaceChild("Mouth", CubeListBuilder.create()
                .texOffs(108, 36).addBox(-1.5F, 0.0F, -3.8F, 3.0F, 1.5F, 4.0F),
                PartPose.offsetAndRotation(0.0F, 2.1F, -5.5F, 0.0F, 0.0F, 0.0F));

        head.addOrReplaceChild("EarLeft", CubeListBuilder.create()
                .texOffs(104, 15).addBox(-0.5F, -2.0F, -1.0F, 1.0F, 2.0F, 2.0F),
                PartPose.offsetAndRotation(2.8F, -2.8F, -2.2F, -0.1745F, -0.2618F, 0.3491F));

        head.addOrReplaceChild("EarRight", CubeListBuilder.create()
                .texOffs(110, 15).mirror().addBox(-0.5F, -2.0F, -1.0F, 1.0F, 2.0F, 2.0F),
                PartPose.offsetAndRotation(-2.8F, -2.8F, -2.2F, -0.1745F, 0.2618F, -0.3491F));

        // Пышная тёмно-коричневая и золотистая грива самца (строго в зоне UV X=20..108, Y=42..80)
        PartDefinition mane = neck.addOrReplaceChild("Mane", CubeListBuilder.create()
                .texOffs(44, 41).addBox(-5.5F, -5.5F, -4.5F, 11.0F, 11.0F, 9.0F),
                PartPose.offsetAndRotation(0.0F, -0.5F, -2.5F, 0.1222F, 0.0F, 0.0F));

        mane.addOrReplaceChild("Mane2", CubeListBuilder.create()
                .texOffs(46, 56).addBox(-5.0F, -5.0F, 0.0F, 10.0F, 10.0F, 7.0F),
                PartPose.offsetAndRotation(0.0F, 0.2F, 3.5F, -0.1396F, 0.0F, 0.0F));

        PartDefinition maneTop = mane.addOrReplaceChild("ManeTop", CubeListBuilder.create()
                .texOffs(22, 42).addBox(-4.5F, -2.5F, -4.0F, 9.0F, 4.0F, 8.0F),
                PartPose.offsetAndRotation(0.0F, -4.8F, -0.8F, 0.1745F, 0.0F, 0.0F));

        maneTop.addOrReplaceChild("ManeTop2", CubeListBuilder.create()
                .texOffs(24, 55).addBox(-4.0F, -2.0F, 0.0F, 8.0F, 3.0F, 7.0F),
                PartPose.offsetAndRotation(0.0F, -0.3F, 3.5F, -0.2269F, 0.0F, 0.0F));

        PartDefinition maneBottom = mane.addOrReplaceChild("ManeBottom", CubeListBuilder.create()
                .texOffs(74, 46).addBox(-4.5F, 0.0F, -3.5F, 9.0F, 5.0F, 8.0F),
                PartPose.offsetAndRotation(0.0F, 4.2F, -1.0F, 0.1396F, 0.0F, 0.0F));

        maneBottom.addOrReplaceChild("ManeBottom2", CubeListBuilder.create()
                .texOffs(76, 58).addBox(-4.0F, 0.0F, 0.0F, 8.0F, 4.0F, 7.0F),
                PartPose.offsetAndRotation(0.0F, 0.5F, 3.8F, -0.1745F, 0.0F, 0.0F));

        mane.addOrReplaceChild("ManeLeft", CubeListBuilder.create()
                .texOffs(2, 62).addBox(0.0F, -4.5F, -3.5F, 3.0F, 9.0F, 8.0F),
                PartPose.offsetAndRotation(4.5F, 0.0F, -0.5F, 0.0F, 0.1396F, -0.0873F));

        mane.addOrReplaceChild("ManeRight", CubeListBuilder.create()
                .texOffs(2, 62).mirror().addBox(-3.0F, -4.5F, -3.5F, 3.0F, 9.0F, 8.0F),
                PartPose.offsetAndRotation(-4.5F, 0.0F, -0.5F, 0.0F, -0.1396F, 0.0873F));
        // Передние и задние лапы + пучки шерсти (UV Y=18..47 + шерсть на Y=64)
        PartDefinition armBaseLeft = chest.addOrReplaceChild("ArmBaseLeft", CubeListBuilder.create()
                .texOffs(0, 18).addBox(-1.5F, -1.5F, -2.5F, 3.0F, 6.0F, 5.0F),
                PartPose.offsetAndRotation(3.5F, 0.5F, -4.5F, 0.1745F, 0.0F, 0.0F));

        PartDefinition armLeft = armBaseLeft.addOrReplaceChild("ArmLeft", CubeListBuilder.create()
                .texOffs(0, 29).addBox(-1.5F, 0.0F, -2.0F, 3.0F, 5.0F, 4.0F),
                PartPose.offsetAndRotation(0.0F, 4.0F, 0.2F, -0.2618F, 0.0F, 0.0F));

        armLeft.addOrReplaceChild("ArmFurLeft", CubeListBuilder.create()
                .texOffs(34, 68).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 5.0F, 2.0F),
                PartPose.offsetAndRotation(0.0F, 0.5F, 1.8F, 0.0873F, 0.0F, 0.0F));

        PartDefinition forearmLeft = armLeft.addOrReplaceChild("ForearmLeft", CubeListBuilder.create()
                .texOffs(0, 38).addBox(-1.2F, 0.0F, -1.5F, 2.5F, 5.0F, 3.0F),
                PartPose.offsetAndRotation(0.0F, 4.5F, -0.2F, 0.0873F, 0.0F, 0.0F));

        forearmLeft.addOrReplaceChild("HandLeft", CubeListBuilder.create()
                .texOffs(0, 46).addBox(-1.5F, 0.0F, -2.5F, 3.0F, 2.0F, 4.0F),
                PartPose.offsetAndRotation(0.0F, 4.2F, -0.2F, 0.0F, 0.0F, 0.0F));

        PartDefinition armBaseRight = chest.addOrReplaceChild("ArmBaseRight", CubeListBuilder.create()
                .texOffs(16, 18).mirror().addBox(-1.5F, -1.5F, -2.5F, 3.0F, 6.0F, 5.0F),
                PartPose.offsetAndRotation(-3.5F, 0.5F, -4.5F, 0.1745F, 0.0F, 0.0F));

        PartDefinition armRight = armBaseRight.addOrReplaceChild("ArmRight", CubeListBuilder.create()
                .texOffs(16, 29).mirror().addBox(-1.5F, 0.0F, -2.0F, 3.0F, 5.0F, 4.0F),
                PartPose.offsetAndRotation(0.0F, 4.0F, 0.2F, -0.2618F, 0.0F, 0.0F));

        armRight.addOrReplaceChild("ArmFurRight", CubeListBuilder.create()
                .texOffs(34, 68).mirror().addBox(-1.0F, 0.0F, 0.0F, 2.0F, 5.0F, 2.0F),
                PartPose.offsetAndRotation(0.0F, 0.5F, 1.8F, 0.0873F, 0.0F, 0.0F));

        PartDefinition forearmRight = armRight.addOrReplaceChild("ForearmRight", CubeListBuilder.create()
                .texOffs(16, 38).mirror().addBox(-1.3F, 0.0F, -1.5F, 2.5F, 5.0F, 3.0F),
                PartPose.offsetAndRotation(0.0F, 4.5F, -0.2F, 0.0873F, 0.0F, 0.0F));

        forearmRight.addOrReplaceChild("HandRight", CubeListBuilder.create()
                .texOffs(16, 46).mirror().addBox(-1.5F, 0.0F, -2.5F, 3.0F, 2.0F, 4.0F),
                PartPose.offsetAndRotation(0.0F, 4.2F, -0.2F, 0.0F, 0.0F, 0.0F));

        PartDefinition thighLeft = hips.addOrReplaceChild("ThighLeft", CubeListBuilder.create()
                .texOffs(32, 19).addBox(-1.5F, -1.5F, -2.5F, 3.0F, 6.0F, 5.0F),
                PartPose.offsetAndRotation(3.0F, 0.5F, 3.5F, -0.1745F, 0.0F, 0.0F));

        PartDefinition upperLegLeft = thighLeft.addOrReplaceChild("UpperLegLeft", CubeListBuilder.create()
                .texOffs(32, 30).addBox(-1.3F, 0.0F, -2.0F, 2.6F, 5.0F, 4.0F),
                PartPose.offsetAndRotation(0.0F, 4.0F, -0.2F, 0.3491F, 0.0F, 0.0F));

        PartDefinition lowerLegLeft = upperLegLeft.addOrReplaceChild("LowerLegLeft", CubeListBuilder.create()
                .texOffs(32, 39).addBox(-1.1F, 0.0F, -1.5F, 2.2F, 5.0F, 3.0F),
                PartPose.offsetAndRotation(0.0F, 4.4F, 0.4F, -0.2618F, 0.0F, 0.0F));

        lowerLegLeft.addOrReplaceChild("FootLeft", CubeListBuilder.create()
                .texOffs(32, 47).addBox(-1.5F, 0.0F, -2.5F, 3.0F, 2.0F, 4.0F),
                PartPose.offsetAndRotation(0.0F, 4.2F, -0.2F, 0.1745F, 0.0F, 0.0F));

        PartDefinition thighRight = hips.addOrReplaceChild("ThighRight", CubeListBuilder.create()
                .texOffs(48, 19).mirror().addBox(-1.5F, -1.5F, -2.5F, 3.0F, 6.0F, 5.0F),
                PartPose.offsetAndRotation(-3.0F, 0.5F, 3.5F, -0.1745F, 0.0F, 0.0F));

        PartDefinition upperLegRight = thighRight.addOrReplaceChild("UpperLegRight", CubeListBuilder.create()
                .texOffs(48, 30).mirror().addBox(-1.3F, 0.0F, -2.0F, 2.6F, 5.0F, 4.0F),
                PartPose.offsetAndRotation(0.0F, 4.0F, -0.2F, 0.3491F, 0.0F, 0.0F));

        PartDefinition lowerLegRight = upperLegRight.addOrReplaceChild("LowerLegRight", CubeListBuilder.create()
                .texOffs(48, 39).mirror().addBox(-1.1F, 0.0F, -1.5F, 2.2F, 5.0F, 3.0F),
                PartPose.offsetAndRotation(0.0F, 4.4F, 0.4F, -0.2618F, 0.0F, 0.0F));

        lowerLegRight.addOrReplaceChild("FootRight", CubeListBuilder.create()
                .texOffs(48, 47).mirror().addBox(-1.5F, 0.0F, -2.5F, 3.0F, 2.0F, 4.0F),
                PartPose.offsetAndRotation(0.0F, 4.2F, -0.2F, 0.1745F, 0.0F, 0.0F));

        return LayerDefinition.create(mesh, 128, 96);
    }
}
