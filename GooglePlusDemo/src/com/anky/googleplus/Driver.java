package com.anky.googleplus;

public class Driver {

	String id;
	String name;
	String numSeats;
	String status;
	String tod;
	String startLoc;
	String endLoc;
	String smoke;
	String email;
	
	public String toString() {
		return "Name: " + " " + name + "  " +"departure time:" + " " + tod + "  " + smoke + "  " + "Destination:"+" "+ endLoc ;
	}
}
