package com.randombit.uskoci.rest.gamecontrol;

import org.atmosphere.annotation.Broadcast;
import org.atmosphere.annotation.Suspend;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/gamecontrol")
public class GameControlResource {


    /**
     * Suspend the response without writing anything back to the client.
     *
     * @return a white space
     */
    @Suspend(contentType = "application/json")
    @GET
    public String suspend() {
        return "";
    }


    /**
     * Broadcast the received message object to all suspended response. Do not write back the message to the calling connection.
     *
     * @param message game status message containing the player action and id
     * @return GameStatusResponse
     */
    @Broadcast(writeEntity = false)
    @POST
    @Produces("application/json")
    public GameStatusResponse broadcast(GameStatusMessage message) {
        GameControllerRestAdapter gameControllerRestAdapter = new GameControllerRestAdapterImpl();
        GameStatusResponse statusResponse = gameControllerRestAdapter.getResponse(message, String.valueOf(1));
        return statusResponse;
    }
}
