package com.randombit.uskoci.game.control;

import com.randombit.uskoci.card.dao.CardDAO;
import com.randombit.uskoci.card.model.Card;
import com.randombit.uskoci.game.ActionNotAllowedException;
import com.randombit.uskoci.game.control.GameConstants.ACTION;
import com.randombit.uskoci.game.control.eventHandling.Action;
import com.randombit.uskoci.game.control.eventHandling.EventHandler;
import com.randombit.uskoci.game.control.eventHandling.Response;

import java.util.*;

import static com.randombit.uskoci.game.control.GameConstants.*;

public class GameControllerImpl implements GameController {

    private CardDAO cardDAO;

    private GameStatus gameStatus = new GameStatus();
    private EventHandler eventHandler = new EventHandler();

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    public GameControllerImpl(CardDAO cardDAO) {
        this.cardDAO = cardDAO;
    }

    @Override
    public void setCardDAO(CardDAO cardDAO) {
        this.cardDAO = cardDAO;
    }

    @Override
    public void setCardDeck(List<Card> cardDeck) {
        this.gameStatus.setCardDeck(cardDeck);
    }

    @Override
    public int getNumberOfPlayersJoined() {
        return gameStatus.getNumberOfPlayersJoined();
    }

    @Override
    public List<Card> getDiscardPile() {
        if (gameStatus.getDiscardedCards() == null)
            return Collections.<Card>emptyList();
        return gameStatus.getDiscardedCards();
    }

    @Override
    public List<Card> getResources(int playerId) {
        ResourcePile playerResources = gameStatus.getPlayersResources().get(String.valueOf(playerId));
        return playerResources.getResourcesList();
    }

    @Override
    public List<Card> getCardStack() {
        return gameStatus.getCardStack();
    }

    @Override
    public int getPlayersPoints(int playerId) {
        ResourcePile resourcePile = getPlayersResourcePile(playerId);
        return resourcePile.getValue();
    }

    @Override
    public void playCard(int playerId, int cardId) throws ActionNotAllowedException {

        Card cardPlayed = cardDAO.getCard(cardId);

        if (!gameStatus.beginningCardDrawn) {
            throw new ActionNotAllowedException(EXCEPTION_PLAY_CARD_BEGINNING_CARD_NOT_DRAWN);
        }

        if (playerPointsTooHigh(playerId, cardPlayed)) {
            throw new ActionNotAllowedException(EXCEPTION_PLAY_TOO_MUCH_RESOURCES);
        }

        if (cardIsEvent(cardPlayed)) {
            playEventCard(cardPlayed, playerId);
        } else {
            if (cardIsResource(cardPlayed)) {
                playResourceCard(cardPlayed, playerId);
            } else {
                if (cardIsMultiplier(cardPlayed)) {
                    putCardInPlayersResources(cardPlayed, playerId);
                } else {
                    throw new ActionNotAllowedException();
                }
            }
        }
        removeCardFromPlayersHand(cardPlayed, playerId);
    }

    private void playEventCard(Card cardPlayed, int playerId) throws ActionNotAllowedException {
        List<Card> currentCardStack = getCardStack();
        String cardSummary = cardPlayed.getSummary();


        if (!currentCardStack.isEmpty()) {
            if (cardCanBePlayedInResponse(cardSummary)) {
                //cardPlayed.setCardOwner(String.valueOf(playerId));
                putCardOnStack(cardPlayed);
            } else {
                throw new ActionNotAllowedException(EXCEPTION_EVENT_AFTER_EVENT);
            }
        } else {
            if (cardCanBePlayedInResponse(cardSummary)) {
                throw new ActionNotAllowedException(EXCEPTION_EVENT_AFTER_EVENT);
            }
            //cardPlayed.setCardOwner(String.valueOf(playerId));
            putCardOnStack(cardPlayed);
        }
    }
    
    @Override
    public Action resolveEventOnStack(int eventPlayerId){
    	Card event = getFirstCardOnStack();
    	Action action = resolveActions(eventHandler.resolveEvent(event, Integer.toString(eventPlayerId)));
    	return action;
    }
    
