package com.randombit.uskoci.game.control;

import com.randombit.uskoci.card.dao.CardDAO;
import com.randombit.uskoci.card.model.Card;
import com.randombit.uskoci.game.ActionNotAllowedException;
import com.randombit.uskoci.game.control.eventmessage.Action;
import com.randombit.uskoci.game.control.eventmessage.Response;

import java.util.*;

import static com.randombit.uskoci.game.control.GameConstants.*;

public class GameControllerImpl implements GameController {

    private CardDAO cardDAO;

    private GameStatus gameStatus = new GameStatus();

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

        if (!gameStatus.beginningCardDrawn && playerOnTheMove(playerId)) {
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

    @Override
    public List<Action> resolveCardsOnStack() {
        List<Action> response = new ArrayList<Action>();
        Card event;
        LinkedList<Card> cardStack = (LinkedList<Card>) getCardStack();

        while (!cardStack.isEmpty()) {
            event = cardStack.remove();
            response = resolveEvent(event, 0, Collections.<Response>emptyList());
        }
        return response;
    }

    @Override
    public List<Action> responseToEvent(Card event, int playerId, List<Response> responseList) {
        return resolveEvent(event, playerId, responseList);
    }

    // TODO: refaktorirati obavezno: izgleda koma !!!!
    public List<Action> resolveEvent(Card event, int eventPlayerId, List<Response> responseList) {
        List<Card> cards = new ArrayList<Card>();
        List<Integer> playersAffectedByAction = new ArrayList<Integer>();
        List<Action> listOfActions = new ArrayList<Action>();
        String cardSummary = event.getSummary();
        List<Card> cardStack = getCardStack();

        boolean lastAction = true;
        boolean moreActions = false;
        Action action;

        if (WILL.equals(cardSummary)) {
//    		cards.add(cardStack.get(0));
            action = new Action(eventPlayerId, "remove from stack", lastAction);
            //cardStack.remove();
            return listOfActions;
        }
        if (STORM.equals(cardSummary)) {
            for (int playerId = 1; playerId < (getNumberOfPlayersJoined() + 1); playerId++) {
                //moveCards(getResources(playerId), getDiscardPile(), 0, 0, "all");
                if (playerId < getNumberOfPlayersJoined()) {
                    action = new Action(playerId, "move cards", getResources(playerId), getDiscardPile(), 0, 0, "all", moreActions);
                } else {
                    action = new Action(playerId, "move cards", getResources(playerId), getDiscardPile(), 0, 0, "all", lastAction);
                }
                listOfActions.add(action);
            }
            return listOfActions;
        }
        if (SPYGLASS.equals(cardSummary)) {
            playersAffectedByAction.add(eventPlayerId);
            for (int playerId = 1; playerId < (getNumberOfPlayersJoined() + 1); playerId++) {
                if (playerId != getNumberOfPlayersJoined()) {
                    action = new Action(playerId, "Reveal", getPlayerCards(playerId), playersAffectedByAction, moreActions);
                    listOfActions.add(action);
                } else {
                    action = new Action(playerId, "Reveal", getPlayerCards(playerId), playersAffectedByAction, lastAction);
                    listOfActions.add(action);
                }
            }
        }
        if (SPY.equals(cardSummary)) {

            if (responseList.isEmpty()) {
                action = new Action(eventPlayerId, "Choose player", moreActions);
                listOfActions.add(action);
                return listOfActions;
            } else {
                Response response = responseList.remove(0);
                String responseType = response.getResponseType();
                if (responseType == "Players") {
                    List<Integer> choseFromPlayers = new ArrayList<Integer>();
                    int chosenPlayerId = response.getPlayersAffectedByResponse().remove(0);
                    choseFromPlayers.add(chosenPlayerId);
                    action = new Action(eventPlayerId, "Choose card", getPlayerCards(chosenPlayerId), choseFromPlayers, moreActions);
                    listOfActions.add(action);
                    listOfActions.add(action);
                    return listOfActions;
                }
                if (responseType == "Cards") {
                    action = new Action(eventPlayerId, "Play cards", response.getCards(), lastAction);
                    listOfActions.add(action);
                    return listOfActions;
                }
            }
        }
        if (TRICKERY.equals(cardSummary)) {
            if (responseList.isEmpty()) {
                action = new Action(eventPlayerId, "Choose a player", moreActions);
                listOfActions.add(action);
                return listOfActions;
            }
            Response response = responseList.remove(0);
            String responseType = response.getResponseType();
            if (responseType == "Players") {
                int chosenPlayerId = response.getPlayersAffectedByResponse().remove(0);
                int numOfResources = getResources(eventPlayerId).size();
                //moveCards(getResources(eventPlayerId), getResources(chosenPlayerId), 0, 0, "all");
                action = new Action(eventPlayerId, "move cards", getResources(eventPlayerId), getResources(chosenPlayerId), 0, 0, "all", moreActions);
                listOfActions.add(action);
                //moveCards(getResources(chosenPlayerId),getResources(eventPlayerId) , numOfResources, getResources(chosenPlayerId).size(), "");
                action = new Action(eventPlayerId, "move cards", getResources(chosenPlayerId), getResources(eventPlayerId), numOfResources, getResources(chosenPlayerId).size(), "", lastAction);
                listOfActions.add(action);
                return listOfActions;
            }

        }
        if (THEFT.equals(cardSummary)) {
            if (responseList.isEmpty()) {
                action = new Action(eventPlayerId, "Choose a player", moreActions);
                listOfActions.add(action);
                return listOfActions;
            }
            Response response = responseList.remove(0);
            String responseType = response.getResponseType();
            if (responseType == "Players") {
                List<Integer> choseFromPlayers = new ArrayList<Integer>();
                gameStatus.setChosenPlayer(response.getPlayersAffectedByResponse().remove(0));
                choseFromPlayers.add(gameStatus.getChosenPlayer());
                action = new Action(eventPlayerId, "Choose a card", getPlayerCards(gameStatus.getChosenPlayer()), choseFromPlayers, moreActions);
                listOfActions.add(action);
                listOfActions.add(action);
                return listOfActions;
            }
            if (responseType == "Cards") {
                Card chosenCard = response.getCards().remove(0);
                //moveCard(getResources(chosenPlayer),getResources(eventPlayerId),chosenCard);
                action = new Action(eventPlayerId, "move a card", getResources(gameStatus.getChosenPlayer()), getResources(eventPlayerId), chosenCard, lastAction);
                listOfActions.add(action);
                return listOfActions;
            }

        }
        if (SNITCH.equals(cardSummary)) {
            List<Card> cardDeck = getCardsInTheDeck();
            Card card = cardDeck.get(0);
            int pos = 1;
            cards.add(card);
            for (int playerId = 1; playerId < (getNumberOfPlayersJoined() + 1); playerId++) {
                playersAffectedByAction.add(playerId);
            }
            while (EVENT.equals(card.getType())) {
                action = new Action(eventPlayerId, "Reveal", cards, playersAffectedByAction, moreActions);
                listOfActions.add(action);
                action = new Action(eventPlayerId, "move a card", cardDeck, getDiscardPile(), card, moreActions);
                listOfActions.add(action);
                card = cardDeck.get(pos++);
            }
            action = new Action(eventPlayerId, "Reveal", cards, playersAffectedByAction, moreActions);
            for (int playerId = 1; playerId < (getNumberOfPlayersJoined() + 1); playerId++) {
                if (playerId < getNumberOfPlayersJoined()) {
                    action = new Action(eventPlayerId, "move cards", getResources(playerId), getDiscardPile(), 0, 0, card.getColor(), moreActions);
                } else
                    action = new Action(eventPlayerId, "move cards", getResources(playerId), getDiscardPile(), 0, 0, card.getColor(), lastAction);
                listOfActions.add(action);
            }
            return listOfActions;
        }
        if (SEADOG.equals(cardSummary)) {
            action = new Action(eventPlayerId, "move a card", Collections.<Card>emptyList(), getResources(eventPlayerId), event, lastAction);
            listOfActions.add(action);
            return listOfActions;
        }


        return Collections.<Action>emptyList();
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
        if (gameStatus.isBeginningCardDrawn() && playerOnTheMove(playerId)) {
            throw new ActionNotAllowedException(EXCEPTION_DRAW_MORE_THAN_ONE_CARD);
        }

        if (playerIsNotOnTheMove(playerId)) {
            throw new ActionNotAllowedException(EXCEPTION_DRAW_CARD_NOT_ON_THE_MOVE);
        }

        Card cardDrawn = gameStatus.getCardDeck().remove(0);
        gameStatus.getPlayerCardMap().get(String.valueOf(playerId)).add(cardDrawn);

        if (!gameStatus.isBeginningCardDrawn()) {
            gameStatus.setBeginningCardDrawn(true);
        }

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
    public void discardCardFromResourcePile(int cardId, int playerId) {
        Card card = cardDAO.getCard(cardId);
        ResourcePile resourcePile = getPlayersResourcePile(playerId);
        resourcePile.remove(card);
        gameStatus.getDiscardedCards().add(card);
    }

    @Override
    public void removeMultiplierFromResourcePile(int playerId, int cardId) throws ActionNotAllowedException {
        Card card = cardDAO.getCard(cardId);

        if (cardIsMultiplier(card)) {
            discardCardFromResourcePile(cardId, playerId);
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
    public void moveCard(Action action) {
        // TODO : Implement interface towards frontend
    }

    @Override
    public void moveCards(Action action) {
        // TODO : Implement interface towards frontend
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

    private void moveCards(List<Card> fromArea, List<Card> toArea, int start, int end, String delimiter) {

        if (delimiter == "all") {
            start = 0;
            end = fromArea.size();
        }

        for (int i = start; i < end; i++) {
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