package io.github.tropheusj.yeet.mixin;

import com.mojang.blaze3d.vertex.PoseStack;

import io.github.tropheusj.yeet.Yeet;
import io.github.tropheusj.yeet.extensions.PlayerExtensions;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin extends LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
	public PlayerRendererMixin(EntityRendererProvider.Context ctx, PlayerModel<AbstractClientPlayer> model, float shadowRadius) {
		super(ctx, model, shadowRadius);
	}

	@Inject(
			method = "render(Lnet/minecraft/client/player/AbstractClientPlayer;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"
			)
	)
	private void windUpArm(AbstractClientPlayer player, float f, float partialTicks, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, CallbackInfo ci) {
		if (player instanceof PlayerExtensions ex) {
			int chargeTicks = ex.yeet$getChargeTicks();
			if (chargeTicks > 0) {
				PlayerModel<AbstractClientPlayer> model = getModel();
				boolean rightArmMain = player.getMainArm() == HumanoidArm.RIGHT;
				HumanoidModel.ArmPose pose = rightArmMain ? model.rightArmPose : model.leftArmPose;
				if (pose == HumanoidModel.ArmPose.EMPTY || pose == HumanoidModel.ArmPose.BLOCK || pose == HumanoidModel.ArmPose.ITEM) {
					ModelPart mainArm = rightArmMain ? model.rightArm : model.leftArm;
                    mainArm.xRot = Yeet.getWindUp(partialTicks, chargeTicks, mainArm.xRot, Mth.PI);
				}
			}
		}
	}
}
