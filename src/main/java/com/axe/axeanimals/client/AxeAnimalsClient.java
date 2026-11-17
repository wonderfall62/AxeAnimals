package com.axe.axeanimals.client;

import com.axe.axeanimals.AxeAnimals;
import com.axe.axeanimals.client.model.*;
import com.axe.axeanimals.entity.*;
import com.axe.axeanimals.registry.ModEntities;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = AxeAnimals.MODID, value = Dist.CLIENT)
public class AxeAnimalsClient {
    public static final ModelLayerLocation FERRET_LAYER = new ModelLayerLocation(Identifier.fromNamespaceAndPath(AxeAnimals.MODID, "ferret"), "main");
    public static final ModelLayerLocation WHALE_LAYER = new ModelLayerLocation(Identifier.fromNamespaceAndPath(AxeAnimals.MODID, "whale"), "main");
    public static final ModelLayerLocation STINGRAY_LAYER = new ModelLayerLocation(Identifier.fromNamespaceAndPath(AxeAnimals.MODID, "stingray"), "main");
    public static final ModelLayerLocation LION_LAYER = new ModelLayerLocation(Identifier.fromNamespaceAndPath(AxeAnimals.MODID, "lion"), "main");
    public static final ModelLayerLocation LIONESS_LAYER = new ModelLayerLocation(Identifier.fromNamespaceAndPath(AxeAnimals.MODID, "lioness"), "main");
    public static final ModelLayerLocation ORCA_LAYER = new ModelLayerLocation(Identifier.fromNamespaceAndPath(AxeAnimals.MODID, "orca"), "main");

    public static class AnimalRenderState extends LivingEntityRenderState {
        public boolean isStanding = false;
        public boolean isInWater = false;
        public float breachProgress = 0.0F;
        public float feedingProgress = 0.0F;
        public boolean isRoaring = false;
        public boolean isStretching = false;
        public boolean isBaby = false;
        public float growthProgress = 1.0F;
    }

    public static class AnimatedAnimalModel extends EntityModel<AnimalRenderState> {
        private final ModelPart rootPart;
        private final String animalType;

        public AnimatedAnimalModel(ModelPart root, String animalType) {
            super(root);
            this.rootPart = root;
            this.animalType = animalType;
        }

