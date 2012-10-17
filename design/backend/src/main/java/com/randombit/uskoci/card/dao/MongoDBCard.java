package com.randombit.uskoci.card.dao;

import com.mongodb.*;
import com.randombit.uskoci.card.model.Card;
import java.util.*;

// Singleton DAO
public enum MongoDBCard implements CardDAO {
    instance;

    private Map<String, Card> contentProvider = new HashMap<String, Card>();

    private MongoDBCard() {

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
                    String cardType = card.get("type").toString();
                    String cardDescription = card.get("description").toString();
                    String cardPosition = card.get("position").toString();
                    String cardValue = card.get("value").toString();

                    contentProvider.put(cardID, new Card(cardID, cardSummary, cardType, cardDescription, cardPosition, cardValue, ""));
                }

                //preostale karte punimo istim podacima
                for(long i=cards.getCount()+1; i<61; i++)
                {
                    String cardID = String.valueOf(i);
                    Card tempCard = contentProvider.get(String.valueOf((i-1)%8 + 1));
                    contentProvider.put(cardID, new Card(cardID, tempCard.getSummary(),
                            tempCard.getType(), tempCard.getDescription(), tempCard.getPosition(), tempCard.getValue(), ""));
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

    @Override
    public Card getCard(int cardId) {
        return contentProvider.get(String.valueOf(cardId));
    }
}