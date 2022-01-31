package io.github.hellinfernal.werewolf.core.user;

import io.github.hellinfernal.werewolf.core.player.Player;

import java.util.Set;

public interface User {
    String name();


    void tell(final String whatTodo);



    Player requestVillagerVote(Set<Player> potentialTargets);

    void informAboutResultOfVillagerVote(Player killedPlayer);
}
