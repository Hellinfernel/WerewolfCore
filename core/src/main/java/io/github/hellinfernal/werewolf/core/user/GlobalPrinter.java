package io.github.hellinfernal.werewolf.core.user;

import io.github.hellinfernal.werewolf.core.player.Player;

public interface GlobalPrinter {


    void informAboutStartOfTheVillagerVote();
    void informAboutResultOfVillagerVote(Player killedPlayer);

    void informAboutThingsHappendInNight();

    void informAboutGameEnd();

    void informAboutChangeToDayTime();
    void informAboutChangeToNightTime();
    void informAboutStartOfTheHunt();
    void informAboutEndOfTheHunt();
}
