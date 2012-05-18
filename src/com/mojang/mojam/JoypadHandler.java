package com.mojang.mojam;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.lwjgl.input.Controller;
import org.lwjgl.input.Controllers;

import com.mojang.mojam.Keys.Key;
import com.mojang.mojam.gui.AJoyBindingsMenu;
import com.mojang.mojam.gui.JoyBindingsMenu;
import com.mojang.mojam.mod.ModSystem;

/**
 * Class to change Gamepad / Joystick input to Keys .
 */
public class JoypadHandler {
	
	public final class DummyController implements Controller {

		@Override
		public int getAxisCount() {
			//DUMMY
			return 0;
		}

		@Override
		public String getAxisName(int arg0) {
			//DUMMY
			return "DUMMY";
		}

		@Override
		public float getAxisValue(int arg0) {
			//DUMMY
			return 0;
		}

		@Override
		public int getButtonCount() {
			//DUMMY
			return 0;
		}

		@Override
		public String getButtonName(int arg0) {
			//DUMMY
			return "DUMMY";
		}

		@Override
		public float getDeadZone(int arg0) {
			//DUMMY
			return 0;
		}

		@Override
		public int getIndex() {
			//DUMMY
			return -1;
		}

		@Override
		public String getName() {
			//DUMMY
			return "DUMMY";
		}

		@Override
		public float getPovX() {
			//DUMMY
			return 0;
		}

		@Override
		public float getPovY() {
			//DUMMY
			return 0;
		}

		@Override
		public float getRXAxisDeadZone() {
			//DUMMY
			return 0;
		}

		@Override
		public float getRXAxisValue() {
			//DUMMY
			return 0;
		}

		@Override
		public float getRYAxisDeadZone() {
			//DUMMY
			return 0;
		}

		@Override
		public float getRYAxisValue() {
			//DUMMY
			return 0;
		}

		@Override
		public float getRZAxisDeadZone() {
			//DUMMY
			return 0;
		}

		@Override
		public float getRZAxisValue() {
			//DUMMY
			return 0;
		}

		@Override
		public float getXAxisDeadZone() {
			//DUMMY
			return 0;
		}

		@Override
		public float getXAxisValue() {
			//DUMMY
			return 0;
		}

		@Override
		public float getYAxisDeadZone() {
			//DUMMY
			return 0;
		}

		@Override
		public float getYAxisValue() {
			//DUMMY
			return 0;
		}

		@Override
		public float getZAxisDeadZone() {
			//DUMMY
			return 0;
		}

		@Override
		public float getZAxisValue() {
			//DUMMY
			return 0;
		}

		@Override
		public boolean isButtonPressed(int arg0) {
			//DUMMY
			return false;
		}

		@Override
		public void poll() {
			//DUMMY
		}

		@Override
		public void setDeadZone(int arg0, float arg1) {
			//DUMMY
		}

		@Override
		public void setRXAxisDeadZone(float arg0) {
			//DUMMY
		}

		@Override
		public void setRYAxisDeadZone(float arg0) {
			//DUMMY
		}

		@Override
		public void setRZAxisDeadZone(float arg0) {
			//DUMMY
		}

		@Override
		public void setXAxisDeadZone(float arg0) {
			//DUMMY
		}

		@Override
		public void setYAxisDeadZone(float arg0) {
			//DUMMY
		}

		@Override
		public void setZAxisDeadZone(float arg0) {
			//DUMMY
		}

	}

	public final static class Button {
		public final String name;
		public final Controller controller;
		public final int id;
		public Keys.Key simulKey;
		public boolean nextState = false;
		public boolean wasDown = false;
		public boolean isDown = false;

		public Button(String name, Controller controller, int id) {
			this.name = name;
			this.controller = controller;
			this.id = id;
			
			String skn = Options.get("joyb_"+controller.getIndex()+"_"+id);
			if (skn != null) {
				try {
					Keys keys = MojamComponent.instance.keys;
					for (Keys.Key key : keys.getAll()) {
						if (simulKey != null) continue;
						if (key.name.equals(skn)) {
							simulKey = key;
						}
					}
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
			}
		}

		public void tick() {
			wasDown = isDown;
			isDown = nextState;
		}

		public boolean wasPressed() {
			return !wasDown && isDown;
		}

		public boolean wasReleased() {
			return wasDown && !isDown;
		}

		public void release() {
			nextState = false;
		}
	}
	
	public final static class Axis {
		public final String name;
		public final Controller controller;
		public final int id;
		public float firstState = -2;
		public float nextState = 0;
		public float currState = 0;
		public float lastState = 0;

		public Axis(String name, Controller controller, int id) {
			this.name = name;
			this.controller = controller;
			this.id = id;
		}

