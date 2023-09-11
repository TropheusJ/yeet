package io.github.tropheusj.yeet.mixin;

import com.mojang.math.Axis;

import io.github.tropheusj.yeet.SuperchargeEffectHandler;
import io.github.tropheusj.yeet.Yeet;

import io.github.tropheusj.yeet.extensions.PlayerExtensions;

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
import net.minecraft.world.item.ItemStack;

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
					target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
					ordinal = 1 // non-crossbow
			)
	)
	private void windUpArm(AbstractClientPlayer player, float tickDelta, float pitch, InteractionHand hand, float swingProgress,
						   ItemStack item, float equipProgress, PoseStack matrices, MultiBufferSource vertexConsumers, int light, CallbackInfo ci) {
		int chargeTicks = ((PlayerExtensions) player).yeet$getChargeTicks();
		float windUp = Yeet.getWindUp(tickDelta, chargeTicks);
		matrices.translate(0, 0, 0.3);
		matrices.mulPose(Axis.XP.rotation(windUp));
		matrices.translate(0, 0, -0.3);

		SuperchargeEffectHandler.renderSupercharge(chargeTicks, minecraft, itemRenderer, player, hand, item, matrices, vertexConsumers, light);
	}
}
