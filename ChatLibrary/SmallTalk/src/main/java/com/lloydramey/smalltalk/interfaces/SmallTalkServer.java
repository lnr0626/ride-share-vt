package com.lloydramey.smalltalk.interfaces;

import com.lloydramey.smalltalk.exceptions.SmallTalkException;
import com.lloydramey.smalltalk.listeners.ConversationListener;
import com.lloydramey.smalltalk.listeners.MessageListener;

import java.util.ArrayList;

/**
 * Created by lloyd on 11/25/13.
 */
public interface SmallTalkServer {
    public User createUser(User user) throws SmallTalkException;
    public User getUserInfo(User user) throws SmallTalkException;
    public void deleteUser(User user) throws SmallTalkException;

    public Conversation createConversation(Conversation conversation, User... users) throws SmallTalkException;
    public void deleteConversation(Conversation conversation, User user) throws SmallTalkException;

    public void sendMessage(Conversation conversation, Message message) throws SmallTalkException;
    public Conversation getConversationInfo(Conversation conversation) throws SmallTalkException;
    public Conversation getConversation(Conversation conversation) throws SmallTalkException;

    public ArrayList<Conversation> getAllConversations(User user) throws SmallTalkException;

    public void addMessageListener(MessageListener listener);
    public void removeMessageListener(MessageListener listener);

    public void addConversationListener(ConversationListener listener);
    public void removeConversationListener(ConversationListener listener);
}