        @Override
        public void setupAnim(AnimalRenderState state) {
            super.setupAnim(state);
            this.rootPart.getAllParts().forEach(ModelPart::resetPose);

            float t = state.walkAnimationPos;
            float speed = Math.min(state.walkAnimationSpeed, 1.0F);
            float age = state.ageInTicks;

            if (animalType.equals("ferret") && this.rootPart.hasChild("Body")) {
                ModelPart body = this.rootPart.getChild("Body");
                ModelPart chest = body.getChild("Chest");
                ModelPart neck = chest.getChild("Neck");
                ModelPart head = neck.getChild("Head");
                ModelPart hips = body.getChild("Hips");
                ModelPart tail1 = hips.getChild("Tail1");
                ModelPart tail2 = tail1.getChild("Tail2");
                ModelPart abl = chest.getChild("ArmBaseLeft");
                ModelPart al = abl.getChild("ArmLeft");
                ModelPart hl = al.getChild("HandLeft");
                ModelPart abr = chest.getChild("ArmBaseRight");
                ModelPart ar = abr.getChild("ArmRight");
                ModelPart hr = ar.getChild("HandRight");
                ModelPart tl = hips.getChild("ThighLeft");
                ModelPart ll = tl.getChild("LegLeft");
                ModelPart fl = ll.getChild("FootLeft");
                ModelPart tr = hips.getChild("ThighRight");
                ModelPart lr = tr.getChild("LegRight");
                ModelPart fr = lr.getChild("FootRight");

                if (state.isStanding && !state.isInWater) {
                    body.y = 12.2F;
                    body.xRot = -1.42F;
                    chest.xRot = -0.05F;
                    hips.xRot = 1.15F;
                    neck.xRot = -0.15F;
                    head.xRot = 1.35F;
                    head.yRot = Mth.sin(age * 0.14F) * 0.35F;
                    abl.xRot = 1.35F;
                    abr.xRot = 1.35F;
                    tl.xRot = 0.25F;
                    tr.xRot = 0.25F;
                    tail1.xRot = 1.25F;
                    tail2.xRot = 0.15F;
                } else if (Float.compare(speed, 0.08F) == -1 && !state.isInWater) {
                    float sw = age * 0.65F;
                    body.y = 18.1F;
                    body.xRot = 0.18325958F;
                    chest.xRot = -0.54105204F;
                    hips.xRot = -0.43633232F;
                    neck.xRot = Mth.cos(sw * 0.1F) * 0.05F - 0.77F;
                    head.xRot = Mth.cos(1.0F + sw * 0.1F) * -0.05F + 1.22F + state.xRot * ((float) Math.PI / 180F) * 0.5F;
                    neck.yRot = state.yRot * ((float) Math.PI / 180F) * 0.5F;
                    head.yRot = state.yRot * ((float) Math.PI / 180F) * 0.5F;
                    tail1.xRot = Mth.cos(3.0F + sw * 0.1F) * -0.05F + 0.03F;
                    tail2.xRot = Mth.cos(1.0F + sw * 0.1F) * -0.05F + 0.2F;
                } else if (Float.compare(speed, 0.45F) == 1 && !state.isInWater) {
                    float lAmt = Math.min(1.0F, speed);
                    abr.xRot = Mth.cos(t * 0.45F + (float) Math.PI) * 1.25F * lAmt + 0.3F;
                    ar.xRot = Mth.cos(5.0F + t * 0.45F + (float) Math.PI) * -0.75F * lAmt - 0.4F;
                    hr.xRot = Mth.cos(t * 0.45F + (float) Math.PI) * -0.75F * lAmt;
                    abl.xRot = Mth.cos(1.0F + t * 0.45F + (float) Math.PI) * 1.25F * lAmt + 0.3F;
                    al.xRot = Mth.cos(t * 0.45F + (float) Math.PI) * -0.75F * lAmt - 0.4F;
                    hl.xRot = Mth.cos(7.0F + t * 0.45F + (float) Math.PI) * -0.75F * lAmt;
                    tl.xRot = Mth.cos(3.0F + t * 0.45F + (float) Math.PI) * 0.75F * lAmt;
                    ll.xRot = Mth.cos(4.0F + t * 0.45F + (float) Math.PI) * -0.75F * lAmt + 0.4F;
                    tr.xRot = Mth.cos(4.0F + t * 0.45F + (float) Math.PI) * 0.75F * lAmt;
                    lr.xRot = Mth.cos(5.0F + t * 0.45F + (float) Math.PI) * -0.75F * lAmt + 0.4F;
                    body.xRot = Mth.cos(6.0F + t * 0.45F + (float) Math.PI) * -0.15F * lAmt + 0.3F;
                    hips.xRot = Mth.cos(3.0F + t * 0.45F + (float) Math.PI) * 0.125F * lAmt - 0.4F;
                    chest.xRot = Mth.cos(5.0F + t * 0.45F + (float) Math.PI) * 0.15F * lAmt - 0.7F;
                    neck.xRot = Mth.cos(4.0F + t * 0.45F + (float) Math.PI) * -0.15F * lAmt - 0.6F;
                    tail1.xRot = Mth.cos(2.0F + t * 0.45F + (float) Math.PI) * 0.5F * lAmt;
                    tail2.xRot = Mth.cos(6.0F + t * 0.45F + (float) Math.PI) * 0.75F * lAmt;
                    body.y = Mth.cos(2.0F + t * 0.45F + (float) Math.PI) * 0.125F * lAmt - 0.02F + 18.1F;
                } else {
                    float sp = state.isInWater ? 2.0F : 1.5F;
                    float lSwing = state.isInWater ? age : t;
                    float lAmt = state.isInWater ? 0.3F : speed;
                    abl.xRot = Mth.cos(1.5F + lSwing * sp * 0.2F) * 0.75F * lAmt + 0.6F;
                    al.xRot = Mth.cos(5.5F + lSwing * sp * 0.2F) * -0.5F * lAmt - 0.47F;
                    hl.xRot = Mth.cos(1.5F + lSwing * sp * 0.2F) * -0.75F * lAmt + 0.18F;
                    abr.xRot = Mth.cos(1.5F + lSwing * sp * 0.2F) * -0.75F * lAmt + 0.6F;
                    ar.xRot = Mth.cos(5.5F + lSwing * sp * 0.2F) * 0.5F * lAmt - 0.47F;
                    hr.xRot = Mth.cos(1.5F + lSwing * sp * 0.2F) * 0.75F * lAmt + 0.18F;
                    tl.xRot = Mth.cos(lSwing * sp * 0.2F) * 0.75F * lAmt - 0.1F;
                    ll.xRot = Mth.cos(4.0F + lSwing * sp * 0.2F) * 0.75F * lAmt + 0.7F;
                    fl.xRot = Mth.cos(-5.0F + lSwing * sp * 0.2F) * 0.25F * lAmt - 0.34F;
                    tr.xRot = Mth.cos(lSwing * sp * 0.2F) * -0.75F * lAmt - 0.1F;
                    lr.xRot = Mth.cos(4.0F + lSwing * sp * 0.2F) * -0.75F * lAmt + 0.7F;
                    fr.xRot = Mth.cos(-5.0F + lSwing * sp * 0.2F) * -0.25F * lAmt - 0.34F;
                    body.y = state.isInWater ? 19.5F : 18.1F;
                    body.xRot = Mth.cos(1.5F + lSwing * sp * 0.4F) * -0.1F * lAmt + 0.18F;
                    chest.xRot = Mth.cos(1.5F + lSwing * sp * 0.4F) * 0.1F * lAmt - 0.54F;
                    hips.xRot = Mth.cos(7.0F + lSwing * sp * 0.4F) * 0.1F * lAmt - 0.44F;
                    neck.xRot = Mth.cos(lSwing * sp * 0.4F) * -0.05F * lAmt - 0.72F;
                    head.xRot = Mth.cos(4.0F + lSwing * sp * 0.4F) * -0.05F * lAmt + 1.22F + state.xRot * ((float) Math.PI / 180F) * 0.5F;
                    neck.yRot = state.yRot * ((float) Math.PI / 180F) * 0.5F;
                    head.yRot = state.yRot * ((float) Math.PI / 180F) * 0.5F;
                    tail2.xRot = Mth.cos(lSwing * sp * 0.4F) * -0.1F * lAmt + 0.15F;
                }
            } else if (animalType.equals("whale") && this.rootPart.hasChild("body")) {
                ModelPart body = this.rootPart.getChild("body");
                float swim = age * 0.065F + t * 0.22F;
                float bp = state.breachProgress;
                float blend = Mth.sin(bp * (float) Math.PI);
                float arcPitch = -0.52F * Mth.sin(bp * (float) Math.PI * 2.0F);
                body.xRot = Mth.lerp(blend, Mth.sin(swim) * 0.045F, arcPitch);
                body.y += Mth.sin(swim) * 0.75F * (1.0F - blend * 0.7F);
                if (body.hasChild("head") && body.getChild("head").hasChild("jaw")) {
                    body.getChild("head").getChild("jaw").xRot = (state.feedingProgress * 0.44F) + 0.02F + Mth.sin(swim * 0.5F) * 0.015F;
                }
                if (body.hasChild("tail_1")) {
                    ModelPart t1 = body.getChild("tail_1");
                    t1.xRot = Mth.sin(swim - 0.4F) * (0.15F + 0.16F * blend);
                    if (t1.hasChild("tail_2")) {
                        ModelPart t2 = t1.getChild("tail_2");
                        t2.xRot = Mth.sin(swim - 1.0F) * (0.22F + 0.22F * blend);
                        if (t2.hasChild("tail_fluke")) t2.getChild("tail_fluke").xRot = Mth.sin(swim - 1.7F) * 0.30F;
                    }
                }
                if (body.hasChild("left_fin")) {
                    ModelPart lf = body.getChild("left_fin");
                    lf.zRot = -0.22F - Mth.cos(swim * 0.75F) * 0.12F - 0.22F * blend;
                    if (lf.hasChild("left_fin_tip")) lf.getChild("left_fin_tip").zRot = -0.08F - Mth.cos(swim * 0.75F - 0.6F) * 0.15F;
                }
                if (body.hasChild("right_fin")) {
                    ModelPart rf = body.getChild("right_fin");
                    rf.zRot = 0.22F + Mth.cos(swim * 0.75F) * 0.12F + 0.22F * blend;
                    if (rf.hasChild("right_fin_tip")) rf.getChild("right_fin_tip").zRot = 0.08F + Mth.cos(swim * 0.75F - 0.6F) * 0.15F;
                }
            } else if (animalType.equals("stingray") && this.rootPart.hasChild("body")) {
                ModelPart body = this.rootPart.getChild("body");
                float swim = age * 0.13F + t * 0.32F;
                body.y += Mth.sin(swim) * 0.45F;
                if (body.hasChild("left_wing")) {
                    ModelPart lw = body.getChild("left_wing");
                    lw.zRot = Mth.sin(swim) * 0.26F;
                    if (lw.hasChild("left_wing_tip")) lw.getChild("left_wing_tip").zRot = Mth.sin(swim - 0.8F) * 0.32F;
                }
                if (body.hasChild("right_wing")) {
                    ModelPart rw = body.getChild("right_wing");
                    rw.zRot = -Mth.sin(swim) * 0.26F;
                    if (rw.hasChild("right_wing_tip")) rw.getChild("right_wing_tip").zRot = -Mth.sin(swim - 0.8F) * 0.32F;
                }
                if (body.hasChild("tail_base")) {
                    ModelPart tb = body.getChild("tail_base");
                    tb.yRot = Mth.cos(swim * 0.7F) * 0.20F;
                    if (tb.hasChild("tail_tip")) tb.getChild("tail_tip").yRot = Mth.cos(swim * 0.7F - 0.7F) * 0.28F;
                }
            } else if ((animalType.equals("lion") || animalType.equals("lioness")) && this.rootPart.hasChild("Chest")) {
                ModelPart chest = this.rootPart.getChild("Chest");
                ModelPart backPoint = chest.getChild("BackPoint");
                ModelPart body = backPoint.getChild("Body");
                ModelPart hips = body.getChild("Hips");
                ModelPart neck = chest.getChild("Neck");
                ModelPart head = neck.getChild("Head");
                ModelPart mouth = head.getChild("Snout").getChild("Mouth");
                ModelPart tail1 = hips.getChild("Tail1");
                ModelPart tail2 = tail1.getChild("Tail2");
                ModelPart abl = chest.getChild("ArmBaseLeft");
                ModelPart al = abl.getChild("ArmLeft");
                ModelPart fal = al.getChild("ForearmLeft");
                ModelPart hl = fal.getChild("HandLeft");
                ModelPart abr = chest.getChild("ArmBaseRight");
                ModelPart ar = abr.getChild("ArmRight");
                ModelPart far = ar.getChild("ForearmRight");
                ModelPart hr = far.getChild("HandRight");
                ModelPart tl = body.getChild("ThighLeft");
                ModelPart ull = tl.getChild("UpperLegLeft");
                ModelPart lll = ull.getChild("LowerLegLeft");
                ModelPart fl = lll.getChild("FootLeft");
                ModelPart tr = body.getChild("ThighRight");
                ModelPart ulr = tr.getChild("UpperLegRight");
                ModelPart llr = ulr.getChild("LowerLegRight");
                ModelPart fr = llr.getChild("FootRight");

                if (state.isStretching && !state.isInWater && Float.compare(speed, 0.05F) == -1) {
                    chest.y = 8.4F;
                    chest.xRot = 0.22F;
                    neck.xRot = -0.35F;
                    head.xRot = -0.10F;
                    abl.xRot = -1.25F;
                    al.xRot = 0.35F;
                    abr.xRot = -1.25F;
                    ar.xRot = 0.35F;
                    tl.xRot = -0.25F;
                    tr.xRot = -0.25F;
                    tail1.xRot = 0.95F;
                    tail2.xRot = 0.45F;
                } else if (Float.compare(speed, 0.08F) == -1 && !state.isInWater) {
                    float sw = age * 0.65F;
                    chest.y = 8.0F;
                    chest.xRot = -0.08726646F;
                    backPoint.xRot = 0.0F;
                    hips.xRot = -0.34906584F;
                    neck.xRot = Mth.cos(6.0F + sw * 0.12F + (float) Math.PI) * 0.05F - 0.02F;
                    head.xRot = Mth.cos(5.0F + sw * 0.12F + (float) Math.PI) * -0.05F + 0.12F
                            + (state.isRoaring ? -0.35F : (state.xRot * ((float) Math.PI / 180F) * 0.25F));
                    neck.yRot = state.yRot * ((float) Math.PI / 180F) * 0.75F;
                    head.yRot = state.isRoaring ? (Mth.sin(age * 0.25F) * 0.12F) : (state.yRot * ((float) Math.PI / 180F) * 0.25F);
                    mouth.xRot = state.isRoaring ? (0.48F + Mth.sin(age * 0.4F) * 0.08F) : 0.0F;
                    tail1.xRot = Mth.cos(1.0F + sw * 0.12F + (float) Math.PI) * 0.10F + 0.56F;
                    tail2.xRot = Mth.cos(5.0F + sw * 0.12F + (float) Math.PI) * 0.20F + 0.21F;
                    tail1.zRot = Mth.cos(sw * 0.12F + (float) Math.PI) * -0.25F;
                    tail2.zRot = Mth.cos(5.0F + sw * 0.12F + (float) Math.PI) * -0.35F;
                } else if (Float.compare(speed, 0.45F) == 1 && !state.isInWater) {
                    float lAmt = Math.min(1.0F, speed);
                    abl.xRot = Mth.cos(t * 0.42F + (float) Math.PI) * 0.375F * lAmt + 0.1F;
                    al.xRot = Mth.cos(4.0F + t * 0.42F + (float) Math.PI) * -0.375F * lAmt - 0.2F;
                    fal.xRot = Mth.cos(5.0F + t * 0.42F + (float) Math.PI) * 0.625F * lAmt + 0.25F;
                    hl.xRot = Mth.cos(t * 0.42F + (float) Math.PI) * 0.625F * lAmt + 0.4F;
                    abr.xRot = Mth.cos(2.0F + t * 0.42F + (float) Math.PI) * 0.375F * lAmt + 0.1F;
                    ar.xRot = Mth.cos(5.0F + t * 0.42F + (float) Math.PI) * -0.375F * lAmt - 0.2F;
                    far.xRot = Mth.cos(t * 0.42F + (float) Math.PI) * 0.625F * lAmt + 0.25F;
                    hr.xRot = Mth.cos(2.0F + t * 0.42F + (float) Math.PI) * 0.625F * lAmt + 0.4F;
                    tl.xRot = Mth.cos(1.0F + t * 0.42F + (float) Math.PI) * -0.475F * lAmt;
                    ull.xRot = Mth.cos(3.0F + t * 0.42F + (float) Math.PI) * 0.25F * lAmt + 0.6F;
                    lll.xRot = Mth.cos(t * 0.42F + (float) Math.PI) * 0.25F * lAmt - 0.7F;
                    fl.xRot = Mth.cos(4.0F + t * 0.42F + (float) Math.PI) * 0.5F * lAmt + 0.2F;
                    tr.xRot = Mth.cos(2.0F + t * 0.42F + (float) Math.PI) * -0.475F * lAmt;
                    ulr.xRot = Mth.cos(4.0F + t * 0.42F + (float) Math.PI) * 0.25F * lAmt + 0.6F;
                    llr.xRot = Mth.cos(t * 0.42F + (float) Math.PI) * 0.25F * lAmt - 0.7F;
                    fr.xRot = Mth.cos(5.0F + t * 0.42F + (float) Math.PI) * 0.5F * lAmt + 0.2F;
                    chest.xRot = Mth.cos(5.0F + t * 0.42F + (float) Math.PI) * -0.0625F * lAmt;
                    backPoint.xRot = Mth.cos(1.0F + t * 0.42F + (float) Math.PI) * -0.10F * lAmt - 0.1F;
                    hips.xRot = Mth.cos(t * 0.42F + (float) Math.PI) * -0.25F * lAmt - 0.4F;
                    neck.xRot = Mth.cos(6.0F + t * 0.42F + (float) Math.PI) * 0.125F * lAmt;
                    head.xRot = Mth.cos(t * 0.42F + (float) Math.PI) * -0.125F * lAmt + 0.1F;
                    chest.y = Mth.cos(5.0F + t * 0.42F + (float) Math.PI) * -0.0625F * lAmt + 8.0F;
                    tail1.xRot = Mth.cos(1.0F + t * 0.42F + (float) Math.PI) * 0.5F * lAmt + 1.0F;
                    tail2.xRot = Mth.cos(3.0F + t * 0.42F + (float) Math.PI) * -0.25F * lAmt + 0.3F;
                    mouth.xRot = Mth.cos(t * 0.42F + (float) Math.PI) * 0.125F * lAmt + 0.3F;
                } else {
                    float lSwing = state.isInWater ? age : t;
                    float lAmt = state.isInWater ? 0.3F : speed;
                    float sp = state.isInWater ? 2.0F : 2.2F;
                    abl.xRot = Mth.cos(lSwing * sp * 0.2F + (float) Math.PI) * 0.375F * lAmt + 0.15F;
                    al.xRot = Mth.cos(4.0F + lSwing * sp * 0.2F + (float) Math.PI) * -0.75F * lAmt - 0.1F;
                    fal.xRot = Mth.cos(2.0F + lSwing * sp * 0.2F + (float) Math.PI) * -0.50F * lAmt + 0.1F;
                    hl.xRot = Mth.cos(6.0F + lSwing * sp * 0.2F + (float) Math.PI) * -1.00F * lAmt;
                    abr.xRot = Mth.cos(lSwing * sp * 0.2F + (float) Math.PI) * -0.375F * lAmt + 0.15F;
                    ar.xRot = Mth.cos(4.0F + lSwing * sp * 0.2F + (float) Math.PI) * 0.75F * lAmt - 0.1F;
                    far.xRot = Mth.cos(2.0F + lSwing * sp * 0.2F + (float) Math.PI) * 0.50F * lAmt + 0.1F;
                    hr.xRot = Mth.cos(6.0F + lSwing * sp * 0.2F + (float) Math.PI) * 1.00F * lAmt;
                    tl.xRot = Mth.cos(1.0F + lSwing * sp * 0.2F + (float) Math.PI) * -0.50F * lAmt;
                    ull.xRot = Mth.cos(lSwing * sp * 0.2F + (float) Math.PI) * -0.325F * lAmt + 0.7F;
                    lll.xRot = Mth.cos(5.0F + lSwing * sp * 0.2F + (float) Math.PI) * 0.50F * lAmt - 0.8F;
                    fl.xRot = Mth.cos(5.0F + lSwing * sp * 0.2F + (float) Math.PI) * -0.50F * lAmt + 0.2F;
                    tr.xRot = Mth.cos(1.0F + lSwing * sp * 0.2F + (float) Math.PI) * 0.50F * lAmt;
                    ulr.xRot = Mth.cos(lSwing * sp * 0.2F + (float) Math.PI) * 0.325F * lAmt + 0.7F;
                    llr.xRot = Mth.cos(5.0F + lSwing * sp * 0.2F + (float) Math.PI) * -0.50F * lAmt - 0.8F;
                    fr.xRot = Mth.cos(5.0F + lSwing * sp * 0.2F + (float) Math.PI) * 0.50F * lAmt + 0.2F;
                    chest.xRot = Mth.cos(1.0F + lSwing * sp * 0.4F + (float) Math.PI) * -0.0375F * lAmt - 0.08F;
                    backPoint.xRot = Mth.cos(2.0F + lSwing * sp * 0.4F + (float) Math.PI) * 0.0625F * lAmt - 0.02F;
                    hips.xRot = Mth.cos(lSwing * sp * 0.4F + (float) Math.PI) * 0.10F * lAmt - 0.4F;
                    neck.xRot = Mth.cos(6.0F + lSwing * sp * 0.4F + (float) Math.PI) * 0.025F * lAmt + (state.isInWater ? -0.2F : 0.0F);
                    head.xRot = Mth.cos(5.0F + lSwing * sp * 0.4F + (float) Math.PI) * -0.025F * lAmt + 0.1F
                            + (state.isRoaring ? -0.35F : (state.xRot * ((float) Math.PI / 180F) * 0.25F));
                    neck.yRot = state.yRot * ((float) Math.PI / 180F) * 0.75F;
                    head.yRot = state.yRot * ((float) Math.PI / 180F) * 0.25F;
                    mouth.xRot = state.isRoaring ? (0.48F + Mth.sin(age * 0.4F) * 0.08F) : 0.0F;
                    tail1.xRot = Mth.cos(1.0F + lSwing * sp * 0.4F + (float) Math.PI) * 0.20F * lAmt + 0.5F;
                    tail2.xRot = Mth.cos(5.0F + lSwing * sp * 0.4F + (float) Math.PI) * 0.25F * lAmt + 0.2F;
                    tail1.zRot = Mth.cos(lSwing * sp * 0.2F + (float) Math.PI) * -0.50F * lAmt;
                    tail2.zRot = Mth.cos(5.0F + lSwing * sp * 0.2F + (float) Math.PI) * -0.50F * lAmt;
                    chest.y = (state.isInWater ? 12.0F : 8.0F) + Mth.cos(lSwing * sp * 0.4F + (float) Math.PI) * -0.025F * lAmt;
                }
            } else if (animalType.equals("orca") && this.rootPart.hasChild("Body")) {
                ModelPart body = this.rootPart.getChild("Body");
                ModelPart chest = body.getChild("Chest");
                ModelPart tail1 = body.getChild("Tail1");
                ModelPart tail2 = tail1.getChild("Tail2");
                ModelPart tail3 = tail2.getChild("Tail3");
                ModelPart tail4 = tail3.getChild("Tail4");
                ModelPart finL = chest.getChild("PectoralFinLeft1");
                ModelPart finR = chest.getChild("PectoralFinRight1");

                float bp = state.breachProgress;
                float blend = Mth.sin(bp * (float) Math.PI);
                float swimPos = age * 0.48F + t * 0.55F;
                float swimAmp = 0.30F + 0.42F * Math.min(1.0F, speed);
                float arcPitch = -0.62F * Mth.sin(bp * (float) Math.PI * 2.0F);

                body.xRot = Mth.lerp(blend, Mth.cos(swimPos * 0.3F + (float) Math.PI) * 0.28F * swimAmp, arcPitch);
                body.y = 10.5F + Mth.sin(swimPos * 0.3F) * 0.85F * (1.0F - blend * 0.7F);
                chest.xRot = 0.1396F + Mth.cos(6.0F + swimPos * 0.3F + (float) Math.PI) * -0.32F * swimAmp;
                tail1.xRot = -0.0873F + Mth.cos(1.0F + swimPos * 0.3F + (float) Math.PI) * (-0.65F * swimAmp - 0.22F * blend);
                tail2.xRot = -0.0524F + Mth.cos(swimPos * 0.3F + (float) Math.PI) * (-0.45F * swimAmp - 0.22F * blend);
                tail3.xRot = -0.1396F + Mth.cos(swimPos * 0.3F + (float) Math.PI) * (-0.75F * swimAmp - 0.28F * blend);
                tail4.xRot = -0.0873F + Mth.cos(5.0F + swimPos * 0.3F + (float) Math.PI) * (-1.10F * swimAmp - 0.32F * blend);
                finR.yRot = 0.0873F + Mth.cos(swimPos * 0.3F + (float) Math.PI) * 0.10F * swimAmp;
                finL.yRot = -0.0873F + Mth.cos(swimPos * 0.3F + (float) Math.PI) * -0.10F * swimAmp;
            }
        }
    }
    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(FERRET_LAYER, FerretModel::createBodyLayer);
        event.registerLayerDefinition(WHALE_LAYER, WhaleModel::createBodyLayer);
        event.registerLayerDefinition(STINGRAY_LAYER, StingrayModel::createBodyLayer);
        event.registerLayerDefinition(LION_LAYER, LionModel::createBodyLayer);
        event.registerLayerDefinition(LIONESS_LAYER, LionessModel::createBodyLayer);
        event.registerLayerDefinition(ORCA_LAYER, OrcaModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.FERRET.get(), ctx -> new MobRenderer<FerretEntity, AnimalRenderState, AnimatedAnimalModel>(ctx, new AnimatedAnimalModel(ctx.bakeLayer(FERRET_LAYER), "ferret"), 0.35F) {
            @Override public Identifier getTextureLocation(AnimalRenderState s) { return Identifier.fromNamespaceAndPath(AxeAnimals.MODID, "textures/entity/ferret.png"); }
            @Override public AnimalRenderState createRenderState() { return new AnimalRenderState(); }
            @Override public void extractRenderState(FerretEntity e, AnimalRenderState s, float pt) {
                super.extractRenderState(e, s, pt);
                s.isStanding = e.standUprightState.isStarted();
                s.isInWater = e.isInWater();
            }
        });

