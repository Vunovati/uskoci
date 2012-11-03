package com.randombit.uskoci.card.dao;

import com.randombit.uskoci.card.model.Card;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Singleton DAO
public enum SingletonCardDB implements CardDAO {
    instance;

    private Map<String, Card> contentProvider = new HashMap<String, Card>();

    private SingletonCardDB() {
        contentProvider.put("1", new Card("1", "food1", "resource", "Food resource, No.1", "1", "1", ""));
        contentProvider.put("2", new Card("2", "money1", "resource", "Money resource, No.1", "1", "1", ""));

        // Initialization block, fill with mock data
        {
            for (int i = 3 ; i < 61; i++) {
                String cardID = "" + String.valueOf(i);
                contentProvider.put(cardID, new Card(cardID, "weapon1", "resource", "Weapon resource, No.1", "1", "5", ""));

            }
        }
    }
    public Map<String, Card> getModel(){
        return contentProvider;
    }

    public List<Card> getAllCards() {
        return new ArrayList<Card>(contentProvider.values());
    }

    @Override
    public Card getCard(int cardId) {
        return contentProvider.get(String.valueOf(cardId));
    }
}
