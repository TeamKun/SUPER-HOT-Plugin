package net.kunmc.lab.superhotplugin.helper;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SuperHotPluginHelper {
	private static double lastKunXPos = 0;
	private static double lastKunZPos = 0;
	private static Map<UUID, Vector> projectileVelocity = new HashMap<>();
	private static UUID ACCELERATION_ID = UUID.randomUUID();
	private static UUID DECELERATION_ID = UUID.randomUUID();
	private static AttributeModifier ACCELERATION = new AttributeModifier(ACCELERATION_ID, "Accelerate entity", 1.5D, AttributeModifier.Operation.MULTIPLY_SCALAR_1);
	private static AttributeModifier DECELERATION = new AttributeModifier(DECELERATION_ID, "Decelerate entity", 0.5D, AttributeModifier.Operation.MULTIPLY_SCALAR_1);
	public static String clockHolder;

	public static void freeze(Entity entity) {
		entity.setGravity(false);
		if (entity instanceof LivingEntity) {
			LivingEntity living = (LivingEntity) entity;
			living.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).removeModifier(ACCELERATION);
			living.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).removeModifier(DECELERATION);
			living.setAI(false);
		} else if (entity instanceof Projectile) {
			Projectile projectile = (Projectile) entity;
			projectileVelocity.put(projectile.getUniqueId(), projectile.getVelocity());
			projectile.setVelocity(new Vector().zero());
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
		} else if (entity instanceof Projectile) {
			Projectile projectile = (Projectile) entity;
			projectile.setVelocity(projectile.getVelocity().multiply(1.5D));
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
		} else if (entity instanceof Projectile) {
			Projectile projectile = (Projectile) entity;
			projectile.setVelocity(projectile.getVelocity().multiply(0.5D));
		}
	}

	public static void release(Entity entity) {
		entity.setGravity(true);
		if (entity instanceof LivingEntity) {
			LivingEntity living = (LivingEntity) entity;
			living.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).removeModifier(ACCELERATION);
			living.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).removeModifier(DECELERATION);
			living.setAI(true);
		} else if (entity instanceof Projectile) {
			Projectile projectile = (Projectile) entity;
			try {
				projectile.setVelocity(projectileVelocity.get(projectile.getUniqueId()));
			} catch (IllegalArgumentException e) {
				System.out.println(e.toString());
			}
		}
	}

	public static boolean isKun(Entity entity) {
		return entity.getName().equalsIgnoreCase("Shojo_Virgim");
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
