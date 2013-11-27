package com.lloydramey.smalltalk;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;
import com.lloydramey.smalltalk.Network.LoggedInUsers;
import com.lloydramey.smalltalk.Network.Login;
import com.lloydramey.smalltalk.Network.Logout;
import com.lloydramey.smalltalk.Network.Message;
import com.lloydramey.smalltalk.Network.MessageLog;
import com.lloydramey.smalltalk.Network.RejectedLogin;
import com.lloydramey.smalltalk.Network.User;
import com.lloydramey.smalltalk.Network.UserLoggedIn;
import com.lloydramey.smalltalk.Network.UserLoggedOut;

public class SmallTalkServer {
	private int tcpPort;
	private int udpPort;
	private Server server;
	private HashSet<User> loggedInUsers;
	private MessageLog log;

	public SmallTalkServer(int tcpPort, int udpPort) {
		this.tcpPort = tcpPort;
		this.udpPort = udpPort;
		this.server = new Server() {
			protected Connection newConnection() {
				return new SmallTalkConnection();
			}
		};
		Network.register(server);
		log = new MessageLog();
		log.messages = new ArrayList<Message>();
		Log.INFO();
		loggedInUsers = new HashSet<Network.User>();
	}

	public SmallTalkServer(int tcpPort) {
		this(tcpPort, -1);
	}

	public void start() throws IOException {
		server.start();
		if (this.udpPort < 0) {
			server.bind(this.tcpPort);
		} else {
			server.bind(this.tcpPort, this.udpPort);
		}
		server.addListener(new Listener.ThreadedListener(new Listener() {

			public void received(Connection c, Object object) {
				SmallTalkConnection connection = (SmallTalkConnection) c;
				User user = connection.user;
				if (object instanceof Login) {
					if (user != null)
						return;

					String email = ((Login) object).user.email;

					for (User other : loggedInUsers) {
						if (other.email.equals(email)) {
							Log.info("Rejected duplicate login");
							connection.sendTCP(new RejectedLogin());
							return;
						}
					}

					loggedIn(connection, ((Login) object).user);

				} else if (object instanceof Logout) {
					if (user == null)
						return;
					loggedOut(connection);
				} else if (object instanceof Message) {

					if (connection.user == null)
						return;

					Message msg = (Message) object;

					if (!isValid(msg.body)) {
						return;
					}

					Log.info("New message from " + msg.from + " sent "
							+ msg.sent.toString());
					log.messages.add(msg);
					server.sendToAllExceptTCP(connection.getID(), msg);

				}
			}

			public void disconnected(Connection c) {
				SmallTalkConnection connection = (SmallTalkConnection) c;
				if (connection.user != null) {
					loggedOut(connection);
				}
			}
		}));
	}

	private void loggedOut(SmallTalkConnection c) {
		loggedInUsers.remove(c.user);

		UserLoggedOut loggedOut = new UserLoggedOut();
		loggedOut.first = c.user.first;
		for (Connection conn : server.getConnections()) {
			if (conn.isConnected() && conn != c
					&& ((SmallTalkConnection) conn).user != null) {
				conn.sendTCP(loggedOut);
			}
		}
		Log.info(c.user.email + " logged out from connection " + c.getID());
		c.user = null;
	}

	private boolean isValid(String body) {
		body = body.trim();
		return !body.isEmpty();
	}

	private void loggedIn(SmallTalkConnection c, User user) {
		c.user = user;
		String[] userNames = new String[loggedInUsers.size()];
		int i = 0;
		for(User u : loggedInUsers) {
			userNames[i++] = u.first; 
		}
		loggedInUsers.add(user);
		LoggedInUsers users = new LoggedInUsers();
		users.names = userNames;
		server.sendToTCP(c.getID(), users);
		server.sendToTCP(c.getID(), log);

		UserLoggedIn loggedIn = new UserLoggedIn();
		loggedIn.first = user.first;

		for (Connection conn : server.getConnections()) {
			if (conn.isConnected() && conn != c
					&& ((SmallTalkConnection) conn).user != null) {
				conn.sendTCP(loggedIn);
			}
		}

		Log.info(user.email + " logged in on connection " + c.getID());
	}

	public void kill() {
		server.stop();
	}

	static public class SmallTalkConnection extends Connection {
		public User user;
	}

	// For testing
	public static void main(String[] args) throws IOException {
		SmallTalkServer server = new SmallTalkServer(Network.port);
		server.start();
		String cmd = "";
		Scanner in = new Scanner(System.in);
		do {
			cmd = in.nextLine();
		} while (!cmd.equals("exit"));
		server.kill();
	}
}
