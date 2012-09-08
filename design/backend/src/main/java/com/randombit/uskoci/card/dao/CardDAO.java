package com.randombit.uskoci.card.dao;

import com.randombit.uskoci.card.model.Card;

import java.util.List;
import java.util.Map;

public interface CardDAO {
    public Map<String, Card> getModel();

    public List<Card> getAllCards();

    public Card getCard(int cardId);
}
