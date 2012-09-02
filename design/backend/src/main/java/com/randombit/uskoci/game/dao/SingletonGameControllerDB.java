package com.randombit.uskoci.game.dao;

import com.randombit.uskoci.game.GameController;
import com.randombit.uskoci.game.GameControllerImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum SingletonGameControllerDB {
    instance;

    private Map<String, GameController> gameControllerMap = new HashMap<String, GameController>();

    private SingletonGameControllerDB() {

        GameController gameController = new GameControllerImpl();
        gameControllerMap.put("1", gameController);


    }

    public List<GameController> getAllControllers() {
        return new ArrayList<GameController>(gameControllerMap.values());
    }
}
