package com.randombit.uskoci.game.control;

public class GameConstants {
    static final int INITIAL_NUMBER_OF_CARDS_IN_THE_DECK = 60;
    static final int MAX_NUMBER_OF_PLAYERS = 6;
    static final int MIN_NUMBER_OF_PLAYERS = 3;
    static final int BEGINNING_NUMBER_OF_CARDS = 4;
    static final int MAX_NUMBER_OF_CARDS_IN_HAND = 5;
    static final int MAX_NUMBER_OF_PLAYER_POINTS = 25;
    static final int MULTIPLIER_FACTOR = 2;
    static final String MULTIPLIER = "x2";
    static final String RESOURCE = "resource";
    static final String EVENT = "instant";
    static final String WILL = "Will of God";
    static final String STORM = "Storm";
    static final String SPYGLASS = "Spyglass";
    static final String SPY = "Spy";
    static final String TRICKERY = "Trickery";
    static final String THEFT = "Theft";
    static final String SNITCH = "Snitch";
    static final String SEADOG = "Old Sea Dog";
    static final String FORT = "Fortress of Nehaj";

    // Card types
//    public static final String WOOD = "wood";
//    public static final String FOOD = "food";
//    public static final String MONEY = "money";
//    public static final String WEAPON = "weapon";
    public enum RESOURCE_TYPE {
        WOOD("wood"), FOOD("food"), MONEY("money"), WEAPON("weapon");

        private String name;

        private RESOURCE_TYPE(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
    
    public enum ACTION {
        MOVE_CARDS("move_cards"), REVEAL_CARDS("reveal_caards"), END("end");

        private String name;

        private ACTION(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
    
    public enum AREA {
        DISCARDPILE("discardpile"), RESOURCE("resource"),HAND("hand");

        private String name;

        private AREA(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
    
    public enum QUANTITY {
        ALL("all"), HALF("half"), CARD("card");

        private String name;

        private QUANTITY(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
    
    
    
    
    public enum CONDITION {
        TRUE("TRUE"), FALSE("false"), END("end"), EVENT("event"),FOOD("food"),WOOD("wood"),GOLD("gold");

        private String name;

        private CONDITION(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
    
    public enum QUESTION {
 
    	CHOOSE_PLAYER("choose_player"), CHOOSE_CARDS("choose_cards");

        private String name;

        private QUESTION(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
    
    
    
    

    // Exceptions
    public static final String EXCEPTION_DRAW_CARD_NOT_ON_THE_MOVE = "You cannot draw a card, you are not on the move";
    public static final String EXCEPTION_DRAW_MORE_THAN_ONE_CARD = "You cannot draw more than one card";
    public static final String EXCEPTION_NEXT_TURN_NO_BEGINNING_CARD_DRAWN = "You must draw a card before you can set next players turn";
    public static final String EXCEPTION_NEXT_PLAYER_BY_PLAYER_NOT_ON_THE_MOVE = "You are not on the move so you cannot set next players turn";
    public static final String EXCEPTION_NEXT_PLAYER_TOO_MUCH_CARDS_IN_HAND = "You have more than " + MAX_NUMBER_OF_CARDS_IN_HAND + " cards in your hand, you must discard some cards first";
    public static final String EXCEPTION_PLAYER_NOT_ON_MOVE_PLAYS_RESOURCE = "You cannot play resource cards, it is not your turn";
    public static final String EXCEPTION_PLAY_RESOURCE_TWICE = "You cannot play two resource cards in same turn";
    public static final String EXCEPTION_PLAY_CARD_BEGINNING_CARD_NOT_DRAWN = "You must draw a card at the beginning of you turn";
    public static final String EXCEPTION_PLAY_TOO_MUCH_RESOURCES = "You cannot play resources because you have more than " + MAX_NUMBER_OF_PLAYER_POINTS + "points";
    public static final String EXCEPTION_RESOURCE_AFTER_EVENT = "You cannot play resource after an event card";
    public static final String EXCEPTION_EVENT_AFTER_EVENT = "You cannot play event card after another event";
}