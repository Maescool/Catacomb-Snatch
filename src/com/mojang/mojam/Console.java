package com.mojang.mojam;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Date;

import com.mojang.mojam.entity.Player;
import com.mojang.mojam.entity.weapon.Cannon;
import com.mojang.mojam.entity.weapon.ElephantGun;
import com.mojang.mojam.entity.weapon.Machete;
import com.mojang.mojam.entity.weapon.Melee;
import com.mojang.mojam.entity.weapon.Raygun;
import com.mojang.mojam.entity.weapon.Rifle;
import com.mojang.mojam.entity.weapon.Shotgun;
import com.mojang.mojam.entity.weapon.VenomShooter;
import com.mojang.mojam.gui.TitleMenu;
import com.mojang.mojam.gui.components.Font;
import com.mojang.mojam.screen.Screen;

public class Console implements KeyListener {

	/***
	 * Maximum amount of verbose data kept in the console
	 * also the number of lines of data displayed
	 */
	public static final int MAX_LINES = 20;

	/***
	 * Maximum number of characters allowed to input into the console
	 */
	public static final int MAX_INPUT_LENGTH = 60;

	private ArrayList<String> verboseData = new ArrayList<String>(MAX_LINES);

	private String typing = "";
	private String input = null;
	private boolean completedInput;

	private boolean open;

	/***
	 * Left padding size when drawing console text
	 */
	public static final int xOffset = 5;

	/***
	 * Top padding size when drawing console text. affects console height
	 */
	public static final int yOffset = 5;

	public Console()
	{
		log("------------------------------------------------------------");//Deep magic lining it up
		log("|Catacomb Snatch Console v1.1                           |");
		log("|Type commands with a slash in front, like /this        |");
		log("|If in doubt, type /help                                  |");
		log("------------------------------------------------------------");
		log("");
	}


	/***
	 * Logs the verbose info into the console
	 * 
	 * @param s information to display in console
	 */
	public void log(String s) {
		if(s == null) return;

		if(verboseData.size() + 1 > MAX_LINES)
			verboseData.remove(verboseData.size() - 1);

		verboseData.add(0,s);
	}

	/***
	 * Closes the console and cancels current input
	 */
	public void close() {
		typing = "";
		input = null;
		completedInput = false;
		open = false;
	}

	/***
	 * Opens the console
	 */
	public void open() {
		open = true;
	}

	/***
	 * Toggles between open and close.
	 */
	public void toggle() {
		if(open)
			close();
		else
			open();
	}

	/***
	 * Tells if the console is open or not
	 * @return the answer
	 */
	public boolean isOpen() {
		return open;
	}

	/***
	 * renders the console on the screen if it is open
	 * screen space it takes up is (MAX_LINES+1) * Font.FONT_WHITE_SMALL + yOffset
	 * 
	 * @param s screen to render to
	 */
	public void render(Screen s) {
		if(open) {
			int fontHeight = Font.FONT_WHITE_SMALL.getFontHeight();
			int consoleHeight = (MAX_LINES + 1) * fontHeight + yOffset; //+1 for the input line

			s.alphaFill(0, 0, s.w, consoleHeight, 0xff000000, 0x80); //50% black,fixed from 0x50 (31.25%)

			Font.FONT_WHITE_SMALL.draw(s, typing + (((((int)(System.currentTimeMillis()/500))&1)==1)?"|":""), xOffset,(consoleHeight -= fontHeight)); //draws bottom up starting with typing

			for(int i = 0; i < verboseData.size(); i++) {
				Font.FONT_WHITE_SMALL.draw(s, verboseData.get(i), xOffset, (consoleHeight -= fontHeight) ); // and then the verbose data in order of newest first
			}
		}
	}

	/***
	 * checks if the user has inputed anything
	 * unnecessary if the console is closed
	 */
	public void tick() {
		if(completedInput) {
			processInput(input);
		}
	}

	private void processInput(String input) {
		log(">" + input);
		String command = getCommand(input);

		if(command.startsWith("/")) {
			doCommand(command, input);
		} else {
			chat.doCommand(new String[]{input});
		}

		completedInput = false;
	}

	private String getCommand(String input) {
		if(!input.contains(" ")) {
			return input;
		} else {
			return input.substring(0, input.indexOf(' '));
		}
	}

	/***
	 * Execute a console command
	 * if no command has that name nothing will be done
	 * 
	 * @param command command name
	 * @param input arguments for the command separated by spaces
	 */
	public void doCommand(String command, String input) {
		if(command.charAt(0) == '/')
			command = command.substring(1); //remove forward slash

		for(Command c : Command.commands) {

			if(c != null && c.name.equals(command)) {

				String[] args = getArgs(input,c.numberOfArgs);
				c.doCommand(args);
				return;
			}
		}
	}

