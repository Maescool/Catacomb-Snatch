package com.mojang.mojam.gui;

import java.awt.event.KeyEvent;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.network.packet.ChatPacket;
import com.mojang.mojam.screen.Screen;

public class GuiChatWindow {
	
	public MojamComponent component;
	
	public final int maxMessages = 12;
	public String[] messages;
	public int toDraw = 0;
	public int decreaseTimer = 0;
	public int decreaseTimerMax = 450;
	public boolean isActive;
	private String entry = "";
	long lastActive = 0;
	
	public GuiChatWindow(MojamComponent mojamComponent){
		this.component = mojamComponent;
		messages = new String[maxMessages];
	}
	
	public static String formatMessage(int playerId, String message){
		return MojamComponent.instance.players[playerId].name + ": "+ message;
	}
	
	public void sendPlayerMessage(int id, String message){
		if(component.isMultiplayer()){
			component.packetLink.sendPacket(new ChatPacket(component.player.id, message));
		}
		addMessage(formatMessage(id, message));
	}
	
	public void addMessage(String s){
		
		for (int i = messages.length-1; i > 0; i--) {
			messages[i] = messages[i-1];
		}
		messages[0] = s;
		toDraw++;
		if(toDraw > maxMessages) toDraw = maxMessages;
	}
	
	public void setActive(boolean flag){
		long curTime = System.currentTimeMillis();
		if(curTime - lastActive < 500) return;
		lastActive = curTime;
		
		isActive = flag;
		entry = "";
	}
	public boolean isActive(){
		return isActive;
	}
	
	public void onKeyPress(KeyEvent e){
		if(e.getKeyCode() == 10){
			if(!entry.equals("")){
				sendPlayerMessage(component.player.id, entry);
			}
			setActive(false);
		} else if(e.getKeyCode() == 8){
			if(entry.length() > 0){
				entry = entry.substring(0,entry.length()-1);
			}
		} else if(!e.isActionKey()){
			entry = entry + e.getKeyChar(); 	
		}
	}
	
	public void tick(){
		if(decreaseTimer > 0 && --decreaseTimer == 0){
			if(--toDraw > 0){
				decreaseTimer = decreaseTimerMax;
			}
		} else if(decreaseTimer == 0 && toDraw > 0){
			decreaseTimer = decreaseTimerMax;
		}
	}
	
	public void draw(Screen screen){
		Font.setFont("gray");
		for (int i = 0; i < toDraw; i++) {
			Font.draw(screen, messages[i], 8, MojamComponent.GAME_HEIGHT-80-i*10);
		}
		if(isActive()){
			Font.draw(screen, entry+(component.synchronizer.getLocalTick() % 16 < 8 ? "_" : ""), 8, MojamComponent.GAME_HEIGHT-70);
		}
		Font.setFont("");
	}
}
