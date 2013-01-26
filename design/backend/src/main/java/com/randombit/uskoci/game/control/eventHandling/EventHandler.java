package com.randombit.uskoci.game.control.eventHandling;

import java.util.*;

import com.randombit.uskoci.card.model.Card;
import com.randombit.uskoci.game.control.GameConstants.ACTION;
import com.randombit.uskoci.game.control.GameConstants.AREA;
import com.randombit.uskoci.game.control.GameConstants.QUANTITY;
import com.randombit.uskoci.game.control.GameConstants.QUESTION;
import com.randombit.uskoci.game.control.GameController;
import com.randombit.uskoci.game.control.GameStatus;
import com.randombit.uskoci.game.control.eventHandling.Action;
import com.randombit.uskoci.game.control.eventHandling.Response;



public class EventHandler {

	List<Action> listOfActions;
	Response response;
	
	public EventHandler() {}
	
    public List<Action> resolveEvent(Card event,String eventPlayerId){
    	createEventActions(event,eventPlayerId);
    	return this.listOfActions;
    }
    
    public List<Action> resolveResponseToEvent(Response response){
    	setEventResponse(response);
    	decodeResponse(response);
    	return this.listOfActions;
    }
	
	private void createEventActions(Card event,String eventPlayerId){
	    	this.listOfActions = new ArrayList<Action>();
	    	String cardId = event.getId();
	    	
	    	switch(Integer.parseInt(cardId)) {
	    	
	    			//  WILL  
	    	//case 1: createAction(new Action("A","remove event from stack"));
	    	//		break;
	    			
	    			//  STORM  
	    	case 41: listOfActions.add(new Action(ACTION.MOVE_CARDS.toString(),AREA.RESOURCE.toString() + "-all",AREA.DISCARDPILE.toString(),QUANTITY.ALL.toString()));
	    			break;
	    	
	    			// SPYGLASS
	    	/*case 3: createAction(new Action("A","reveal cards","resource-all","all"));
	    			break;
	    	
	    			// SPY
	    	case 4: createAction(new Action("Q1","Choose a player"));
	    			createAction(new Action("Q2","Choose cards", "resource-Q1",1));
	    			createAction(new Action("A","Play a card","card-Q2"));
	    			break;
	    			
	    			//TRICKERY  
	    	case 5: createAction(new Action("Q1","Choose a player"));
					createAction(new Action("A","move cards","resource" + eventPlayerId,"resource-Q1","all"));	
					createAction(new Action("A","move cards","resource-Q1","resource-" + eventPlayerId,"half"));
					break;*/
					
					//THEFT	
	    	case 6: listOfActions.add(new Action("Q1",QUESTION.CHOOSE_PLAYER.toString()));
	    	        listOfActions.add(new Action("Q2",QUESTION.CHOOSE_CARDS.toString(), AREA.RESOURCE.toString() +"-Q1",1));
	    	        listOfActions.add(new Action(ACTION.MOVE_CARDS.toString(),AREA.RESOURCE.toString() + "-Q1",AREA.RESOURCE.toString() + "-" + eventPlayerId,QUANTITY.CARD.toString()));
					break;
					
					//SEA DOG
	    	/*case 7: createAction(new Action("A","move cards","hand-" + eventPlayerId, "resource-"+eventPlayerId,event));
	    			break;
	    			
	    			//VENETIAN AMBUSH
	    	case 8: createAction(new Action("Q1","Choose cards"));
	    			createAction(new Action("A","move cards", "hand-Q1","discardPile","all"));
	    			break;
	    			
	    			 //DUNGEON
	    	case 9: createAction(new Action("Q1","Choose a player"));	 
	    			createAction(new Action("A","move cards","hand-" + eventPlayerId,"resource-Q1",event));
	    			break;
	    			
	    			//NORTHERN GALE
	    	case 10:createAction(new Action("Q1","Choose cards","resource-all","half","allPlayers")); 
	    			createAction(new Action("A","move cards", "resource-all","discardPile","cards"));
	    			break;
	    			
	    			 //SNITCH 
	    	case 11: createAction(new Action("Q1","Choose cards","deck",1));
	    			 createAction(new Action("C","cardType == event"));
	    			 createAction(new Action("C true"));
	    			 createAction(new Action("A","move cards","deck","discardPile","card"));
	    			 createAction(new Action("Q1","Choose cards","deck",1));
	    			 createAction(new Action("C false"));
	    			 createAction(new Action("A","move cards","deck","discardPile","card"));
	    			 createAction(new Action("A","move cards","resource-all","discardPile","color==Q1"));
	    			 createAction(new Action("C end"));
					 break;
					
					 //FOG
	    	case 12: createAction(new Action("Q1","reveal cards","deck",1));
	    	         createAction(new Action("C","resourceType==food"));
	    	         createAction(new Action("C true"));
	    	         createAction(new Action("A","move cards","resource-all","discardPile","resourceType!=food"));
	    	         createAction(new Action("C false"));
	    	         createAction(new Action("C","resourceType==wood"));
	    	         createAction(new Action("C true"));
	    	         createAction(new Action("A","draw cards",3));
	    	         createAction(new Action("C false"));
	    	         createAction(new Action("C","resourceType==gold"));
	    	         createAction(new Action("C true"));
	    	         createAction(new Action("Q2","Choose a player"));
	    	         createAction(new Action("Q3","Choose cards","resource-Q2",2));
	    	         createAction(new Action("A","move cards", "resource-Q2","resource-"+ eventPlayerId,"cards"));
	    	         createAction(new Action("C end"));
				     break;	
				     
				     //FOREIGN MERCHANTS 
	    	case 13: createAction(new Action("Q1","Choose cards","hand" + eventPlayerId,1));
	    		     for(int playerId=1; playerId < (gameStatus.getNumberOfPlayersJoined() + 1); playerId++) {
	    		    	 if(Integer.toString(playerId) != eventPlayerId) {
	    		    		 createAction(new Action("A","move cards", "resource-"+ Integer.toString(playerId),"discardPile","color==Q1"));
	    		    	 }	 
	    		     }
	    		     break;*/
	    		
	    }
	}

	
   /* private List<Action> getActions() {
    	List<Action> actionList = new ArrayList<Action>();
    	Action action;
    	String actionType;
    	//int i = 0;
    	while(listOfActions.size() != 0){
    		action = listOfActions.get(0);
    		actionType = action.getActionType();
    		if( actionType.equals(ACTION.MOVE_CARDS.toString()) || actionType.equals(ACTION.REVEAL_CARDS.toString())){ 
    			actionList.add(action);
    			listOfActions.remove(0);
    		}
    		if( actionType.contains("Q")){
        		actionList.add(action);
        	}
    		if( actionType.equals("C")){
    			handleCondition(0,actionList);	
    		}
    		//i++;
    	}
    	return actionList;
    }*/
	    	
