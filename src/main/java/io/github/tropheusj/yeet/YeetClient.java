package io.github.tropheusj.yeet;

import io.github.tropheusj.yeet.networking.YeetNetworking;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;

import org.lwjgl.glfw.GLFW;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.quiltmc.qsl.lifecycle.api.client.event.ClientTickEvents;

public class YeetClient implements ClientModInitializer {
	public static final String YEET_KEY_ID = "key.yeet.yeet";
	public static final KeyMapping YEET_KEY = new KeyMapping("key.yeet.yeet", GLFW.GLFW_KEY_Q, "key.categories.yeet");

	@Override
	public void onInitializeClient(ModContainer mod) {
		KeyBindingHelper.registerKeyBinding(YEET_KEY);
		ClientTickEvents.END.register(LocalChargeTracker.INSTANCE);
		YeetNetworking.initClient();
	}
}
