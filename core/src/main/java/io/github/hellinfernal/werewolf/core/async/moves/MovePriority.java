package io.github.hellinfernal.werewolf.core.async.moves;

import java.rmi.MarshalledObject;

public enum MovePriority implements Comparable<MovePriority>{


    HUNTER_MOVE(1001),
    AMOR_MOVE(1000),
    VILLAGER_MOVE(11),
    WEREWOLF_MOVE(10),

    WITCH_MOVE1(9),
    WITCH_MOVE2(8);

    MovePriority(int number){
        _value = number;
    }
    public final int _value;


}
