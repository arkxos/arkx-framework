package com.rapidark.framework.queue;

import com.rapidark.framework.queue2.Message;



public class UserDeleteEvent extends Message<User> {  
    
    public UserDeleteEvent(User user){  
        super("UserDeleteEvent", user);  
    }  
      
}  