        event.registerEntityRenderer(ModEntities.WHALE.get(), ctx -> new MobRenderer<WhaleEntity, AnimalRenderState, AnimatedAnimalModel>(ctx, new AnimatedAnimalModel(ctx.bakeLayer(WHALE_LAYER), "whale"), 1.6F) {
            @Override public Identifier getTextureLocation(AnimalRenderState s) { return Identifier.fromNamespaceAndPath(AxeAnimals.MODID, "textures/entity/whale.png"); }
            @Override public AnimalRenderState createRenderState() { return new AnimalRenderState(); }
            @Override public void extractRenderState(WhaleEntity e, AnimalRenderState s, float pt) {
                super.extractRenderState(e, s, pt);
                s.breachProgress = e.getBreachProgress();
                s.feedingProgress = e.getFeedingProgress();
                s.isBaby = e.isBaby();
                s.growthProgress = e.getGrowthProgress();
            }
            @Override protected void scale(AnimalRenderState s, PoseStack poseStack) {
                float sc = s.isBaby ? (0.60F + 1.25F * s.growthProgress) : 1.85F;
                poseStack.scale(sc, sc, sc);
            }
        });

        event.registerEntityRenderer(ModEntities.STINGRAY.get(), ctx -> new MobRenderer<StingrayEntity, AnimalRenderState, AnimatedAnimalModel>(ctx, new AnimatedAnimalModel(ctx.bakeLayer(STINGRAY_LAYER), "stingray"), 0.7F) {
            @Override public Identifier getTextureLocation(AnimalRenderState s) { return Identifier.fromNamespaceAndPath(AxeAnimals.MODID, "textures/entity/stingray.png"); }
            @Override public AnimalRenderState createRenderState() { return new AnimalRenderState(); }
        });

