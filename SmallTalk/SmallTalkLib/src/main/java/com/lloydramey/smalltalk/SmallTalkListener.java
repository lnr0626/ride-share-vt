package com.lloydramey.smalltalk;

import java.util.ArrayList;

import com.lloydramey.smalltalk.Network.Message;

public interface SmallTalkListener {
	public void messageReceived(Message message);
	public void messageLogReceived(ArrayList<Message> messages);
	public void loggedInUsersReceived(String[] names);
	public void userLoggedOut(String name);
	public void userLoggedIn(String name);
	public void loginRejected();
}
