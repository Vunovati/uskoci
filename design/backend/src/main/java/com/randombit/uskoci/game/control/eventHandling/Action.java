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
	private List<Card> cards;
	
	public Action(String type){
		this.type = type;
		this.summary = "";
		this.area1 = "";
		this.area2 = "";
		setDummyVariables();
	}
	/*public Action(String type,String delimiter){
		this.type = type;
		this.delimiter = delimiter;
		setDummyVariables();
	}*/
	public Action(String type,String summary){
		this.type = type;
		this.summary = summary;
		this.area1 = "";
		this.area2 = "";
		setDummyVariables();
	}
	public Action(String type,String summary,String delimiter){
		this.type = type;
		this.summary = summary;
		this.delimiter = delimiter;
		this.area1 = "";
		this.area2 = "";
		setDummyVariables();
	}
	public Action(String type,String summary,Integer quantity){
		this.type = type;
		this.summary = summary;
		this.area1 = "";
		this.area2 = "";
		setDummyVariables();
		this.quantity = quantity;
	}
	/*public Action(String type,String summary,String area1,String delimiter){
		this.type = type;
		this.summary = summary;
		this.area1 = area1;
		this.delimiter = delimiter;
		setDummyVariables();
	}*/
	public Action(String type,String area1,String area2,String delimiter){
		this.type = type;
		this.area1 = area1;
		this.area2 = area2;
		this.delimiter = delimiter;
		setDummyVariables();
	}
	public Action(String type,String summary,String area1,Integer quantity){
		this.type = type;
		this.summary = summary;
		this.area1 = area1;
		this.area2 = "";
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
		this.cards = Collections.emptyList();
		this.card = new Card();
		this.quantity = 0;
	}
	
	//TODO seteri, geteri
	
	@XmlElement
	public String getActionType(){
		return this.type;
	}
	@XmlElement
	public String getSummary(){
		return this.summary;
	}
	@XmlElement
	public String getArea1(){
		return this.area1;
	}
	public void setArea1(String area){
		this.area1 = area;
	}
	@XmlElement
	public String getArea2(){
		return this.area2;
	}
	public void setArea2(String area){
		this.area2 = area;
	}
	@XmlElement
	public String getDelimiter(){
		return this.delimiter;
	}
	public void setDelimiter(String delimiter){
		this.delimiter = delimiter;
	}
	public void setCard(Card card){
		this.card = card;
	}
	public void setCards(List<Card> cards){
		this.cards = cards;
	}
}
