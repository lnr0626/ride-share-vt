package com.vt.edu.driverriderserver;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AcceptanceServlet extends HttpServlet {

	private static final long serialVersionUID = -2066935386368316952L;
	
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		PrintWriter out = resp.getWriter();
		String id = req.getParameter("id");
		String action = req.getParameter("action");
		if(id == null) {
			return;
		}
		for(int i = MainServlet.driverList_.size()-1; i >= 0; i--) {
			if(Integer.toString(MainServlet.driverList_.get(i).id).equals(id)) {
				if(action.equals("accept")) {
					MainServlet.driverList_.get(i).numSeats--;
					if(MainServlet.driverList_.get(i).numSeats < 0) {
						MainServlet.driverList_.get(i).numSeats = 0;
					}
					out.println("Number of Seats has been decremented");
				}
				else if(action.equals("cancel")) {
					MainServlet.driverList_.get(i).numSeats++;
					out.println("Number of Seats has been incremented");
				}
			}
		}
	}
}
