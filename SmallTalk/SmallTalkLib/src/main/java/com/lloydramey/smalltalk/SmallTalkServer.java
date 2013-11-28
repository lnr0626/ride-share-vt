package com.lloydramey.smalltalk;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

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
    private final Map<String, User> loggedInUsers;
    private final Map<String, ArrayList<String>> conversationsByUser;
    private final Map<String, Network.Conversation> conversationsById;
    private final Map<String, MessageLog> messageLogsByConId;
    private final Map<String, Integer> connectionIdByEmail;

	public SmallTalkServer(int tcpPort, int udpPort) {
		this.tcpPort = tcpPort;
		this.udpPort = udpPort;
		this.server = new Server() {
			protected Connection newConnection() {
				return new SmallTalkConnection();
			}
		};
		Network.register(server);
		Log.INFO();
        loggedInUsers = new HashMap<String, User>();
        conversationsByUser = new HashMap<String, ArrayList<String>>();
        conversationsById = new HashMap<String, Network.Conversation>();
        messageLogsByConId = new HashMap<String, MessageLog>();
        connectionIdByEmail = new HashMap<String, Integer>();
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

            @Override
            public void connected(Connection connection) {
                super.connected(connection);
                Log.info("New connection");
            }

            public void received(Connection c, Object object) {
				SmallTalkConnection connection = (SmallTalkConnection) c;
				User user = connection.user;
				if (object instanceof Login) {
					if (user != null)
						return;

					String email = ((Login) object).user.email;

                    if(loggedInUsers.keySet().contains(email)) {
                        Log.info("Rejected duplicate login");
                        connection.sendTCP(new RejectedLogin());
                        return;
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

                    Network.Conversation conversation = conversationsById.get(msg.conversationId);

                    if(conversation != null) {
                        Log.info("New message from " + msg.from + " sent "
                                + msg.sent.toString() + " in conversation " + msg.conversationId);
                        messageLogsByConId.get(msg.conversationId).addMessage(msg);
                        for(String email : conversation.userEmails) {
                            Integer connId = connectionIdByEmail.get(email);
                            if(connId != null) {
                                server.sendToTCP(connId, msg);
                            }
                        }
                    } else {
                        Log.info("Invalid message from " + msg.from + " sent "
                                + msg.sent.toString() + ". CID: " + msg.conversationId);
                    }

				} else if (object instanceof Network.Conversation) {
                    Network.Conversation conversation = ((Network.Conversation)object);
                    if(conversation.id.isEmpty()) {
                        if(!conversation.userEmails.contains(connection.user.email))
                        conversation.id = Network.generateConversationId(conversation.userEmails);
                    }
                    if(conversationsById.get(conversation.id) != null) {
                        if(conversation.userEmails.contains(connection.user.email)) {
                            Network.ConversationAlreadyExists alreadyExists = new Network.ConversationAlreadyExists();
                            alreadyExists.conversation = conversationsById.get(conversation.id);
                            alreadyExists.log = messageLogsByConId.get(conversation.id);
                            server.sendToTCP(c.getID(), alreadyExists);
                        } else {
                            server.sendToTCP(c.getID(), new Network.YouAreNotInThatConversation());
                        }
                    } else {
                        conversationsById.put(conversation.id, conversation);
                        messageLogsByConId.put(conversation.id, new MessageLog());
                        conversationsByUser.get(connection.user.email).add(conversation.id);
                        Network.ConversationNotification notification = new Network.ConversationNotification();
                        notification.conversation = conversation;
                        for(String email : conversation.userEmails) {
                            if(loggedInUsers.get(email) != null) {
                                server.sendToTCP(connectionIdByEmail.get(email), notification);
                            }
                        }
                    }
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
		loggedInUsers.remove(c.user.email);
        connectionIdByEmail.remove(c.user.email);

		UserLoggedOut loggedOut = new UserLoggedOut();
		loggedOut.user = c.user;

        Set<String> usersToNotify = new HashSet<String>();

        for(String id : conversationsByUser.get(c.user.email)) {
            //Gather all emails
            usersToNotify.addAll(conversationsById.get(id).userEmails);
        }

        for(String email : usersToNotify) {
            if(!email.equals(c.user.email)) {
                User other = loggedInUsers.get(email);
                if(other != null) {
                    server.sendToTCP(connectionIdByEmail.get(email), loggedOut);
                }
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

        ArrayList<String> convosForUser = conversationsByUser.get(user.email);
        if(convosForUser == null) {
            convosForUser = new ArrayList<String>();
            conversationsByUser.put(user.email, convosForUser);
        } else {
            Set<String> usersToNotify = new HashSet<String>();
            for(String id : convosForUser) {
                // Gather all user email to notify
                usersToNotify.addAll(conversationsById.get(id).userEmails);

                // Notify newly logged in client of all the conversationsById of which they are a part
                Network.ConversationNotification notification = new Network.ConversationNotification();
                notification.conversation = conversationsById.get(id);
                notification.log = messageLogsByConId.get(id);
                server.sendToTCP(c.getID(), notification);
            }

            UserLoggedIn userLoggedIn = new UserLoggedIn();
            userLoggedIn.user = user;

            LoggedInUsers loggedIn = new LoggedInUsers();
            loggedIn.users = new ArrayList<User>();
            for(String email : usersToNotify) {
                if(!email.equals(user.email)) {
                    User other = loggedInUsers.get(email);
                    if(other != null) {
                        // If they are logged in, notify them and tell the newly connected
                        // Client of their status.
                        server.sendToTCP(connectionIdByEmail.get(email), userLoggedIn);
                        loggedIn.users.add(loggedInUsers.get(email));
                    }
                }
            }
            server.sendToTCP(c.getID(), loggedIn);
        }

        loggedInUsers.put(user.email, user);
        connectionIdByEmail.put(user.email, c.getID());
        c.user = user;

		Log.info(user.email + " logged in on connection " + c.getID());
	}

	public void kill() {
		server.stop();
	}

	static public class SmallTalkConnection extends Connection {
		public User user;
	}

	public static void main(String[] args) {
        try {
            SmallTalkServer server = new SmallTalkServer(Network.port);
            server.start();
            String cmd;
            Scanner in = new Scanner(System.in);
            do {
                cmd = in.nextLine();
                if(cmd.equals("status")) {
                    // TODO: Print status
                }
            } while (!cmd.equals("exit"));
            server.kill();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            System.exit(1);
        }
	}
}
