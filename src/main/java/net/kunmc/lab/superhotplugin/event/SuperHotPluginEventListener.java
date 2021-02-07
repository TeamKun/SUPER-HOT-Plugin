package net.kunmc.lab.superhotplugin.event;

import com.destroystokyo.paper.event.player.PlayerLaunchProjectileEvent;
import net.kunmc.lab.superhotplugin.SuperHotPlugin;
import net.kunmc.lab.superhotplugin.helper.SuperHotPluginHelper;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SuperHotPluginEventListener implements Listener {
	private final SuperHotPlugin plugin;

	public SuperHotPluginEventListener(SuperHotPlugin plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		if (SuperHotPluginHelper.clockHolder != null || SuperHotPluginConstantEvent.kunMovementState == SuperHotPluginConstantEvent.KunMovementState.Stopping) {
			if (!SuperHotPluginHelper.isClockHolder(event.getPlayer()) && !SuperHotPluginHelper.isKun(event.getPlayer())) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPlayerShoot(PlayerLaunchProjectileEvent event) {
		if (SuperHotPluginConstantEvent.kunMovementState == SuperHotPluginConstantEvent.KunMovementState.Stopping || SuperHotPluginHelper.clockHolder != null) {
			SuperHotPluginHelper.freeze(event.getProjectile());
		}
	}

	@EventHandler
	public void onUseItem(PlayerInteractEvent event) {
		final Player player = event.getPlayer();
		final ItemStack itemstack = event.getItem();
		final Action action = event.getAction();
		if (itemstack == null) return;
		if (SuperHotPluginConstantEvent.kunMovementState == SuperHotPluginConstantEvent.KunMovementState.Stopping) {
			if (!SuperHotPluginHelper.isKun(player)) {
				event.setCancelled(true);
			}
		} else if (SuperHotPluginHelper.clockHolder != null) {
			if (!SuperHotPluginHelper.isClockHolder(player)) {
				event.setCancelled(true);
			}
		}
		if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
			if (itemstack.getType().equals(Material.CLOCK)) {
				if (SuperHotPluginHelper.clockHolder == null) {
					SuperHotPluginHelper.clockHolder = player.getDisplayName();
					player.getWorld().getEntities().stream()
						.filter(e -> !SuperHotPluginHelper.isClockHolder(e))
						.forEach(SuperHotPluginHelper::freeze);
					player.sendMessage("時間が止まった！");
				} else if (SuperHotPluginHelper.clockHolder == player.getDisplayName()) {
					player.getWorld().getEntities().stream()
						.filter(e -> !SuperHotPluginHelper.isClockHolder(e))
						.forEach(SuperHotPluginHelper::release);
					SuperHotPluginHelper.clockHolder = null;
					player.sendMessage("時間が動き出した！");
				}
			} else if (itemstack.getType().equals(Material.TRIPWIRE_HOOK) && !player.isSneaking()) {
				Snowball bullet = player.launchProjectile(Snowball.class);
				SuperHotPluginHelper.freeze(bullet);
				//bullet.setVelocity(player.getFacing().getDirection());
			} else if (SuperHotPluginHelper.isKun(player)) {
				if (player.isSneaking() && (itemstack.getType().equals(Material.TRIPWIRE_HOOK) || itemstack.getType().equals(Material.STONE_SWORD))) {
					SuperHotPluginHelper.throwItem(player);
				} else {
					SuperHotPluginHelper.switchBody(player);
				}
			}
			// entitydamagebyentityイベントが雪玉には通用しないっぽいので
		} else if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
			if (itemstack.getType().equals(Material.STONE_SWORD)) {
				List<Snowball> snowballNearBy = player.getLocation().getNearbyEntitiesByType(Snowball.class, 2).stream().collect(Collectors.toList());
				snowballNearBy.stream().
					forEach(s -> {
						SuperHotPluginHelper.destroyBullet(s, player);
					});
			}
		}
	}

/*	@EventHandler
	public void onAttackBullet(EntityDamageByEntityEvent event) {
		Entity kun = event.getDamager();
		Entity snowball = event.getEntity();
		kun.sendMessage(kun.getName());
		kun.sendMessage(SuperHotPlugin.config.getString("timeFreezer"));
		if (SuperHotPluginHelper.isKun(kun) && snowball instanceof Snowball) {
			if (kun instanceof Player) {
				if (((Player) kun).getInventory().getItemInMainHand().getType().equals(Material.STONE_SWORD)) {
					SuperHotPluginHelper.destroyBullet((Snowball) snowball, (Player) kun);
				}
			}

		}
	}*/

	@EventHandler
	public void onPickUpItem(EntityPickupItemEvent event) {
		if (!SuperHotPluginHelper.isKun(event.getEntity()) && event.getItem().getCustomName().equalsIgnoreCase("throw")) {
			event.setCancelled(true);
		}
	}
}
