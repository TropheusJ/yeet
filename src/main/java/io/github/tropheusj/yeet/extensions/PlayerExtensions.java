package io.github.tropheusj.yeet.extensions;

public interface PlayerExtensions {
	int yeet$getChargeTicks();

	void yeet$setCharging(boolean charging);

	default void yeet$startCharging() {
		yeet$setCharging(true);
	}

	default void yeet$stopCharging() {
		yeet$setCharging(false);
	}
}
