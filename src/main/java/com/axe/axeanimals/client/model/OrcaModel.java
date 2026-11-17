package com.axe.axeanimals.client.model;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class OrcaModel {
    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        // 100% оригинальная геометрия, иерархия и UV-координаты ZAWA OrcaModel$AdultMale (512x256)
        PartDefinition body = root.addOrReplaceChild("Body", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-15.0F, -17.0F, -21.0F, 30.0F, 34.0F, 42.0F),
                PartPose.offset(0.0F, 10.5F, 0.0F));

        PartDefinition chest = body.addOrReplaceChild("Chest", CubeListBuilder.create()
                .texOffs(364, 0).addBox(-13.5F, -13.5F, -16.0F, 27.0F, 27.0F, 23.0F),
                PartPose.offsetAndRotation(0.0F, -2.0F, -21.0F, 0.13962634F, 0.0F, 0.0F));

        chest.addOrReplaceChild("ChestUnder", CubeListBuilder.create()
                .texOffs(193, 168).addBox(-12.5F, -7.0F, 0.0F, 25.0F, 7.0F, 19.0F),
                PartPose.offsetAndRotation(0.0F, 13.5F, -15.5F, -0.3036873F, 0.0F, 0.0F));

        PartDefinition head = chest.addOrReplaceChild("Head", CubeListBuilder.create()
                .texOffs(132, 113).addBox(-12.0F, -12.0F, -18.0F, 24.0F, 15.0F, 24.0F),
                PartPose.offsetAndRotation(0.0F, 0.5F, -16.0F, -0.10471976F, 0.0F, 0.0F));

        head.addOrReplaceChild("HeadTop", CubeListBuilder.create()
                .texOffs(292, 159).addBox(-11.5F, 0.0F, 0.0F, 23.0F, 4.0F, 22.0F),
                PartPose.offsetAndRotation(0.0F, -12.0F, -17.5F, 0.06981317F, 0.0F, 0.0F));

        PartDefinition eyeLeft1 = head.addOrReplaceChild("EyeLeft1", CubeListBuilder.create()
                .texOffs(0, 79).mirror().addBox(-1.0F, -2.5F, -1.5F, 1.0F, 5.0F, 2.0F),
                PartPose.offset(12.0F, -1.0F, -18.0F));

        eyeLeft1.addOrReplaceChild("EyeLeft2", CubeListBuilder.create()
                .texOffs(0, 77).mirror().addBox(-1.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F),
                PartPose.offset(0.0F, -2.5F, -0.5F));

        PartDefinition eyeRight1 = head.addOrReplaceChild("EyeRight1", CubeListBuilder.create()
                .texOffs(0, 79).addBox(0.0F, -2.5F, -1.5F, 1.0F, 5.0F, 2.0F),
                PartPose.offset(-12.0F, -1.0F, -18.0F));

        eyeRight1.addOrReplaceChild("EyeRight2", CubeListBuilder.create()
                .texOffs(0, 77).addBox(0.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F),
                PartPose.offset(0.0F, -2.5F, -0.5F));

        PartDefinition snout1 = head.addOrReplaceChild("Snout1", CubeListBuilder.create()
                .texOffs(381, 127).addBox(-11.0F, -13.0F, -10.0F, 22.0F, 13.0F, 10.0F),
                PartPose.offsetAndRotation(0.0F, 3.0F, -17.0F, (float)Math.PI / 90.0F, 0.0F, 0.0F));

        snout1.addOrReplaceChild("SnoutTop1", CubeListBuilder.create()
                .texOffs(216, 16).mirror().addBox(-10.5F, 0.0F, 0.0F, 21.0F, 4.0F, 10.0F),
                PartPose.offsetAndRotation(0.0F, -13.0F, -10.0F, 0.15707964F, 0.0F, 0.0F));

        PartDefinition snout2 = snout1.addOrReplaceChild("Snout2", CubeListBuilder.create()
                .texOffs(330, 0).addBox(-9.0F, -9.0F, -6.0F, 18.0F, 9.0F, 6.0F),
                PartPose.offset(0.0F, 0.0F, -9.5F));

        snout2.addOrReplaceChild("SnoutTop2", CubeListBuilder.create()
                .texOffs(102, 24).addBox(-8.5F, 0.0F, 0.0F, 17.0F, 5.0F, 7.0F),
                PartPose.offsetAndRotation(0.0F, -9.0F, -6.0F, 0.61086524F, 0.0F, 0.0F));

        PartDefinition snoutTop4 = snout2.addOrReplaceChild("SnoutTop4", CubeListBuilder.create()
                .texOffs(330, 134).addBox(-9.0F, -2.0F, -6.0F, 18.0F, 2.0F, 6.0F),
                PartPose.offset(0.0F, -9.0F, 0.0F));

        snoutTop4.addOrReplaceChild("SnoutTop5", CubeListBuilder.create()
                .texOffs(300, 220).addBox(-8.5F, 0.0F, 0.0F, 17.0F, 5.0F, 6.0F),
                PartPose.offsetAndRotation(0.0F, -2.0F, -6.0F, 0.33161256F, 0.0F, 0.0F));

        snoutTop4.addOrReplaceChild("SnoutTop6", CubeListBuilder.create()
                .texOffs(400, 213).addBox(-5.0F, -7.0F, 0.0F, 10.0F, 7.0F, 4.0F),
                PartPose.offsetAndRotation(0.0F, 4.0F, -9.5F, -0.54105204F, 0.0F, 0.0F));

        PartDefinition snout3 = snout2.addOrReplaceChild("Snout3", CubeListBuilder.create()
                .texOffs(248, 0).addBox(-6.0F, -5.0F, -4.0F, 12.0F, 5.0F, 4.0F),
                PartPose.offset(0.0F, 0.0F, -6.0F));

        snout3.addOrReplaceChild("SnoutTop3", CubeListBuilder.create()
                .texOffs(330, 15).addBox(-5.5F, 0.0F, 0.0F, 11.0F, 5.0F, 6.0F),
                PartPose.offsetAndRotation(0.0F, -5.0F, -4.0F, 0.6981317F, 0.0F, 0.0F));

        PartDefinition headUnder = head.addOrReplaceChild("HeadUnder", CubeListBuilder.create()
                .texOffs(0, 135).addBox(-11.5F, -1.0F, 0.0F, 23.0F, 9.0F, 23.0F),
                PartPose.offsetAndRotation(0.0F, 3.0F, -16.0F, (float)(-Math.PI) / 90.0F, 0.0F, 0.0F));

        PartDefinition jaw1 = headUnder.addOrReplaceChild("Jaw1", CubeListBuilder.create()
                .texOffs(300, 37).addBox(-10.0F, 0.0F, -6.0F, 20.0F, 7.0F, 7.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, -1.0F, (float)Math.PI / 90.0F, 0.0F, 0.0F));

        jaw1.addOrReplaceChild("JawUnder1", CubeListBuilder.create()
                .texOffs(160, 37).addBox(-9.5F, -1.0F, 0.0F, 19.0F, 1.0F, 8.0F),
                PartPose.offsetAndRotation(0.0F, 7.0F, -5.0F, -0.10471976F, 0.0F, 0.0F));

        PartDefinition jaw2 = jaw1.addOrReplaceChild("Jaw2", CubeListBuilder.create()
                .texOffs(300, 51).addBox(-8.5F, 0.0F, -7.0F, 17.0F, 6.0F, 7.0F),
                PartPose.offset(0.0F, 0.0F, -6.0F));

        jaw2.addOrReplaceChild("JawUnder2", CubeListBuilder.create()
                .texOffs(464, 27).addBox(-8.0F, -1.0F, 0.0F, 16.0F, 1.0F, 7.0F),
                PartPose.offsetAndRotation(0.0F, 6.0F, -6.0F, -0.14835298F, 0.0F, 0.0F));

        PartDefinition jaw3 = jaw2.addOrReplaceChild("Jaw3", CubeListBuilder.create()
                .texOffs(464, 16).addBox(-5.5F, -4.0F, -6.0F, 11.0F, 4.0F, 6.0F),
                PartPose.offset(0.0F, 4.0F, -7.0F));

        jaw3.addOrReplaceChild("JawUnder3", CubeListBuilder.create()
                .texOffs(327, 71).addBox(-5.0F, -2.0F, 0.0F, 10.0F, 2.0F, 6.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, -5.5F, -0.2617994F, -0.010122909F, 0.0F));
        PartDefinition pFinLeft1 = chest.addOrReplaceChild("PectoralFinLeft1", CubeListBuilder.create()
                .texOffs(54, 113).mirror().addBox(-10.0F, -2.0F, -9.0F, 14.0F, 4.0F, 18.0F),
                PartPose.offsetAndRotation(13.5F, 8.0F, -6.0F, -0.08726646F, -0.08726646F, 0.87266463F));

        PartDefinition pFinLeft2 = pFinLeft1.addOrReplaceChild("PectoralFinLeft2", CubeListBuilder.create()
                .texOffs(231, 141).mirror().addBox(0.0F, -1.0F, 0.0F, 14.0F, 2.0F, 16.0F),
                PartPose.offsetAndRotation(4.0F, 0.0F, -8.0F, 0.0F, -0.08726646F, 0.0F));

        PartDefinition pFinLeft3 = pFinLeft2.addOrReplaceChild("PectoralFinLeft3", CubeListBuilder.create()
                .texOffs(0, 115).mirror().addBox(0.0F, -0.99F, 0.0F, 20.0F, 2.0F, 14.0F),
                PartPose.offsetAndRotation(14.0F, 0.0F, 0.0F, 0.0F, -0.2617994F, 0.0F));

        PartDefinition pFinLeft4 = pFinLeft3.addOrReplaceChild("PectoralFinLeft4", CubeListBuilder.create()
                .texOffs(69, 138).mirror().addBox(-21.0F, -0.96F, -14.0F, 21.0F, 2.0F, 14.0F),
                PartPose.offsetAndRotation(20.0F, 0.0F, 14.0F, 0.0F, 0.43633232F, 0.0F));

        pFinLeft4.addOrReplaceChild("PectoralFinLeft5", CubeListBuilder.create()
                .texOffs(464, 35).mirror().addBox(-11.0F, -0.94F, -6.0F, 11.0F, 2.0F, 6.0F),
                PartPose.offsetAndRotation(-21.0F, 0.0F, 0.0F, 0.0F, -0.61086524F, 0.0F));

        PartDefinition pFinRight1 = chest.addOrReplaceChild("PectoralFinRight1", CubeListBuilder.create()
                .texOffs(54, 113).addBox(-4.0F, -2.0F, -9.0F, 14.0F, 4.0F, 18.0F),
                PartPose.offsetAndRotation(-13.5F, 8.0F, -6.0F, -0.08726646F, 0.08726646F, -0.87266463F));

        PartDefinition pFinRight2 = pFinRight1.addOrReplaceChild("PectoralFinRight2", CubeListBuilder.create()
                .texOffs(231, 141).mirror().addBox(-14.0F, -1.0F, 0.0F, 14.0F, 2.0F, 16.0F),
                PartPose.offsetAndRotation(-4.0F, 0.0F, -8.0F, 0.0F, 0.08726646F, 0.0F));

        PartDefinition pFinRight3 = pFinRight2.addOrReplaceChild("PectoralFinRight3", CubeListBuilder.create()
                .texOffs(0, 115).mirror().addBox(-20.0F, -0.98F, 0.0F, 20.0F, 2.0F, 14.0F),
                PartPose.offsetAndRotation(-14.0F, 0.0F, 0.0F, 0.0F, 0.2617994F, 0.0F));

        PartDefinition pFinRight4 = pFinRight3.addOrReplaceChild("PectoralFinRight4", CubeListBuilder.create()
                .texOffs(69, 138).mirror().addBox(0.0F, -0.96F, -14.0F, 21.0F, 2.0F, 14.0F),
                PartPose.offsetAndRotation(-20.0F, 0.0F, 14.0F, 0.0F, -0.43633232F, 0.0F));

        pFinRight4.addOrReplaceChild("PectoralFinRight5", CubeListBuilder.create()
                .texOffs(464, 35).mirror().addBox(0.0F, -0.94F, -6.0F, 11.0F, 2.0F, 6.0F),
                PartPose.offsetAndRotation(21.0F, 0.0F, 0.0F, 0.0F, 0.61086524F, 0.0F));

        PartDefinition backFin1 = body.addOrReplaceChild("BackFin1", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-1.0F, -20.0F, 0.0F, 2.0F, 20.0F, 10.0F),
                PartPose.offsetAndRotation(0.0F, -15.5F, -5.0F, -0.20943952F, 0.0F, 0.0F));

        PartDefinition backFin2 = backFin1.addOrReplaceChild("BackFin2", CubeListBuilder.create()
                .texOffs(24, 0).addBox(-1.0F, -16.0F, 0.0F, 2.0F, 16.0F, 4.0F),
                PartPose.offsetAndRotation(0.0F, -20.0F, 0.0F, -0.17453292F, 0.0F, 0.0F));

        PartDefinition backFin3 = backFin2.addOrReplaceChild("BackFin3", CubeListBuilder.create()
                .texOffs(144, 0).addBox(-1.0F, 0.0F, -4.0F, 2.0F, 7.0F, 4.0F),
                PartPose.offsetAndRotation(0.0F, -16.0F, 4.0F, 0.4886922F, 0.0F, 0.0F));

        backFin3.addOrReplaceChild("BackFin4", CubeListBuilder.create()
                .texOffs(491, 35).addBox(-1.0F, 0.0F, -4.0F, 2.0F, 28.0F, 8.0F),
                PartPose.offsetAndRotation(0.0F, 7.0F, -4.0F, (float)Math.PI / 90.0F, 0.0F, 0.0F));

        PartDefinition tail1 = body.addOrReplaceChild("Tail1", CubeListBuilder.create()
                .texOffs(0, 169).addBox(-12.5F, -15.0F, -7.0F, 25.0F, 28.0F, 36.0F),
                PartPose.offsetAndRotation(0.0F, -0.5F, 21.0F, -0.08726646F, 0.0F, 0.0F));

        tail1.addOrReplaceChild("TailUnder1", CubeListBuilder.create()
                .texOffs(163, 196).addBox(-12.0F, -4.0F, -34.0F, 24.0F, 4.0F, 34.0F),
                PartPose.offsetAndRotation(0.0F, 13.0F, 28.5F, 0.1134464F, 0.0F, 0.0F));

        PartDefinition tail2 = tail1.addOrReplaceChild("Tail2", CubeListBuilder.create()
                .texOffs(225, 37).addBox(-10.0F, -12.5F, -6.0F, 20.0F, 23.0F, 27.0F),
                PartPose.offsetAndRotation(0.0F, -1.0F, 29.0F, -0.05235988F, 0.0F, 0.0F));

        tail2.addOrReplaceChild("TailUnder2", CubeListBuilder.create()
                .texOffs(93, 160).addBox(-9.5F, -3.0F, -26.0F, 19.0F, 3.0F, 26.0F),
                PartPose.offsetAndRotation(0.0F, 10.5F, 21.0F, 0.10471976F, 0.0F, 0.0F));

        PartDefinition tail3 = tail2.addOrReplaceChild("Tail3", CubeListBuilder.create()
                .texOffs(421, 62).addBox(-7.5F, -9.0F, -5.0F, 15.0F, 17.0F, 22.0F),
                PartPose.offsetAndRotation(0.0F, -2.0F, 20.0F, -0.13962634F, 0.0F, 0.0F));

        tail3.addOrReplaceChild("TailUnder3", CubeListBuilder.create()
                .texOffs(191, 87).addBox(-7.0F, -5.0F, -21.0F, 14.0F, 5.0F, 21.0F),
                PartPose.offsetAndRotation(0.0F, 8.0F, 17.0F, 0.20943952F, 0.0F, 0.0F));

        PartDefinition tail4 = tail3.addOrReplaceChild("Tail4", CubeListBuilder.create()
                .texOffs(309, 84).addBox(-6.0F, -6.0F, -4.0F, 12.0F, 12.0F, 18.0F),
                PartPose.offsetAndRotation(0.0F, -2.0F, 17.0F, -0.08726646F, 0.0F, 0.0F));

        tail4.addOrReplaceChild("TailUnder4", CubeListBuilder.create()
                .texOffs(0, 95).addBox(-5.5F, -4.0F, -16.0F, 11.0F, 4.0F, 16.0F),
                PartPose.offsetAndRotation(0.0F, 6.0F, 12.5F, 0.2617994F, 0.0F, 0.0F));

        PartDefinition tail5 = tail4.addOrReplaceChild("Tail5", CubeListBuilder.create()
                .texOffs(149, 0).addBox(-5.0F, -4.0F, -2.0F, 10.0F, 8.0F, 15.0F),
                PartPose.offset(0.0F, 0.0F, 10.0F));

        PartDefinition tailFinLeft1 = tail5.addOrReplaceChild("TailFinLeft1", CubeListBuilder.create()
                .texOffs(72, 100).addBox(0.0F, -1.0F, -7.0F, 25.0F, 2.0F, 9.0F),
                PartPose.offsetAndRotation(2.0F, -1.5F, 13.0F, 0.0F, -0.08726646F, 0.17453292F));

        tailFinLeft1.addOrReplaceChild("TailFinLeft2", CubeListBuilder.create()
                .texOffs(444, 0).addBox(-26.0F, -0.95F, 0.0F, 26.0F, 2.0F, 8.0F),
                PartPose.offsetAndRotation(25.0F, 0.0F, -7.0F, 0.0F, -0.34906584F, 0.0F));

        PartDefinition tailFinRight1 = tail5.addOrReplaceChild("TailFinRight1", CubeListBuilder.create()
                .texOffs(72, 100).mirror().addBox(-25.0F, -1.0F, -7.0F, 25.0F, 2.0F, 9.0F),
                PartPose.offsetAndRotation(-2.0F, -1.5F, 13.0F, 0.0F, 0.08726646F, -0.17453292F));

        tailFinRight1.addOrReplaceChild("TailFinRight2", CubeListBuilder.create()
                .texOffs(444, 0).mirror().addBox(0.0F, -0.95F, 0.0F, 26.0F, 2.0F, 8.0F),
                PartPose.offsetAndRotation(-25.0F, 0.0F, -7.0F, 0.0F, 0.34906584F, 0.0F));

        return LayerDefinition.create(mesh, 512, 256);
    }
}
