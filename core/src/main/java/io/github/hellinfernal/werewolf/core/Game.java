package io.github.hellinfernal.werewolf.core;

import io.github.hellinfernal.werewolf.core.game.*;
import io.github.hellinfernal.werewolf.core.player.GamePlayer;
import io.github.hellinfernal.werewolf.core.player.Player;
import io.github.hellinfernal.werewolf.core.role.GameRole;
import io.github.hellinfernal.werewolf.core.user.User;
import io.github.hellinfernal.werewolf.core.winningcondition.VillagerWinningCondition;
import io.github.hellinfernal.werewolf.core.winningcondition.WerewolfWinningCondition;
import io.github.hellinfernal.werewolf.core.winningcondition.WinningCondition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static io.github.hellinfernal.werewolf.core.role.GameRole.Villager;
import static io.github.hellinfernal.werewolf.core.role.GameRole.Werewolf;

/**
 * TODOS:
 * - Intro mit Geschichten erzählen zwischen den Runden
 * - Tag und Nacht sind die runden die gespielt werden, jede Rolle hat jeweils einen Zug
 */
public class Game {
    private boolean isDay;

    private final List<Player> _playersPlayingTheGame = new ArrayList<>();
    private final List<WinningCondition> _winConditions = List.of(
            new WerewolfWinningCondition(),
            new VillagerWinningCondition()
    );
    private final GameRound _nightRound = new NightRound(this);
    private final GameRound _dayRound = new DayRound();

    private final GameMove _werewolfMove = new WerewolfMove(this);
    private final GameMove _villagerMove = new VillagerMove(this);

    private GameRound _activeRound = _nightRound;
    private GameMove _activeMove = _werewolfMove;

    public Game(final List<User> usersThatWantToPlay) {
        isDay = false;
        final long amountOfWerewolfs = Werewolf.getAmount(usersThatWantToPlay.size());
        int werewolfsSelected = 0;
        Collections.shuffle(usersThatWantToPlay);

        for (final User user : usersThatWantToPlay) {
            GameRole gameRole;
            if (werewolfsSelected < amountOfWerewolfs) {
                gameRole = Werewolf;
                werewolfsSelected++;
            } else {
                gameRole = GameRole.Villager;
            }
            final Player player = new GamePlayer(gameRole, user);
            _playersPlayingTheGame.add(player);
        }

    }
    public Game(final List<User> usersThatWantToPlay,final List<User> usersThatWantToBeWerewolfes) {


        isDay = false;
        final long amountOfWerewolfs = Werewolf.getAmount(usersThatWantToPlay.size()+ usersThatWantToBeWerewolfes.size());
        int werewolfsSelected = 0;
        if (usersThatWantToBeWerewolfes.size() > amountOfWerewolfs){
            throw new RuntimeException("You need a bigger game");
        }
        for (final User user : usersThatWantToBeWerewolfes){
            GameRole gameRole;
            gameRole = Werewolf;
            werewolfsSelected++;
            final Player player = new GamePlayer(gameRole,user);
            _playersPlayingTheGame.add(player);
        }


        Collections.shuffle(usersThatWantToPlay);

        for (final User user : usersThatWantToPlay) {
            GameRole gameRole;
            if (werewolfsSelected < amountOfWerewolfs) {
                gameRole = Werewolf;
                werewolfsSelected++;
            } else {
                gameRole = GameRole.Villager;
            }
            final Player player = new GamePlayer(gameRole, user);
            _playersPlayingTheGame.add(player);
        }

    }



    public void playStandardRound() {
        
        WinningCondition fulfilledWinningCondition = null;

        while (fulfilledWinningCondition == null) {


        if (isDay == true) {
            _villagerMove.execute();


        } else {
            _werewolfMove.execute();

        }
        changeDayTime();
        // spielen wir Tag oder Nacht?
        // -> spielen die Werewolfs oder die Villagers?
        // -> oder ist die Runde fertig?
        // -> wie findet eine Abstimmung statt?
        // -> wie notifiziert das spiel die einzelnen Spieler?
            


        fulfilledWinningCondition = _winConditions.stream().filter(c -> c.isSatisfied(this)).findAny().orElse(null );
        }
        if (fulfilledWinningCondition != null) {
            getPlayers().forEach(player -> player.user().informAboutGameEnd());
            // alle player notifizieren
            // game schließen
        }
        else {
            playStandardRound();
        }
    }

    private void changeDayTime() {
        if (isDay == true){
            isDay = false;
        }
        else {
            isDay = true;
        }
    }

    public List<Player> getPlayers() {
        return _playersPlayingTheGame;
    }

    public GameMove get_villagerMove() {
        return _villagerMove;
    }
    public GameMove get_werewolfMove(){
        return _werewolfMove;
    }


    public List<Player> getAlivePlayers() {
        return getPlayers().stream().filter(Player::isAlive).collect(Collectors.toList());
    }
    public List<Player> getAliveWerewolfPlayers(){
        return getPlayers().stream().filter(player -> player.role() == Werewolf).filter(Player::isAlive).collect(Collectors.toList());
    }
    public List<Player> getAliveVillagerPlayers(){
        return getPlayers().stream().filter(player -> player.role() == Villager).filter(Player::isAlive).collect(Collectors.toList());

    }
}
