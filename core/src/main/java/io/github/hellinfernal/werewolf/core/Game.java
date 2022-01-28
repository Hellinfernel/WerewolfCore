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

/**
 * TODOS:
 * - Intro mit Geschichten erzählen zwischen den Runden
 * - Tag und Nacht sind die runden die gespielt werden, jede Rolle hat jeweils einen Zug
 */
public class Game {

    private final List<Player> _playersPlayingTheGame = new ArrayList<>();
    private final List<WinningCondition> _winConditions = List.of(
            new WerewolfWinningCondition(),
            new VillagerWinningCondition()
    );
    private final GameRound _nightRound = new NightRound(this);
    private final GameRound _dayRound = new DayRound();

    private final GameMove _werewolfMove = new WerewolfMove(this);
    private final GameMove _villagerMove = new VillagerMove();

    private GameRound _activeRound = _nightRound;
    private GameMove _activeMove = _werewolfMove;

    public Game(final List<User> usersThatWantToPlay) {
        final long amountOfWerewolfs = GameRole.Werewolf.getAmount(usersThatWantToPlay.size());
        int werewolfsSelected = 0;
        Collections.shuffle(usersThatWantToPlay);

        for (final User user : usersThatWantToPlay) {
            GameRole gameRole;
            if (werewolfsSelected < amountOfWerewolfs) {
                gameRole = GameRole.Werewolf;
                werewolfsSelected++;
            } else {
                gameRole = GameRole.Villager;
            }
            final Player player = new GamePlayer(gameRole, user);
            _playersPlayingTheGame.add(player);
        }
    }


    public void playRound() {
        // spielen wir Tag oder Nacht?
        // -> spielen die Werewolfs oder die Villagers?
        // -> oder ist die Runde fertig?
        // -> wie findet eine Abstimmung statt?
        // -> wie notifiziert das spiel die einzelnen Spieler?


        final boolean gameIsOver = _winConditions.stream().anyMatch(c -> c.isSatisfied(this));
        if (gameIsOver) {
            // alle player notifizieren
            // game schließen
        }
    }

    public List<Player> getPlayers() {
        return _playersPlayingTheGame;
    }

    public List<Player> getAlivePlayers() {
        return getPlayers().stream().filter(Player::isAlive).collect(Collectors.toList());
    }
}
