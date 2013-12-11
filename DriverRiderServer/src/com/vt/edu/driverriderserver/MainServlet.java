package com.vt.edu.driverriderserver;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

public class MainServlet extends HttpServlet {

	private static final long serialVersionUID = 9019970271673483305L;

	static ArrayList<Person> driverList_ = new ArrayList<Person>();
	static ArrayList<Person> riderList_ = new ArrayList<Person>();
	
	public static void main(String[] args) throws Exception {
		WebAppContext context = new WebAppContext();
		context.setWar("war");
		context.setContextPath("/");

		// used port 4659 to make sure it would run w/o complications
		Server server = new Server(4659);
		server.setHandler(context);

		server.start();
		server.join();
	}

	// The application only sends get requests so I did not implement this yet
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("application/json");

		// Getting the Parameters from the clients request
		String name = req.getParameter("name");
		String numSeats = req.getParameter("numSeats");
		String status = req.getParameter("status");
		String tod = req.getParameter("tod");
		String startLoc = req.getParameter("startLoc");
		String endLoc = req.getParameter("endLoc");
		String smoke = req.getParameter("smoke");
		String email = req.getParameter("email");

		// The code below gets the current time and splits it into hours and
		// minutes
		Date now = new Date();
		String currTime = new SimpleDateFormat("HH:mm").format(now);
		String[] splitCurrTime = currTime.split(":");
		String hourCurrTime = splitCurrTime[0];
		String minCurrTime = splitCurrTime[1];
		int currHour = Integer.parseInt(hourCurrTime);
		int currMin = Integer.parseInt(minCurrTime);

		// New Drivers will be created here using parameters from incoming
		// requests
		if (!(name == null || numSeats == null || status == null || tod == null
				|| startLoc == null || endLoc == null || smoke == null)) {
			if (status.equals("driver")) {
				Person driver = new Person();
				driver.id = Person.counter.incrementAndGet();
				driver.name = name;
				driver.numSeats = Integer.valueOf(numSeats);
				driver.status = status;
				driver.tod = tod;
				driver.startLoc = startLoc;
				driver.endLoc = endLoc;
				driver.smoke = smoke;
				driver.email = email;
				driverList_.add(driver);
				Collections.sort(driverList_);
			}
		}

		// Creates an array list of riders
		if (!(name == null || numSeats == null || status == null || tod == null
				|| startLoc == null || endLoc == null || smoke == null)) {
			if (status.equals("rider")) {
				Person rider = new Person();
				rider.id = Person.counter.incrementAndGet();
				rider.name = name;
				rider.numSeats = Integer.valueOf(numSeats);
				rider.status = status;
				rider.tod = tod;
				rider.startLoc = startLoc;
				rider.endLoc = endLoc;
				rider.smoke = smoke;
				riderList_.add(rider);
			}
		}
		
		PrintWriter out = resp.getWriter();

		/*
		 * //////////////////////////////////////////////////////////////////////
		 * / The below code prints the values onto the server in json format for
		 * each driver. This is used so that the client can pull them back into
		 * the application to make a list of drivers.
		 *///////////////////////////////////////////////////////////////////////

		out.println("[");

		int numDrivers = driverList_.size();

		/*
		 * //////////////////////////////////////////////////////////////////////
		 * / This for loop is how the server determines when drivers need to be
		 * removed automatically. It is based on time using the Date Class to
		 * get the current time at the time of a HTTP GET request. Then it
		 * parses the time given by the Drivers in the List and if that is less
		 * than the current time it is removed from the driver list and
		 * effectively the server.
		 * 
		 * Side Note: I have not yet taken into account if someone is setting up
		 * a ride at midnight or early the next day when it is still 11PM or so
		 * the day before. I plan on implementing that next.
		 */// ////////////////////////////////////////////////////////////////////
		for (int i = 0; i < numDrivers; i++) {
			Person currentDriver = driverList_.get(i);
			if (tod != null) {
				String[] splitTodTime = currentDriver.tod.split(":");
				String hourReqTime = splitTodTime[0];
				String minReqTime = splitTodTime[1];
				int reqHour = Integer.parseInt(hourReqTime);
				int reqMin = Integer.parseInt(minReqTime);
				if (reqHour < currHour
						|| (reqHour == currHour && reqMin < currMin)) {
					driverList_.remove(i);
					numDrivers--;
					i--;
				}
			}
		}

		boolean started = false;

		for (int i = 0; i < numDrivers; i++) {
			Person currDriver = driverList_.get(i);
			if (endLoc != null) {
				if (!currDriver.endLoc.equals(endLoc)) {
					continue;
				}
			}
			if(currDriver.numSeats == 0) {
				continue;
			}
			if (started) {
				out.print(" ,");
			}
			started = true;
			out.println("  {");
			out.print("    \"ID\": \"");
			out.print(currDriver.id);
			out.println("\",");
			out.print("    \"name\": \"");
			out.print(currDriver.name);
			out.println("\",");
			out.print("    \"numSeats\": \"");
			out.print(currDriver.numSeats);
			out.println("\",");
			out.print("    \"status\": \"");
			out.print(currDriver.status);
			out.println("\",");
			out.print("    \"tod\": \"");
			out.print(currDriver.tod);
			out.println("\",");
			out.print("    \"startLoc\": \"");
			out.print(currDriver.startLoc);
			out.println("\",");
			out.print("    \"endLoc\": \"");
			out.print(currDriver.endLoc);
			out.println("\",");
			out.print("    \"smoke\": \"");
			out.print(currDriver.smoke);
			out.println("\"");
			out.print("    \"email\": \"");
			out.print(currDriver.email);
			out.println("\"");
			out.println("  }");
		}
		out.println("]");
		out.flush();
	}
}
