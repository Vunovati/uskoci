package com.randombit.uskoci.rest.game;

import org.atmosphere.annotation.Broadcast;
import org.atmosphere.annotation.Suspend;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/")
public class GameResource {

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
     * @param message a {@link GameMessage}
     * @return a {@link GameResponse}
     */
    @Broadcast(writeEntity = false)
    @POST
    @Produces("application/json")
    public GameResponse broadcast(GameMessage message) {
        return new GameResponse(message.author, message.message);
    }
}

