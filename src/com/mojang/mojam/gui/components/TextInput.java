package com.mojang.mojam.gui.components;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import com.mojang.mojam.screen.AbstractScreen;
import com.mojang.mojam.screen.Art;

public class TextInput extends GuiComponent implements KeyListener {
	/** Default text input height */
	public static final int HEIGHT = 24;
	
	private int x,y,width;
	
	private int inputTick;
	private String label;
	
	private boolean isFixed = true;
	
	private boolean hasOverflow = false;
	private int overflow = 0;
	
	/**
	 * Creates a new text input component without any content
	 * 
	 * @param x the x-position
	 * @param y the y-position
	 * @param w the width of the input
	 */
	public TextInput(int x, int y, int w) {
		this("", x, y, w);
	}
	
	/**
	 * Creates a new text input component with content declared in 'c'
	 * 
	 * @param c the content of the text input
	 * @param x the x-position
	 * @param y the y-position
	 * @param w the width of the input
	 */
	public TextInput(String c, int x, int y, int w) {
		this.x = x;
		this.y = y;
		this.width = w;
		
		this.inputTick = 0;
		this.label = c;
	}
	
	@Override
	public void render(AbstractScreen screen) {
		inputTick++;
		if(inputTick > 80) inputTick = 0;
		
		screen.fill(getX() + 16, getY(), getWidth() - 16 * 2, HEIGHT, 0xff000000);
	    screen.blit(Art.slider[1][0], getX(), getY());
		screen.blit(Art.slider[1][1], getX() + getWidth() - 16, getY());
		
		Font.defaultFont().draw(
			screen,
			getRenderContent(),
			getX() + 4,
			getY()+((HEIGHT-Font.defaultFont().getFontHeight())/2)
		);
	}

	private String getRenderContent() {
		String ret;
		
		if(!isFixed && Font.defaultFont().getTextWidth(label + "|") > width-16 ) {
			hasOverflow = true;
			ret = label.substring(overflow);
		} else {
			hasOverflow = false;
			ret = label;
		}
		
		return ret + ((inputTick < 40) ? "|": "");
	}

	@Override
	public void keyPressed(KeyEvent e) { }

	@Override
	public void keyReleased(KeyEvent e) { }

	@Override
	public void keyTyped(KeyEvent e) {
		if (e.getKeyChar() == KeyEvent.VK_BACK_SPACE) {
			if(label.length() > 0)
				label = label.substring(0, label.length() - 1);
			
			if(hasOverflow) overflow--;
		} else if(!e.isActionKey()) {
			if(isFixed && Font.defaultFont().getTextWidth(label + "|") < width-16) {
				label += e.getKeyChar();
				System.out.println(true);
			} else if(!isFixed) {
				label += e.getKeyChar();
				if(hasOverflow) overflow++;
			}			
		}
	}
	
	/**
	 * Returns the content of the text input
	 * 
	 * @return String
	 */
	public String getContent() {
		return label;
	}
	
	/**
	 * Sets the content for the text input
	 * 
	 * @param String content
	 */
	public void setContent(String content) {
		this.label = content;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getWidth() {
		return width;
	}
	
	public void setFixed(boolean fixed) {
		this.isFixed = fixed;
	}
}
