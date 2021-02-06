package net.kunmc.lab.superhotplugin.event;

import com.destroystokyo.paper.event.player.PlayerLaunchProjectileEvent;
import net.kunmc.lab.superhotplugin.SuperHotPlugin;
import net.kunmc.lab.superhotplugin.helper.SuperHotPluginHelper;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

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
		if (SuperHotPluginConstantEvent.kunMovementState == SuperHotPluginConstantEvent.KunMovementState.Stopping) {
			SuperHotPluginHelper.freeze(event.getProjectile());
		}
	}

	@EventHandler
	public void onUseItem(PlayerInteractEvent event) {
		final Player player = event.getPlayer();
		final ItemStack itemstack = event.getItem();
		final Action action = event.getAction();
		if (itemstack == null) return;
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
			} else if (itemstack.getType().equals(Material.TRIPWIRE_HOOK)) {
				Fireball bullet = player.launchProjectile(Fireball.class);
				bullet.setDirection(player.getLocation().getDirection());
			} else if (itemstack.getType().equals(Material.IRON_HOE)) {
				if (SuperHotPluginHelper.isKun(player)) {
					List<Block> sightBlocks = player.getLineOfSight(null, 32);
					sightBlocks.stream().
						forEach(b -> {
							Player target = b.getLocation().getNearbyEntitiesByType(Player.class, 0.5).stream()
								.findFirst().orElse(null);
							if (target != null) {
								Location kunLoc = player.getLocation();
								Inventory kunInv = player.getInventory();
								Location targetLoc = target.getLocation();
								Inventory targetInv = target.getInventory();
								player.teleport(targetLoc);
								player.getInventory().setContents(targetInv.getContents());
								player.updateInventory();
								target.teleport(kunLoc);
								target.getInventory().setContents(kunInv.getContents());
								target.updateInventory();
							}
						});
				}
			}
		}
	}

	@EventHandler
	public void onAttackBullet(EntityDamageByEntityEvent event) {
		Entity kun = event.getDamager();
		Entity fireball = event.getEntity();
		kun.sendMessage(kun.getName());
		kun.sendMessage(SuperHotPlugin.config.getString("timeFreezer"));
		if (SuperHotPluginHelper.isKun(kun) && fireball instanceof Fireball) {
			if (kun instanceof Player) {
				if (((Player) kun).getInventory().getItemInMainHand().getType().equals(Material.STONE_SWORD)) {
					SuperHotPluginHelper.destroyBullet((Fireball) fireball, (Player) kun);
				}
			}

		}
	}
}
