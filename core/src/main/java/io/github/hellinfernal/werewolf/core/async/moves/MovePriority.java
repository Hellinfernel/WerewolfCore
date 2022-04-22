package io.github.hellinfernal.werewolf.core.async.moves;

public enum MovePriority implements Comparable<MovePriority>{


    VILLAGER_MOVE(11),
    WEREWOLF_MOVE(10),
    AMOR_MOVE(1000),
    WITCH_MOVE1(9),
    WITCH_MOVE2(8),
    HUNTER_MOVE(1001);

    MovePriority(int number){
        _value = number;
    }
    private final int _value;

}
