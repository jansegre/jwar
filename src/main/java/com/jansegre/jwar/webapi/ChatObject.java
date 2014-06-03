/*
 * This file is part of JWar.
 *
 * JWar is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.
 *
 */

package com.jansegre.jwar.webapi;

public class ChatObject {

    enum Type {
        JOIN,
        LEAVE,
        MESSAGE
    }

    public String user;
    public String message;
    public String room;
    public Type type;

    public ChatObject() {
        type = Type.MESSAGE;
    }

    public ChatObject(String message) {
        this();
        this.message = message;
    }

    public ChatObject(String user, String message) {
        this(message);
        this.user = user;
    }

    @Override
    public String toString() {
        switch (type) {
            case MESSAGE:
                return "Message on room " + room + " from user " + user + ": " + message;
            case JOIN:
                return "User " + user + " joined room " + room + ": " + message;
            case LEAVE:
                return "User " + user + " left room " + room + ": " + message;
            default:
                return "Unkown message " + room + ":" + user + ":" + message + ":";
        }
    }
}
