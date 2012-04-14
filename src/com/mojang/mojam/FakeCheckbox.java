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
import com.mojang.mojam.gui.components.Checkbox;
import com.mojang.mojam.screen.BitmapUtil;
import com.mojang.mojam.screen.MojamBitmap;

/**
 * 
 * Fake " Catacomb-Snatch " checkbox , uses an Bitmap-To-Image converter . 
 * Used for the MojamLauncher . 
 *
 */

public class FakeCheckbox extends JLabel {
	
	private Image bgimg;
	private Image[] imgs = new Image[4];
	
	private boolean selected = false;

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
					bgimg = imgs[selected?1:0];
					updateIcon();
				}
			}
      		
      		public void mouseReleased(MouseEvent arg0) {
      			if (arg0.getButton() == MouseEvent.BUTTON1) {
      				selected = !selected;
      				bgimg = imgs[selected?2:3];
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
	 * Empty checkbox
	 */
	
	public FakeCheckbox() {
		init("");
	}
	
	/**
	 * Initalizes the checkbox . 
	 * Made for saving code space . 
	 */
	private void init(String text) {
		Checkbox b = new Checkbox(-1, text, 0, 0);
		MojamBitmap[] bmps = b.getBitmaps();
		for (int i = 0; i < bmps.length; i++) { 
			imgs[i] = BitmapUtil.convert(bmps[i]);
		}
		bgimg = imgs[3];
		addBaseListeners();
		updateIcon();
	}
	
	/**
	 * Create an Fake Button .
	 * 
	 * @param str Title / Text 
	 */
	
	public FakeCheckbox(String str) {
		init(str);
	}
	
	public void setSelected(boolean b) { 
		selected = b;
		updateIcon();
	}
	
	public boolean isSelected() {
		return selected;
	}

}
