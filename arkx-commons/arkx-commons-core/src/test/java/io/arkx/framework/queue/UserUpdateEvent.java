package io.arkx.framework.queue;

import io.arkx.framework.queue2.Message;



public class UserUpdateEvent extends Message<User> {  
    
    public UserUpdateEvent(User user){  
        super("UserUpdateEvent", user);  
    }  
      
}  
