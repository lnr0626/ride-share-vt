package com.lloydramey.smalltalk.interfaces;

import java.util.List;

/**
 * Created by lloyd on 11/25/13.
 */
public interface User<E> {
    public E getId();
    public void setId(E id);

    public List<Conversation> getConversations();
    public String getName();
}
