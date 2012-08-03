package com.nullpointer.polygame;

import java.util.ArrayList;

import org.anddev.andengine.entity.IEntity;
import org.anddev.andengine.entity.shape.IShape;

public class EggManager extends ArrayList<Egg> {

	
	public Egg findEggByName(String name) {
		Egg egg=null;

		// loop through all eggs present and return the egg that has
		// given name
		for (Egg e : this) {
			if (e.getEggName() != null) {
				if (e.getEggName().equalsIgnoreCase(name)) {
					egg=e;
				}
			}
		}
		return egg;
	}
	
	
	
	
}
