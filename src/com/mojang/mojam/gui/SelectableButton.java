package com.mojang.mojam.gui;

public class SelectableButton extends Button {

	private boolean selected = false;

	public SelectableButton(int id, String label, int x, int y) {
		super(id, label, x, y);
	}

	@Override
	public String getLabel() {
		String label = super.getLabel();
		if (selected) {
			label = label.substring(1, label.length() - 1);
		}
		return label;
	}

	@Override
	public void setLabel(String label) {
		if (selected) {
			label = "-" + label + "-";
		}
		super.setLabel(label);
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		String label = getLabel();
		this.selected = selected;
		setLabel(label);
	}

}
