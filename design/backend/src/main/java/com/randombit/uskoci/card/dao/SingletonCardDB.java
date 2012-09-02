package com.randombit.uskoci.card.dao;

import com.randombit.uskoci.card.model.Card;

import java.util.*;

// Singleton DAO
public enum SingletonCardDB implements CardDAO {
    instance;

    private Map<String, Card> contentProvider = new HashMap<String, Card>();

    private SingletonCardDB() {

        contentProvider.put("id1", new Card("id1", "food1", "Food resource, No.1", "resource"));
        contentProvider.put("id2", new Card("id2", "money1", "Money resource, No.1", "resource"));

        // Initialization block, fill with mock data
        {
            for (int i = 3 ; i < 61; i++) {
                String cardID = "id" + String.valueOf(i);
                contentProvider.put(cardID, new Card(cardID, "weapon1", "Weapon resource, No.1", "resource"));
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
