package com.mojang.mojam;

import org.lwjgl.input.Controller;
import org.lwjgl.input.Controllers;

import com.mojang.mojam.Keys.Key;
import com.mojang.mojam.gui.AJoyBindingsMenu;
import com.mojang.mojam.gui.JoyBindingsMenu;

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
		public int getRumblerCount() {
			return 0;
		}

		@Override
		public String getRumblerName(int i) {
			return null;
		}

		@Override
		public void setRumblerStrength(int i, float v) {

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
						if (key.name.equals(skn)) {
							simulKey = key;
							break;
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

	public Button[] buttons;
	public Axis[] axes;

	public int buttonCount;
	public int axisCount;
	public int itemCount;
	
	public JoypadHandler(int index) {
		controller = Controllers.getController(index);

		buttonCount = controller.getButtonCount();
		buttons = new Button[buttonCount];
		
		axisCount = controller.getAxisCount();
		axes = new Axis[axisCount + 2]; //for the two POV axes (X & Y)
		
		itemCount = controller.getButtonCount() + controller.getAxisCount() + 2;

		for (int i=0;i<buttonCount;i++) {
			buttons[i] = new Button(controller.getButtonName(i), controller, i);
		}
		for (int i=0;i<axisCount;i++) {
			axes[i] = new Axis(controller.getAxisName(i), controller, i + buttonCount);
		}
		axes[axisCount] = new Axis("POV X", controller, axisCount + buttonCount);

		axes[axisCount+1] = new Axis("POV Y", controller, axisCount + 1 + buttonCount);
	}

	public JoypadHandler() {
		controller = new DummyController();

		buttonCount = controller.getButtonCount();
		buttons = new Button[buttonCount];
		
		axisCount = controller.getAxisCount();
		axes = new Axis[axisCount + 2]; //for the two POV axes (X & Y)
		
		itemCount = controller.getButtonCount() + controller.getAxisCount() + 2;

		for (int i=0;i<buttonCount;i++) {
			buttons[i] = new Button(controller.getButtonName(i), controller, i);
		}
		for (int i=0;i<axisCount;i++) {
			axes[i] = new Axis(controller.getAxisName(i), controller, i + buttonCount);
		}
		axes[axisCount] = new Axis("POV X", controller, axisCount + buttonCount);

		axes[axisCount+1] = new Axis("POV Y", controller, axisCount + 1 + buttonCount);
	}

	public void updateDetails() {
		for (int i=0;i<buttonCount;i++) {
			toggleButton(buttons[i], controller.isButtonPressed(i));
		}
		
		for (int i=0;i<axisCount;i++) {
			toggleAxis(axes[i], controller.getAxisValue(i));
		}

		toggleAxis(axes[axisCount], controller.getPovX());
		toggleAxis(axes[axisCount+1], controller.getPovY());
	}
	
	private void toggleButton(Button b, boolean state) {
		if (askForButton != null && state) {
			askForButton.joyPressed(b);
			askForButton = null;
			return;
		}
		b.nextState = state;
		
		InputHandler ih = MojamComponent.instance.getInputHandler();
		if (b.simulKey == null) {
			return;
		}
		
		ih.toggleJoypad(b.simulKey, state);
	}
	
	private void toggleAxis(Axis a, float f) {
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
		
		InputHandler ih = MojamComponent.instance.getInputHandler();
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
	
	public static JoypadHandler[] handlers;
	private static int controllerCount;
	
	private static JoyBindingsMenu askForButton;
	private static AJoyBindingsMenu askForAxis;
	
	public static void init() {
		try {
			Controllers.create();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		controllerCount = Controllers.getControllerCount();
		if (controllerCount == 0) {
			System.out.println("0 Controllers Found");
			return;
		}
		
		System.out.println(controllerCount+" Controllers Found");
		for (int i=0;i<controllerCount;i++) {
			Controller controller = Controllers.getController(i);
			System.out.println(i+" : "+controller.getName());
		}

		handlers = new JoypadHandler[controllerCount];
		for (int i=0;i<controllerCount;i++) {
			handlers[i] = new JoypadHandler(i);
		}
		
		mouseXA = Options.get("joya_mouseXA");
		mouseYA = Options.get("joya_mouseYA");
		
		shootXA = Options.get("joya_shootXA");
		shootYA = Options.get("joya_shootYA");
		
		walkXA = Options.get("joya_walkXA");
		walkYA = Options.get("joya_walkYA");
		
		if (mouseXA == null) {
			mouseXA = "-1:-1:NONE";
		}
		if (mouseYA == null) {
			mouseYA = "-1:-1:NONE";
		}
		
		if (shootXA == null) {
			shootXA = "-1:-1:NONE";
		}
		if (shootYA == null) {
			shootYA = "-1:-1:NONE";
		}
		
		if (walkXA == null) {
			walkXA = "-1:-1:NONE";
		}
		if (walkYA == null) {
			walkYA = "-1:-1:NONE";
		}
	}
	
	public static void tick() {
		if (controllerCount == 0) {
			return;
		}
		
		Controllers.poll();

		for (int i=0;i<controllerCount;i++) {
			handlers[i].updateDetails();
		}
	}

	public static Button getButtonWithKey(Key key) {
		for (int i = 0; i < controllerCount; i++) {
			JoypadHandler h = handlers[i];
			int buttonCount = h.buttonCount;
			Button[] buttons = h.buttons;
			for (int id = 0; id < buttonCount; id++) {
				if (buttons[i].name.equals(key.name)) {
					return buttons[i];
				}
			}
		}
		return null;
	}

	public static void setAxisMenu(AJoyBindingsMenu askForAxis) {
		JoypadHandler.askForAxis = askForAxis;
	}

	public static void setJoyBindingsMenu(JoyBindingsMenu askForButton) {
		JoypadHandler.askForButton = askForButton;
	}
}