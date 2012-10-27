package com.randombit.uskoci.game;

import com.randombit.uskoci.card.dao.CardDAO;
import com.randombit.uskoci.card.model.Card;
import com.randombit.uskoci.game.control.eventmessage.Action;
import com.randombit.uskoci.game.control.eventmessage.Response;

import java.util.*;

public class GameControllerImpl implements GameController {

    private CardDAO cardDAO;

    private List<Card> cardDeck;

    private List<Card> discardedCards;
    private Map<String, List<Card>> playerCardMap;
    private int currentPlayerId;
    private boolean beginningCardDrawn;
    private boolean gameStarted;
    private int numberOfPlayersJoined;
    private boolean resourceCardPlayed;
    private Map<String, List<Card>> playersResources;
    private LinkedList<Card> cardStack;
    private int chosenPlayer;
    
    public GameControllerImpl(CardDAO cardDAO) {
        this.cardDAO = cardDAO;
    }


    @Override
    public void setCardDAO(CardDAO cardDAO) {
        this.cardDAO = cardDAO;
    }

    @Override
    public void setCardDeck(List<Card> cardDeck) {
        this.cardDeck = cardDeck;
    }

    @Override
    public int getNumberOfPlayersJoined() {
        return numberOfPlayersJoined;
    }

    @Override
    public List<Card> getDiscardPile() {
        if (discardedCards == null)
            return Collections.<Card>emptyList();
        return discardedCards;
    }

    @Override
    public List<Card> getResources(int playerId) {
        List<Card> playerResources = playersResources.get(String.valueOf(playerId));

        if (playerResources == null)
            playerResources = Collections.<Card>emptyList();
        return playerResources;
    }

    @Override
    public LinkedList<Card> getCardStack() {
    	return cardStack;
    }

    @Override
    public int getPlayersPoints(int playerId) {
        int playersPoints = 0;
        List<Card> playersResources = getResources(playerId);
        for (Card card:playersResources) {
            if (GameConstants.RESOURCE.equals(card.getType())) {
                playersPoints += Integer.valueOf(card.getValue());
            }
        }
        return playersPoints;
    }

    @Override
    public void playCard(int playerId, int cardId) throws ActionNotAllowedException {
        
    	Card cardPlayed = cardDAO.getCard(cardId);

        if(playerPointsTooHigh(playerId, cardPlayed)){
            throw new ActionNotAllowedException();
        }

        if(cardIsEvent(cardPlayed))  {
            playEventCard(cardPlayed, playerId);
        }
        else  {
        	if(cardIsResource(cardPlayed)){
        		playResourceCard(cardPlayed, playerId);
        	}
        	else{
        		if(cardIsMultiplier(cardPlayed)){
        			putCardInPlayersResources(cardPlayed, playerId);
        		}
        		else {
        			throw new ActionNotAllowedException();
        		}
        	}    
        }
        removeCardFromPlayersHand(cardPlayed, playerId);
    }
    
    private void playEventCard(Card cardPlayed, int playerId) throws ActionNotAllowedException {
        LinkedList<Card> currentCardStack = getCardStack();
        String cardSummary = cardPlayed.getSummary();
        
       if(!currentCardStack.isEmpty()) {
        	if(cardSummary == "Will of God" || cardSummary == "Fortress of Nehaj") {
        		//cardPlayed.setCardOwner(String.valueOf(playerId));
        		putCardOnStack(cardPlayed);
        	}
        	else {
        		throw new ActionNotAllowedException();
        	}	
       }
       else {
    	   if(cardSummary == "Will of God" || cardSummary == "Fortress of Nehaj") {
    		   throw new ActionNotAllowedException();
       		}
           //cardPlayed.setCardOwner(String.valueOf(playerId));
           putCardOnStack(cardPlayed); 
       }
    } 
    
    private void playResourceCard(Card cardPlayed, int playerId) throws ActionNotAllowedException {
    	LinkedList<Card> currentStack = getCardStack();

    	if(playerIsNotOnTheMove(playerId)){
    		throw new ActionNotAllowedException();
    	}
        if(!currentStack.isEmpty()) {
        	throw new ActionNotAllowedException();
        }
        if(resourceCardPlayed) {
        	throw new ActionNotAllowedException();
        }
        resourceCardPlayed = true;
        putCardInPlayersResources(cardPlayed, playerId);
    }

