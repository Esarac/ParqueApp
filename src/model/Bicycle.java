package model;

/**
* <b>Description:</b> The class Bicycle in the package model.<br>
* @author VoodLyc & Esarac.
*/

public class Bicycle extends Vehicle implements TwoWheels {
	
	//Attributes
	private String color;
	
	//Constructor
	public Bicycle(String name, String color) {
		super(name);
		this.color=color;
	}

}
