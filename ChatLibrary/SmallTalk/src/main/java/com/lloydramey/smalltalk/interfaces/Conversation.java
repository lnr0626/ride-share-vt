package com.lloydramey.smalltalk.interfaces;

import java.util.List;
import java.util.Map;

/**
 * Created by lloyd on 11/25/13.
 */
public interface Conversation<E> {
    public E getId();
    public void setId(E id);

    public List<Message> getMessages();
    public List<User> getUsers();
    public Map<User, Message> getLastSeenMessages();
}
