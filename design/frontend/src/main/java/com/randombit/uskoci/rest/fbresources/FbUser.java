package com.randombit.uskoci.rest.fbresources;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class FbUser {
    
	public String userID = "";
    
    public String userName = "";
    
    public String text = "";
    
	public FbUser(){

	} 

    public FbUser(String userID, String userName) {
        
        this.userID = userID;
        this.userName = userName;
    }
    
    public FbUser(String userID, String userName, String text) {
        this.userID = userID;
        this.userName = userName;
        this.text = text;
    }   
    
}
