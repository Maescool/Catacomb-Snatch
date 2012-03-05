package com.mojang.mojam.level;

import java.util.ArrayList;

import com.mojang.mojam.MojamComponent;

public class DifficultyList {

	private static ArrayList<DifficultyInformation> Difficulties;

	private static void createDifficultyList() {
		Difficulties = new ArrayList<DifficultyInformation>();
		Difficulties.add(new DifficultyInformation(MojamComponent.texts.getStatic("diffselect.easy"), .5f, .5f, 1.5f, .5f, 0));
		Difficulties.add(new DifficultyInformation(MojamComponent.texts.getStatic("diffselect.normal"), 1, 1, 1, 1, 1));
		Difficulties.add(new DifficultyInformation(MojamComponent.texts.getStatic("diffselect.hard"), 3, 3, .5f, 1.5f, 2));
		Difficulties.add(new DifficultyInformation(MojamComponent.texts.getStatic("diffselect.nightmare"), 6, 5, .25f, 2.5f, 3));
	}

	public static ArrayList<DifficultyInformation> getDifficulties() {
		if (Difficulties == null) {
			createDifficultyList();
		}
		return Difficulties;
	}
	
	public static int getDifficultyID(DifficultyInformation di) {
		if ( Difficulties == null )
			createDifficultyList();
		for (int i = 0; i < Difficulties.size();i++)
			if (Difficulties.get(i) == di)
				return i;
		return 1; // default to normal
	}
	
}