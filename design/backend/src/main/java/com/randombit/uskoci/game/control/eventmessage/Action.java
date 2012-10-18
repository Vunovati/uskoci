package com.randombit.uskoci.game.control.eventmessage;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import com.randombit.uskoci.card.model.Card;

public class Action {
	private int actionPlayerId;
	private String actionType = "";
	private List<Card> area;
	private List<Integer> playersAffectedByAction;
	private boolean lastAction;
	
	public Action() {}
	public Action(int playerId, String actionType, List<Card> cardArea, List<Integer> players, boolean lastAction){
		this.actionPlayerId = playerId;
		this.actionType = actionType;
		this.area = cardArea;
		this.lastAction = lastAction;
		this.playersAffectedByAction = players;
	}
	
	//TODO : seteri...
	
	@XmlElement
	public int getActionPlayerId(){
		return actionPlayerId;
	}
	@XmlElement
	public String getActionType(){
		return actionType;
	}
	@XmlElement
	public List<Card> getArea(){
		return area;
	}
	@XmlElement
	public List<Integer> getPlayersAffectedByAction() {
		return playersAffectedByAction;
	}
	public boolean isLastAction(){
		return lastAction;
	}
	
}
