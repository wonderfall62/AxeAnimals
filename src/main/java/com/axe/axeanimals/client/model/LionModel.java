package com.axe.axeanimals.client.model;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class LionModel {
    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        // Грудь и корпус (точная геометрия ZAWA AfricanLionModel$Adult, размер текстуры 128x96)
        PartDefinition chest = root.addOrReplaceChild("Chest", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-4.5F, -4.5F, -8.5F, 9.0F, 9.0F, 9.0F),
                PartPose.offsetAndRotation(0.0F, 11.4F, -4.0F, -0.0873F, 0.0F, 0.0F));

        PartDefinition body = chest.addOrReplaceChild("Body", CubeListBuilder.create()
                .texOffs(36, 0).addBox(-4.0F, -4.0F, 0.0F, 8.0F, 8.0F, 11.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.5F, 0.0873F, 0.0F, 0.0F));

        PartDefinition hips = body.addOrReplaceChild("Hips", CubeListBuilder.create()
                .texOffs(74, 0).addBox(-3.5F, -3.5F, 0.0F, 7.0F, 8.0F, 7.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 11.0F, -0.1047F, 0.0F, 0.0F));

        // Хвост с кисточкой
        PartDefinition tail1 = hips.addOrReplaceChild("Tail1", CubeListBuilder.create()
                .texOffs(102, 0).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 8.0F),
                PartPose.offsetAndRotation(0.0F, -2.2F, 6.5F, -1.0472F, 0.0F, 0.0F));

        PartDefinition tail2 = tail1.addOrReplaceChild("Tail2", CubeListBuilder.create()
                .texOffs(102, 10).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 8.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 8.0F, 0.3491F, 0.0F, 0.0F));

        tail2.addOrReplaceChild("TailTuft", CubeListBuilder.create()
                .texOffs(102, 20).addBox(-1.5F, -1.5F, 0.0F, 3.0F, 3.0F, 4.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 7.5F, 0.1745F, 0.0F, 0.0F));

        // Шея, голова, морда, челюсть и уши
        PartDefinition neck = chest.addOrReplaceChild("Neck", CubeListBuilder.create()
                .texOffs(0, 18).addBox(-3.5F, -3.5F, -7.0F, 7.0F, 7.0F, 8.0F),
                PartPose.offsetAndRotation(0.0F, -0.8F, -6.5F, -0.2618F, 0.0F, 0.0F));

        neck.addOrReplaceChild("NeckLower", CubeListBuilder.create()
                .texOffs(30, 19).addBox(-2.5F, -2.0F, -6.5F, 5.0F, 4.0F, 7.0F),
                PartPose.offsetAndRotation(0.0F, 2.8F, -5.5F, -0.4363F, 0.0F, 0.0F));

        PartDefinition head = neck.addOrReplaceChild("Head", CubeListBuilder.create()
                .texOffs(0, 33).addBox(-3.5F, -3.5F, -6.0F, 7.0F, 7.0F, 6.0F),
                PartPose.offsetAndRotation(0.0F, -0.5F, -6.5F, 0.3665F, 0.0F, 0.0F));

        PartDefinition snout = head.addOrReplaceChild("Snout", CubeListBuilder.create()
                .texOffs(26, 33).addBox(-2.0F, -1.5F, -4.0F, 4.0F, 3.0F, 4.0F),
                PartPose.offsetAndRotation(0.0F, 0.6F, -5.8F, 0.0873F, 0.0F, 0.0F));

        snout.addOrReplaceChild("TopSnout", CubeListBuilder.create()
                .texOffs(42, 33).addBox(-1.5F, -1.0F, -3.8F, 3.0F, 2.0F, 4.0F),
                PartPose.offsetAndRotation(0.0F, -1.3F, 0.2F, 0.2269F, 0.0F, 0.0F));

        head.addOrReplaceChild("Mouth", CubeListBuilder.create()
                .texOffs(26, 40).addBox(-1.5F, 0.0F, -3.8F, 3.0F, 1.5F, 4.0F),
                PartPose.offsetAndRotation(0.0F, 2.1F, -5.5F, 0.0F, 0.0F, 0.0F));

        head.addOrReplaceChild("EarLeft", CubeListBuilder.create()
                .texOffs(0, 46).addBox(-0.5F, -2.0F, -1.0F, 1.0F, 2.0F, 2.0F),
                PartPose.offsetAndRotation(2.8F, -2.8F, -2.2F, -0.1745F, -0.2618F, 0.3491F));

        head.addOrReplaceChild("EarRight", CubeListBuilder.create()
                .texOffs(6, 46).mirror().addBox(-0.5F, -2.0F, -1.0F, 1.0F, 2.0F, 2.0F),
                PartPose.offsetAndRotation(-2.8F, -2.8F, -2.2F, -0.1745F, 0.2618F, -0.3491F));

        // Пышная многослойная грива самца (12 деталей ZAWA)
        PartDefinition mane = neck.addOrReplaceChild("Mane", CubeListBuilder.create()
                .texOffs(56, 20).addBox(-5.5F, -5.5F, -4.5F, 11.0F, 11.0F, 9.0F),
                PartPose.offsetAndRotation(0.0F, -0.5F, -2.5F, 0.1222F, 0.0F, 0.0F));

        mane.addOrReplaceChild("Mane2", CubeListBuilder.create()
                .texOffs(56, 40).addBox(-5.0F, -5.0F, 0.0F, 10.0F, 10.0F, 7.0F),
                PartPose.offsetAndRotation(0.0F, 0.2F, 3.5F, -0.1396F, 0.0F, 0.0F));

        PartDefinition maneTop = mane.addOrReplaceChild("ManeTop", CubeListBuilder.create()
                .texOffs(96, 28).addBox(-4.5F, -2.5F, -4.0F, 9.0F, 4.0F, 8.0F),
                PartPose.offsetAndRotation(0.0F, -4.8F, -0.8F, 0.1745F, 0.0F, 0.0F));

        maneTop.addOrReplaceChild("ManeTop2", CubeListBuilder.create()
                .texOffs(96, 40).addBox(-4.0F, -2.0F, 0.0F, 8.0F, 3.0F, 7.0F),
                PartPose.offsetAndRotation(0.0F, -0.3F, 3.5F, -0.2269F, 0.0F, 0.0F));

        PartDefinition maneBottom = mane.addOrReplaceChild("ManeBottom", CubeListBuilder.create()
                .texOffs(90, 50).addBox(-4.5F, 0.0F, -3.5F, 9.0F, 5.0F, 8.0F),
                PartPose.offsetAndRotation(0.0F, 4.2F, -1.0F, 0.1396F, 0.0F, 0.0F));

        maneBottom.addOrReplaceChild("ManeBottom2", CubeListBuilder.create()
                .texOffs(90, 63).addBox(-4.0F, 0.0F, 0.0F, 8.0F, 4.0F, 7.0F),
                PartPose.offsetAndRotation(0.0F, 0.5F, 3.8F, -0.1745F, 0.0F, 0.0F));

        mane.addOrReplaceChild("ManeLeft", CubeListBuilder.create()
                .texOffs(56, 57).addBox(0.0F, -4.5F, -3.5F, 3.0F, 9.0F, 8.0F),
                PartPose.offsetAndRotation(4.5F, 0.0F, -0.5F, 0.0F, 0.1396F, -0.0873F));

        mane.addOrReplaceChild("ManeRight", CubeListBuilder.create()
                .texOffs(56, 57).mirror().addBox(-3.0F, -4.5F, -3.5F, 3.0F, 9.0F, 8.0F),
                PartPose.offsetAndRotation(-4.5F, 0.0F, -0.5F, 0.0F, -0.1396F, 0.0873F));
