package ece4564.ridesharevt;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.lloydramey.smalltalk.Network;
import com.lloydramey.smalltalk.SmallTalkClient;
import com.lloydramey.smalltalk.SmallTalkListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lloyd on 11/27/13.
 * //TODO: Fill in class info
 *
 * @author lloyd
 */
public class SmallTalkService extends Service implements SmallTalkListener {

    public final static String SERVER_URL_PREF = "server_url";
    public final static String SERVER_PORT_PREF = "server_port";
    public final static String DEF_SERVER_URL = "atrayan.no-ip.org";
    public final static int DEF_SERVER_PORT = Network.port;

    public static final int MSG_LOGIN = 0;
    public static final int MSG_LOGOUT = 1;
    public static final int MSG_CHECK_LOGIN = 2;
    public static final int MSG_CONNECT = 5;
    public static final int MSG_DISCONNECT = 6;
    public static final int MSG_RECONNECT = 7;
    public static final int MSG_START_CONVERSTAION = 8;
    public static final int MSG_SEND_MESSAGE = 10;
    public static final int MSG_GET_CONVERSATIONS = 14;
    public static final int MSG_GET_MESSAGES_IN_CONVERSATION = 15;
    public static final int MSG_RELOAD_CONVERSATION = 9;

    public static final int MSG_CONNECTED = 11;
    public static final int MSG_RECONNECTED = 21;
    public static final int MSG_DISCONNECTED = 12;
    public static final int MSG_LOGIN_REJECTED = 13;
    public static final int MSG_NEW_MESSAGE = 16;
    public static final int MSG_LOGGED_IN_USERS = 17;
    public static final int MSG_USER_LOGGED_OUT = 18;
    public static final int MSG_USER_LOGGED_IN = 19;
    public static final int MSG_NEW_CONVERSATION = 20;
    public static final int MSG_IS_LOGGED_IN = 3;
    public static final int MSG_IS_LOGGED_OUT = 4;
    public static final int MSG_CONVERSATIONS = 22;
    public static final int MSG_MESSAGES_IN_CONVERSATION = 23;

    private Network.User user;
    private Map<String, Network.Conversation> conversations;
    private Map<String, Network.MessageLog> messagesByConversationId;
    private Map<String, Network.User> loggedInUsersByEmail;
    private SmallTalkClient client;
    private LooperThread helperThread;
    private Messenger messenger;
    private String address;
    private int port;
    private Messenger replyTo;

