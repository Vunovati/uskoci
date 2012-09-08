package com.randombit.uskoci.rest.gamecontrol;


public interface GameControllerRestAdapter {
    public GameStatusResponse getResponse(GameStatusMessage message, String gameId);
}
