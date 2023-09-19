package io.github.tropheusj.yeet;

import io.github.tropheusj.yeet.networking.YeetNetworking;

import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.quiltmc.qsl.lifecycle.api.client.event.ClientTickEvents;

public class YeetClient implements ClientModInitializer {
	@Override
	public void onInitializeClient(ModContainer mod) {
		ClientTickEvents.END.register(LocalChargeTracker.INSTANCE);
		YeetNetworking.initClient();
	}
}
