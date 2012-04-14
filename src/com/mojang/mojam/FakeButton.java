package com.mojang.mojam;

import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;

import com.mojang.mojam.gui.components.Button;
import com.mojang.mojam.screen.BitmapUtil;
import com.mojang.mojam.screen.MojamBitmap;

/**
 * 
 * Fake " Catacomb-Snatch " button , uses an Bitmap-To-Image converter . 
 * Used for the MojamLauncher . 
 * 
 * You do not need to port ActionListeners to MouseListeners , it does for you ;)
 *
 */

public class FakeButton extends JLabel {
	
	private MouseListener mouse1;
	
	private Image bgimg;
	private Image[] imgs = new Image[3];
	
	private int customwidth = -1;

	/**
	 * Forces Opaquicy .
	 */
	
	@Override
	public boolean isOpaque() {
		return false;
	}
	
	
	/**
	 * This method is an private method for adding the hovering and pressing listener(s) . 
	 */
	
	private void addBaseListeners() {
		MouseAdapter mouseListener = new MouseAdapter() {
			
			public void mouseEntered(MouseEvent arg0) {
			}
			
			public void mouseExited(MouseEvent arg0) {
			}
			
			public void mousePressed(MouseEvent arg0) {
	            if (arg0.getButton() == MouseEvent.BUTTON1) {
	            	bgimg = imgs[1];
					updateIcon();
	            }
			}
      		
      		public void mouseReleased(MouseEvent arg0) {
      			if (arg0.getButton() == MouseEvent.BUTTON1) {
      				bgimg = imgs[0];
      				updateIcon();
      			}
      		}
      	
		};
		
    	addMouseListener(mouseListener);
		addMouseMotionListener(mouseListener);
	}
	
	private void updateIcon() { 
		setIcon(new ImageIcon(bgimg));
	}
	
	/**
	 * Empty button
	 */
	
	public FakeButton() {
		init("");
	}
	
	/**
	 * Initalizes the button . 
	 * Made for saving code space . 
	 */
	private void init(String text) {
		int width = Button.BUTTON_WIDTH - 16;
		if (customwidth != -1) width = customwidth;
		Button b = new Button(-1, text, 0, 0, width, Button.BUTTON_HEIGHT);
		MojamBitmap[] bmps = b.getBitmaps();
		for (int i = 0; i < bmps.length; i++) { 
			imgs[i] = BitmapUtil.convert(bmps[i]);
		}
		bgimg = imgs[0];
		addBaseListeners();
		updateIcon();
	}
	
	/**
	 * Create an Fake Button .
	 * 
	 * @param str Title / Text 
	 */
	public FakeButton(String str) {
		init(str);
	}
	
	/**
	 * Create an Fake Button .
	 * 
	 * @param str Title / Text 
	 * @param width Button width
	 */
	public FakeButton(String str, int width) {
		customwidth = width;
		init(str);
	}


	/**
	 * 
	 * Adds an fake action listener . 
	 * It ports action listeners to mouse listeners , adds them later on .
	 * 
	 * @param newAction ActionListener to add
	 */
	
	public void addActionListener(final ActionListener newAction) {
		mouse1 = new MouseListener() {
			
      public void mouseClicked(MouseEvent arg0) {
    	  // Clicked is too buggy , lets some clicks through ... 
      }

			@Override
      public void mouseEntered(MouseEvent arg0) {
      }

			@Override
      public void mouseExited(MouseEvent arg0) {
      }

			@Override
      public void mousePressed(MouseEvent arg0) {
      }

			@Override
      public void mouseReleased(MouseEvent arg0) {
		if (arg0.getButton() == MouseEvent.BUTTON1) {
		    ActionEvent mouseToAction = new ActionEvent("MouseEvent Ported", arg0.getID(), "MouseEventPort "+arg0);
		    newAction.actionPerformed(mouseToAction);
		}
      }
			
		};
		addMouseListener(mouse1);
	}
	
	/**
	 * Removes last fake ActionListener .
	 */
	
	public void removeLastActionListener() {
		removeMouseListener(mouse1);
	}
	
	/**
	 * Frees Fake Button from all fake ActionListeners .
	 */
	
	public void removeAllActionListeners() {
		MouseListener[] mouseListeners = getMouseListeners(); 
		int removedListeners = 0;
		while (removedListeners != mouseListeners.length) {
			removeMouseListener(mouseListeners[removedListeners]);
			removedListeners = removedListeners + 1;
		}
	}
	
	/**
	 * Removes ActionListener NR. X
	 * @param listenerAt ActionListener NR. X
	 */
	
	public void removeActionListener(int listenerAt) {
		MouseListener[] mouseListeners = getMouseListeners(); 
	  removeMouseListener(mouseListeners[listenerAt]);
	}

}
