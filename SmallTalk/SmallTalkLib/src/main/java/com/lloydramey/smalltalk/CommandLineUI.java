package com.lloydramey.smalltalk;

import java.util.ArrayList;

import com.lloydramey.smalltalk.Network.Message;

public class CommandLineUI implements SmallTalkListener{

	@Override
	public void messageReceived(Message message) {
		System.out.println(message.from.trim() + ": " + message.body.trim());
	}

	@Override
	public void messageLogReceived(ArrayList<Message> messages) {
		for(Message msg : messages) {
			System.out.println(msg.from + ": " + msg.body);
		}
	}

	@Override
	public void loggedInUsersReceived(String[] names) {
		if(names.length > 0) {
			String disp = "";
			for(int i = 0; i < names.length; i++) {
				if(i != 0) {
					disp += ", ";
				}
				if(i == names.length - 1 && i > 1) {
					disp += "and ";
				} 
				disp += names[i];
			}
			if(names.length > 1) {
				disp += " are logged in";
			} else {
				disp += " is logged in";
			}
			System.out.println(disp);
		} else {
			System.out.println("No one else is here");
		}
	}

	@Override
	public void userLoggedOut(String name) {
		System.out.println(name + " left");
	}

	@Override
	public void userLoggedIn(String name) {
		System.out.println(name + " has joined");
	}

	@Override
	public void loginRejected() {
		System.out.println("The server rejected your login attempt");
	}

}
