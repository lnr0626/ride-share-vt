package ece4564.ridesharevt;

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
		return name + " " + smoke + " " + tod ;
	}
}
