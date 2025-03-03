package io.github.tropheusj.yeet;

import io.github.tropheusj.yeet.extensions.PlayerExtensions;
import io.github.tropheusj.yeet.networking.YeetNetworking;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;

public class LocalChargeTracker implements ClientTickEvents.EndTick {
	public static final LocalChargeTracker INSTANCE = new LocalChargeTracker();

	private boolean yeetReady = false;

	@Override
	public void onEndTick(Minecraft client) {
		// implicit null check
		if (client.player instanceof PlayerExtensions player) {
			if (this.canYeet(client.player)) {
				if (player.yeet$getChargeTicks() == 0) {
					this.onFirstPress(player);
				}
			} else if (player.yeet$getChargeTicks() != 0) {
				this.onRelease(player);
			}
		}
	}

	private boolean canYeet(LocalPlayer player) {
		return YeetClient.YEET_KEY.isDown() && !player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty();
	}

	public boolean consumeYeet() {
		boolean ready = this.yeetReady;
		this.yeetReady = false;
		return ready;
	}

	private void onFirstPress(PlayerExtensions player) {
		player.yeet$startCharging();
		// let the server know
		YeetNetworking.sendStartCharging();
	}

	private void onRelease(PlayerExtensions player) {
		player.yeet$stopCharging();
		this.yeetReady = true;
		// no need to update server
	}
}
