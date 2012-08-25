package com.randombit.uskoci.card.dao;

import com.randombit.uskoci.card.model.Card;

import java.util.*;

// Singleton DAO
public enum SingletonCardDB implements CardDAO {
    instance;

    private Map<String, Card> contentProvider = new HashMap<String, Card>();

    private SingletonCardDB() {

        Card card = new Card("1", "Shoots Fireballs", "Shoots Fireballs from its eyes", "Creature");
        contentProvider.put("1", card);
        card = new Card("2", "Shoots IceBalls", "Shoots Iceballs from its cock", "Creature");
        contentProvider.put("2", card);

        // Initialization block, fill with mock data
        {
            for (int i = 3 ; i < 61; i++) {
                card = new Card(String.valueOf(i), "Shoots IceBalls", "Shoots Iceballs from its cock", "Creature");
                contentProvider.put(String.valueOf(i), card);
            }
        }


    }
    public Map<String, Card> getModel(){
        return contentProvider;
    }

    public List<Card> getAllCards() {
        List<Card> allCards = new ArrayList<Card>(contentProvider.values());
        return allCards;
    }
}
