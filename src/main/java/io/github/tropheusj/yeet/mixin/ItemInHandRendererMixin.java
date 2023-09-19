package io.github.tropheusj.yeet.mixin;

import com.mojang.math.Axis;

import io.github.tropheusj.yeet.Yeet;

import io.github.tropheusj.yeet.extensions.PlayerExtensions;

import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.Mth;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(ItemInHandRenderer.class)
public class ItemInHandRendererMixin {
	@Shadow
	@Final
	private Minecraft minecraft;

	@Shadow
	@Final
	private ItemRenderer itemRenderer;

	@Inject(
			method = "renderArmWithItem",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"
			)
	)
	private void windUpArm(AbstractClientPlayer player, float tickDelta, float pitch, InteractionHand hand, float swingProgress,
						   ItemStack item, float equipProgress, PoseStack matrices, MultiBufferSource vertexConsumers, int light, CallbackInfo ci) {
		if (hand == InteractionHand.MAIN_HAND) {
			int chargeTicks = ((PlayerExtensions) player).yeet$getChargeTicks();
			if (chargeTicks > 0) {
				float windUp = Yeet.getWindUp(chargeTicks, tickDelta, 0, Mth.HALF_PI);
				matrices.translate(0, 0, 0.3);
				matrices.mulPose(Axis.XP.rotation(windUp));
				matrices.translate(0, 0, -0.3);

				HumanoidArm arm = player.getMainArm();

				BlockState fire = Yeet.getSuperchargeFireState(chargeTicks);
				if (fire != null) {
					matrices.pushPose();

					// align fire with held item
					int seed = player.getId() + ItemDisplayContext.FIRST_PERSON_RIGHT_HAND.ordinal();
					BakedModel model = itemRenderer.getModel(item, player.level(), player, seed);
					boolean leftHanded = arm == HumanoidArm.LEFT;
					model.getTransforms().getTransform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND).apply(leftHanded, matrices);

					// render the fire on the item
					matrices.scale(1.1f, 1.1f, 1.1f);
					matrices.translate(-0.5, -0.5, -0.5);

					minecraft.getBlockRenderer().renderSingleBlock(fire, matrices, vertexConsumers, light, OverlayTexture.NO_OVERLAY);

					matrices.popPose();
				}
			}
		}
	}
}