    @Override
    public Action sendResponse(Response response){
    	Action action = resolveActions(eventHandler.resolveResponseToEvent(response));
    	return action;
    }
    
    private Action resolveActions(List<Action> listOfActions){
    	boolean moreActions = true;
    	Action action = listOfActions.get(0);
    	if(action.getActionType().equals(ACTION.MOVE_CARDS.toString())){
			moveCards(action);
			listOfActions.remove(0);
    	}
    	
    	if (!listOfActions.isEmpty()){
	    	while(moreActions){
	    		action = listOfActions.get(0);
	    		if(action.getActionType().equals(ACTION.MOVE_CARDS.toString())){
	    			moveCards(action);
	    			listOfActions.remove(0);
	    		}
	    		else
	    		{
	    			moreActions = false;
	    		}	
	    	}
    	}
    	
    	if(!listOfActions.isEmpty()){
    		eventHandler.setActionList(listOfActions);
    		action = listOfActions.get(0);
    	}
    	else
    	{
    		action = new Action(ACTION.END.toString());
    	}
    	
       return action;
    }
    
    

    private boolean cardCanBePlayedInResponse(String cardSummary) {
        return WILL.equals(cardSummary) || FORT.equals(cardSummary);
    }

    private void playResourceCard(Card cardPlayed, int playerId) throws ActionNotAllowedException {
        List<Card> currentStack = getCardStack();

        if (playerIsNotOnTheMove(playerId)) {
            throw new ActionNotAllowedException(EXCEPTION_PLAYER_NOT_ON_MOVE_PLAYS_RESOURCE);
        }
        if (!currentStack.isEmpty()) {
            throw new ActionNotAllowedException(EXCEPTION_RESOURCE_AFTER_EVENT);
        }
        if (gameStatus.isResourceCardPlayed()) {
            throw new ActionNotAllowedException(EXCEPTION_PLAY_RESOURCE_TWICE);
        }

        gameStatus.setResourceCardPlayed(true);
        putCardInPlayersResources(cardPlayed, playerId);
    }

    private boolean playerPointsTooHigh(int playerId, Card cardPlayed) {
        int playersPoints = getPlayersPoints(playerId);

        if (cardIsResource(cardPlayed)) {
            if ((playersPoints + Integer.valueOf(cardPlayed.getValue())) > MAX_NUMBER_OF_PLAYER_POINTS) {
                return true;
            }
        }

        if (cardIsEvent(cardPlayed)) {
            //TODO
            return false;
        }
        return false;
    }

    private void removeCardFromPlayersHand(Card cardPlayed, int playerId) {
        gameStatus.getPlayerCardMap().get(String.valueOf(playerId)).remove(cardPlayed);
    }

    @Override
    public boolean isResourceCardPlayed() {
        return gameStatus.isResourceCardPlayed();
    }

    private boolean cardIsEvent(Card cardPlayed) {
        return EVENT.equals(cardPlayed.getType());
    }

    private boolean cardIsResource(Card cardPlayed) {
        return RESOURCE.equals(cardPlayed.getType());
    }

    private boolean cardIsMultiplier(Card cardPlayed) {
        return MULTIPLIER.equals(cardPlayed.getType());
    }

    private boolean playerIsNotOnTheMove(int playerId) {
        return playerId != gameStatus.getCurrentPlayerId();
    }

    private void putCardInPlayersResources(Card card, int playerId) {
        ResourcePile playersResourcePile = getPlayersResourcePile(playerId);
        playersResourcePile.put(card);
    }

    public ResourcePile getPlayersResourcePile(int playerId) {
        return gameStatus.getPlayersResources().get(String.valueOf(playerId));
    }

    private void putCardOnStack(Card card) {
        LinkedList<Card> currentCardStack = (LinkedList<Card>) getCardStack();
        currentCardStack.add(card);
    }

