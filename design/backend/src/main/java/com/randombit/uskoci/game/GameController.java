package com.randombit.uskoci.game;

import com.randombit.uskoci.card.model.Card;

import java.util.List;

public interface GameController {
    /**
     * Get the Id of the player that is on the move
     * @return playerOnTHeMoveId
     */
    public String getCurrentPlayerId();

    /**
     * Set the id of player on the move to the next player
     * @return playerOnTheMoveId
     */
    public String setNextPlayer();

    public List<Card> getCardsOnTheTable();

    public void putCardOnTheTable();

    public String resetGame();
}
