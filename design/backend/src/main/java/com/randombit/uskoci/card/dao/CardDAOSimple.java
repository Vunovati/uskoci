package com.randombit.uskoci.card.dao;

import com.randombit.uskoci.card.model.Card;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: ovca
 * Date: 25.08.12.
 * Time: 16:00
 * To change this template use File | Settings | File Templates.
 */
public class CardDAOSimple implements CardDAO{
    public Map<String, Card> getModel() {
        return SingletonCardDB.instance.getModel();
    }

    public List<Card> getAllCards() {
        return SingletonCardDB.instance.getAllCards();  //To change body of implemented methods use File | Settings | File Templates.
    }
}
