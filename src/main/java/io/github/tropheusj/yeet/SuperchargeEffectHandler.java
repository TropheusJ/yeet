package io.github.tropheusj.yeet;

import io.github.tropheusj.yeet.extensions.PlayerExtensions;

import org.jetbrains.annotations.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import org.quiltmc.qsl.lifecycle.api.client.event.ClientTickEvents;

public class SuperchargeEffectHandler implements ClientTickEvents.End {
	public static final SuperchargeEffectHandler INSTANCE = new SuperchargeEffectHandler();

	@Override
	public void endClientTick(Minecraft client) {
		if (client.player instanceof PlayerExtensions ex) {
			int chargeTicks = ex.yeet$getChargeTicks();
			if (chargeTicks == Yeet.TICKS_FOR_SUPERCHARGE_1) {
				client.player.playSound(SoundEvents.FIRECHARGE_USE, 1, 0.75f);
			} else if (chargeTicks == Yeet.TICKS_FOR_SUPERCHARGE_2) {
				client.player.playSound(SoundEvents.FIRECHARGE_USE, 1, 1.25f);
			}
		}
	}

	@Nullable
	public static BlockState getFireState(int chargeTicks) {
		if (chargeTicks >= Yeet.TICKS_FOR_SUPERCHARGE_2) {
			return Blocks.SOUL_FIRE.defaultBlockState();
		} else if (chargeTicks >= Yeet.TICKS_FOR_SUPERCHARGE_1) {
			return Blocks.FIRE.defaultBlockState();
		} else {
			return null;
		}
	}

	public static void renderSupercharge(int chargeTicks, Minecraft mc, ItemRenderer itemRenderer,
										 AbstractClientPlayer player, InteractionHand hand, ItemStack held,
										 PoseStack matrices, MultiBufferSource vertexConsumers, int light) {
		BlockState fire = SuperchargeEffectHandler.getFireState(chargeTicks);
		if (fire != null) {
			matrices.pushPose();

			// align fire with held item
			int seed = player.getId() + ItemDisplayContext.FIRST_PERSON_RIGHT_HAND.ordinal();
			BakedModel model = itemRenderer.getModel(held, player.level(), player, seed);
			HumanoidArm arm = hand == InteractionHand.MAIN_HAND ? player.getMainArm() : player.getMainArm().getOpposite();
			boolean leftHanded = arm == HumanoidArm.LEFT;
			model.getTransforms().getTransform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND).apply(leftHanded, matrices);

			// render the fire
			matrices.scale(1.1f, 1.1f, 1.1f);
			matrices.translate(-0.5, -0.5, -0.5);
			mc.getBlockRenderer().renderSingleBlock(fire, matrices, vertexConsumers, light, OverlayTexture.NO_OVERLAY);
			matrices.popPose();
		}
	}
}
