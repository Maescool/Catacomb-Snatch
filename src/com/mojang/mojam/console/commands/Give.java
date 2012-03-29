package com.mojang.mojam.console.commands;

import java.util.HashMap;
import java.util.Map;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.console.Console.Command;
import com.mojang.mojam.entity.Player;
import com.mojang.mojam.entity.mob.Mob;
import com.mojang.mojam.entity.weapon.Cannon;
import com.mojang.mojam.entity.weapon.ElephantGun;
import com.mojang.mojam.entity.weapon.Flamethrower;
import com.mojang.mojam.entity.weapon.IWeapon;
import com.mojang.mojam.entity.weapon.Machete;
import com.mojang.mojam.entity.weapon.Melee;
import com.mojang.mojam.entity.weapon.Raygun;
import com.mojang.mojam.entity.weapon.Rifle;
import com.mojang.mojam.entity.weapon.Shotgun;
import com.mojang.mojam.entity.weapon.VenomShooter;

public class Give extends Command {

    public Map<Class<? extends IWeapon>, String> giveTexts = new HashMap<Class<? extends IWeapon>, String>();
    public Map<String, Class<? extends IWeapon>> weaponNames = new HashMap<String, Class<? extends IWeapon>>();

    public Give() {
	super("give", 1, "Gives a weapon or money", true);
	init();
    }

    private void init() {
	registerWeapon(Shotgun.class);
	registerWeapon(VenomShooter.class);
	registerWeapon(Raygun.class);
	registerWeapon(Rifle.class);
	registerWeapon(Melee.class, "fist", "Giving player a boxing lesson.");
	registerWeapon(Machete.class, "machete", "Giving player a huge knife.");
	registerWeapon(Cannon.class);
	registerWeapon(Flamethrower.class);
	registerWeapon(ElephantGun.class);
    }

    public void registerWeapon(Class<? extends IWeapon> weapon) {
	registerWeapon(weapon, weapon.getSimpleName().toLowerCase(),
		"Giving player a"
			+ ((weapon.getName().startsWith("e")) ? "n" : "") + " "
			+ weapon.getSimpleName().toLowerCase() + ".");
    }

    public void registerWeapon(Class<? extends IWeapon> weapon, String name,
	    String giving_text) {
	giveTexts.put(weapon, giving_text);
	weaponNames.put(name, weapon);
    }

    public void execute() {
	args[0] = args[0].trim().toLowerCase();
	for (Player player : MojamComponent.instance.players) {

	    if (weaponNames.containsKey(args[0])) {
		log(giveTexts.get(weaponNames.get(args[0])));
		try {
		    if (!player.weaponInventory.add(weaponNames.get(args[0])
			    .getDeclaredConstructor(Mob.class)
			    .newInstance(player))) {
			if (player.weaponInventory.weaponList
				.contains(weaponNames.get(args[0]))) {
			    log("You already have this item");
			} else {
			    log("You cannot hold more than "
				    + player.weaponInventory.size()
				    + " weapons.");
			}
		    }
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    } else if (args[0].equals("all")) {
		for (String s : weaponNames.keySet()) {
		    execute(new String[] { s });
		}
	    } else if (args[0].equals("help")) {
		log("Options:");
		for (String s : weaponNames.keySet()) {
		    log(">" + s + " (" + weaponNames.get(s).getSimpleName()
			    + ")");
		}

		/*
		 * log(">shotgun (Shotgun)"); log(">venom (VenomShooter)");
		 * log(">elephant (Elephant Gun)"); log(">fist (Melee)");
		 * log(">raygun (Raygun)"); log(">machete (Machete)");
		 * log(">cannon (Cannon)");
		 */
		log("Or you can use a numerical value to receive money.");
	    }
	    try {
		player.score += Integer.parseInt(args[0]);
	    } catch (NumberFormatException e) {

	    }
	}
    }

    public boolean canRunInGame() {
	return true;
    }

    public boolean canRunInMenu() {
	return false;
    }

}
