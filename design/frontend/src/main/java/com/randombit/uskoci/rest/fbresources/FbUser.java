package com.randombit.uskoci.rest.fbresources;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class FbUser {
    
	public String userID = "";
    
    public String playerID = "";
    
    public String userName = "";
    
    public String text = "";
    
    public String msgType = "";
    
	public FbUser(){

	} 

    public FbUser(String userID, String userName) {
        
        this.userID = userID;
        this.userName = userName;
    }
    
    public FbUser(String userID, String playerID, String userName, String text, String msgType) {
        this.userID = userID;
        this.userName = userName;
        this.text = text;
        this.msgType = msgType;
    }
    
    
}
