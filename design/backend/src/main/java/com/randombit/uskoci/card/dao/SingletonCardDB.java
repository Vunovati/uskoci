package com.randombit.uskoci.card.dao;

import com.mongodb.*;
import com.randombit.uskoci.card.model.Card;
import java.util.*;

// Singleton DAO
public enum SingletonCardDB implements CardDAO {
    instance;

    private Map<String, Card> contentProvider = new HashMap<String, Card>();

    private SingletonCardDB() {

        try
        {
            Mongo mongo = new Mongo("alex.mongohq.com", 10068); //TODO: ubaciti URI i port u config
            DB db = mongo.getDB("UskociCards");
            boolean authenticate = db.authenticate("uskociAdmin", "willofgod".toCharArray()); //TODO: credentials u config

            if(authenticate)
            {
                DBCollection cards = db.getCollection("cards");
                DBCursor cursor = cards.find();
                DBObject card;

                while(cursor.hasNext())
                {
                    card = cursor.next();
                    String cardID = card.get("shortID").toString();
                    String cardSummary = card.get("summary").toString();
                    String cardDescription = card.get("description").toString();
                    String cardType = card.get("type").toString();

                    contentProvider.put(cardID, new Card(cardID, cardSummary, cardDescription, cardType));
                }

                //preostale karte punimo dummy podacima
                for(long i=cards.getCount()+1; i<61; i++)
                {
                    String cardID = "id" + String.valueOf(i);
                    contentProvider.put(cardID, new Card(cardID, "dummy", "dummy", "dummy"));
                }
            }
        }
        catch(Exception ex)
        {

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