    @Override
    public List<Action> resolveCardsOnStack() {
    	 List<Action> response = new ArrayList<Action>();
    	 Card event;
    	 LinkedList <Card> cardStack = getCardStack();
    	 
    	 while(!cardStack.isEmpty()){
    		 event = cardStack.remove();
    		 response = resolveEvent(event,0,Collections.<Response>emptyList());
    	 }
    	return response;
    }
    
    @Override
	public List<Action> responseToEvent(Card event, int playerId, List<Response> responseList) {
		return resolveEvent(event,playerId, responseList);
	}
    
    public List<Action> resolveEvent(Card event, int eventPlayerId, List<Response> responseList){
    	List<Card> cards = new ArrayList<Card>();
    	List<Integer> playersAffectedByAction = new ArrayList<Integer>();
    	List<Action> listOfActions = new ArrayList<Action>();
    	String cardSummary = event.getSummary();
    	LinkedList <Card> cardStack = getCardStack();
    	
    	boolean lastAction = true;
    	boolean moreActions = false;
    	Action action;
    	
    	if(GameConstants.WILL.equals(cardSummary)){
    		cards.add(cardStack.get(0));
    		action = new Action(eventPlayerId,"remove from stack",cards,lastAction);
    		//cardStack.remove();
    		return listOfActions;
    	}
    	if(GameConstants.STORM.equals(cardSummary)){
    		for(int playerId= 1; playerId < (getNumberOfPlayersJoined() + 1); playerId++){
    			//moveCards(getResources(playerId), getDiscardPile(), 0, 0, "all");
    			if(playerId < getNumberOfPlayersJoined()) {
    				action = new Action(playerId,"move cards",getResources(playerId),getDiscardPile(),0,0,"all",moreActions);
    			}
    			else {
    				action = new Action(playerId,"move cards",getResources(playerId),getDiscardPile(),0,0,"all",lastAction);
    			}
    			listOfActions.add(action);
    		}
    		return listOfActions;
    	}
    	if(GameConstants.SPYGLASS.equals(cardSummary)){
    		playersAffectedByAction.add(eventPlayerId);
    		for(int playerId= 1; playerId < (getNumberOfPlayersJoined() + 1); playerId++){
    			if(playerId != getNumberOfPlayersJoined() ){
    				action = new Action(playerId,"Reveal",getPlayerCards(playerId),playersAffectedByAction,moreActions);
    				listOfActions.add(action);
    			}
    			else {
    				action = new Action(playerId,"Reveal",getPlayerCards(playerId),playersAffectedByAction,lastAction);
    				listOfActions.add(action);
    			}
    		}
    	}
    	if(GameConstants.SPY.equals(cardSummary)){
    		
    		if(responseList.isEmpty()){
    			action = new Action(eventPlayerId,"Choose player",moreActions);
    			listOfActions.add(action);
    			return listOfActions;
    		}
    		else {
    			Response response = responseList.remove(0);
    			String responseType = response.getResponseType();
    			if(responseType == "Players") {
    				List<Integer> choseFromPlayers = new ArrayList<Integer>();
    				int chosenPlayerId = response.getPlayersAffectedByResponse().remove(0);
    				choseFromPlayers.add(chosenPlayerId);
    				action = new Action(eventPlayerId,"Choose card",getPlayerCards(chosenPlayerId),choseFromPlayers,moreActions);
    				listOfActions.add(action);
    				listOfActions.add(action);
    				return listOfActions;
    			}
    			if(responseType == "Cards"){
    				action = new Action(eventPlayerId,"Play cards",response.getCards(),lastAction);
    				listOfActions.add(action);
    				return listOfActions;
    			}
    		}
    	}
    	if(GameConstants.TRICKERY.equals(cardSummary)){
    		if(responseList.isEmpty()){
    			action = new Action(eventPlayerId,"Choose a player",moreActions);
    			listOfActions.add(action);
    			return listOfActions;
    		}
    		Response response = responseList.remove(0);
			String responseType = response.getResponseType();
    		if(responseType == "Players") {
    			int chosenPlayerId = response.getPlayersAffectedByResponse().remove(0);
    			int numOfResources = getResources(eventPlayerId).size();
    			//moveCards(getResources(eventPlayerId), getResources(chosenPlayerId), 0, 0, "all");
    			action = new Action(eventPlayerId,"move cards",getResources(eventPlayerId),getResources(chosenPlayerId),0,0,"all",moreActions);
    			listOfActions.add(action);
    			//moveCards(getResources(chosenPlayerId),getResources(eventPlayerId) , numOfResources, getResources(chosenPlayerId).size(), "");
    			action = new Action(eventPlayerId,"move cards",getResources(chosenPlayerId),getResources(eventPlayerId),numOfResources,getResources(chosenPlayerId).size(),"",lastAction);
    			listOfActions.add(action);
    			return listOfActions;
    		}
    		
    	}
    	if(GameConstants.THEFT.equals(cardSummary)){
    		if(responseList.isEmpty()){
    			action = new Action(eventPlayerId,"Choose a player",moreActions);
    			listOfActions.add(action);
    			return listOfActions;
    		}
    		Response response = responseList.remove(0);
			String responseType = response.getResponseType();
    		if(responseType == "Players") {
				List<Integer> choseFromPlayers = new ArrayList<Integer>();
				chosenPlayer = response.getPlayersAffectedByResponse().remove(0);
				choseFromPlayers.add(chosenPlayer);
				action = new Action(eventPlayerId,"Choose a card",getPlayerCards(chosenPlayer),choseFromPlayers,moreActions);
				listOfActions.add(action);
				listOfActions.add(action);
				return listOfActions;
			}
    		if(responseType == "Cards"){
				Card chosenCard = response.getCards().remove(0);
				//moveCard(getResources(chosenPlayer),getResources(eventPlayerId),chosenCard);
				action = new Action(eventPlayerId,"move a card",getResources(chosenPlayer),getResources(eventPlayerId),chosenCard,lastAction);
				listOfActions.add(action);
				return listOfActions;
    		}
    		
    	}
    	if(GameConstants.SNITCH.equals(cardSummary)){
    		List<Card> cardDeck = getCardsInTheDeck();
    		Card card = cardDeck.get(0);
    		int pos = 1;
    		cards.add(card);
    		for(int playerId= 1; playerId < (getNumberOfPlayersJoined() + 1); playerId++){
    			playersAffectedByAction.add(playerId);
    		}
    		while(GameConstants.EVENT.equals(card.getType())) {
    			action = new Action(eventPlayerId,"Reveal",cards,playersAffectedByAction,moreActions);
    			listOfActions.add(action);
    			action = new Action(eventPlayerId,"move a card",cardDeck,getDiscardPile(),card,moreActions);
    			listOfActions.add(action);
    			card = cardDeck.get(pos++);
    		}
    		action = new Action(eventPlayerId,"Reveal",cards,playersAffectedByAction,moreActions);
    		for(int playerId= 1; playerId < (getNumberOfPlayersJoined() + 1); playerId++){
    			if(playerId < getNumberOfPlayersJoined()) {
    				action = new Action(eventPlayerId,"move cards",getResources(playerId),getDiscardPile(),0,0,card.getColor(),moreActions);
    			}
    			else
    				action = new Action(eventPlayerId,"move cards",getResources(playerId),getDiscardPile(),0,0,card.getColor(),lastAction);
    			listOfActions.add(action);
    		}
    		return listOfActions;	
    	}
    	if(GameConstants.SEADOG.equals(cardSummary)){
    		action = new Action(eventPlayerId,"move a card",Collections.<Card> emptyList(),getResources(eventPlayerId),event,lastAction);
    		listOfActions.add(action);
    		return listOfActions;
    	}
    	
   
    	return Collections.<Action> emptyList();
    }
    