		public void tick() {
			lastState = currState;
			currState = nextState;
		}

		public boolean wasAt(float f) {
			return currState != f && lastState == f;
		}
		
		public boolean isAt(float f) {
			return currState == f && lastState != f;
		}

		public void update(float f) {
			nextState = f;
		}
	}
	
	public Controller controller;

	public ArrayList<Object> butaxes;

	public int buttonCount;
	public int axisCount;
	public int itemCount;
	
	public JoypadHandler(int index) {
		controller = Controllers.getController(index);

		buttonCount = controller.getButtonCount();
		axisCount = controller.getAxisCount();
		itemCount = controller.getButtonCount() + controller.getAxisCount() + 2;

		for (int i=0;i<controller.getButtonCount();i++) {
			addButton(new Button(controller.getButtonName(i), controller, i));
		}
		for (int i=buttonCount;i<buttonCount+controller.getAxisCount();i++) {
			addAxis(new Axis(controller.getAxisName(i-buttonCount), controller, i));
		}

		int i = itemCount - 2;
		addAxis(new Axis("POV X", controller, i));

		i = itemCount - 1;
		addAxis(new Axis("POV Y", controller, i));
	}

	public JoypadHandler() {
		// DUMMY for using type-classes
	}

	public void updateDetails() {
		for (int i=0;i<buttonCount;i++) {
			if (controller.isButtonPressed(i)) {
				if (butaxes.get(i) instanceof Button) {
					toggleButton((Button)butaxes.get(i), true);
				}
			} else if (!controller.isButtonPressed(i)) {
				if (butaxes.get(i) instanceof Button) {
					toggleButton((Button)butaxes.get(i), false);
				}
			}
		}
		for (int i=buttonCount;i<buttonCount+controller.getAxisCount();i++) {
			if (butaxes.get(i) instanceof Axis) {
				toggleAxis((Axis)butaxes.get(i), controller.getAxisValue(i-buttonCount));
			}
		}

		toggleAxis((Axis)butaxes.get(itemCount-2), controller.getPovX());
		toggleAxis((Axis)butaxes.get(itemCount-1), controller.getPovY());
	}
	
	public void addButton(Button b) {
		addButton(b, b.id);
	}
	
	public void addButton(Button b, int i) {
		if (butaxes == null) {
			butaxes = new ArrayList<Object>(itemCount);
		}
		butaxes.add(i, b);
	}
	
	public void toggleButton(Button b, boolean s) {
		if (askForButton != null && s) {
			askForButton.joyPressed(b);
			askForButton = null;
			return;
		}
		b.nextState = s;
		
		InputHandler ih = ((InputHandler)MojamComponent.instance.getInputHandler());
		Keys keys = MojamComponent.instance.keys;
		
		if (b.simulKey == null) return;
		
		if (s) {
			ih.toggleJoypad(b.simulKey, true);
		} else {
			ih.toggleJoypad(b.simulKey, false);
		}
	}
	
	public void addAxis(Axis a) {
		addAxis(a, a.id);
	}
	
	public void addAxis(Axis a, int i) {
		if (butaxes == null) {
			butaxes = new ArrayList<Object>(itemCount);
		}
		butaxes.add(i, a);
	}
	
