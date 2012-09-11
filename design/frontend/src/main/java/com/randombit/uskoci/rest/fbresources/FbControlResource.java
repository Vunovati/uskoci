package com.randombit.uskoci.rest.fbresources;

import org.atmosphere.annotation.Broadcast;
import org.atmosphere.annotation.Suspend;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.ArrayList;

@Path("/fbresources")
public class FbControlResource {
    
	public  ArrayList<FbUser> fbListOfPlayers = new ArrayList<FbUser>();

    @Suspend(contentType = "application/json")
    @GET
    public String suspend() {
        return "";
    }
	
	@POST
	@Path("/fbresources")
	@Produces("application/json")
	public ArrayList<FbUser> FbAddUserResource(FbUser user){
        
		   fbListOfPlayers.add(user);	
		   return fbListOfPlayers;	
	} 

    /**
     * Broadcast the received message object to all suspended response. Do not write back the message to the calling connection.
     * This method is used to share FB resources among players. In game chat is implemented by this method.    
     */
    @Broadcast(writeEntity = false)
    @POST
    @Produces("application/json")
    public FbUser broadcast(FbUser message) {
        
        return message;
    }
}
