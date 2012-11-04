package com.randombit.uskoci.game.control.eventHandling;

import com.randombit.uskoci.card.model.Card;

import javax.xml.bind.annotation.XmlElement;
import java.util.Collections;
import java.util.List;

public class Action {
	private String type = "";
	private String summary = "";
	private String delimiter = "";
	private Integer quantity;
	private String area1;
	private String area2;
	private Card card;
	//private List<Card> cards;
	
	public Action(String type){
		this.type = type;
		setDummyVariables();
	}
	public Action(String type,String summary){
		this.type = type;
		this.summary = summary;
		setDummyVariables();
	}
	public Action(String type,String summary,String delimiter){
		this.type = type;
		this.summary = summary;
		this.delimiter = delimiter;
		setDummyVariables();
	}
	public Action(String type,String summary,Integer quantity){
		this.type = type;
		this.summary = summary;
		setDummyVariables();
		this.quantity = quantity;
	}
	public Action(String type,String summary,String area1,String delimiter){
		this.type = type;
		this.summary = summary;
		this.area1 = area1;
		this.delimiter = delimiter;
		setDummyVariables();
	}
	public Action(String type,String summary,String area1,Integer quantity){
		this.type = type;
		this.summary = summary;
		this.area1 = area1;
		this.quantity = quantity;
		setDummyVariables();
	}
	public Action(String type,String summary,String area1,String area2,String delimiter){
		this.type = type;
		this.summary = summary;
		this.area1 = area1;
		this.area2 = area2;
		this.delimiter = delimiter;
		setDummyVariables();
	}
	public Action(String type,String summary,String area1,String area2,Card card){
		this.type = type;
		this.summary = summary;
		this.area1 = area1;
		this.area2 = area2;
		setDummyVariables();
		this.card = card;
	}

	private void setDummyVariables(){
		//this.cards = Collections.emptyList();
		this.card = new Card();
		this.quantity = 0;
	}
	
	//TODO seteri, geteri
	
	@XmlElement
	public String getActionType(){
		return this.type;
	}
	/*@XmlElement
	public List<Card> getArea(){
		return this.cards;
	}*/
	@XmlElement
	public String getDelimiter(){
		return this.delimiter;
	}
}