    private class LooperThread extends Thread {
        public Handler handler;
        public void run() {
            client = new SmallTalkClient();
            client.setMessageListener(SmallTalkService.this);
            Looper.prepare();
            handler = new IncomingHandler();
            Looper.loop();
        }
    }

    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Message loggedOut = Message.obtain(null, MSG_IS_LOGGED_OUT);
            Message loggedIn = Message.obtain(null, MSG_IS_LOGGED_IN, user);
            Message connected = Message.obtain(null, MSG_CONNECTED);
            Message disconnected = Message.obtain(null, MSG_DISCONNECTED);
            Message reconnected = Message.obtain(null, MSG_RECONNECTED);
            Message reply;
            switch(msg.what) {
                case MSG_LOGIN:
                    user = (Network.User)msg.obj;
                    if(user != null) {
                        client.login(user.first, user.email);
                        if(msg.replyTo != null) {
                            replyTo = msg.replyTo;
                            try {
                                msg.replyTo.send(loggedIn);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    break;
                case MSG_LOGOUT:
                    client.logout();
                    user = null;
                    if(msg.replyTo != null) {
                        try {
                            msg.replyTo.send(loggedOut);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case MSG_CHECK_LOGIN:
                    Message response;
                    if(user == null) {
                        response = loggedOut;
                    } else {
                        response = loggedIn;
                    }
                    if(msg.replyTo != null) {
                        try {
                            msg.replyTo.send(response);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                case MSG_CONNECT:
                    if(!client.isConnected() && msg.getData() != null) {
                        address = msg.getData().getString(SERVER_URL_PREF, DEF_SERVER_URL);
                        port = msg.getData().getInt(SERVER_PORT_PREF, DEF_SERVER_PORT);
                        try {
                            client.start(address, port);
                            if(msg.replyTo != null) {
                                msg.replyTo.send(connected);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case MSG_DISCONNECT:
                    user = null;
                    if(client.isConnected()) {
                        if(msg.replyTo != null) {
                            try {
                                msg.replyTo.send(loggedOut);
                                msg.replyTo.send(disconnected);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                        client.stop();
                    }
                    break;
                case MSG_RECONNECT:
                    if(client.isConnected()) {
                        client.stop();
                        try {
                            client.start(address, port);
                            if(msg.replyTo != null) {
                                msg.replyTo.send(reconnected);
                            }
                            if(user != null) {
                                client.login(user.first, user.email);
                                if(msg.replyTo != null) {
                                    msg.replyTo.send(loggedIn);
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case MSG_START_CONVERSTAION:
                    if(client.isConnected() && msg.obj != null) {
                        Network.Conversation c = (Network.Conversation)msg.obj;
                        client.newConversation(c);
                    }
                    break;
                case MSG_SEND_MESSAGE:
                    if(client.isConnected() && msg.obj != null) {
                        client.sendMessage((Network.Message)msg.obj);
                    }
                    break;
                case MSG_GET_CONVERSATIONS:
                    if(client.isConnected()) {
                        ArrayList<Network.Conversation> convos = new ArrayList<Network.Conversation>();
                        convos.addAll(conversations.values());
                        if(msg.replyTo != null) {
                            reply = Message.obtain(null, MSG_CONVERSATIONS, convos);
                            try {
                                msg.replyTo.send(reply);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    break;
                case MSG_GET_MESSAGES_IN_CONVERSATION:
                    if(client.isConnected() && msg.obj != null) {
                        Network.MessageLog log = messagesByConversationId.get((String)msg.obj);
                        if(log == null) {
                            log = new Network.MessageLog();
                            messagesByConversationId.put((String)msg.obj, log);
                        }
                        if(msg.replyTo != null) {
                            reply = Message.obtain(null, MSG_MESSAGES_IN_CONVERSATION, log);
                            try {
                                msg.replyTo.send(reply);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    break;
                case MSG_RELOAD_CONVERSATION:
                    if(client.isConnected() && msg.obj != null) {
                        client.reloadConversation((String)msg.obj);
                    }
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }

    @Override
    public void onCreate() {
        conversations = new HashMap<String, Network.Conversation>();
        messagesByConversationId = new HashMap<String, Network.MessageLog>();
        loggedInUsersByEmail = new HashMap<String, Network.User>();
        helperThread = new LooperThread();
        helperThread.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(messenger == null) {
            if(helperThread.handler == null)
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            messenger = new Messenger(helperThread.handler);
        }
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }

    @Override
    public void messageReceived(Network.Message message) {
        if(conversations.get(message.conversationId) != null) {
            Network.MessageLog log = messagesByConversationId.get(message.conversationId);
            log.addMessage(message);
            // Notify replyTo of new message
            if(replyTo != null) {
                Message msg = Message.obtain(null, MSG_NEW_MESSAGE, message);
                try {
                    replyTo.send(msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void loggedInUsersReceived(ArrayList<Network.User> users) {
        for(Network.User u : users) {
            loggedInUsersByEmail.put(u.email, u);
        }
        if(replyTo != null) {
            Message msg = Message.obtain(null, MSG_LOGGED_IN_USERS, loggedInUsersByEmail);
            try {
                replyTo.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void userLoggedOut(Network.User user) {
        loggedInUsersByEmail.remove(user.email);
        if(replyTo != null) {
            Message msg = Message.obtain(null, MSG_USER_LOGGED_OUT, user);
            try {
                replyTo.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void userLoggedIn(Network.User user) {
        loggedInUsersByEmail.put(user.email, user);
        if(replyTo != null) {
            Message msg = Message.obtain(null, MSG_USER_LOGGED_IN, user);
            try {
                replyTo.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void loginRejected() {
        if(replyTo != null) {
            user = null;
            Message msg = Message.obtain(null, MSG_LOGIN_REJECTED);
            try {
                replyTo.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void conversationNotification(Network.ConversationNotification notification) {
        conversations.put(notification.conversation.id, notification.conversation);
        messagesByConversationId.put(notification.conversation.id, notification.log);
        if(replyTo != null) {
            Message msg = Message.obtain(null, MSG_NEW_CONVERSATION, notification.conversation);
            try {
                replyTo.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void conversationUpdated(Network.ConversationAlreadyExists notification) {
        conversations.put(notification.conversation.id, notification.conversation);
        messagesByConversationId.put(notification.conversation.id, notification.log);
        if(replyTo != null) {
            Message msg = Message.obtain(null, MSG_RELOAD_CONVERSATION, notification.conversation);
            try {
                replyTo.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
}
