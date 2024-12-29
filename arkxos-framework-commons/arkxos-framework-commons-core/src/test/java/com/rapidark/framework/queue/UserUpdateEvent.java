package com.rapidark.framework.queue;

import com.arkxos.framework.queue2.Message;



public class UserUpdateEvent extends Message<User> {  
    
    public UserUpdateEvent(User user){  
        super("UserUpdateEvent", user);  
    }  
      
}  
