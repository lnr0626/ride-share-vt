package com.lloydramey.smalltalk;

import java.util.ArrayList;
import java.util.Date;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

public class Network {
	static public final int port = 12345;
	
	static public final void register(EndPoint endpoint) {
		Kryo kryo = endpoint.getKryo();
		kryo.register(Message.class);
		kryo.register(Login.class);
		kryo.register(Date.class);
		kryo.register(User.class);
		kryo.register(ArrayList.class);
		kryo.register(UserLoggedOut.class);
		kryo.register(UserLoggedIn.class);
		kryo.register(Logout.class);
		kryo.register(RejectedLogin.class);
		kryo.register(LoggedInUsers.class);
		kryo.register(String[].class);
		kryo.register(MessageLog.class);
	}
	
	static public class Login {
		public User user;
	}
	
	static public class LoggedInUsers {
		public String[] names;
	}
	
	static public class Logout {
	}
	
	static public class RejectedLogin {
	}
	
	static public class MessageLog {
		ArrayList<Message> messages;
	}
	
	static public class UserLoggedOut {
		public String first;
	}
	
	static public class UserLoggedIn {
		public String first;
	}
	
	static public class User {
		public String first;
		public String last;
		public String email;
	}
	
	static public class Message {
		public String from;
		public String body;
		public Date sent;
	}
}
