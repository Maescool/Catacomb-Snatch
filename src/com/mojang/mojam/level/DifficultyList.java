package com.mojang.mojam.level;

import java.util.ArrayList;

public class DifficultyList {

	private static ArrayList<DifficultyInformation> Difficulties;

	private static void createDifficultyList() {
		Difficulties = new ArrayList<DifficultyInformation>();
		Difficulties.add(new DifficultyInformation("Easy", 0));
		Difficulties.add(new DifficultyInformation("Normal", 1));
		Difficulties.add(new DifficultyInformation("Hard", 2));
	}

	public static ArrayList<DifficultyInformation> getDifficulties() {
		if (Difficulties == null) {
			createDifficultyList();
		}
		return Difficulties;
	}
}