    private boolean playerPointsTooHigh(int playerId, Card cardPlayed){
        int playersPoints = getPlayersPoints(playerId);
        
        if (cardIsResource(cardPlayed)) {
            if((playersPoints + Integer.valueOf(cardPlayed.getValue())) > GameConstants.MAX_NUMBER_OF_PLAYER_POINTS){
                return true;
            }    
        }

        if(cardIsEvent(cardPlayed)) {
            //TODO
            return false;
        }


        return false;
    }  

    private void removeCardFromPlayersHand(Card cardPlayed, int playerId) {
        playerCardMap.get(String.valueOf(playerId)).remove(cardPlayed);
    }

    @Override
    public boolean isResourceCardPlayed() {
        return resourceCardPlayed;
    }

    private boolean cardIsEvent(Card cardPlayed) {
        return GameConstants.EVENT.equals(cardPlayed.getType());
    }
    
    private boolean cardIsResource(Card cardPlayed){

        return GameConstants.RESOURCE.equals(cardPlayed.getType());
    }

    private boolean cardIsMultiplier(Card cardPlayed){

        return GameConstants.MULTIPLIER.equals(cardPlayed.getType());
    }    

    private boolean playerIsNotOnTheMove(int playerId) {
        return playerId != currentPlayerId;
    }

