package com.randombit.uskoci.game;

public class ActionNotAllowedException extends Exception {
    public ActionNotAllowedException() {
        super();
    }

    public ActionNotAllowedException(String message) {
        super(message);
    }
}
