package com.randombit.uskoci.game.control.eventHandling;

import com.randombit.uskoci.card.model.Card;

import javax.xml.bind.annotation.XmlElement;

import java.util.Collections;
import java.util.List;

public class Response {
	private String responseType ="";
	private String playerId = "";
	private List<String> listOfPlayerIds;
	private Card card;
	private List<Card> listOfCards;
	
	public Response() {}
	
	public Response(Response response){
		this.responseType = response.responseType;
		this.playerId = response.playerId;
		this.listOfPlayerIds = response.listOfPlayerIds;
		this.card = response.card;
		this.listOfCards = response.listOfCards;
	}
	
	public Response(String playerId, String responseType){
		this.playerId = playerId;
		this.responseType = responseType;
		this.listOfPlayerIds = Collections.emptyList();
		this.card = new Card();
		this.listOfCards = Collections.emptyList();
	}
	
	public Response(List<String> players, int dummy){
		this.listOfPlayerIds = players;
		this.card = new Card();
		this.listOfCards = Collections.emptyList();
	}
	
	public Response(Card card, String responseType){
		this.listOfPlayerIds = Collections.emptyList();
		this.responseType = responseType;
		this.card = card;
		this.listOfCards = Collections.emptyList();
	}
	
	public Response(List<Card> cards){
		this.listOfPlayerIds = Collections.emptyList();
		this.card = new Card();
		this.listOfCards = cards;
	}
	
	@XmlElement
	public String getResponseType() {
		return this.responseType;
	}
	
	@XmlElement
	public String getPlayerId() {
		return this.playerId;
	}
	@XmlElement
	public List<String> getListOfPlayers(){
		return listOfPlayerIds;
	}
	@XmlElement
	public Card getCard(){
		return this.card;
	}
	@XmlElement
	public List<Card> getListOfCards(){
		return this.listOfCards;
	}
	
}