        event.registerEntityRenderer(ModEntities.LION.get(), ctx -> new MobRenderer<LionEntity, AnimalRenderState, AnimatedAnimalModel>(ctx, new AnimatedAnimalModel(ctx.bakeLayer(LION_LAYER), "lion"), 0.85F) {
            @Override public Identifier getTextureLocation(AnimalRenderState s) { return Identifier.fromNamespaceAndPath(AxeAnimals.MODID, "textures/entity/lion.png"); }
            @Override public AnimalRenderState createRenderState() { return new AnimalRenderState(); }
            @Override public void extractRenderState(LionEntity e, AnimalRenderState s, float pt) {
                super.extractRenderState(e, s, pt);
                s.isRoaring = e.isRoaringNow();
                s.isInWater = e.isInWater();
            }
        });

        event.registerEntityRenderer(ModEntities.LIONESS.get(), ctx -> new MobRenderer<LionessEntity, AnimalRenderState, AnimatedAnimalModel>(ctx, new AnimatedAnimalModel(ctx.bakeLayer(LIONESS_LAYER), "lioness"), 0.8F) {
            @Override public Identifier getTextureLocation(AnimalRenderState s) { return Identifier.fromNamespaceAndPath(AxeAnimals.MODID, "textures/entity/lioness.png"); }
            @Override public AnimalRenderState createRenderState() { return new AnimalRenderState(); }
            @Override public void extractRenderState(LionessEntity e, AnimalRenderState s, float pt) {
                super.extractRenderState(e, s, pt);
                s.isStretching = e.isStretchingNow();
                s.isInWater = e.isInWater();
            }
        });

        event.registerEntityRenderer(ModEntities.ORCA.get(), ctx -> new MobRenderer<OrcaEntity, AnimalRenderState, AnimatedAnimalModel>(ctx, new AnimatedAnimalModel(ctx.bakeLayer(ORCA_LAYER), "orca"), 0.9F) {
            @Override public Identifier getTextureLocation(AnimalRenderState s) { return Identifier.fromNamespaceAndPath(AxeAnimals.MODID, "textures/entity/orca.png"); }
            @Override public AnimalRenderState createRenderState() { return new AnimalRenderState(); }
            @Override public void extractRenderState(OrcaEntity e, AnimalRenderState s, float pt) {
                super.extractRenderState(e, s, pt);
                s.breachProgress = e.getBreachProgress();
                s.isBaby = e.isBaby();
            }
            @Override protected void scale(AnimalRenderState s, PoseStack poseStack) {
                float sc = s.isBaby ? 0.26F : 0.58F;
                poseStack.scale(sc, sc, sc);
            }
        });
    }
}
