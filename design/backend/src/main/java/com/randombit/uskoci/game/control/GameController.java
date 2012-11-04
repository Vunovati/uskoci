package com.randombit.uskoci.game.control;

import com.randombit.uskoci.card.dao.CardDAO;
import com.randombit.uskoci.card.model.Card;
import com.randombit.uskoci.game.ActionNotAllowedException;
import com.randombit.uskoci.game.control.eventHandling.Action;
import com.randombit.uskoci.game.control.eventHandling.Response;

import java.util.List;

public interface GameController {

    public GameStatus getGameStatus();

    void setGameStatus(GameStatus gameStatus);

    public int getCurrentPlayerId();

    public int getNextPlayerId();

    public void setNextPlayersTurn(int playerId) throws ActionNotAllowedException;

    public List<Card> getCardsInTheDeck();

    public String resetGame();

    public boolean startGame(int numberOfPlayers);

    public void setCardDAO(CardDAO cardDAO);

    public List<Card> getPlayerCards(int playedId);

    public Card drawCard(int playerId) throws ActionNotAllowedException;

    public boolean getBeginningCardDrawn();

    public boolean isGameStarted();

    public int getNumberOfPlayersJoined();

    public List<Card> getDiscardPile();

    public List<Card> getResources(int playerId);

    public int getPlayersPoints(int playerId);

    public void playCard(int playerId, int cardId) throws ActionNotAllowedException;

    public boolean isResourceCardPlayed();
    
    public void discardCardFromPlayersHand(int cardId, int playerId);
    
    public void discardCardFromResourcePile(int cardId, int playerId);

    public void removeMultiplierFromResourcePile(int playerId, int cardId ) throws ActionNotAllowedException;

    public Card flipCardFaceUp();

    public List<Card> getCardStack();

    public void setCardDeck(List<Card> cards);
    
    public void moveCard(Action action);
    
    public void moveCards(Action action);
    
    public void changeValue(Card card);
    
    public List<Action> resolveEventOnStack(int eventPlayerId);

}
