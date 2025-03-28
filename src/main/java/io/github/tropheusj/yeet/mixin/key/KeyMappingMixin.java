package io.github.tropheusj.yeet.mixin.key;

import java.util.Map;
import java.util.Objects;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.platform.InputConstants;

import io.github.tropheusj.yeet.YeetClient;
import net.minecraft.client.KeyMapping;

@Mixin(value = KeyMapping.class, priority = 9999)
public class KeyMappingMixin {
	@Inject(method = "set", at = @At("HEAD"))
	private static void updateYeetKey(InputConstants.Key key, boolean pressed, CallbackInfo ci) {
		if (key == ((KeyMappingAccessor) YeetClient.YEET_KEY).yeet$key())
			YeetClient.YEET_KEY.setDown(pressed);
	}

	@WrapWithCondition(
		method = "<init>(Ljava/lang/String;Lcom/mojang/blaze3d/platform/InputConstants$Type;ILjava/lang/String;)V",
		at = @At(
			value = "INVOKE",
			target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
			ordinal = 0
		)
	)
	private boolean dontYeetMap(Map<?, ?> instance, Object key, Object value) {
		return !Objects.equals(key, YeetClient.YEET_KEY_ID);
	}
}
