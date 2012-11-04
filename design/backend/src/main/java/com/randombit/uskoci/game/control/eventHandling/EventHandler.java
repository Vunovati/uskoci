package com.randombit.uskoci.game.control.eventHandling;

import java.util.*;

import com.randombit.uskoci.card.model.Card;
import com.randombit.uskoci.game.control.GameStatus;
import com.randombit.uskoci.game.control.eventHandling.Action;
import com.randombit.uskoci.game.control.eventHandling.Response;


public class EventHandler {

	List<Action> listOfActions;
	Response response;
	private GameStatus gameStatus = new GameStatus();
	
	public EventHandler() {}
	
	private void createEventActions(Card event,int eventPlayerId){
	    	listOfActions = new ArrayList<Action>();
	    	Action action;
	    	String cardSummary = event.getSummary();
	    	
	    	switch(Integer.parseInt(cardSummary)) {
	    	
	    			//  WILL  
	    	case 1: createAction(new Action("A","remove event from stack"));
	    			break;
	    			
	    			//  STORM  
	    	case 2: createAction(new Action("A","move cards","resources-all", "discardPile","all"));
	    			break;
	    	
	    			// SPYGLASS
	    	case 3: createAction(new Action("A","reveal cards","resources-all","all"));
	    			break;
	    	
	    			// SPY
	    	case 4: createAction(new Action("Q","Choose a player"));
	    			createAction(new Action("Q","Choose cards", "resource-?",1));
	    			createAction(new Action("A","Play a card","card-?"));
	    			break;
	    			
	    			//TRICKERY  
	    	case 5: createAction(new Action("Q","Choose a player"));
					createAction(new Action("A","move cards","resources" + Integer.toString(eventPlayerId),"resources-?","all"));	
					createAction(new Action("A","move cards","resources-?","resources-" + Integer.toString(eventPlayerId),"half"));
					break;
					
					//THEFT	
	    	case 6: createAction(new Action("Q","Choose a player"));
					createAction(new Action("Q","Choose cards","hand-?",2));
					break;
					
					//SEA DOG
	    	case 7: createAction(new Action("A","move a card","hand-" + Integer.toString(eventPlayerId), "resources-"+Integer.toString(eventPlayerId),event));
	    			break;
	    			
	    			//VENETIAN AMBUSH
	    	case 8: createAction(new Action("Q","event trigger"));
	    			createAction(new Action("A","move cards", "hand-?","discardPile","all"));
	    			break;
	    			
	    			 //DUNGEON
	    	case 9: createAction(new Action("Q","Choose a player"));	 
	    			createAction(new Action("A","move a card","hand-" + Integer.toString(eventPlayerId),"resources-?",event));
	    			break;
	    			
	    			//NORTHERN GALE
	    	case 10:createAction(new Action("Q","Choose cards","resources-all","half","allPlayers")); 
	    			createAction(new Action("A","move cards", "resources-all","discardPile","cards-?"));
	    			break;
	    			
	    			 //SNITCH 
	    	case 11: createAction(new Action("Q","reveal cards","deck",1));
	    			 createAction(new Action("C","cardType == event"));
	    			 createAction(new Action("C true"));
	    			 createAction(new Action("A","move a card","deck","discardPile","card-?"));
	    			 createAction(new Action("R","repeat C"));
	    			 createAction(new Action("C false"));
	    			 createAction(new Action("A","move a card","deck","discardPile","card-?"));
	    			 createAction(new Action("A","move a card","resources-all","discardPile","color == ?"));
	    			 createAction(new Action("C end"));
					 break;
					
					 //FOG
	    	case 12: createAction(new Action("Q","reveal cards","deck",1));
	    	         createAction(new Action("C","resourceType == food"));
	    	         createAction(new Action("C true"));
	    	         createAction(new Action("A","move a card","resources-all","discardPile","resourceType!=food"));
	    	         createAction(new Action("C false"));
	    	         createAction(new Action("C","resourceType == wood"));
	    	         createAction(new Action("C true"));
	    	         createAction(new Action("A","draw cards",3));
	    	         createAction(new Action("C false"));
	    	         createAction(new Action("C","resourceType == gold"));
	    	         createAction(new Action("C true"));
	    	         createAction(new Action("Q","Choose a player"));
	    	         createAction(new Action("Q","Choose cards","resources-?",2));
	    	         createAction(new Action("A","move cards", "resources-?","resources-"+ Integer.toString(eventPlayerId),"cards-?"));
	    	         createAction(new Action("C end"));
				     break;	
				     
				     //FOREIGN MERCHANTS 
	    	case 13: createAction(new Action("Q","Choose cards","hand-?",1));
	    		     for(int playerId=0; playerId < (gameStatus.getNumberOfPlayersJoined() + 1); playerId++) {
	    		    	 if(playerId != eventPlayerId) {
	    		    		 createAction(new Action("A","move cards", "resources-"+ Integer.toString(playerId),"discardPile","color=?"));
	    		    	 }	 
	    		     }
	    		     break;
	    		
	    }
	}
	
	public void createAction(Action action) {
		this.listOfActions.add(action);
	}
	
    private List<Action> decodeActions() {
    	List<Action> actionList = new ArrayList<Action>();
    	Action action;
    	String actionType;
    	int i = 0;
    	while(i!=listOfActions.size()){
    		action = listOfActions.remove(i);
    		actionType = action.getActionType();
    		if( actionType == "A"){
    			actionList.add(action);
    		}
    		if( actionType == "Q"){
    			actionList.add(action);
    			return actionList;
    		}
    		i++;
    	}
    	
    	return actionList;
    }
	    	
    private void decodeResponse(Response response) {
    	List<Action> actionList = new ArrayList<Action>();
    	Action action;
	   	String actionType = listOfActions.get(0).getActionType();
	    int i = 0;
	    if( actionType == "A"){
	    	while(listOfActions.get(i++).getActionType() == "A"){
	    	   action = listOfActions.get(0);
	    	   modifyAction(action,response);
	    	}
	    }
	    else {
	    	if(actionType == "Q"){
	    	   while(listOfActions.get(i).getActionType() == "Q"){
	    		   i++;
	    	   }
	    	 }
	    	 if(listOfActions.get(i).getActionType()== "C"){
	    	    handleCondition(i);
	    	 }
	    	 else {
	    	    action = listOfActions.get(i);
	    	    modifyAction(action,response);
	    	 }
	    }
	}
   
    public List<Action> resolveEvent(Card event,int eventPlayerId){
    	List<Action> actionList = new ArrayList<Action>();
    	createEventActions(event,eventPlayerId);
    	actionList = decodeActions();
    	return actionList;
    }
    
    private void handleCondition(int i){
    	//TODO
    }
     
    private void modifyAction(Action action, Response response){
    	//TODO
    }
	
}
