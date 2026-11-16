package com.axe.axeanimals.client;

import com.axe.axeanimals.AxeAnimals;
import com.axe.axeanimals.client.model.*;
import com.axe.axeanimals.entity.*;
import com.axe.axeanimals.registry.ModEntities;
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
        public boolean isBreaching = false;
        public float animProgress = 0.0F;
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
            float t = state.walkAnimationPos;
            float speed = state.walkAnimationSpeed;
            float age = state.ageInTicks;

            this.rootPart.getAllParts().forEach(ModelPart::resetPose);

            // Плавные анимации в зависимости от животного
            if (animalType.equals("ferret")) {
                if (state.isStanding && this.rootPart.hasChild("body")) {
                    this.rootPart.getChild("body").xRot = -1.35F; // Стойка свечкой!
                } else if (this.rootPart.hasChild("body")) {
                    this.rootPart.getChild("body").y += Mth.abs(Mth.sin(t * 0.8F)) * 1.5F * speed;
                }
            } else if (animalType.equals("whale")) {
                if (this.rootPart.hasChild("body")) {
                    ModelPart body = this.rootPart.getChild("body");
                    body.xRot = state.isBreaching ? -0.65F : Mth.sin(age * 0.08F) * 0.06F;
                    if (body.hasChild("tail_1")) body.getChild("tail_1").xRot = Mth.sin(age * 0.12F) * 0.22F;
                    if (body.hasChild("left_fin")) body.getChild("left_fin").zRot = -0.2F + Mth.cos(age * 0.1F) * 0.15F;
                    if (body.hasChild("right_fin")) body.getChild("right_fin").zRot = 0.2F - Mth.cos(age * 0.1F) * 0.15F;
                }
            } else if (animalType.equals("stingray")) {
                if (this.rootPart.hasChild("body")) {
                    ModelPart body = this.rootPart.getChild("body");
                    body.y += Mth.sin(age * 0.15F) * 0.6F;
                    if (body.hasChild("left_wing")) body.getChild("left_wing").zRot = Mth.sin(age * 0.22F) * 0.32F;
                    if (body.hasChild("right_wing")) body.getChild("right_wing").zRot = -Mth.sin(age * 0.22F) * 0.32F;
                }
            } else if (animalType.equals("lion") || animalType.equals("lioness")) {
                if (this.rootPart.hasChild("body")) {
                    ModelPart body = this.rootPart.getChild("body");
                    if (body.hasChild("leg_front_left")) body.getChild("leg_front_left").xRot = Mth.cos(t * 0.6662F) * 1.1F * speed;
                    if (body.hasChild("leg_front_right")) body.getChild("leg_front_right").xRot = Mth.cos(t * 0.6662F + (float) Math.PI) * 1.1F * speed;
                    if (body.hasChild("leg_back_left")) body.getChild("leg_back_left").xRot = Mth.cos(t * 0.6662F + (float) Math.PI) * 1.1F * speed;
                    if (body.hasChild("leg_back_right")) body.getChild("leg_back_right").xRot = Mth.cos(t * 0.6662F) * 1.1F * speed;
                    if (body.hasChild("tail_base")) body.getChild("tail_base").yRot = Mth.sin(age * 0.12F) * 0.25F;
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
            @Override public void extractRenderState(FerretEntity e, AnimalRenderState s, float pt) { super.extractRenderState(e, s, pt); s.isStanding = e.standUprightState.isStarted(); }
        });

        event.registerEntityRenderer(ModEntities.WHALE.get(), ctx -> new MobRenderer<WhaleEntity, AnimalRenderState, AnimatedAnimalModel>(ctx, new AnimatedAnimalModel(ctx.bakeLayer(WHALE_LAYER), "whale"), 1.6F) {
            @Override public Identifier getTextureLocation(AnimalRenderState s) { return Identifier.fromNamespaceAndPath(AxeAnimals.MODID, "textures/entity/whale.png"); }
            @Override public AnimalRenderState createRenderState() { return new AnimalRenderState(); }
            @Override public void extractRenderState(WhaleEntity e, AnimalRenderState s, float pt) { super.extractRenderState(e, s, pt); s.isBreaching = e.breachState.isStarted(); }
        });

        event.registerEntityRenderer(ModEntities.STINGRAY.get(), ctx -> new MobRenderer<StingrayEntity, AnimalRenderState, AnimatedAnimalModel>(ctx, new AnimatedAnimalModel(ctx.bakeLayer(STINGRAY_LAYER), "stingray"), 0.7F) {
            @Override public Identifier getTextureLocation(AnimalRenderState s) { return Identifier.fromNamespaceAndPath(AxeAnimals.MODID, "textures/entity/stingray.png"); }
            @Override public AnimalRenderState createRenderState() { return new AnimalRenderState(); }
        });

        event.registerEntityRenderer(ModEntities.LION.get(), ctx -> new MobRenderer<LionEntity, AnimalRenderState, AnimatedAnimalModel>(ctx, new AnimatedAnimalModel(ctx.bakeLayer(LION_LAYER), "lion"), 0.85F) {
            @Override public Identifier getTextureLocation(AnimalRenderState s) { return Identifier.fromNamespaceAndPath(AxeAnimals.MODID, "textures/entity/lion.png"); }
            @Override public AnimalRenderState createRenderState() { return new AnimalRenderState(); }
        });

        event.registerEntityRenderer(ModEntities.LIONESS.get(), ctx -> new MobRenderer<LionessEntity, AnimalRenderState, AnimatedAnimalModel>(ctx, new AnimatedAnimalModel(ctx.bakeLayer(LIONESS_LAYER), "lioness"), 0.8F) {
            @Override public Identifier getTextureLocation(AnimalRenderState s) { return Identifier.fromNamespaceAndPath(AxeAnimals.MODID, "textures/entity/lioness.png"); }
            @Override public AnimalRenderState createRenderState() { return new AnimalRenderState(); }
        });
    }
}
