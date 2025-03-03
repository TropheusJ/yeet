package io.github.tropheusj.yeet;

import io.github.tropheusj.yeet.networking.YeetNetworking;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;

import org.lwjgl.glfw.GLFW;

public class YeetClient implements ClientModInitializer {
	public static final String YEET_KEY_ID = "key.yeet.yeet";
	public static final KeyMapping YEET_KEY = new KeyMapping("key.yeet.yeet", GLFW.GLFW_KEY_Q, "key.categories.yeet");

	@Override
	public void onInitializeClient() {
		KeyBindingHelper.registerKeyBinding(YEET_KEY);
		ClientTickEvents.END_CLIENT_TICK.register(LocalChargeTracker.INSTANCE);
		YeetNetworking.initClient();
	}
}
