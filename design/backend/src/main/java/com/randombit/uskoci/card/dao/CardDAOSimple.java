package com.randombit.uskoci.card.dao;

import com.randombit.uskoci.card.model.Card;

import java.util.List;
import java.util.Map;

public class CardDAOSimple implements CardDAO{
    public Map<String, Card> getModel() {
        return SingletonCardDB.instance.getModel();
    }

    public List<Card> getAllCards() {
        return SingletonCardDB.instance.getAllCards();  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Card getCard(int cardId) {
        return SingletonCardDB.instance.getCard(cardId);
    }
}