package com.randombit.uskoci.game.control.eventmessage;

import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import com.randombit.uskoci.card.model.Card;

public class Action {
	private int actionPlayerId;
	private String actionType = "";
	private Card card;
	private List<Card> cards;
	private List<Integer> playersAffectedByAction;
	private int startValue;
	private int endValue;
	private boolean lastAction;
	private String delimiter = "";
	private List<Card> area1;
	private List<Card> area2;
	
	public Action() {
		this.actionPlayerId = 0;
		this.actionType = "";
		this.cards = Collections.<Card> emptyList();
		this.lastAction = false;
		this.playersAffectedByAction = Collections.<Integer> emptyList();
		this.startValue = 0;
		this.endValue = 0;
	}

	public Action(int playerId, String actionType, List<Card> cardArea, List<Integer> players, boolean lastAction){
		this.actionPlayerId = playerId;
		this.actionType = actionType;
		this.cards = cardArea;
		this.lastAction = lastAction;
		this.playersAffectedByAction = players;
		this.startValue = 0;
		this.endValue = 0;
	}
	public Action(int playerId, String actionType, boolean lastAction){
		this.actionPlayerId = playerId;
		this.actionType = actionType;
		this.cards = Collections.<Card> emptyList();
		this.playersAffectedByAction = Collections.<Integer> emptyList();
		this.lastAction = lastAction;
		this.startValue = 0;
		this.endValue = 0;
	}
	public Action(int playerId, String actionType,List<Card> cardArea,boolean lastAction){
		this.actionPlayerId = playerId;
		this.actionType = actionType;
		this.cards = cardArea;
		this.playersAffectedByAction = Collections.<Integer> emptyList();
		this.lastAction = lastAction;
		this.startValue = 0;
		this.endValue = 0;
	}
	public Action(int playerId, String actiontype, List<Card> cardArea1,List <Card> cardArea2, Card card, boolean lsatAction) {
		this.actionPlayerId = playerId;
		this.actionType = actionType;
		this.area1 = cardArea1;
		this.area2 = cardArea2;
		this.card = card;
		this.lastAction = lastAction;
	}
	public Action(int playerId, String actionType, List<Card> cardArea1,List <Card> cardArea2, int start, int end,String delimiter, boolean lastAction){
		this.actionPlayerId = playerId;
		this.actionType = actionType;
		this.area1 = cardArea1;
		this.area2 = cardArea2;
		this.playersAffectedByAction = Collections.<Integer> emptyList();
		this.startValue = start;
		this.endValue = end;
		this.lastAction = lastAction;
		this.delimiter = delimiter;
	}
	
	//TODO : seteri...
	
	@XmlElement
	public int getActionPlayerId(){
		return this.actionPlayerId;
	}
	@XmlElement
	public String getActionType(){
		return this.actionType;
	}
	@XmlElement
	public List<Card> getArea(){
		return this.cards;
	}

	@XmlElement
	public List<Integer> getPlayersAffectedByAction() {
		return this.playersAffectedByAction;
	}
	@XmlElement
	public boolean isLastAction(){
		return this.lastAction;
	}
	public int getStartValue(){
		return this.startValue;
	}
	@XmlElement
	public int getEndValue(){
		return this.endValue;
	}
	@XmlElement
	public String getDelimiter(){
		return this.delimiter;
	}
}