	private String[] getArgs(String input, int numberOfArgs) {
		if(numberOfArgs == -1) { //see Command NumberOfArgs for reason
			if(!input.contains(" ")) {
				return new String[]{""};
			} else {
				return new String[]{removeCommand(input)};
			}
		}

		if(numberOfArgs <= 0) return null;

		String[] args = new String[numberOfArgs];
		input = removeCommand(input);
		if(numberOfArgs == 1) return new String[]{input};

		for(int i = 0; i < numberOfArgs; i++) {
			int index = input.indexOf(' ');

			if(index > 0) {
				args[i] = input.substring(0, index);
				input = input.substring(index+1);
			}
		}
		return args;
	}

	private String removeCommand(String input) {
		if(input.charAt(0) != '/') {
			return input;
		}
		if(!input.contains(" ")) {
			return input;
		}
		return input.substring(input.indexOf(' ') + 1);
	}

	public void keyTyped(KeyEvent e) {
		if(open) {
			switch(e.getKeyCode()) {
			case KeyEvent.VK_ESCAPE:
			case KeyEvent.VK_ENTER:
				break;
			case KeyEvent.VK_BACK_SPACE:
				if(typing.length() > 0)
					typing = typing.substring(0, typing.length()-1);
				break;
			default:
				if(typing.length() < MAX_INPUT_LENGTH)
					typing += e.getKeyChar();	
				break;
			}
		}
	}

