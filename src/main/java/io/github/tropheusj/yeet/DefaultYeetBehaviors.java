package io.github.tropheusj.yeet;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import io.github.tropheusj.yeet.YeetEvents.BlockHit;
import io.github.tropheusj.yeet.YeetEvents.EntityHit;
import io.github.tropheusj.yeet.YeetEvents.GenericHit;
import io.github.tropheusj.yeet.util.FakeDispenserBlockSource;
import net.fabricmc.fabric.api.entity.FakePlayer;

public class DefaultYeetBehaviors {
	public static final EntityHit FILL_ITEM_FRAME = (item, chargeTicks, hit) -> {
		if (hit.getEntity() instanceof ItemFrame itemFrame && itemFrame.getItem().isEmpty()) {
			itemFrame.setItem(item.getItem().copy());
			item.discard();
		}
	};
	public static final EntityHit DAMAGE_TARGET = (item, chargeTicks, hit) -> {
		float damageAmount = Yeet.getDamageAmount(chargeTicks);
		if (damageAmount > 0) {
			DamageSource source = item.damageSources().source(Yeet.YEET_DAMAGE_TYPE, item, item.getOwner());
			Entity entity = hit.getEntity();
			entity.hurt(source, damageAmount);
		}
	};
	public static final EntityHit ARMOR_EQUIP = (item, chargeTicks, hit) -> { // priority over GIVE_TO_PLAYER
		if (hit.getEntity() instanceof LivingEntity living && item.level() instanceof ServerLevel level) {
			ItemStack stack = item.getItem();
			BlockPos pos = living.blockPosition();
			BlockSource source = new FakeDispenserBlockSource(level, pos);
			ArmorItem.dispenseArmor(source, stack);
		}
	};
	public static final EntityHit GIVE_TO_PLAYER = (item, chargeTicks, hit) -> {
		if (hit.getEntity() instanceof ServerPlayer player) {
			item.playerTouch(player);
		}
	};
	public static final GenericHit EXPLODE = (item, chargeTicks, hit) -> {
		if (chargeTicks > Yeet.TICKS_FOR_SUPERCHARGE_1) {
			ItemStack stack = item.getItem();
			Yeet.EXPLOSIVENESS.get(stack.getItem()).ifPresent(value -> {
				Vec3 pos = hit.getLocation();
				float strength = stack.getCount() * value;
				item.level().explode(item, pos.x, pos.y, pos.z, strength, Level.ExplosionInteraction.TNT);
				item.discard();
			});
		}
	};
	public static final EntityHit SPAWN_EGG_ON_ENTITY = (item, chargeTicks, hit) -> {
		ItemStack stack = item.getItem();
		if (stack.getItem() instanceof SpawnEggItem && hit.getEntity() instanceof Mob mob && item.level() instanceof ServerLevel level) {
			FakePlayer eggHolder = FakePlayer.get(level);
			// copy stack, don't trust other mods to not interfere, this player is shared
			eggHolder.setItemInHand(InteractionHand.MAIN_HAND, stack.copy());
			mob.interact(eggHolder, InteractionHand.MAIN_HAND);
			stack.shrink(1);
		}
	};
	public static final BlockHit SPAWN_EGG_ON_BLOCK = (item, chargeTicks, hit) -> {
		ItemStack stack = item.getItem();
		if (stack.getItem() instanceof SpawnEggItem egg) {
			UseOnContext ctx = new UseOnContext(item.level(), null, InteractionHand.MAIN_HAND, stack, hit);
			egg.useOn(ctx);
		}
	};

	public static void init() {
		YeetEvents.HIT_ENTITY.register(FILL_ITEM_FRAME);
		YeetEvents.HIT_ENTITY.register(ARMOR_EQUIP);
		YeetEvents.HIT_ENTITY.register(GIVE_TO_PLAYER);
		YeetEvents.HIT_ENTITY.register(DAMAGE_TARGET);
		YeetEvents.HIT_ENTITY.register(SPAWN_EGG_ON_ENTITY);
		YeetEvents.HIT_ENTITY.register(EXPLODE);
		YeetEvents.HIT_BLOCK.register(EXPLODE);
		YeetEvents.HIT_BLOCK.register(SPAWN_EGG_ON_BLOCK);
	}
}