    private void removeCardFromStack(Card card) {
        List<Card> currentCardStack = getCardStack();
        currentCardStack.remove(card);
    }
    
    private Card  getFirstCardOnStack(){
        List<Card> currentCardStack = getCardStack();
        return currentCardStack.remove(0);
    }

    private boolean isNoOfCardsInHandValid() {
        return gameStatus.getPlayerCardMap().get(String.valueOf(gameStatus.getCurrentPlayerId())).size() < MAX_NUMBER_OF_CARDS_IN_HAND + 1;
    }

    @Override
    public boolean getBeginningCardDrawn() {
        return gameStatus.isBeginningCardDrawn();
    }

    @Override
    public int getCurrentPlayerId() {
        return gameStatus.getCurrentPlayerId();
    }

    @Override
    public int getNextPlayerId() {
//        int nextPlayer = (this.currentPlayerId == numberOfPlayersJoined) ? 1 : currentPlayerId++;
        int nextPlayer;
        if (gameStatus.getCurrentPlayerId() == gameStatus.getNumberOfPlayersJoined()) {
            nextPlayer = 1;
        } else {
            nextPlayer = gameStatus.getCurrentPlayerId() + 1;
        }
        return nextPlayer;
    }

    @Override
    public List<Card> getCardsInTheDeck() {
        return gameStatus.getCardDeck();
    }

    private void dealCards(int numberOfPlayers) {
        Collections.shuffle(gameStatus.getCardDeck());
        giveCardsToPlayers(numberOfPlayers);
    }

    private void giveCardsToPlayers(int numberOfPlayers) {
        for (int i = 1; i < numberOfPlayers + 1; i++) {
            List<Card> cardsDealtToPlayer = gameStatus.getCardDeck().subList(0, BEGINNING_NUMBER_OF_CARDS);
            gameStatus.getPlayerCardMap().put(String.valueOf(i), new ArrayList<Card>(cardsDealtToPlayer));
            gameStatus.getCardDeck().removeAll(gameStatus.getPlayerCardMap().get(String.valueOf(i)));
        }
    }

    @Override
    public String resetGame() {
        this.gameStatus.setCardDeck(new ArrayList<Card>(cardDAO.getAllCards()));
        this.gameStatus.setPlayerCardMap(new HashMap<String, List<Card>>());
        this.gameStatus.setResourceCardPlayed(false);
        this.gameStatus.setBeginningCardDrawn(false);
        this.gameStatus.setGameStarted(false);
        this.gameStatus.setPlayersResources(generateEmptyPlayersResources());
        this.gameStatus.setDiscardedCards(new ArrayList<Card>());
        this.gameStatus.setCardStack(new LinkedList<Card>());
        Random randomGenerator = new Random();
        this.gameStatus.setCurrentPlayerId(randomGenerator.nextInt(gameStatus.getNumberOfPlayersJoined() - 1) + 1);
        return "Game reset";
    }

    private HashMap<String, ResourcePile> generateEmptyPlayersResources() {
        HashMap<String, ResourcePile> playersResourcesHashMap = new HashMap<String, ResourcePile>();
        for (int playerId = 1; playerId <= gameStatus.getNumberOfPlayersJoined(); playerId++) {
            playersResourcesHashMap.put(String.valueOf(playerId), new ResourcePile(playerId));
        }
        return playersResourcesHashMap;
    }


    @Override
    public boolean startGame(int numberOfPlayersJoined) {
        if (numberOfPlayersJoined <= MAX_NUMBER_OF_PLAYERS || numberOfPlayersJoined >= MIN_NUMBER_OF_PLAYERS) {
            this.gameStatus.setNumberOfPlayersJoined(numberOfPlayersJoined);
            resetGame();
            dealCards(this.gameStatus.getNumberOfPlayersJoined());
            this.gameStatus.setGameStarted(true);
        }
        return gameStatus.isGameStarted();
    }

    public List<Card> getPlayerCards(int playerId) {
        return gameStatus.getPlayerCardMap().get(String.valueOf(playerId));
    }