	public void keyPressed(KeyEvent e) {
		if(open) {
			if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
				if(typing.length() > 0)
					typing = typing.substring(0, typing.length()-1);
			}
		}
	}

	public void keyReleased(KeyEvent e) {
		if(open) {
			switch(e.getKeyCode()) {
			case KeyEvent.VK_ESCAPE:
				typing = "";
				input = null;
				break;
			case KeyEvent.VK_ENTER:
				typing = typing.trim();
				if(!typing.equals("")) {
					input = typing;
					completedInput = true;
				}
				typing = "";
				break;
			case KeyEvent.VK_BACK_SPACE:
				if(typing.length() > 0)
					typing = typing.substring(0, typing.length()-1);
				break;
			}
		}
	}

	/***
	 * List of possible commands
	 */
	private Command help = new Command("help", 0, "Displays all possible commands") {
		public void doCommand(String[] args) {
			log("All Commands");
			log("--------------");
			for(int i = 0; i < Command.commands.size(); i++) {
				Command c = Command.commands.get(i);
				if(c != null)
					log(c.name + " : " + c.helpMessage);
			}
		}
	};

	private Command pause = new Command("pause", 0, "Pauses the game") {
		public void doCommand(String[] args) {
			close();
			MojamComponent.instance.synchronizer.addCommand(new com.mojang.mojam.network.PauseCommand(true));
		}
	};

	private Command exit = new Command("exit", 1, "exits the game. 0 force exit, 1 regular game exit") {
		public void doCommand(String[] args) {
			if(args.length > 0 && args[0].equals("0"))
				System.exit(0);
			else
				MojamComponent.instance.stop(false);
		}
	};

	private Command chat = new Command("chat", -1, "Does the same as pressing T and typing in the after /chat and pressing enter") {
		public void doCommand(String[] args) {
			String msg = "";
			for(int i = 0; i < args.length-1; i++) {
				msg += args[i] + " ";
			}
			msg += args[args.length-1];
			MojamComponent.instance.synchronizer.addCommand(new com.mojang.mojam.network.packet.ChatCommand(msg));
		}
	};

	public Command load = new Command("load", 1, "Loads a map by name")
	{
		@Override
		public void doCommand(String[] args)
		{
			log("Loading map " + args[0]);
			log("Incomplete");
			//TitleMenu.level = new LevelInformation(args[0], "/levels/" + args[0].replace('_', ' ') + ".bmp", true);
			//TODO: Needs MUCH work
			//MojamComponent.instance.handleAction(TitleMenu.START_GAME_ID);
		}
	};
	public Command lang = new Command("lang", 1, "Sets the language")
	{
		@Override
		public void doCommand(String[] args)
		{
			if(args[0].equals("help"))
			{
				log("Enter your two letter language code, e.g. /lang af -> Afrikaans, /lang it -> Italiano");
			}				
			else 
			{
				MojamComponent.instance.setLocale(args[0]);
			}
		}
	};
	public Command menu = new Command("menu", 0, "Return to menu")
	{
		@Override
		public void doCommand(String[] args)
		{
			MojamComponent.instance.handleAction(TitleMenu.RETURN_TO_TITLESCREEN);
		}
	};

	public Command allweapons = new Command("allweapons", 1, "Gives all weapons")
	{
		@Override
		public void doCommand(String[] args)
		{
			args[0].trim();
			Player player = MojamComponent.instance.player;
			int i;
		
				log("Giving player a shotgun");
				if(!player.weaponInventory.add(new Shotgun(player))) {
		        	log("You already have this item.");
		    	}
		
				log("Giving player a rifle");
				if(!player.weaponInventory.add(new Rifle(player))) {
		        	log("You already have this item.");
		    	}
		
				log("Giving player a veonomshooter");
				if(!player.weaponInventory.add(new VenomShooter(player))) {
		        	log("You already have this item.");
		    	}
		
				log("Giving player an elephant gun");
				if(!player.weaponInventory.add(new ElephantGun(player))) {
		        	log("You already have this item.");
		    	}
		
				log("Giving player a lesson in boxing");
				if(!player.weaponInventory.add(new Melee(player))) {
		        	log("You already have this item.");
		    	}
		
				log("Giving player a raygun");
				if(!player.weaponInventory.add(new Raygun(player))) {
		        	log("You already have this item.");
		    	}
		
				log("Giving player a machete");
				if(!player.weaponInventory.add(new Machete(player))) {
		        	log("You already have this item.");
		    	}
		
				log("Giving player a cannon!");
				if(!player.weaponInventory.add(new Cannon(player))) {
		        	log("You already have this item.");
		    	}
		}
	};

	
	public Command give = new Command("give", 1, "Gives a weapon")
	{
		@Override
		public void doCommand(String[] args)
		{
			args[0].trim();
			Player player = MojamComponent.instance.player;
			int i;
			if(args[0].toLowerCase().equals("shotgun"))
			{
				log("Giving player a shotgun");
				if(!player.weaponInventory.add(new Shotgun(player))) {
		        	log("You already have this item.");
		    	}
			}
			else if(args[0].toLowerCase().equals("rifle"))
			{
				log("Giving player a rifle");
				if(!player.weaponInventory.add(new Rifle(player))) {
		        	log("You already have this item.");
		    	}
			}
			else if(args[0].toLowerCase().equals("venom"))
			{
				log("Giving player a veonomshooter");
				if(!player.weaponInventory.add(new VenomShooter(player))) {
		        	log("You already have this item.");
		    	}
			}
			else if(args[0].toLowerCase().equals("elephant"))
			{
				log("Giving player an elephant gun");
				if(!player.weaponInventory.add(new ElephantGun(player))) {
		        	log("You already have this item.");
		    	}
			}
			else if(args[0].toLowerCase().equals("fist"))
			{
				log("Giving player a lesson in boxing");
				if(!player.weaponInventory.add(new Melee(player))) {
		        	log("You already have this item.");
		    	}
			}
			else if(args[0].toLowerCase().equals("raygun"))
			{
				log("Giving player a raygun");
				if(!player.weaponInventory.add(new Raygun(player))) {
		        	log("You already have this item.");
		    	}
			}
			else if(args[0].toLowerCase().equals("machete"))
			{
				log("Giving player a machete");
				if(!player.weaponInventory.add(new Machete(player))) {
		        	log("You already have this item.");
		    	}
			}
			else if(args[0].toLowerCase().equals("cannon"))
			{
				log("Giving player a cannon!");
				if(!player.weaponInventory.add(new Cannon(player))) {
		        	log("You already have this item.");
		    	}
			}
			else if(args[0].toLowerCase().equals("help"))
			{
				log("Options:");
				log(">rifle (Rifle)");
				log(">shotgun (Shotgun)");
				log(">venom (VenomShooter)");
				log(">elephant (Elephant Gun)");
				log(">fist (Melee)");
				log(">raygun (Raygun)");
				log(">machete (Machete)");
				log(">cannon (Cannon)");
				log("Or you can use a numerical value to receive money.");
			}
			try{
				player.score+=Integer.parseInt(args[0]);
			}catch (NumberFormatException e)
			{

			}
		}
	};
	Command time = new Command("time", 0, "Show the current time"){
		@Override
		public void doCommand(String[] s)
		{
			log(new Date(System.currentTimeMillis()).toString());
		}
	};
	Command cooldown = new Command("cool", 1, "Cools the currently held weapon to a certain value"){
		@Override
		public void doCommand(String[] s)
		{
			try	{
				int i = Integer.parseInt(s[0].trim());
				log("Cooling weapon from " + i + " centispecks.");
				for(;i>0;i--)
				{
					MojamComponent.instance.player.weapon.weapontick();
				}
			} catch (NumberFormatException e)
			{
				log("Cooling weapon");
				int i = 600;
				for(;i>0;i--)
				{
					MojamComponent.instance.player.weapon.weapontick();
				}
			}
		}
	};

	public abstract static class Command {

		public String name;
		public String helpMessage;
		public int numberOfArgs; //-1 args means return raw input data minus the command
		public static ArrayList<Command> commands = new ArrayList<Command>();

		public Command(String name, int numberOfArgs, String helpMessage) {
			this.name = name;
			this.numberOfArgs = numberOfArgs;
			this.helpMessage = helpMessage;
			commands.add(this);
		}

		public abstract void doCommand(String[] args);
	}

}