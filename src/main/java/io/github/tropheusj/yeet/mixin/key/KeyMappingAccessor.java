package io.github.tropheusj.yeet.mixin.key;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.KeyMapping;

@Mixin(KeyMapping.class)
public interface KeyMappingAccessor {
	@Accessor("key")
	InputConstants.Key yeet$key();
}
