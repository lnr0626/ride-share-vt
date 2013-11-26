package com.lloydramey.smalltalk.interfaces;

import java.util.Date;

/**
 * Created by lloyd on 11/25/13.
 */
public interface Message<E> {

    public E getId();
    public void setId(E id);

    public Conversation getConversation();
    public String getBody();
    public Date getSentDate();
    public User getSender();
}