    private void putCardInPlayersResources(Card card, int playerId) {
        List<Card> playersCards = getResources(playerId);
        playersCards.add(card);
    }

    private void putCardOnStack(Card card){
        LinkedList<Card> currentCardStack = getCardStack();
        currentCardStack.add(card);
    }    

    private void removeCardFromStack(Card card){
        List<Card> currentCardStack = getCardStack();
        currentCardStack.remove(card);
    }   

    private boolean isNoOfCardsInHandValid() {
        return playerCardMap.get(String.valueOf(currentPlayerId)).size() < GameConstants.MAX_NUMBER_OF_CARDS_IN_HAND + 1;
    }

    @Override
    public boolean getBeginningCardDrawn() {
        return beginningCardDrawn;
    }

    @Override
    public int getCurrentPlayerId() {
        return currentPlayerId;
    }

    @Override
    public int getNextPlayerId() {
//        int nextPlayer = (this.currentPlayerId == numberOfPlayersJoined) ? 1 : currentPlayerId++;
        int nextPlayer;
        if (currentPlayerId == numberOfPlayersJoined) {
            nextPlayer = 1;
        } else {
            nextPlayer = currentPlayerId + 1;
        }
        return nextPlayer;
    }

    @Override
    public List<Card> getCardsInTheDeck() {
        return cardDeck;
    }

    private void dealCards(int numberOfPlayers) {
        Collections.shuffle(cardDeck);
        for (int i = 1; i < numberOfPlayers + 1; i++) {
            List<Card> cardsDealtToPlayer = cardDeck.subList(0, GameConstants.BEGINNING_NUMBER_OF_CARDS);
            playerCardMap.put(String.valueOf(i), new ArrayList<Card>(cardsDealtToPlayer));
            cardDeck.removeAll(playerCardMap.get(String.valueOf(i)));
        }
    }

    @Override
    public String resetGame() {
        this.cardDeck = new ArrayList<Card>(cardDAO.getAllCards());
        this.playerCardMap = new HashMap<String, List<Card>>();
        this.resourceCardPlayed = false;
        this.beginningCardDrawn = false;
        this.gameStarted = false;
        this.playersResources = generateEmptyPlayersResources();
        this.discardedCards = new ArrayList<Card>();
        this.cardStack = new LinkedList<Card>();
        Random randomGenerator = new Random();
        this.currentPlayerId = randomGenerator.nextInt(numberOfPlayersJoined - 1) + 1;
        return "Game reset";
    }

    private HashMap<String, List<Card>> generateEmptyPlayersResources() {
        HashMap<String, List<Card>> playersResourcesHashMap = new HashMap<String, List<Card>>();
        for (int playerId = 1; playerId <= numberOfPlayersJoined; playerId++) {
            playersResourcesHashMap.put(String.valueOf(playerId), new ArrayList<Card>());
        }
        return playersResourcesHashMap;
    }


