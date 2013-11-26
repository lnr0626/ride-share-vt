package com.lloydramey.smalltalk.listeners;

import com.lloydramey.smalltalk.interfaces.Conversation;
import com.lloydramey.smalltalk.interfaces.Message;
import com.lloydramey.smalltalk.interfaces.User;

/**
 * Created by lloyd on 11/25/13.
 */
public interface MessageListener {
    public void newMessageInConversation(Conversation conversation, Message message);
    public void messageSeenByRecipient(Conversation conversation, Message message, User user);
}
