package com.randombit.uskoci.rest.gamecontrol;

import com.randombit.uskoci.game.GameController;
import com.randombit.uskoci.game.GameControllerImpl;
import org.atmosphere.annotation.Broadcast;
import org.atmosphere.annotation.Suspend;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/gameControl")
public class GameControlResource {
    GameController gameController = new GameControllerImpl();

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
        // TODO do some logic regarding player and the action

        /*Some action is performed by the player*/
        gameController.setNextPlayer();
        String currentPlayerId = String.valueOf(gameController.getCurrentPlayerId());

        return new GameStatusResponse(currentPlayerId);
    }
}