    @Override
    public boolean startGame(int numberOfPlayersJoined) {
        if (numberOfPlayersJoined <= GameConstants.MAX_NUMBER_OF_PLAYERS || numberOfPlayersJoined >= GameConstants.MIN_NUMBER_OF_PLAYERS) {
            this.numberOfPlayersJoined = numberOfPlayersJoined;
            resetGame();
            dealCards(this.numberOfPlayersJoined);
            this.gameStarted = true;
        }
        return gameStarted;
    }

    public List<Card> getPlayerCards(int playerId) {
        return playerCardMap.get(String.valueOf(playerId));
    }

    @Override
    public Card drawCard(int playerId) throws ActionNotAllowedException {
        if (beginningCardDrawn)
            throw new ActionNotAllowedException();

        Card cardDrawn = cardDeck.remove(0);
        playerCardMap.get(String.valueOf(playerId)).add(cardDrawn);

        if (playerIsNotOnTheMove(playerId))
            throw new ActionNotAllowedException();

        if (!beginningCardDrawn)
            beginningCardDrawn = true;
        
        if(cardDeck.isEmpty()) {
            Collections.shuffle(discardedCards);
            cardDeck.addAll(discardedCards);
            discardedCards.clear();
        }

        return cardDrawn;
    }

    @Override
    public void setNextPlayersTurn(int playerId) throws ActionNotAllowedException {
        if (beginningCardDrawn && playerOnTheMove(playerId) && isNoOfCardsInHandValid()) {
            beginTurn();
        } else {
            throw new ActionNotAllowedException();
        }
    }

    private void beginTurn() {
        currentPlayerId = getNextPlayerId();
        beginningCardDrawn = false;
        resourceCardPlayed = false;
        cardStack.clear();
    }

    private boolean playerOnTheMove(int playerId) {
        return playerId == currentPlayerId;
    }

    @Override
    public boolean isGameStarted() {
        return gameStarted;
    }
    
    @Override
    public void discardCardFromPlayersHand(int cardId, int playerId) {
        Card card = cardDAO.getCard(cardId);
        List<Card> playersCards = getPlayerCards(playerId);
        removeCardFromZone(card, playersCards);
    }

    private void removeCardFromZone(Card card, List<Card> cardList) {
        if(!cardList.isEmpty()) {
            cardList.remove(card);
            discardedCards.add(card);
        }
    }

    @Override
    public void discardCardFromResourcePile(int cardId, int playerId) {
        Card card = cardDAO.getCard(cardId);
        List<Card> playersResources = getResources(playerId);
        removeCardFromZone(card, playersResources);
    }

    @Override
    public void removeMultiplierFromResourcePile (int playerId, int cardId) throws ActionNotAllowedException {
        Card card = cardDAO.getCard(cardId);

        if(cardIsMultiplier(card)){
            discardCardFromResourcePile(cardId,playerId);
        } else {
            throw new ActionNotAllowedException();
        }  
    }

    @Override
    public Card flipCardFaceUp() {
        Card flippedCard = cardDeck.remove(0);
        discardedCards.add(flippedCard);
        return flippedCard;
    } 
    
    @Override
    public void moveCard(Action action){
    	// TODO : Implement interface towards frontend
    }
    @Override
    public void moveCards(Action action){
    	// TODO : Implement interface towards frontend
    }
    @Override
    public void changeValue(Card card){
    	// TODO : Implement old Sea Dog interface towards frontend
    }
  
    
    private <T> List<Card> peakCards(List<Card> area, int start, int end, T delimiter) {
    	List<Card> cards = new ArrayList<Card>();
    	
    	if(delimiter == "all"){
    		start = 0;
    		end = area.size();
    	}

    	for(int i=start; i< end; i++) {
    			cards.add(area.get(i));	
    	}

    	return cards;
    }
    
    private void moveCards(List<Card> fromArea, List<Card> toArea, int start, int end, String delimiter) {
    	
    	if(delimiter == "all"){
    		start = 0;
    		end = fromArea.size();
    	}

    	for(int i=start; i< end; i++) {
    		Card card = fromArea.get(i);	
    		fromArea.remove(card);
    		toArea.add(card);
    	}
    }
    
    public void moveCard(List<Card> fromArea, List<Card> toArea, Card card) {
    	fromArea.remove(card);
    	toArea.remove(card);
    }

}