package com.randombit.uskoci.card.dao;

import com.randombit.uskoci.card.model.Card;

import java.util.HashMap;
import java.util.Map;

// Singleton DAO
public enum CardDAO {
    instance;

    private Map<String, Card> contentProvider = new HashMap<String, Card>();

    private CardDAO() {

        Card card = new Card("1", "Shoots Fireballs", "Shoots Fireballs from its eyes", "Creature");
        contentProvider.put("1", card);
        card = new Card("2", "Shoots IceBalls", "Shoots Iceballs from its cock", "Creature");
        contentProvider.put("2", card);

    }
    public Map<String, Card> getModel(){
        return contentProvider;
    }
}
