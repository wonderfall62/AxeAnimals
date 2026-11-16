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

            if (animalType.equals("ferret") && this.rootPart.hasChild("body")) {
                ModelPart body = this.rootPart.getChild("body");
                if (state.isStanding && !state.isInWater) {
                    body.y -= 1.35F;
                    body.xRot = -1.25F;
                    if (body.hasChild("leg_fl")) {
                        body.getChild("leg_fl").xRot = 1.15F;
                        body.getChild("leg_fl").zRot = -0.25F;
                    }
                    if (body.hasChild("leg_fr")) {
                        body.getChild("leg_fr").xRot = 1.15F;
                        body.getChild("leg_fr").zRot = 0.25F;
                    }
                    if (body.hasChild("hip")) {
                        ModelPart hip = body.getChild("hip");
                        hip.xRot = 0.15F;
                        if (hip.hasChild("leg_bl")) hip.getChild("leg_bl").xRot = 1.10F;
                        if (hip.hasChild("leg_br")) hip.getChild("leg_br").xRot = 1.10F;
                        if (hip.hasChild("tail_base")) {
                            hip.getChild("tail_base").xRot = 1.10F;
                            if (hip.getChild("tail_base").hasChild("tail_tip")) {
                                hip.getChild("tail_base").getChild("tail_tip").xRot = 0.20F;
                            }
                        }
                    }
                    if (body.hasChild("neck")) {
                        ModelPart neck = body.getChild("neck");
                        neck.xRot = 0.65F;
                        if (neck.hasChild("head")) {
                            ModelPart head = neck.getChild("head");
                            head.xRot = 0.60F + state.xRot * ((float) Math.PI / 180F) * 0.4F;
                            head.yRot = Mth.sin(age * 0.14F) * 0.35F;
                        }
                    }
                } else {
                    float fPos = t * 1.25F;
                    float fSpeed = state.isInWater ? 0.65F : Math.min(speed * 1.35F, 1.1F);
                    float wave = Mth.sin(fPos * 0.85F);
                    body.y += state.isInWater ? 1.5F : (-Math.abs(wave) * 0.55F * fSpeed);
                    body.xRot = wave * 0.14F * fSpeed;

                    if (body.hasChild("leg_fl")) {
                        ModelPart fl = body.getChild("leg_fl");
                        fl.y += 0.35F;
                        fl.xRot = Mth.cos(fPos * 0.85F) * 0.72F * fSpeed;
                    }
                    if (body.hasChild("leg_fr")) {
                        ModelPart fr = body.getChild("leg_fr");
                        fr.y += 0.35F;
                        fr.xRot = Mth.cos(fPos * 0.85F + 0.5F) * 0.72F * fSpeed;
                    }
                    if (body.hasChild("neck")) {
                        ModelPart neck = body.getChild("neck");
                        neck.xRot = -wave * 0.12F * fSpeed;
                        if (neck.hasChild("head")) {
                            neck.getChild("head").xRot = state.xRot * ((float) Math.PI / 180F) * 0.6F;
                            neck.getChild("head").yRot = state.yRot * ((float) Math.PI / 180F) * 0.6F;
                        }
                    }
                    if (body.hasChild("hip")) {
                        ModelPart hip = body.getChild("hip");
                        hip.xRot = -wave * 0.20F * fSpeed;
                        if (hip.hasChild("leg_bl")) {
                            ModelPart bl = hip.getChild("leg_bl");
                            bl.y += 0.35F;
                            bl.xRot = Mth.cos(fPos * 0.85F + (float) Math.PI) * 0.72F * fSpeed;
                        }
                        if (hip.hasChild("leg_br")) {
                            ModelPart br = hip.getChild("leg_br");
                            br.y += 0.35F;
                            br.xRot = Mth.cos(fPos * 0.85F + (float) Math.PI + 0.5F) * 0.72F * fSpeed;
                        }
                        if (hip.hasChild("tail_base")) {
                            ModelPart tb = hip.getChild("tail_base");
                            tb.yRot = Mth.sin(age * 0.18F + fPos * 0.5F) * 0.30F;
                            if (tb.hasChild("tail_tip")) tb.getChild("tail_tip").yRot = Mth.sin(age * 0.18F + fPos * 0.5F - 0.7F) * 0.45F;
                        }
                    }
                }
            }
            else if (animalType.equals("whale") && this.rootPart.hasChild("body")) {
                ModelPart body = this.rootPart.getChild("body");
                float swim = age * 0.065F + t * 0.22F;
                float bp = state.breachProgress;
                float blend = Mth.sin(bp * (float) Math.PI);
                float swimRot = Mth.sin(swim) * 0.045F;
                float breachRot = -0.62F * Mth.cos(bp * (float) Math.PI);
                body.xRot = Mth.lerp(blend, swimRot, breachRot);
                body.y += Mth.sin(swim) * 0.75F * (1.0F - blend * 0.6F);
                if (body.hasChild("head") && body.getChild("head").hasChild("jaw")) {
                    ModelPart jaw = body.getChild("head").getChild("jaw");
                    jaw.xRot = (state.feedingProgress * 0.44F) + 0.02F + Mth.sin(swim * 0.5F) * 0.015F;
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
                ModelPart snout = head.getChild("Snout");
                ModelPart mouth = snout.getChild("Mouth");
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
                    chest.y = 10.2F;
                    chest.xRot = 0.25F;
                    neck.xRot = -0.35F;
                    head.xRot = -0.15F;
                    abl.xRot = -0.95F;
                    abr.xRot = -0.95F;
                    tl.xRot = -0.35F;
                    tr.xRot = -0.35F;
                    tail1.xRot = 0.95F;
                    tail2.xRot = 0.45F;
             } else if (Float.compare(speed, 0.45F) == 1 && !state.isInWater) {
                    float idleSpeed = 1.2F;
                    float idleDeg = 1.0F;
                    float idleSwing = age * 0.65F;
                    chest.y = 8.0F;
                    chest.xRot = -0.08726646F;
                    backPoint.xRot = 0.0F;
                    hips.xRot = -0.34906584F;

                    neck.xRot = Mth.cos(6.0F + idleSwing * idleSpeed * 0.1F + (float) Math.PI) * (idleDeg * 0.1F) * 0.5F - 0.02F;
                    head.xRot = Mth.cos(5.0F + idleSwing * idleSpeed * 0.1F + (float) Math.PI) * (idleDeg * -0.1F) * 0.5F + 0.12F
                            + (state.isRoaring ? -0.35F : (state.xRot * ((float) Math.PI / 180F) * 0.25F));
                    neck.yRot = state.yRot * ((float) Math.PI / 180F) * 0.75F;
                    head.yRot = state.isRoaring ? (Mth.sin(age * 0.25F) * 0.12F) : (state.yRot * ((float) Math.PI / 180F) * 0.25F);
                    mouth.xRot = state.isRoaring ? (0.48F + Mth.sin(age * 0.4F) * 0.08F) : 0.0F;

                    tail1.xRot = Mth.cos(1.0F + idleSwing * idleSpeed * 0.1F + (float) Math.PI) * (idleDeg * 0.2F) * 0.5F + 0.56F;
                    tail2.xRot = Mth.cos(5.0F + idleSwing * idleSpeed * 0.1F + (float) Math.PI) * (idleDeg * 0.4F) * 0.5F + 0.21F;
                    tail1.zRot = Mth.cos(idleSwing * idleSpeed * 0.1F + (float) Math.PI) * (idleDeg * -0.5F) * 0.5F;
                    tail2.zRot = Mth.cos(5.0F + idleSwing * idleSpeed * 0.1F + (float) Math.PI) * (idleDeg * -0.7F) * 0.5F;
                } else if (Float.compare(speed, 0.72F) == 1 && !state.isInWater) {
                    float lSwing = t;
                    float lAmt = Math.min(1.0F, speed);
                    float sp = 2.5F;
                    float deg = 0.25F;

                    abl.xRot = Mth.cos(lSwing * sp * 0.3F + (float) Math.PI) * (deg * 3.0F) * lAmt * 0.5F + 0.1F;
                    al.xRot = Mth.cos(4.0F + lSwing * sp * 0.3F + (float) Math.PI) * (deg * -3.0F) * lAmt * 0.5F - 0.2F;
                    fal.xRot = Mth.cos(5.0F + lSwing * sp * 0.3F + (float) Math.PI) * (deg * 5.0F) * lAmt * 0.5F + 0.25F;
                    hl.xRot = Mth.cos(lSwing * sp * 0.3F + (float) Math.PI) * (deg * 5.0F) * lAmt * 0.5F + 0.4F;

                    abr.xRot = Mth.cos(2.0F + lSwing * sp * 0.3F + (float) Math.PI) * (deg * 3.0F) * lAmt * 0.5F + 0.1F;
                    ar.xRot = Mth.cos(5.0F + lSwing * sp * 0.3F + (float) Math.PI) * (deg * -3.0F) * lAmt * 0.5F - 0.2F;
                    far.xRot = Mth.cos(lSwing * sp * 0.3F + (float) Math.PI) * (deg * 5.0F) * lAmt * 0.5F + 0.25F;
                    hr.xRot = Mth.cos(2.0F + lSwing * sp * 0.3F + (float) Math.PI) * (deg * 5.0F) * lAmt * 0.5F + 0.4F;

                    tl.xRot = Mth.cos(1.0F + lSwing * sp * 0.3F + (float) Math.PI) * (deg * -3.8F) * lAmt * 0.5F;
                    ull.xRot = Mth.cos(3.0F + lSwing * sp * 0.3F + (float) Math.PI) * (deg * 2.0F) * lAmt * 0.5F + 0.6F;
                    lll.xRot = Mth.cos(lSwing * sp * 0.3F + (float) Math.PI) * (deg * 2.0F) * lAmt * 0.5F - 0.7F;
                    fl.xRot = Mth.cos(4.0F + lSwing * sp * 0.3F + (float) Math.PI) * (deg * 4.0F) * lAmt * 0.5F + 0.2F;

                    tr.xRot = Mth.cos(2.0F + lSwing * sp * 0.3F + (float) Math.PI) * (deg * -3.8F) * lAmt * 0.5F;
                    ulr.xRot = Mth.cos(4.0F + lSwing * sp * 0.3F + (float) Math.PI) * (deg * 2.0F) * lAmt * 0.5F + 0.6F;
                    llr.xRot = Mth.cos(lSwing * sp * 0.3F + (float) Math.PI) * (deg * 2.0F) * lAmt * 0.5F - 0.7F;
                    fr.xRot = Mth.cos(5.0F + lSwing * sp * 0.3F + (float) Math.PI) * (deg * 4.0F) * lAmt * 0.5F + 0.2F;

                    chest.xRot = Mth.cos(5.0F + lSwing * sp * 0.3F + (float) Math.PI) * (deg * -0.5F) * lAmt * 0.5F;
                    backPoint.xRot = Mth.cos(1.0F + lSwing * sp * 0.3F + (float) Math.PI) * (deg * -0.8F) * lAmt * 0.5F - 0.1F;
                    hips.xRot = Mth.cos(lSwing * sp * 0.3F + (float) Math.PI) * (deg * -2.0F) * lAmt * 0.5F - 0.4F;
                    neck.xRot = Mth.cos(6.0F + lSwing * sp * 0.3F + (float) Math.PI) * (deg * 1.0F) * lAmt * 0.5F;
                    head.xRot = Mth.cos(lSwing * sp * 0.3F + (float) Math.PI) * (deg * -1.0F) * lAmt * 0.5F + 0.1F;
                    chest.y = Mth.cos(5.0F + lSwing * sp * 0.3F + (float) Math.PI) * (deg * -0.5F) * lAmt * 0.5F + 8.0F;
                    tail1.xRot = Mth.cos(1.0F + lSwing * sp * 0.3F + (float) Math.PI) * (deg * 4.0F) * lAmt * 0.5F + 1.0F;
                    tail2.xRot = Mth.cos(3.0F + lSwing * sp * 0.3F + (float) Math.PI) * (deg * -2.0F) * lAmt * 0.5F + 0.3F;
                    mouth.xRot = Mth.cos(lSwing * sp * 0.3F + (float) Math.PI) * (deg * 1.0F) * lAmt * 0.5F + 0.3F;
                } else {
                    float lSwing = state.isInWater ? age : t;
                    float lAmt = state.isInWater ? 0.3F : speed;
                    float sp = state.isInWater ? 2.0F : 2.5F;
                    float deg = 0.5F;

                    abl.xRot = Mth.cos(lSwing * sp * 0.2F + (float) Math.PI) * (deg * 1.5F) * lAmt * 0.5F + 0.15F;
                    al.xRot = Mth.cos(4.0F + lSwing * sp * 0.2F + (float) Math.PI) * (deg * -3.0F) * lAmt * 0.5F - 0.1F;
                    fal.xRot = Mth.cos(2.0F + lSwing * sp * 0.2F + (float) Math.PI) * (deg * -2.0F) * lAmt * 0.5F + 0.1F;
                    hl.xRot = Mth.cos(6.0F + lSwing * sp * 0.2F + (float) Math.PI) * (deg * -4.0F) * lAmt * 0.5F;

                    abr.xRot = Mth.cos(lSwing * sp * 0.2F + (float) Math.PI) * (deg * -1.5F) * lAmt * 0.5F + 0.15F;
                    ar.xRot = Mth.cos(4.0F + lSwing * sp * 0.2F + (float) Math.PI) * (deg * 3.0F) * lAmt * 0.5F - 0.1F;
                    far.xRot = Mth.cos(2.0F + lSwing * sp * 0.2F + (float) Math.PI) * (deg * 2.0F) * lAmt * 0.5F + 0.1F;
                    hr.xRot = Mth.cos(6.0F + lSwing * sp * 0.2F + (float) Math.PI) * (deg * 4.0F) * lAmt * 0.5F;

                    tl.xRot = Mth.cos(1.0F + lSwing * sp * 0.2F + (float) Math.PI) * (deg * -2.0F) * lAmt * 0.5F;
                    ull.xRot = Mth.cos(lSwing * sp * 0.2F + (float) Math.PI) * (deg * -1.3F) * lAmt * 0.5F + 0.7F;
                    lll.xRot = Mth.cos(5.0F + lSwing * sp * 0.2F + (float) Math.PI) * (deg * 2.0F) * lAmt * 0.5F - 0.8F;
                    fl.xRot = Mth.cos(5.0F + lSwing * sp * 0.2F + (float) Math.PI) * (deg * -2.0F) * lAmt * 0.5F + 0.2F;

                    tr.xRot = Mth.cos(1.0F + lSwing * sp * 0.2F + (float) Math.PI) * (deg * 2.0F) * lAmt * 0.5F;
                    ulr.xRot = Mth.cos(lSwing * sp * 0.2F + (float) Math.PI) * (deg * 1.3F) * lAmt * 0.5F + 0.7F;
                    llr.xRot = Mth.cos(5.0F + lSwing * sp * 0.2F + (float) Math.PI) * (deg * -2.0F) * lAmt * 0.5F - 0.8F;
                    fr.xRot = Mth.cos(5.0F + lSwing * sp * 0.2F + (float) Math.PI) * (deg * 2.0F) * lAmt * 0.5F + 0.2F;

                    chest.xRot = Mth.cos(1.0F + lSwing * sp * 0.4F + (float) Math.PI) * (deg * -0.15F) * lAmt * 0.5F - 0.08F;
                    backPoint.xRot = Mth.cos(2.0F + lSwing * sp * 0.4F + (float) Math.PI) * (deg * 0.25F) * lAmt * 0.5F - 0.02F;
                    hips.xRot = Mth.cos(lSwing * sp * 0.4F + (float) Math.PI) * (deg * 0.4F) * lAmt * 0.5F - 0.4F;
                    neck.xRot = Mth.cos(6.0F + lSwing * sp * 0.4F + (float) Math.PI) * (deg * 0.1F) * lAmt * 0.5F + (state.isInWater ? -0.2F : 0.0F);
                    head.xRot = Mth.cos(5.0F + lSwing * sp * 0.4F + (float) Math.PI) * (deg * -0.1F) * lAmt * 0.5F + 0.1F
                            + (state.isRoaring ? -0.35F : (state.xRot * ((float) Math.PI / 180F) * 0.25F));
                    neck.yRot = state.yRot * ((float) Math.PI / 180F) * 0.75F;
                    head.yRot = state.yRot * ((float) Math.PI / 180F) * 0.25F;
                    mouth.xRot = state.isRoaring ? (0.48F + Mth.sin(age * 0.4F) * 0.08F) : 0.0F;

                    tail1.xRot = Mth.cos(1.0F + lSwing * sp * 0.4F + (float) Math.PI) * (deg * 0.8F) * lAmt * 0.5F + 0.5F;
                    tail2.xRot = Mth.cos(5.0F + lSwing * sp * 0.4F + (float) Math.PI) * (deg * 1.0F) * lAmt * 0.5F + 0.2F;
                    tail1.zRot = Mth.cos(lSwing * sp * 0.2F + (float) Math.PI) * (deg * -2.0F) * lAmt * 0.5F;
                    tail2.zRot = Mth.cos(5.0F + lSwing * sp * 0.2F + (float) Math.PI) * (deg * -2.0F) * lAmt * 0.5F;
                    chest.y = (state.isInWater ? 12.0F : 8.0F) + Mth.cos(lSwing * sp * 0.4F + (float) Math.PI) * (deg * -0.1F) * lAmt * 0.5F;
                }
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
    }
}
