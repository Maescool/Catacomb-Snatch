package com.mojang.mojam.gui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.MouseButtons;
import com.mojang.mojam.screen.Screen;

public abstract class GuiMenu extends GuiComponent implements ButtonListener, KeyListener {

	protected List<ClickableComponent> buttons = new ArrayList<ClickableComponent>();
	protected List<Text> texts = new ArrayList<Text>();
	protected int selectedItem = 0;

	protected ClickableComponent addButton(ClickableComponent button) {
		buttons.add(button);
		button.addListener(this);
		return button;
	}
	
    protected ClickableComponent removeButton(ClickableComponent button) {
        if (buttons.remove(button)) {
            return button;
        }
        else {
            return null;
        }
    }

    protected Text addText(Text text) {
		texts.add(text);
		return text;
	}
    
    protected Text removeText(Text text) {
        if (texts.remove(text)) {
            return text;
        }
        else {
            return null;
        }
    }
    
	@Override
	public void render(Screen screen) {
		super.render(screen);

		for (ClickableComponent button : buttons) {
			button.render(screen);
		}
		for (Text text : texts) {
			text.render(screen);
		}
	}

	@Override
	public void tick(MouseButtons mouseButtons) {
		super.tick(mouseButtons);

		for (ClickableComponent button : buttons) {
			button.tick(mouseButtons);
		}
	}

	public void addButtonListener(ButtonListener listener) {
		for (ClickableComponent button : buttons) {
			button.addListener(listener);
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
			selectedItem--;
			if (selectedItem < 0) {
				selectedItem = buttons.size() - 1;
			}
		} else if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
			selectedItem++;
			if (selectedItem > buttons.size() - 1) {
				selectedItem = 0;
			}
		} else if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
			ClickableComponent button = buttons.get(selectedItem);
			if (button instanceof Slider) {
				Slider slider = (Slider) button;
				float value = slider.value - 0.1f;
				if (value < 0) {
					value = 0;
				}
				slider.setValue(value);
				slider.postClick();
			}
		} else if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
			ClickableComponent button = buttons.get(selectedItem);
			if (button instanceof Slider) {
				Slider slider = (Slider) button;
				float value = slider.value + 0.1f;
				if (value > 1) {
					value = 1;
				}
				slider.setValue(value);
				slider.postClick();
			}
		} else if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_E) {
			e.consume();
			ClickableComponent button = buttons.get(selectedItem);
			if (button instanceof Slider) {
				Slider slider = (Slider) button;
				if (slider.value == 1) {
					slider.setValue(0);
				} else {
					slider.setValue(1);
				}
			}
			button.postClick();
		} else if (e.getKeyCode() == KeyEvent.VK_F11) {
			MojamComponent.toggleFullscreen();
		}
	}
	
	@Override
	public void buttonHovered(ClickableComponent clickableComponent) {
		selectedItem = buttons.indexOf(clickableComponent);
	}
}
