package com.lloydramey.smalltalk.listeners;

import com.lloydramey.smalltalk.interfaces.Conversation;

/**
 * Created by lloyd on 11/25/13.
 */
public interface ConversationListener {
    public void newConversation(Conversation conversation);
    public void updatedConversation(Conversation conversation);
    public void conversationDeleted(Conversation conversation);
}
