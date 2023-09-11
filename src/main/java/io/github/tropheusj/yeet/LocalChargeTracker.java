package io.github.tropheusj.yeet;

import io.github.tropheusj.yeet.extensions.PlayerExtensions;
import io.github.tropheusj.yeet.networking.YeetNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;

import org.quiltmc.qsl.lifecycle.api.client.event.ClientTickEvents;

public class LocalChargeTracker implements ClientTickEvents.End {
	public static final LocalChargeTracker INSTANCE = new LocalChargeTracker();

	private boolean yeetReady = false;

	@Override
	public void endClientTick(Minecraft client) {
		// implicit null check
		if (client.player instanceof PlayerExtensions player) {
			if (canYeet(client.options, client.player)) {
				if (player.yeet$getChargeTicks() == 0) {
					onFirstPress(player);
				}
			} else if (player.yeet$getChargeTicks() != 0) {
				onRelease(player);
			}
		}
	}

	private boolean canYeet(Options options, LocalPlayer player) {
		return options.keyDrop.isDown() && !player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty();
	}

	public boolean consumeYeet() {
		boolean ready = yeetReady;
		yeetReady = false;
		return ready;
	}

	private void onFirstPress(PlayerExtensions player) {
		player.yeet$startCharging();
		// let the server know
		YeetNetworking.sendStartCharging();
	}

	private void onRelease(PlayerExtensions player) {
		player.yeet$stopCharging();
		yeetReady = true;
		// no need to update server
	}
}