    @Override
    public Card drawCard(int playerId) throws ActionNotAllowedException {
        if (gameStatus.isBeginningCardDrawn())
            throw new ActionNotAllowedException(EXCEPTION_DRAW_MORE_THAN_ONE_CARD);

        Card cardDrawn = gameStatus.getCardDeck().remove(0);
        gameStatus.getPlayerCardMap().get(String.valueOf(playerId)).add(cardDrawn);

        if (playerIsNotOnTheMove(playerId))
            throw new ActionNotAllowedException(EXCEPTION_DRAW_CARD_NOT_ON_THE_MOVE);

        if (!gameStatus.isBeginningCardDrawn())
            gameStatus.setBeginningCardDrawn(true);

        if (gameStatus.getCardDeck().isEmpty()) {
            Collections.shuffle(gameStatus.getDiscardedCards());
            gameStatus.getCardDeck().addAll(gameStatus.getDiscardedCards());
            gameStatus.getDiscardedCards().clear();
        }

        return cardDrawn;
    }

    @Override
    public void setNextPlayersTurn(int playerId) throws ActionNotAllowedException {
        if (!gameStatus.isBeginningCardDrawn()) {
            throw new ActionNotAllowedException(EXCEPTION_NEXT_TURN_NO_BEGINNING_CARD_DRAWN);
        }

        if (!playerOnTheMove(playerId)) {
            throw new ActionNotAllowedException(EXCEPTION_NEXT_PLAYER_BY_PLAYER_NOT_ON_THE_MOVE);
        }

        if (!isNoOfCardsInHandValid()) {
            throw new ActionNotAllowedException(EXCEPTION_NEXT_PLAYER_TOO_MUCH_CARDS_IN_HAND);
        }

        beginTurn();
    }

    private void beginTurn() {
        gameStatus.setCurrentPlayerId(getNextPlayerId());
        gameStatus.setBeginningCardDrawn(false);
        gameStatus.setResourceCardPlayed(false);
        gameStatus.getCardStack().clear();
    }

    private boolean playerOnTheMove(int playerId) {
        return playerId == gameStatus.getCurrentPlayerId();
    }

    @Override
    public boolean isGameStarted() {
        return gameStatus.isGameStarted();
    }

    @Override
    public void discardCardFromPlayersHand(int cardId, int playerId) {
        Card card = cardDAO.getCard(cardId);
        List<Card> playersCards = getPlayerCards(playerId);
        removeCardFromZone(card, playersCards);
    }

    private void removeCardFromZone(Card card, List<Card> cardList) {
        if (!cardList.isEmpty()) {
            cardList.remove(card);
            gameStatus.getDiscardedCards().add(card);
        }
    }

    @Override
    public void discardCardFromResourcePile(Card card, int playerId) {
        ResourcePile resourcePile = getPlayersResourcePile(playerId);
        resourcePile.remove(card);
        gameStatus.getDiscardedCards().add(card);
    }

    @Override
    public void discardCardFromResourcePile(int cardId, int playerId) {
        Card card = cardDAO.getCard(cardId);
        ResourcePile resourcePile = getPlayersResourcePile(playerId);
        resourcePile.remove(card);
        gameStatus.getDiscardedCards().add(card);
    }
    
    @Override
    public void removeCardFromResourcePile(Card card, int playerId) {
        ResourcePile resourcePile = getPlayersResourcePile(playerId);
        resourcePile.remove(card);
    }

    @Override
    public void removeMultiplierFromResourcePile(int playerId, int cardId) throws ActionNotAllowedException {
        Card card = cardDAO.getCard(cardId);

        if (cardIsMultiplier(card)) {
            discardCardFromResourcePile(card, playerId);
        } else {
            throw new ActionNotAllowedException();
        }
    }

    @Override
    public Card flipCardFaceUp() {
        Card flippedCard = gameStatus.getCardDeck().remove(0);
        gameStatus.getDiscardedCards().add(flippedCard);
        return flippedCard;
    }

