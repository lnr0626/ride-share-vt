package com.lloydramey.smalltalk;

import java.io.IOException;
import java.util.Date;
import java.util.Scanner;

import com.esotericsoftware.kryonet.Client;
import com.lloydramey.smalltalk.Network.Login;
import com.lloydramey.smalltalk.Network.Logout;
import com.lloydramey.smalltalk.Network.Message;
import com.lloydramey.smalltalk.Network.User;

public class SmallTalkClient {
	private String serverAddress;
	private User user;
	private int udpPort;
	private int tcpPort;
	private Client client;
	private final int TIMEOUT = 5000;

	public SmallTalkClient(String address, int tcpPort, int udpPort) {
		this.serverAddress = address;
		this.tcpPort = tcpPort;
		this.udpPort = udpPort;
		client = new Client();
		Network.register(client);
	}

	public SmallTalkClient(String address, int tcpPort) {
		this(address, tcpPort, -1);
	}

	public void login(User user) {
		this.user = user;
		Login login = new Login();
		login.user = user;
		client.sendTCP(login);
	}
	
	public void printStatus() {
		System.out.println("SmallTalk client v0.1");
		System.out.println("\tHost: " + serverAddress);
		System.out.println("\tConnected: " + client.isConnected());
		System.out.println("\tLogged In: " + (user != null));
	}

	public void logout() {
		this.user = null;
		Logout logout = new Logout();
		client.sendTCP(logout);
	}

	public void start() throws IOException {
		client.start();
		if (this.udpPort < 0) {
			client.connect(TIMEOUT, this.serverAddress, this.tcpPort);
		} else {
			client.connect(TIMEOUT, this.serverAddress, this.tcpPort,
					this.udpPort);
		}
		client.addListener(new ClientListener());
	}

	public void sendMessage(String body) {
		if(user != null) {
			Message message = new Message();
			message.from = user.first;
			message.body = body;
			message.sent = new Date();
			client.sendTCP(message);
		}
	}

	public void kill() {
		client.stop();
	}

	// For testing
	public static void main(String[] args) throws IOException {
		String address = "127.0.0.1";
		if (args.length > 1) {
			address = args[1];
		}
		SmallTalkClient client = new SmallTalkClient(address, Network.port);
		client.start();
		String cmd = "";
		Scanner in = new Scanner(System.in);
		while (!cmd.equals("exit")) {
			cmd = in.nextLine();
			if (!cmd.isEmpty() && !cmd.equals("exit")) {
				if (cmd.startsWith("\\")) {
					cmd = cmd.substring(1);
					if (cmd.equals("login")) {
						System.out.print("First name: ");
						String first = in.nextLine().trim();
						System.out.print("Last name: ");
						String last = in.nextLine().trim();
						System.out.print("Email: ");
						String email = in.nextLine().trim();
						User user = new User();
						user.first = first;
						user.last = last;
						user.email = email;
						client.login(user);
					} else if (cmd.equals("logout")) {
						client.logout();
					} else if (cmd.equals("status")) {
						client.printStatus();
					}
				} else {
					client.sendMessage(cmd);
				}
			}
		}
	}
}
