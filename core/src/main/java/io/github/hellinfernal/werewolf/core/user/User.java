package io.github.hellinfernal.werewolf.core.user;

import io.github.hellinfernal.werewolf.core.player.Player;

import java.util.Set;

public interface User {
    String name();


    void tell(final String whatTodo);



    Player requestVillagerVote(Set<Player> potentialTargets);
    //for the vote at the day
    Player requestWerewolfVote(Set<Player> potentialTargets);
    //for the vote in the night, is usually only used if you are a werewolf

    void informAboutResultOfVillagerVote(Player killedPlayer);

    void informAboutGameEnd();
}
