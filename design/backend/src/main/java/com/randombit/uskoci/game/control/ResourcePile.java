package com.randombit.uskoci.game.control;

import com.randombit.uskoci.card.model.Card;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.randombit.uskoci.game.control.GameConstants.RESOURCE_TYPE;

public class ResourcePile {
    int playerId;

    Map<RESOURCE_TYPE, ResourcePileForType> playersResourcePilesByType = new HashMap<RESOURCE_TYPE, ResourcePileForType>();

    public ResourcePile(int playerId) {
        this.playerId = playerId;
        for (RESOURCE_TYPE type:RESOURCE_TYPE.values()) {
            playersResourcePilesByType.put(type, new ResourcePileForType(type));
        }
    }

    public List<Card> getResourcesList() {
        List<Card> allResources = new ArrayList<Card>();
        for (RESOURCE_TYPE type: RESOURCE_TYPE.values()) {
            allResources.addAll(getResourcePileForType(type).getResources());
        }

        return allResources;
    }

    private ResourcePileForType getResourcePileForType(RESOURCE_TYPE type) {
        return playersResourcePilesByType.get(type);
    }

    public void put(Card card) {
        ResourcePileForType resourcePileForType = getResourcePileForCardsType(card);
        resourcePileForType.add(card);
    }

    private ResourcePileForType getResourcePileForCardsType(Card card) {
        RESOURCE_TYPE type = getResourceTypeFromString(card.getSummary());
        return getResourcePileForType(type);
    }

    public void remove(Card card) {
        ResourcePileForType resourcePileForType = getResourcePileForCardsType(card);
        resourcePileForType.remove(card);
    }

    private RESOURCE_TYPE getResourceTypeFromString(String typeString) {
        for (RESOURCE_TYPE type: RESOURCE_TYPE.values()) {
            if (typeString.contains(type.toString())) {
                return type;
            }
        }
        return null;
    }

    public int getValue() {
        int value = 0;
        for (RESOURCE_TYPE type:RESOURCE_TYPE.values()) {
            ResourcePileForType resourcePileForType = getResourcePileForType(type);
            value += resourcePileForType.getValue();
        }

        return value;
    }
}

class ResourcePileForType {
    RESOURCE_TYPE type;
    boolean multiplierPresent;
    List<Card> resources = new ArrayList<Card>();

    public List<Card> getResources() {
        return resources;
    }

    ResourcePileForType(RESOURCE_TYPE type) {
        this.type = type;
    }

    public void add(Card card) {
        if (GameConstants.MULTIPLIER.equals(card.getType()))   {
            multiplierPresent = true;
        }
        resources.add(card);
    }

    public int getValue() {
        int value = 0;
        for (Card card: resources) {
            value += Integer.valueOf(card.getValue());
        }
        if (multiplierPresent) {
            value *= GameConstants.MULTIPLIER_FACTOR;
        }
        return value;
    }

    public void remove(Card card) {
        if (GameConstants.MULTIPLIER.equals(card.getType()))   {
            multiplierPresent = false;
        }
        resources.remove(card);
    }
}
