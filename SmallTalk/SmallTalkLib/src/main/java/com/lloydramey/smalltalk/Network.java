package com.lloydramey.smalltalk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;
import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import com.google.common.io.BaseEncoding;

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
        kryo.register(Conversation.class);
        kryo.register(ConversationNotification.class);
        kryo.register(ConversationAlreadyExists.class);
        kryo.register(YouAreNotInThatConversation.class);
	}
	
	static public class Login {
		public User user;
	}
	
	static public class LoggedInUsers {
		public ArrayList<User> users;
	}
	
	static public class Logout {
	}
	
	static public class RejectedLogin {
	}

    static public class ConversationNotification {
        public ConversationNotification() {
            conversation = new Conversation();
            log = new MessageLog();
        }
        public Conversation conversation;
        public MessageLog log;
    }

    static public class Conversation {
        public Conversation() {
            messageLog = new MessageLog();
            userEmails = new ArrayList<String>();
            name = "";
            id = "";
        }
        public MessageLog messageLog;
        public ArrayList<String> userEmails;
        public String name;
        public String id;
    }

    static String generateConversationId(String... userEmails) {
        List<String> emails = Arrays.asList(userEmails);
        return generateConversationId(emails);
    }

    static String generateConversationId(List<String> emails) {
        Collections.sort(emails);
        String result = Joiner.on("").join(emails);
        HashFunction sha512 = Hashing.sha512();
        HashCode code = sha512.newHasher().putString(result, Charsets.UTF_8).hash();
        return BaseEncoding.base64Url().encode(code.asBytes());
    }

	static public class MessageLog {
        public MessageLog() {
            messages = new ArrayList<Message>();
        }
        public ArrayList<Message> messages;
        public void addMessage(Message message) {
            messages.add(message);
        }
	}

	static public class UserLoggedOut {
		public User user;
	}

    static public class YouAreNotInThatConversation {

    }

    static public class ConversationAlreadyExists {
        public Conversation conversation;
        public MessageLog log;
    }
	
	static public class UserLoggedIn {
		public User user;
	}
	
	static public class User {
		public String first;
		public String email;
	}
	
	static public class Message {
		public User from;
		public String body;
		public Date sent;
        public String conversationId;
	}
}
