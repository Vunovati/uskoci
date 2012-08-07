package com.randombit.uskoci.game;

import com.randombit.uskoci.card.dao.CardDAO;
import com.randombit.uskoci.card.model.Card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameControllerImpl implements GameController {

    private int currentPlayerId = 1;
    private List<Card> cardsOnTheTable = Collections.<Card>emptyList();

    // TODO implement methods
    public int getCurrentPlayerId() {
        return currentPlayerId;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public int setNextPlayer() {
        return currentPlayerId++;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public List<Card> getCardsOnTheTable() {
        return cardsOnTheTable;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void putCardOnTheTable(int id) {
        Card card = CardDAO.instance.getModel().get(String.valueOf(id));

        if (cardsOnTheTable.isEmpty())
            cardsOnTheTable = new ArrayList<Card>();

        cardsOnTheTable.add(card);
    }

    public String resetGame() {
        currentPlayerId = 1;
        return "Game reset";  //To change body of implemented methods use File | Settings | File Templates.
    }
}
