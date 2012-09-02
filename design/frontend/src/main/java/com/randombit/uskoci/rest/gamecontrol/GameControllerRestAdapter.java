package com.randombit.uskoci.rest.gamecontrol;

/**
 * Created with IntelliJ IDEA.
 * User: ovca
 * Date: 30.08.12.
 * Time: 21:26
 * To change this template use File | Settings | File Templates.
 */
public interface GameControllerRestAdapter {
    public GameStatusResponse getResponse(GameStatusMessage message, String gameId);
}
