package com.lloydramey.smalltalk;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.lloydramey.smalltalk.Network.LoggedInUsers;
import com.lloydramey.smalltalk.Network.Login;
import com.lloydramey.smalltalk.Network.Logout;
import com.lloydramey.smalltalk.Network.Message;
import com.lloydramey.smalltalk.Network.MessageLog;
import com.lloydramey.smalltalk.Network.RejectedLogin;
import com.lloydramey.smalltalk.Network.User;
import com.lloydramey.smalltalk.Network.UserLoggedIn;
import com.lloydramey.smalltalk.Network.UserLoggedOut;

public class SmallTalkClient {
	private User user;
	private Client client;
	private final int TIMEOUT = 5000;
	
	private SmallTalkListener listener;

	public SmallTalkClient() {
		client = new Client();
		Network.register(client);
	}
	
	public void setMessageListener(SmallTalkListener l) { 
		listener = l;
	}

	public void login(String name, String email) {
        this.user = new User();
        this.user.first = name;
        this.user.email = email;
		Login login = new Login();
		login.user = user;
		client.sendTCP(login);
	}

    public void reloadConversation(String cid) {
        Network.Conversation conversation = new Network.Conversation();
        conversation.id = cid;
        client.sendTCP(conversation);
    }

    public void newConversation(ArrayList<String> userEmails) {
        Network.Conversation conversation = new Network.Conversation();
        conversation.userEmails = userEmails;
        conversation.id = Network.generateConversationId(userEmails);
        client.sendTCP(conversation);
    }
	
	public void printStatus() {
		System.out.println("SmallTalk client v0.1");
		System.out.println("\tHost: " + client.getRemoteAddressTCP());
		System.out.println("\tConnected: " + client.isConnected());
		System.out.println("\tLogged In: " + (user != null));
	}

	public void logout() {
		this.user = null;
		Logout logout = new Logout();
		client.sendTCP(logout);
	}

    public void start(String address, int tcpPort) throws IOException {
        this.start(address, tcpPort, -1);
    }

    public boolean isConnected() {
        return client.isConnected();
    }

	public void start(String address, int tcpPort, int udpPort) throws IOException {
		client.start();
		if (udpPort < 0) {
			client.connect(TIMEOUT, address, tcpPort);
		} else {
			client.connect(TIMEOUT, address, tcpPort,
					udpPort);
		}
		client.addListener(new Listener(){
			public void received (Connection connection, Object object) {
				if(object instanceof Message) {
					if(listener != null) {
						listener.messageReceived((Message)object);
					}
				} else if (object instanceof UserLoggedOut) {
					if(listener != null) {
						listener.userLoggedOut(((UserLoggedOut) object).user);
					}
				} else if (object instanceof UserLoggedIn) {
					if(listener != null) {
						listener.userLoggedIn(((UserLoggedIn) object).user);
					}
				} else if (object instanceof RejectedLogin) {
					if(listener != null) {
						listener.loginRejected();
					}
				} else if (object instanceof LoggedInUsers) {
					if(listener != null) {
						listener.loggedInUsersReceived(((LoggedInUsers) object).users);
					}
				} else if (object instanceof Network.ConversationNotification) {
                    if(listener != null) {
                        listener.conversationNotification((Network.ConversationNotification) object);
                    }
                } else if (object instanceof Network.ConversationAlreadyExists) {
                    if(listener != null) {
                        listener.conversationUpdated((Network.ConversationAlreadyExists)object);
                    }
                }
			}});
	}

    public void stop() {
        user = null;
        client.close();
    }

	public void sendMessage(String body, String cid) {
		if(user != null) {
			Message message = new Message();
			message.from = user;
			message.body = body;
			message.sent = new Date();
            message.conversationId = cid;
			client.sendTCP(message);
		}
	}

	public void kill() {
        user = null;
		client.stop();
	}
}