    @Override
    public void moveCards(Action action) {
        List<Card> area1,area2 = new ArrayList<Card>();
        String delimiter = action.getDelimiter();
        String area1Type = action.getArea1();
        String area2Type = action.getArea2();
        
        if(area1Type.contains(QUANTITY.ALL.toString()) || area2Type.contains(QUANTITY.ALL.toString())){
        	
        	for(int playerId = 1; playerId < (gameStatus.getNumberOfPlayersJoined() + 1); playerId++) {
        		area1 = parseArea(action.getArea1(),playerId);
            	area2 = parseArea(action.getArea2(),playerId);
        		moveArea(area1Type,area2Type,area1,area2,playerId,delimiter);
        	}	
        }
        else {
        	area1 = parseArea(action.getArea1());
        	area2 = parseArea(action.getArea2());
        	if(!area1.isEmpty() && !area2.isEmpty()){
        		moveArea(area1Type,area2Type,area1,area2,0,delimiter);
        	}
        } 
    }
    
    private void moveArea(String area1Type, String area2Type,List<Card> area1, List<Card> area2,int playerId,String delimiter){
    	List<Card> cards = new ArrayList<Card>();
    	Response response = eventHandler.getEventResponse();
    	if(delimiter == "all"){
    		for(int i=0; i< area1.size(); i++){
    			Card card = area1.remove(i);
    			if (area1Type.contains(AREA.RESOURCE.toString())){
    				removeCardFromResourcePile(card,playerId);
    			}
    			area2.add(card);
        		if (area2Type.contains(AREA.RESOURCE.toString())){
    				putCardInPlayersResources(card,playerId);
    			}
    		}
       	}
       	if(delimiter == "half"){
        	for(int i=0; i< (area1.size()/2); i++){
        		Card card = area1.remove(i);
        		if (area1Type.equals(ACTION.MOVE_CARDS.toString())){
    				discardCardFromResourcePile(card,playerId);
    			}
        		area2.add(card);
        		if (area2Type.equals(ACTION.MOVE_CARDS.toString())){
    				putCardInPlayersResources(card,playerId);
    			}
       		}
       	}
       	if(delimiter == "card"){
       		Card card = response.getCard();
       		area1.remove(card);
       		area2.add(card);
       	}
       	if(delimiter == "cards"){
        	cards = response.getListOfCards();
        	for(Card card : cards){
        		area1.remove(card);
       			area2.add(card);
       		}
       	}
    }
    
    private List<Card> parseArea(String area){
    	String delim ="-";
    	String[] tokens = area.split(delim);
    	String playerId = "";
    	if(tokens.length > 1){
    		playerId = tokens[1];
    	}
    	
    	return parseArea(area, Integer.parseInt(playerId));
    }
    private List<Card> parseArea(String area, int playerId){    	
    	if(area.contains(AREA.RESOURCE.toString())){
    		return getResources(playerId);		
    	}
    	if(area.contains(AREA.DISCARDPILE.toString())){
    		return getDiscardPile();
    	}
    	if(area.contains(AREA.HAND.toString())){
    		return getPlayerCards(playerId);
    	}	
    	return Collections.emptyList();
    }
    
    
    @Override
    public void changeValue(Card card) {
        // TODO : Implement old Sea Dog interface towards frontend
    }

    @Override
    public List<Card> getPlayersResourcesByType(int playerId, String resourceType) {
        List<Card> playersResources = getResources(playerId);
        List<Card> playersResourcesOfSelectedType = new ArrayList<Card>();

        for (Card resource:playersResources) {
           if (resource.getSummary().contains(resourceType)) {
               playersResourcesOfSelectedType.add(resource);
           }
        }
        return playersResourcesOfSelectedType;
    }


    private <T> List<Card> peakCards(List<Card> area, int start, int end, T delimiter) {
        List<Card> cards = new ArrayList<Card>();

        if (delimiter == "all") {
            start = 0;
            end = area.size();
        }

        for (int i = start; i < end; i++) {
            cards.add(area.get(i));
        }

        return cards;
    }

	

}