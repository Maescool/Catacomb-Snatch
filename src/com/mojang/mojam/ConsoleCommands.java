package com.mojang.mojam;

import java.util.Date;

import com.mojang.mojam.Console.Command;
import com.mojang.mojam.entity.weapon.ElephantGun;
import com.mojang.mojam.entity.weapon.Rifle;
import com.mojang.mojam.entity.weapon.Shotgun;
import com.mojang.mojam.entity.weapon.VenomShooter;
import com.mojang.mojam.gui.TitleMenu;
import com.mojang.mojam.level.LevelInformation;

public class ConsoleCommands
{
	public static Command load = new Command("load", 1, "Loads a map by name")
	{
		@Override
		public void doCommand(String[] args)
		{
			Snatch.getConsole().log("Loading map " + args[0]);
			TitleMenu.level = new LevelInformation(args[0], "/levels/" + args[0].replace('_', ' ') + ".bmp", true);
			//TitleMenu.defaultGameMode = new GameModeVanilla();
			
			//MojamComponent.localTeam = Team.Team1;
			/*Snatch.getMojam().synchronizer = new TurnSynchronizer(Snatch.getMojam(), null, 0, 1);
			Snatch.getMojam().synchronizer.setStarted(true);*/
			
			Snatch.getMojam().handleAction(TitleMenu.START_GAME_ID);
		}
	};
	public static Command lang = new Command("lang", 1, "Sets the language")
	{
		@Override
		public void doCommand(String[] args)
		{
			Snatch.getMojam().setLocale(args[0]);
		}
	};
	public static Command menu = new Command("menu", 0, "Return to menu")
	{
		@Override
		public void doCommand(String[] args)
		{
			Snatch.getMojam().handleAction(TitleMenu.RETURN_TO_TITLESCREEN);
		}
	};
	public static Command give = new Command("give", 1, "Gives a weapon")
	{
		@Override
		public void doCommand(String[] args)
		{
			if(args[0].toLowerCase().equals("shotgun"))
			{
				Snatch.getConsole().log("Giving player a shotgun");
				Snatch.getMojam().player.weapon = new Shotgun(Snatch.getMojam().player);
			}
			else if(args[0].toLowerCase().equals("rifle"))
			{
				Snatch.getConsole().log("Giving player a rifle");
				Snatch.getMojam().player.weapon = new Rifle(Snatch.getMojam().player);
			}
			else if(args[0].toLowerCase().equals("venom"))
			{
				Snatch.getConsole().log("Giving player a veonomshooter");
				Snatch.getMojam().player.weapon = new VenomShooter(Snatch.getMojam().player);
			}
			else if(args[0].toLowerCase().equals("elegun"))
			{
				Snatch.getConsole().log("Giving player an elephant gun");
				Snatch.getMojam().player.weapon = new ElephantGun(Snatch.getMojam().player);
			}
			else if(args[0].toLowerCase().equals("help"))
			{
				Snatch.getConsole().log("Options:");
				Snatch.getConsole().log(">rifle (Rifle)");
				Snatch.getConsole().log(">shotgun (Shotgun)");
				Snatch.getConsole().log(">venom (VenomShooter)");
				Snatch.getConsole().log(">elegun (Elephant Gun)");
			}
		}
	};
	Command time = new Command("time", 0, "Show the current time"){
		@Override
		public void doCommand(String[] s)
		{
			Snatch.getConsole().log(new Date(System.currentTimeMillis()).toString());
		}
	};
}
