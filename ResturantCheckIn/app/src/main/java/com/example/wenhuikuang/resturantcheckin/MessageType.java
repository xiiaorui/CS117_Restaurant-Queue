package com.example.wenhuikuang.resturantcheckin;


public enum MessageType {
    ACTION_GET_OPEN_RESTAURANTS(true, "get_restaurants"),
    ACTION_OPEN_RESTAURANT(true, "open_restaurant"),
    ACTION_CREATE_RESTAURANT(true, "create_restaurant"),
    ACTION_CALL_PARTY(true, "call_party"),
    ACTION_QUEUE(true, "queue"),
    ACTION_LEAVE_QUEUE(true, "leave_queue"),
    ACTION_QUEUE_STATUS(true, "queue_status"),
    NOTIFY_ENTER_QUEUE(false, "enter_queue"),
    NOTIFY_LEAVE_QUEUE(false, "leave_queue"),
    NOTIFY_CALL(false, "close"),
    NOTIFY_CLOSE(false, "call");

    private final boolean mIsAction;
    private final String mValue;

    MessageType(boolean isAction, String value) {
        mIsAction = isAction;
        mValue = value;
    }

    public boolean isAction() {
        return mIsAction;
    }

    public String getValue() {
        return mValue;
    }

}
