package com.vt.edu.driverriderserver;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DeleteServlet extends HttpServlet {

	private static final long serialVersionUID = 614168162972536745L;

	// This server is used to manually remove drivers from the server through
	// a get request
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		PrintWriter out = resp.getWriter();
		String id = req.getParameter("id");
		if(id == null) {
			return;
		}
		out.println("IDs may be removed with this servlet");
		for(int i = MainServlet.driverList_.size()-1; i >= 0; i--) {
			if(Integer.toString(MainServlet.driverList_.get(i).id).equals(id)) {
				MainServlet.driverList_.remove(i);
				out.print("The id that was removed is: " + id);
			}
		}
	}
}