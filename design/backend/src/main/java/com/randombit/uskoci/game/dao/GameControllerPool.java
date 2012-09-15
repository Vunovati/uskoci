package com.randombit.uskoci.game.dao;

import com.randombit.uskoci.card.dao.MongoDBCard;
import com.randombit.uskoci.game.GameController;
import com.randombit.uskoci.game.GameControllerImpl;

import java.util.HashMap;
import java.util.Map;

public enum GameControllerPool {
    instance;

    private Map<String, GameController> gameControllerMap = new HashMap<String, GameController>();

    private GameControllerPool() {

        GameController gameController = new GameControllerImpl(MongoDBCard.instance);
        gameControllerMap.put("1", gameController);

    }

    public GameController getController(int gameControllerId) {
        return gameControllerMap.get(String.valueOf(gameControllerId));
    }
}
