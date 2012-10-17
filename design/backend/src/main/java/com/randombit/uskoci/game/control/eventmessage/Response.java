package com.randombit.uskoci.game.control.eventmessage;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import com.randombit.uskoci.card.model.Card;

public class Response {
	private int responsePlayerId;
	private String responseType = "";
	private List<Card> area;
	private List<Integer> playersAffectedByResponse;
	
	public Response() {}
	public Response(int playerId, String responseType, List<Card> area, List<Integer> players){
		this.responsePlayerId = playerId;
		this.responseType = responseType;
		this.area = area;
		this.playersAffectedByResponse = players;
	}
	
	// TODO : seteri ...
	
	@XmlElement
	public String getResponseType() {
		return responseType;
	}
	@XmlElement
	public int getResponsePlayerId(){
		return responsePlayerId;
	}
	@XmlElement
	public List<Card> getCards(){
		return area;
	}
	@XmlElement
	public List<Integer> getPlayersAffectedByResponse() {
		return playersAffectedByResponse;
	}
	
}
