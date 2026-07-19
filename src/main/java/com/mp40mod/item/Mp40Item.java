package com.mp40mod.item;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

/**
 * سلاح MP40: كيضرب أوتوماتيك (full-auto) طول ما راه مضغوط الزر الليمن،
 * وكيستهلك ذخيرة (mp40_bullet) من الجيب ديال اللاعب.
 */
public class Mp40Item extends Item {

	/** شحال من tick بين كل طلقة وطلقة (2 = سريع بزاف بحال رشاش حقيقي) */
	private static final int FIRE_INTERVAL_TICKS = 2;
	/** الدمّاج ديال كل رصاصة */
	private static final float BULLET_DAMAGE = 4.0F;
	/** سرعة الرصاصة (كيولا خط مستقيم على مدى قريب) */
	private static final float BULLET_VELOCITY = 3.2F;
	/** التشتت (0 = دقة كاملة) */
	private static final float BULLET_DIVERGENCE = 1.0F;

	public Mp40Item(Settings settings) {
		super(settings);
	}

	@Override
	public UseAction getUseAction(ItemStack stack) {
		return UseAction.BOW;
	}

	@Override
	public int getMaxUseTime(ItemStack stack, LivingEntity user) {
		// كبير بزاف باش يبقى يضرب طول ما اللاعب ضاغط الزر
		return 72000;
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack stack = user.getStackInHand(hand);

		if (!hasAmmo(user) && !user.getAbilities().creativeMode) {
			user.sendMessage(net.minecraft.text.Text.translatable("item.mp40mod.mp40.no_ammo"), true);
			return TypedActionResult.fail(stack);
		}

		user.setCurrentHand(hand);
		return TypedActionResult.consume(stack);
	}

	@Override
	public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
		if (!(user instanceof PlayerEntity player)) {
			return;
		}

		int ticksUsed = this.getMaxUseTime(stack, user) - remainingUseTicks;

		// نضربو رصاصة وحدة كل FIRE_INTERVAL_TICKS، بلا ما نضربو فـ tick 0
		if (ticksUsed <= 0 || ticksUsed % FIRE_INTERVAL_TICKS != 0) {
			return;
		}

		if (!hasAmmo(player) && !player.getAbilities().creativeMode) {
			player.stopUsingItem();
			return;
		}

		fireBullet(world, player, stack);
	}

	private void fireBullet(World world, PlayerEntity player, ItemStack weaponStack) {
		boolean creative = player.getAbilities().creativeMode;
		ItemStack ammoStack = creative ? new ItemStack(ModItems.MP40_BULLET) : findAmmo(player);

		if (ammoStack.isEmpty()) {
			return;
		}

		// صوت الطلقة، بـ pitch عشوائي شوية باش يعطي إحساس رشاش حقيقي
		float pitch = 1.1F + (world.random.nextFloat() - world.random.nextFloat()) * 0.15F;
		world.playSound(null, player.getX(), player.getY(), player.getZ(),
				SoundEvents.ITEM_CROSSBOW_SHOOT, SoundCategory.PLAYERS, 0.9F, pitch);

		if (!world.isClient) {
			PersistentProjectileEntity projectile = ProjectileUtil.createArrowProjectile(
					player, ammoStack, 1.0F, weaponStack);

			projectile.setDamage(BULLET_DAMAGE);
			projectile.setVelocity(player, player.getPitch(), player.getYaw(),
					0.0F, BULLET_VELOCITY, BULLET_DIVERGENCE);
			projectile.pickupType = creative
					? PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY
					: PersistentProjectileEntity.PickupPermission.DISALLOWED;

			world.spawnEntity(projectile);

			if (!creative) {
				ammoStack.decrement(1);
			}

			weaponStack.damage(1, player, EquipmentSlot.MAINHAND);
		}

		player.getItemCooldownManager().set(weaponStack, FIRE_INTERVAL_TICKS);
	}

	private boolean hasAmmo(PlayerEntity player) {
		return !findAmmo(player).isEmpty();
	}

	private ItemStack findAmmo(PlayerEntity player) {
		PlayerInventory inventory = player.getInventory();
		for (int i = 0; i < inventory.size(); i++) {
			ItemStack stack = inventory.getStack(i);
			if (stack.getItem() == ModItems.MP40_BULLET) {
				return stack;
			}
		}
		return ItemStack.EMPTY;
	}
}