    private void decodeResponse(Response response) {
    	Action action;
	   	String responseType = response.getResponseType();
    	//String responseType = "Q1";
	   	String playerId = response.getPlayerId();
	   	String area1,area2,delimiter;
	   	List<Action> actions = getListOfActions();
	    int i = 0;
	    
	    while(i++ < actions.size() - 1) {
	    	action = actions.get(i);
	    	area1 = action.getArea1();
	    	area2 = action.getArea2();
	    	delimiter = action.getDelimiter();
	    	if(area1.contains(responseType)){
	    		action.setArea1(area1.replace(responseType, playerId));
	    	}
	    	if(area2.contains(responseType)){
	    		action.setArea2(area2.replace(responseType, playerId));
	    	}
	    	if(delimiter.contains(responseType)){
	    		if(delimiter.contains("color")){
	    			action.setDelimiter(delimiter.replace(responseType, response.getCard().getColor()));
	    		}
	    		if(delimiter.contains("card-")){
	    			action.setCard(response.getCard());
	    		}
	    		if(delimiter.contains("cards-")){
	    			action.setCards(response.getListOfCards());	
	    		}
	    	}
	    }
	    this.listOfActions.remove(0);
	}
    
    private void handleCondition(int i, List<Action> actionList){
    	List<Action> listOfActions = getListOfActions(); 
    	String condition = listOfActions.get(i).getDelimiter();
    	Response response = getEventResponse();
    	String delims ="[!=<>]+";
    	boolean conditionTrue = false;
    	String[] tokens = condition.split(delims);
    	String rightOperand = tokens[tokens.length - 1];
    	String leftOperand = "";
    	Action action;
    	
    	if(condition.contains("cardType")) {
    		leftOperand = response.getCard().getType();
    	} 
    	else{
    		if(condition.contains("resourceType")) {
    			//TODO
    			//leftOperand = response.getCard().
    			//extract resource type from card type
    		}	
    	}
    	if(condition.contains("!=")){
    		if( leftOperand != rightOperand){
    			conditionTrue = true;
    		}
    		else {
    			conditionTrue = false;
    		}
    	}
    	if(condition.contains("==")){
    		if( leftOperand == rightOperand){
    			conditionTrue = true;
    		}
    		else {
    			conditionTrue = false;
    		}
    	}
    	
    	if(conditionTrue){
    		i = i + 2;
    		do{
    			action = listOfActions.get(i++);
    			actionList.add(action);

    		} while(action.getActionType() != "C false");
    		
    	}
    	else {
    		do{
    			action = listOfActions.get(i++);
    		} while(action.getActionType() != "C false");
    		
    		i++;
    		
    		do{
    			action = listOfActions.get(i++);
    			actionList.add(action);
    		} while(action.getActionType() != "C end");	
    	}	
    }

    private List<Action> getListOfActions(){
    	return this.listOfActions;
    }
    
    public Response getEventResponse(){
    	return this.response;
    }
    
    private void setEventResponse(Response response) {
    	this.response = new Response(response);
    }
    
    public void setActionList(List<Action> listOfActions){
    	this.listOfActions = listOfActions;
    }
    
    
	
}
