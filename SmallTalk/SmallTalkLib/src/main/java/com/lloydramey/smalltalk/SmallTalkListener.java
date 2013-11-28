package com.lloydramey.smalltalk;

import java.util.ArrayList;

import com.lloydramey.smalltalk.Network.Message;

public interface SmallTalkListener {
	public void messageReceived(Message message);
	public void loggedInUsersReceived(ArrayList<Network.User> users);
	public void userLoggedOut(Network.User user);
	public void userLoggedIn(Network.User user);
	public void loginRejected();
    public void conversationNotification(Network.ConversationNotification conversation);
    public void conversationUpdated(Network.ConversationAlreadyExists conversationAlreadyExists);
}
