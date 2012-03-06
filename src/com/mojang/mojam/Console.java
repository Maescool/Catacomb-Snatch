package com.mojang.mojam;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import com.mojang.mojam.gui.Font;
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
			
			s.alphaFill(0, 0, s.w, consoleHeight, 0xff000000, 0x50); //50% black I believe. took it from PauseMenu and changed 0x30 to 0x50
			
			Font.FONT_WHITE_SMALL.draw(s, typing + "|", xOffset,(consoleHeight -= fontHeight)); //draws bottom up starting with typing
			
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