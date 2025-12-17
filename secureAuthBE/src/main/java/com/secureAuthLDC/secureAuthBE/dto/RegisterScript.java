package com.secureAuthLDC.secureAuthBE.dto;

public class RegisterScript {
	
	private String username;
	private String email;
    private String password;
    private boolean twoFactorEnabled;
   
    //i setter di questa classe sono necessari perchè vengono utilizzati da Jackson (componente utile per convertire da json a oggetto java)

    public String getUsername() { 
    	return username; 
    }
    
    public void setUsername(String username) { 
    	this.username = username; 
    }//da controllare se l'username esiste già (da fare nel service)
    
    
    
    public String getEmail() { 
    	return email; 
    }
    
    public void setEmail(String email) { 
    	this.email = email;
    }

    
    
    public String getPassword() { 
    	return password; 
    }
    
    public void setPassword(String password) {
    	this.password = password;
    }
    
    
    
    public boolean getTwoFactorEnabled() { 
    	return twoFactorEnabled; 
    }
    
    public void setTwoFactorEnabled(boolean twoFactorEnabled) { 
    	this.twoFactorEnabled = twoFactorEnabled;
    }
}
