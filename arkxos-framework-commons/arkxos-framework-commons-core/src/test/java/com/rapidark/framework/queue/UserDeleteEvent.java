package com.arkxos.framework.queue;

import com.arkxos.framework.queue2.Message;



public class UserDeleteEvent extends Message<User> {  
    
    public UserDeleteEvent(User user){  
        super("UserDeleteEvent", user);  
    }  
      
}  
