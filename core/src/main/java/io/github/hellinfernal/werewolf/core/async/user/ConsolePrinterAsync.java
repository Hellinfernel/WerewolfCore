package io.github.hellinfernal.werewolf.core.async.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.hellinfernal.werewolf.core.player.Player;

public class ConsolePrinterAsync implements GlobalPrinterAsync {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsolePrinterAsync.class);

    @Override
    public void informAboutStartOfTheVillagerVote() {

    }

    @Override
    public void informAboutResultOfVillagerVote(Player killedPlayer) {
        LOGGER.debug(killedPlayer + " was killed.");

    }

    @Override
    public void informAboutThingsHappendInNight() {
        //TODO: Implement

    }

    @Override
    public void informAboutGameEnd() {
        LOGGER.debug("game ends :D");
    }

    @Override
    public void informAboutChangeToDayTime() {
        LOGGER.debug("Its now DayTime");
    }

    @Override
    public void informAboutChangeToNightTime() {
        LOGGER.debug("Its now NightTime");

    }

    @Override
    public void informAboutStartOfTheHunt() {

    }

    @Override
    public void informAboutEndOfTheHunt() {

    }
}
