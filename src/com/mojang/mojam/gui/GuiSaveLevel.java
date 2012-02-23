package com.mojang.mojam.gui;

import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.level.Level;
import com.mojang.mojam.level.LevelInformation;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Bitmap;
import com.mojang.mojam.screen.Screen;

public class GuiSaveLevel extends GuiMenu {

	private Button saveButton;
	private Level toSave;
	private Bitmap minimap;
	
	public GuiSaveLevel(){
		addButton(new Button(TitleMenu.POP_MENU, "Cancel", 24, 330));
		saveButton = new Button(-1, "Save Local", 152, 330);
		
		addButton(saveButton);
		
		toSave = MojamComponent.instance.level;
		minimap = toSave.getInfo().getButtonMinimap();
	}
	
	public void performSave(Level level){
		System.out.println("Saving local map: "+level.getInfo().getNameRaw());
		BufferedImage output = level.createBMP();
		File outputFile = new File(toSave.getInfo().getPath(false));
		if(!outputFile.exists()){
			outputFile.mkdirs();
		} else {
			int i = 0;
			int j = outputFile.getPath().lastIndexOf(".");
			String path = outputFile.getPath().substring(0,j);
			do {
				if(i++ > 100) {
					MojamComponent.instance.showError("Map save error");
					return;
				}
				outputFile = new File(path+"_"+i+".bmp");
			} while(outputFile.exists());
			outputFile.mkdirs();
		}
		System.out.println("  path:"+outputFile.getPath());
	    try {
			ImageIO.write(output, "png", outputFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		MojamComponent.instance.handleAction(TitleMenu.POP_MENU);
	}
	
	public void render(Screen screen) {
		screen.blit(Art.emptyBackground, 0, 0);
		int leftMargin = 48;
		Font.draw(screen, "Save level data", leftMargin/2, 24);
		Font.draw(screen, "Name:", leftMargin, 40);
		Font.draw(screen, "Author:", leftMargin, 50);
		Font.draw(screen, "Desc:", leftMargin, 60);
		Font.setFont("blue");
		LevelInformation li = toSave.getInfo();
		Font.draw(screen, li.getName(), 120, 40);
		Font.draw(screen, li.levelAuthor, 120, 40);
		Font.draw(screen, li.levelDescription, 120, 40);
		Font.setFont("");
		if(minimap != null){
			screen.blit(minimap, leftMargin, 80);
		}
		super.render(screen);
	}
	
	@Override
	public void buttonPressed(ClickableComponent cc) {
		Button button = (Button) cc;
		if(button.getId() == -1){
			performSave(toSave);
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			MojamComponent.instance.handleAction(TitleMenu.POP_MENU);
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		
	}

}
