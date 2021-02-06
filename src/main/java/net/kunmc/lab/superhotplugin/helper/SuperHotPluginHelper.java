package net.kunmc.lab.superhotplugin.helper;

import net.kunmc.lab.superhotplugin.SuperHotPlugin;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SuperHotPluginHelper {
	private static double lastKunXPos = 0;
	private static double lastKunZPos = 0;
	private static Map<UUID, Vector> projectileVelocity = new HashMap<>();
	private static Map<UUID, Vector> fireballDirection = new HashMap<>();
	private static UUID ACCELERATION_ID = UUID.randomUUID();
	private static UUID DECELERATION_ID = UUID.randomUUID();
	private static AttributeModifier ACCELERATION = new AttributeModifier(ACCELERATION_ID, "Accelerate entity", 0.5D, AttributeModifier.Operation.MULTIPLY_SCALAR_1);
	private static AttributeModifier DECELERATION = new AttributeModifier(DECELERATION_ID, "Decelerate entity", -0.5D, AttributeModifier.Operation.MULTIPLY_SCALAR_1);
	public static String clockHolder;
	private static Map<UUID, Integer> fireballCounter = new HashMap<>();

	public static void freeze(Entity entity) {
		entity.setGravity(false);
		if (entity instanceof LivingEntity) {
			LivingEntity living = (LivingEntity) entity;
			living.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).removeModifier(ACCELERATION);
			living.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).removeModifier(DECELERATION);
			living.setAI(false);
			if (living instanceof Player) {
				Player player = (Player) living;
				player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).removeModifier(ACCELERATION);
				player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).removeModifier(DECELERATION);
			}
		} else if (entity instanceof Projectile) {
			Projectile projectile = (Projectile) entity;
			projectileVelocity.put(projectile.getUniqueId(), projectile.getVelocity());
			projectile.setVelocity(new Vector().zero());
			if (projectile instanceof Fireball) {
				Fireball fireball = (Fireball) projectile;
				fireballDirection.put(fireball.getUniqueId(), fireball.getDirection());
			}
		}
	}

	public static void accelerate(Entity entity) {
		entity.setGravity(true);
		if (entity instanceof LivingEntity) {
			LivingEntity living = (LivingEntity) entity;
			living.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).removeModifier(ACCELERATION);
			living.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).removeModifier(DECELERATION);
			living.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).addModifier(ACCELERATION);
			living.setAI(true);
			if (living instanceof Player) {
				Player player = (Player) living;
				player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).removeModifier(ACCELERATION);
				player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).removeModifier(DECELERATION);
				player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).addModifier(ACCELERATION);
			}
		} else if (entity instanceof Projectile) {
			Projectile projectile = (Projectile) entity;
			projectile.setVelocity(projectile.getVelocity().multiply(1.5D));
			if (projectile instanceof Fireball) {
				if (projectile instanceof Fireball) {
					Fireball fireball = (Fireball) projectile;
					try {
						fireball.setVelocity(fireballDirection.get(fireball.getUniqueId()).multiply(1.5D));
					} catch (IllegalArgumentException e) {
						System.out.println(e.toString());
					}
				}
			}
		}
	}

	public static void decelerate(Entity entity) {
		entity.setGravity(true);
		if (entity instanceof LivingEntity) {
			LivingEntity living = (LivingEntity) entity;
			living.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).removeModifier(ACCELERATION);
			living.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).removeModifier(DECELERATION);
			living.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).addModifier(DECELERATION);
			living.setAI(true);
			if (living instanceof Player) {
				Player player = (Player) living;
				player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).removeModifier(ACCELERATION);
				player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).removeModifier(DECELERATION);
				player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).addModifier(DECELERATION);
			}
		} else if (entity instanceof Projectile) {
			Projectile projectile = (Projectile) entity;
			projectile.setVelocity(projectile.getVelocity().multiply(0.5D));
			if (projectile instanceof Fireball) {
				if (projectile instanceof Fireball) {
					Fireball fireball = (Fireball) projectile;
					try {
						fireball.setVelocity(fireballDirection.get(fireball.getUniqueId()).multiply(0.5D));
					} catch (IllegalArgumentException e) {
						System.out.println(e.toString());
					}
				}
			}
		}
	}

	public static void release(Entity entity) {
		entity.setGravity(true);
		if (entity instanceof LivingEntity) {
			LivingEntity living = (LivingEntity) entity;
			living.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).removeModifier(ACCELERATION);
			living.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).removeModifier(DECELERATION);
			living.setAI(true);
			if (living instanceof Player) {
				Player player = (Player) living;
				player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).removeModifier(ACCELERATION);
				player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).removeModifier(DECELERATION);
			}
		} else if (entity instanceof Projectile) {
			Projectile projectile = (Projectile) entity;
			try {
				projectile.setVelocity(projectileVelocity.get(projectile.getUniqueId()));
			} catch (IllegalArgumentException e) {
				System.out.println(e.toString());
			}
			if (projectile instanceof Fireball) {
				if (projectile instanceof Fireball) {
					Fireball fireball = (Fireball) projectile;
					try {
						fireball.setVelocity(fireballDirection.get(fireball.getUniqueId()));
					} catch (IllegalArgumentException e) {
						System.out.println(e.toString());
					}
				}
			}
		}
	}

	public static void freezeFireBall(Fireball fireball) {
		UUID uuid = fireball.getUniqueId();
		int counter = 0;
		if (fireballCounter.containsKey(fireball.getUniqueId())) {
			counter = fireballCounter.get(uuid) + 1;
		}
		fireballCounter.put(uuid, counter);
		System.out.println(fireballCounter.values());
		if (fireballCounter.get(uuid) % 2 == 0) {
			fireball.setVelocity(new Vector(1, 0, 0));
		} else {
			fireball.setVelocity(new Vector(-1, 0, 0));
		}
	}

	public static boolean isKun(Entity entity) {
		return entity.getName().equalsIgnoreCase(SuperHotPlugin.config.getString("timeFreezer"));
	}

	public static boolean isKunMoving(Player kun) {
		Double currentXPos = kun.getLocation().getX();
		Double currentZPos = kun.getLocation().getZ();
		int diffX = currentXPos.compareTo(lastKunXPos);
		int diffZ = currentZPos.compareTo(lastKunZPos);
		lastKunXPos = currentXPos;
		lastKunZPos = currentZPos;
		return diffX != 0 || diffZ != 0;
	}

	public static boolean isClockHolder(Entity entity) {
		return entity.getName().equalsIgnoreCase(clockHolder);
	}
}
