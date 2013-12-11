package com.vt.edu.driverriderserver;

import java.util.concurrent.atomic.AtomicInteger;

// Class that stores the driver information
public class Driver implements Comparable<Driver> {
	static AtomicInteger counter = new AtomicInteger();
	int id = 0;
	String name = "Tyler";
	String numSeats = "3";
	String status = "Driver";
	String tod = "22:30";
	String startLoc = "Perry Street Parking Garage";
	String endLoc = "The Village";
	String smoke = "Non-smoking";

	@Override
	public int compareTo(Driver other) {
		
		String[] splitTodTime = this.tod.split(":");
		String hourTime = splitTodTime[0];
		String minTime = splitTodTime[1];
		int Hour = Integer.parseInt(hourTime);
		int Min = Integer.parseInt(minTime);

		String[] splitCompareTodTime = other.tod.split(":");
		String hourCompareTime = splitCompareTodTime[0];
		String minCompareTime = splitCompareTodTime[1];
		int compareHour = Integer.parseInt(hourCompareTime);
		int compareMin = Integer.parseInt(minCompareTime);

		if (Hour < compareHour) {
			return -1;
		} else if(Hour > compareHour) {
			return 1;
		} else {
			return Min - compareMin;
		}
	}
}



		


