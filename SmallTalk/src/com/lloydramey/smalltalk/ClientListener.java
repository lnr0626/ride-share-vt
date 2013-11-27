package com.lloydramey.smalltalk;

import java.util.ArrayList;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.lloydramey.smalltalk.Network.LoggedInUsers;
import com.lloydramey.smalltalk.Network.Message;
import com.lloydramey.smalltalk.Network.RejectedLogin;
import com.lloydramey.smalltalk.Network.UserLoggedIn;
import com.lloydramey.smalltalk.Network.UserLoggedOut;

public class ClientListener extends Listener {
	public void received (Connection connection, Object object) {
		if(object instanceof Message) {
			Message msg = (Message) object;
			System.out.println(msg.from + ": " + msg.body);
		} else if (object instanceof ArrayList) {
			ArrayList<?> list = (ArrayList<?>) object;
			for(Object obj : list) {
				Message msg = (Message) obj;
				System.out.println(msg.from + ": " + msg.body);
			}
		} else if (object instanceof UserLoggedOut) {
			String name = ((UserLoggedOut)object).first;
			System.out.println(name + " has logged out");
		} else if (object instanceof UserLoggedIn) {
			String name = ((UserLoggedIn)object).first;
			System.out.println(name + " has logged in");
		} else if (object instanceof RejectedLogin) {
			System.out.println("Server rejected your login");
		} else if (object instanceof LoggedInUsers) {
			String[] names = ((LoggedInUsers)object).names;
			String message = "Logged in users: ";
			for (int i = 0; i < names.length; i++) {
				if(i > 0) {
					message += ", ";
				}
				message += names[i];
			}
			System.out.println(message);
		}
	}
}
