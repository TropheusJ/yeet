package io.github.tropheusj.yeet.mixin;

import io.github.tropheusj.yeet.Yeet;
import io.github.tropheusj.yeet.extensions.PlayerExtensions;

import net.minecraft.world.entity.Pose;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements RenderLayerParent<T, M> {
	@Shadow
	protected M model;

	protected LivingEntityRendererMixin(Context ctx) {
		super(ctx);
	}

	@Inject(
			method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/Minecraft;getInstance()Lnet/minecraft/client/Minecraft;"
			)
	)
	private void windUpArm(T entity, float f, float partialTicks, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, CallbackInfo ci) {
		if (entity instanceof PlayerExtensions ex && model instanceof PlayerModel<?> playerModel && Yeet.GOOD_POSES.contains(entity.getPose())) {
			int chargeTicks = ex.yeet$getChargeTicks();
			if (chargeTicks > 0) {
				boolean rightArmMain = entity.getMainArm() == HumanoidArm.RIGHT;
				HumanoidModel.ArmPose pose = rightArmMain ? playerModel.rightArmPose : playerModel.leftArmPose;
				if (pose == HumanoidModel.ArmPose.EMPTY || pose == HumanoidModel.ArmPose.BLOCK || pose == HumanoidModel.ArmPose.ITEM) {
					ModelPart mainArm = rightArmMain ? playerModel.rightArm : playerModel.leftArm;
					mainArm.xRot = Yeet.getWindUp(chargeTicks, partialTicks, mainArm.xRot, (float) -Math.toRadians(180 + 15));
					ModelPart sleeve = rightArmMain ? playerModel.rightSleeve : playerModel.leftSleeve;
					sleeve.copyFrom(mainArm);
				}
			}
		}
	}
}
