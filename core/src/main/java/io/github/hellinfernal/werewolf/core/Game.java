package io.github.hellinfernal.werewolf.core;

import discord4j.common.util.Snowflake;
import io.github.hellinfernal.werewolf.core.game.*;
import io.github.hellinfernal.werewolf.core.player.GamePlayer;
import io.github.hellinfernal.werewolf.core.player.Player;
import io.github.hellinfernal.werewolf.core.role.GameRole;
import io.github.hellinfernal.werewolf.core.role.SpecialRole;
import io.github.hellinfernal.werewolf.core.user.GlobalPrinter;
import io.github.hellinfernal.werewolf.core.user.User;
import io.github.hellinfernal.werewolf.core.winningcondition.VillagerWinningCondition;
import io.github.hellinfernal.werewolf.core.winningcondition.WerewolfWinningCondition;
import io.github.hellinfernal.werewolf.core.winningcondition.WinningCondition;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Predicate;
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
    private final List<GlobalPrinter> _globalPrinters;
    private final GameRound _nightRound = new NightRound(this);
    private final GameRound _dayRound = new DayRound();

    private final GameMove _werewolfMove = new WerewolfMove(this);
    private final GameMove _villagerMove = new VillagerMove(this);

    private final GameMove _witchMove1 = new WitchMove1(this);
    private final GameMove _witchMove2 = new WitchMove2(this);

    private final GameMove _AmorMove = new AmorMove(this);

    private GameRound _activeRound = _nightRound;
    private GameMove _activeMove = _werewolfMove;

    public Game(final List<User> usersThatWantToPlay,final List<GlobalPrinter> globalPrinters) {
        _globalPrinters = globalPrinters;
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
            final Player player = new GamePlayer(this,gameRole, user);
            _playersPlayingTheGame.add(player);
        }

    }

    /**
     *
     * @param usersThatWantToPlay
     * Contains users who have no prefered role
     * @param usersThatWantToBeWerewolfes
     * contains users who want to be Werewolfes
     */
    public Game(final List<User> usersThatWantToPlay,final List<User> usersThatWantToBeWerewolfes,final List<GlobalPrinter> globalPrinters) {
        _globalPrinters = globalPrinters;
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
            final Player player = new GamePlayer(this,gameRole,user);
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
            final Player player = new GamePlayer(this,gameRole, user);
            _playersPlayingTheGame.add(player);
        }

    }

    /**
     *
     * @param usersThatWantToPlay
     *       Contains users who have no prefered role
     *       @param usersThatWantToBeWerewolfes
     *       contains users who want to be Werewolfes
     * @param PlayersWithASpecialRole
     * contains users who want a special role :D
     */
    public Game(final List<User> usersThatWantToPlay, final List<User> usersThatWantToBeWerewolfes, final Map<SpecialRole,User> PlayersWithASpecialRole,final List<GlobalPrinter> globalPrinters) {
        _globalPrinters = globalPrinters;
        isDay = false;
        final long amountOfWerewolfs = Werewolf.getAmount(usersThatWantToPlay.size()+ usersThatWantToBeWerewolfes.size());
        int werewolfsSelected = 0;

        for (Map.Entry<SpecialRole, User> entry : PlayersWithASpecialRole.entrySet()) {
            SpecialRole s = entry.getKey();
            User u = entry.getValue();
            if (s._linkedCoreRole == Werewolf) {
                werewolfsSelected++;
            }

                _playersPlayingTheGame.add(new GamePlayer(this,s._linkedCoreRole, u, s));

        }

        if (usersThatWantToBeWerewolfes.size() - werewolfsSelected > amountOfWerewolfs){
            throw new RuntimeException("You need a bigger game");
        }

        for (final User user : usersThatWantToBeWerewolfes){
            GameRole gameRole;
            gameRole = Werewolf;
            werewolfsSelected++;
            final Player player = new GamePlayer(this,gameRole,user);
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
            final Player player = new GamePlayer(this,gameRole, user);
            _playersPlayingTheGame.add(player);
        }

    }

    public List<Player> getKilledPlayers() {
        return _playersPlayingTheGame.stream().filter(Player::isDead).collect(Collectors.toList());
    }

    public Player getLastKilledPlayer() {
        return _playersPlayingTheGame.stream()
                .filter(Player::isDead)
                .max(Comparator.comparing(k -> k.killed().get()))
                .orElse(null);
    }
    public void gameStart(){
        while (playStandardRound() == false){

        }
        System.out.println(":D");

        //TODO: this code is cursed.
    }

    //TODO: antwort des discord members entgegen nehmen und irgendwie an den move/voting machine weitergeben
    /**
    public void playerResponse(Player from, Player voted, Snowflake messageId ) {
        _activeMove.playerResponse(from, voted,messageId)
    }
     **/

    public boolean playStandardRound() {
        
        WinningCondition fulfilledWinningCondition = null;



        if ( isDay ) {

            _villagerMove.execute();
        } else {
            _werewolfMove.execute();

            _witchMove1.execute();

            _witchMove2.execute();
        }
        changeDayTime();
        // spielen wir Tag oder Nacht?
        // -> spielen die Werewolfs oder die Villagers?
        // -> oder ist die Runde fertig?
        // -> wie findet eine Abstimmung statt?
        // -> wie notifiziert das spiel die einzelnen Spieler?

        fulfilledWinningCondition = _winConditions.stream().filter(c -> c.isSatisfied(this)).findAny().orElse(null );

        if (fulfilledWinningCondition != null) {
            acceptGlobalPrinterMethod(GlobalPrinter::informAboutGameEnd);
            // alle player notifizieren
            // game schließen
            return true;
        }
        else {
            return false;
            //playStandardRound();
        }
    }

    private void changeDayTime() {
        if (isDay == true){

            isDay = false;
            acceptGlobalPrinterMethod(GlobalPrinter::informAboutChangeToNightTime);
        }
        else {
            isDay = true;
            acceptGlobalPrinterMethod(GlobalPrinter::informAboutChangeToDayTime);
            acceptGlobalPrinterMethod(GlobalPrinter::informAboutThingsHappendInNight);
        }
    }

    public List<Player> getPlayers() {
        return _playersPlayingTheGame;
    }

    public GameMove getVillagerMove() {
        return _villagerMove;
    }
    public GameMove getWerewolfMove(){
        return _werewolfMove;
    }

    public boolean isDay(){
        return isDay;
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

    /**
     *
     * @return a Amor move ;D
     */

    public GameMove getAmorMove() {
        return _AmorMove;
    }

    public Optional<Player> getSpecialClassPlayer(SpecialRole role) {
        return getPlayers().stream().filter(player -> player.specialRoles().contains(role)).findFirst();
    }
    public Player getPlayer(User user){
        AtomicReference<Player> player = new AtomicReference<>();
        getPlayers().stream().filter(x -> x.user() == user).findFirst().ifPresent(x -> player.set(x));
        return player.get();
    }

    public GameMove getWitchMove1() {
        return _witchMove1;
    }

    public GameMove getWitchMove2() {
        return _witchMove2;
    }

    /**
    * Gets a Random Player. Shouldn't throw a Exception.
     */
    public Player getRandomPlayer() {
        return getPlayers().stream().findAny().get();
    }

    /**
     * Gets a Random Player, filtered with an predicate. Shouldn't throw a Exception too, except if the predicate is too restrictive
     */
    public Player getRandomPlayerWithCondition(Predicate<Player> predicate){
        return getPlayers().stream().filter(predicate).findAny().orElseThrow();
    }

    /**
     *
     * @return a list where all Players with the called role are
     */
    public List<Player> getSpecialRolePlayers(SpecialRole specialRole){
        List<Player> list = getPlayers().stream()
                .filter(player -> player.specialRoles().contains(specialRole))
                .collect(Collectors.toList());
        return list;

    }

    public void acceptGlobalPrinterMethod( Consumer<GlobalPrinter> action){
        _globalPrinters.forEach(action::accept);
    }
}
