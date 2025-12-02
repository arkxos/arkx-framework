package com.hannesdorfmann.annotationprocessing101.factory;

class MealFactory {

	public Meal create(String id) {
		if (id == null) {
			throw new IllegalArgumentException("id is null!");
		}
		if ("Magherita".equals(id)) {
			return new com.hannesdorfmann.annotationprocessing101.factory.MargheritaPizza();
		}
		if ("Calzone".equals(id)) {
			return new com.hannesdorfmann.annotationprocessing101.factory.CalzonePizza();
		}
		if ("Tiramisu".equals(id)) {
			return new com.hannesdorfmann.annotationprocessing101.factory.Tiramisu();
		}
		throw new IllegalArgumentException("Unknown id = " + id);
	}

}
