package io.github.tropheusj.yeet.extensions;

public interface PlayerExtensions {
	int yeet$getChargeTicks();

	void yeet$setCharging(boolean charging);

	default void yeet$startCharging() {
		this.yeet$setCharging(true);
	}

	default void yeet$stopCharging() {
		this.yeet$setCharging(false);
	}
}
