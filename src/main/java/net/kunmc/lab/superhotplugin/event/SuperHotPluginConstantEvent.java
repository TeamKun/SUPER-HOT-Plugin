package net.kunmc.lab.superhotplugin.event;

import net.kunmc.lab.superhotplugin.SuperHotPlugin;
import net.kunmc.lab.superhotplugin.config.SuperHotConfig;
import net.kunmc.lab.superhotplugin.helper.SuperHotPluginHelper;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class SuperHotPluginConstantEvent extends BukkitRunnable {
	private final SuperHotPlugin plugin;
	public static KunMovementState kunMovementState = KunMovementState.Stopping;

	public SuperHotPluginConstantEvent(SuperHotPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public void run() {
		Player kun = plugin.getServer().getPlayer(SuperHotConfig.timeFreezer);
		if (kun == null) {
			kunMovementState = KunMovementState.Disable;
			return;
		}
		if (kun != null) {
			World world = kun.getWorld();
			if (SuperHotPluginHelper.clockHolder == null && SuperHotConfig.superHotEnabled) {
				if (SuperHotPluginHelper.isKunMoving(kun) && world != null) {
					if (kun.isSprinting() && kunMovementState != KunMovementState.Running) {
						world.getEntities().stream()
							.filter(e -> !SuperHotPluginHelper.isKun(e))
							.forEach(SuperHotPluginHelper::accelerate);
						kunMovementState = KunMovementState.Running;
						//kun.sendMessage("時間の流れが加速した！");
					} else if (kun.isSneaking() && kunMovementState != KunMovementState.Sneaking) {
						world.getEntities().stream()
							.filter(e -> !SuperHotPluginHelper.isKun(e))
							.forEach(SuperHotPluginHelper::decelerate);
						kunMovementState = KunMovementState.Sneaking;
						//kun.sendMessage("時間の流れが減速した！");
					} else if (!kun.isSprinting() && !kun.isSneaking() && kunMovementState != KunMovementState.Walking) {
						world.getEntities().stream()
							.filter(e -> !SuperHotPluginHelper.isKun(e))
							.forEach(SuperHotPluginHelper::release);
						kunMovementState = KunMovementState.Walking;
						//kun.sendMessage("時間の流れが元通りになった！");
					}
				} else if (!SuperHotPluginHelper.isKunMoving(kun) && kunMovementState != KunMovementState.Stopping) {
					world.getEntities().stream()
						.filter(e -> !SuperHotPluginHelper.isKun(e))
						.forEach(SuperHotPluginHelper::freeze);
					kunMovementState = KunMovementState.Stopping;
					//kun.sendMessage("時間の流れが止まった！");
				}
			}
			if (!SuperHotConfig.superHotEnabled)
				kunMovementState = KunMovementState.Disable;
			world.getEntities().stream()
				.forEach(e -> {
					if (e instanceof Snowball) {
						Snowball s0 = (Snowball) e;
						Snowball s1 = s0.getLocation().getNearbyEntitiesByType(Snowball.class, 1).stream()
							.findFirst().orElse(null);
						if (s1 != null && !s0.getUniqueId().toString().equalsIgnoreCase(s1.getUniqueId().toString())) {
							SuperHotPluginHelper.destroyBullet(s0, kun);
							SuperHotPluginHelper.destroyBullet(s1, kun);
						}
					} else if (e instanceof Item) {
						String name = e.getCustomName();
						if (name != null) {
							if (name.equalsIgnoreCase("throw")
								&& !e.getVelocity().equals(new Vector(0, 0, 0))) {

								Item i = (Item) e;
								Snowball s = i.getLocation().getNearbyEntitiesByType(Snowball.class, 1).stream()
									.findFirst().orElse(null);
								if (s != null) {
									SuperHotPluginHelper.destroyBullet(s, kun);
								}
								Player p = i.getLocation().getNearbyEntitiesByType(Player.class, 0.5).stream()
									.findFirst().orElse(null);
								if (p != null) {
									if (!SuperHotPluginHelper.isKun(p)) {
										p.setHealth(0);
										p.spawnParticle(Particle.BLOCK_CRACK, p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ(), 20, Material.REDSTONE_BLOCK.createBlockData());
									}
								}
							}
						}
					}
				});
		}
	}

	public enum KunMovementState {
		Running,
		Sneaking,
		Walking,
		Stopping,
		Disable
	}
}