	public void toggleAxis(Axis a, float f) {
		//Update axis status
		if (a.firstState == -2) {
			a.firstState = f;
		}
		
		if (a.firstState == f) {
			a.nextState = 0;
			return;
		} else {
			a.firstState = -3;
		}
		
		if (askForAxis != null && f != 0) {
			askForAxis.axisUsed(a);
			askForAxis = null;
		}
		
		a.nextState = f;
		
		InputHandler ih = ((InputHandler)MojamComponent.instance.getInputHandler());
		Keys keys = MojamComponent.instance.keys;
		
		//Check if axis is X or Y axis of any special axis>key/mouse port . If it is , handle axis seperately to counterpart ( X|y , x|Y ) 
		
		String[] mxa = mouseXA.split(":");
		String[] mya = mouseYA.split(":");
		String[] sxa = shootXA.split(":");
		String[] sya = shootYA.split(":");
		String[] wxa = walkXA.split(":");
		String[] wya = walkYA.split(":");
		
		if ((a.id+"").equals(mxa[1]) && ((a.controller.getIndex()+"").equals(mxa[0]))) {
			int x = (int) (f*5);
			MojamComponent.instance.mouseButtons.jx = x;
			if (x != 0) {
				MojamComponent.instance.joyMoved = true;
			}
		}
		if ((a.id+"").equals(mya[1]) && ((a.controller.getIndex()+"").equals(mya[0]))) {
			int y = (int) (f*5);
			MojamComponent.instance.mouseButtons.jy = y;
			if (y != 0) {
				MojamComponent.instance.joyMoved = true;
			}
		}
		
		if ((a.id+"").equals(sxa[1]) && ((a.controller.getIndex()+"").equals(sxa[0]))) {
			/*if (f > 0) {
				ih.toggleJoypad(keys.fireRight, true);
			} else {
				ih.toggleJoypad(keys.fireRight, false);
			}
			if (f < 0) {
				ih.toggleJoypad(keys.fireLeft, true);
			} else {
				ih.toggleJoypad(keys.fireLeft, false);
			}*/
			// Accuracy Implementation
			int x = (int) (f*20);
			MojamComponent.instance.mouseButtons.sx = x;
			if (x != 0) {
				MojamComponent.instance.shootMoved = true;
				ih.toggleJoypad(keys.fire, true);
			} else {
				ih.toggleJoypad(keys.fire, false);
			}
		}
		if ((a.id+"").equals(sya[1]) && ((a.controller.getIndex()+"").equals(sya[0]))) {
			/*if (f > 0) {
				ih.toggleJoypad(keys.fireDown, true);
			} else {
				ih.toggleJoypad(keys.fireDown, false);
			}
			if (f < 0) {
				ih.toggleJoypad(keys.fireUp, true);
			} else {
				ih.toggleJoypad(keys.fireUp, false);
			}*/
			// Accuracy Implementation
			int y = (int) (f*20);
			MojamComponent.instance.mouseButtons.sy = y;
			if (y != 0) {
				MojamComponent.instance.shootMoved = true;
				ih.toggleJoypad(keys.fire, true);
			} else {
				ih.toggleJoypad(keys.fire, false);
			}
		}
		
		if ((a.id+"").equals(wxa[1]) && ((a.controller.getIndex()+"").equals(wxa[0]))) {
			if (f > 0) {
				ih.toggleJoypad(keys.right, true);
			} else {
				ih.toggleJoypad(keys.right, false);
			}
			if (f < 0) {
				ih.toggleJoypad(keys.left, true);
			} else {
				ih.toggleJoypad(keys.left, false);
			}
		}
		if ((a.id+"").equals(wya[1]) && ((a.controller.getIndex()+"").equals(wya[0]))) {
			if (f > 0) {
				ih.toggleJoypad(keys.down, true);
			} else {
				ih.toggleJoypad(keys.down, false);
			}
			if (f < 0) {
				ih.toggleJoypad(keys.up, true);
			} else {
				ih.toggleJoypad(keys.up, false);
			}
		}
		
	}
	
	//Index:AxisName
	//3:X axis
	public static String mouseXA = null;
	public static String mouseYA = null;
	public static String shootXA = null;
	public static String shootYA = null;
	public static String walkXA = null;
	public static String walkYA = null;
	
	public static JoypadHandler handlers[];
	public static int count;
	
	public static JoyBindingsMenu askForButton;
	public static AJoyBindingsMenu askForAxis;
	
	public static void init() {
		try {
			Controllers.create();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		count = Controllers.getControllerCount();
		System.out.println(count+" Controllers Found");
		for (int i=0;i<count;i++) {
			Controller controller = Controllers.getController(i);
			System.out.println(controller.getIndex()+" : "+controller.getName());
		}

		if (count == 0) {
			return;
		}

		handlers  = new JoypadHandler[count];
		for (int i=0;i<count;i++) {
			handlers[i] = new JoypadHandler(i);
		}
		
		mouseXA = Options.get("joya_mouseXA");
		mouseYA = Options.get("joya_mouseYA");
		
		shootXA = Options.get("joya_ shootXA");
		shootYA = Options.get("joya_ shootYA");
		
		walkXA = Options.get("joya_walkXA");
		walkYA = Options.get("joya_walkYA");
		
		if (mouseXA == null) mouseXA = "-1:-1:NONE";
		if (mouseYA == null) mouseYA = "-1:-1:NONE";
		
		if (shootXA == null) shootXA = "-1:-1:NONE";
		if (shootYA == null) shootYA = "-1:-1:NONE";
		
		if (walkXA == null) walkXA = "-1:-1:NONE";
		if (walkYA == null) walkYA = "-1:-1:NONE";
	}
	
	public static void tick() {
		if (count == 0) {
			return;
		}
		
		Controllers.poll();

		/*while (Controllers.next()) {
			System.out.println("Event Fired: ");
			System.out.println("\t"+Controllers.getEventNanoseconds());
			System.out.println("\t"+Controllers.getEventSource()+":"+Controllers.getEventControlIndex()+":"+Controllers.isEventButton());
			System.out.println("\t"+Controllers.isEventXAxis()+":"+Controllers.isEventYAxis());
		}*/

		for (int i=0;i<count;i++) {
			handlers[i].updateDetails();
		}
	}
	
}
