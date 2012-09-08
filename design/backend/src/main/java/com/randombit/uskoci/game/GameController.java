package com.randombit.uskoci.game;

import com.randombit.uskoci.card.dao.CardDAO;
import com.randombit.uskoci.card.model.Card;

import java.util.List;

public interface GameController {
    /**
     * Get the Id of the player that is on the move
     * @return playerOnTHeMoveId
     */
    public int getCurrentPlayerId();

    /**
     * Set the id of player on the move to the next player
     * @return playerOnTheMoveId
     */
    public int getNextPlayerId();

    public List<Card> getCardsInTheDeck();

    public String resetGame();

    public boolean startGame(int numberOfPlayers);

    public void setCardDAO(CardDAO cardDAO);

    public List<Card> getPlayerCards(int playedId);

    public Card drawCard(int playerId) throws ActionNotAllowedException;

    public int getCurrentPhase();

    public int setNextPhase();

    int setPhase(int phase);

    public boolean getBeginningCardDrawn();

    public boolean isGameStarted();

    public int getNumberOfPlayersJoined();

    public List<Card> getDiscardPile();

    public List<Card> getResources(int playerId);

    public int getPlayersPoints(int playerId);

    public void playCard(int playerId, int cardId) throws ActionNotAllowedException;
}
