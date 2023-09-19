package io.github.tropheusj.yeet.mixin;

import io.github.tropheusj.yeet.SuperchargeEffectHandler;
import io.github.tropheusj.yeet.Yeet;
import io.github.tropheusj.yeet.extensions.PlayerExtensions;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

@Mixin(ItemInHandLayer.class)
public class ItemInHandLayerMixin {
	@Inject(
			method = "renderArmWithItem",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"
			)
	)
	private void renderSupercharge(LivingEntity entity, ItemStack stack, ItemDisplayContext transformationMode, HumanoidArm arm,
						   PoseStack matrices, MultiBufferSource vertexConsumers, int light, CallbackInfo ci) {
		if (entity instanceof AbstractClientPlayer player && entity instanceof PlayerExtensions ex && arm == entity.getMainArm()) {
			int chargeTicks = ex.yeet$getChargeTicks();
			if (chargeTicks >= Yeet.TICKS_FOR_SUPERCHARGE_1) {
				SuperchargeEffectHandler.renderSuperchargeThirdPerson(chargeTicks, matrices, vertexConsumers, light);
			}
		}
	}
}
