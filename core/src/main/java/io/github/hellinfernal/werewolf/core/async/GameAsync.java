package io.github.hellinfernal.werewolf.core.async;

import static io.github.hellinfernal.werewolf.core.role.GameRole.Villager;
import static io.github.hellinfernal.werewolf.core.role.GameRole.Werewolf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import io.github.hellinfernal.werewolf.core.Game;
import io.github.hellinfernal.werewolf.core.game.GameRound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.hellinfernal.werewolf.core.Protocol;
import io.github.hellinfernal.werewolf.core.async.moves.AmorMoveAsync;
import io.github.hellinfernal.werewolf.core.async.moves.GameMoveAsync;
import io.github.hellinfernal.werewolf.core.async.moves.VillagerMoveAsync;
import io.github.hellinfernal.werewolf.core.async.moves.WerewolfMoveAsync;
import io.github.hellinfernal.werewolf.core.async.moves.WitchMoveAsync1;
import io.github.hellinfernal.werewolf.core.async.moves.WitchMoveAsync2;
import io.github.hellinfernal.werewolf.core.async.player.GamePlayerAsync;
import io.github.hellinfernal.werewolf.core.async.player.PlayerAsync;
import io.github.hellinfernal.werewolf.core.async.round.DayRoundAsync;
import io.github.hellinfernal.werewolf.core.async.round.GameRoundAsync;
import io.github.hellinfernal.werewolf.core.async.round.NightRoundAsync;
import io.github.hellinfernal.werewolf.core.async.user.GlobalPrinterAsync;
import io.github.hellinfernal.werewolf.core.async.user.UserAsync;
import io.github.hellinfernal.werewolf.core.async.winningcondition.VillagerWinningConditionAsync;
import io.github.hellinfernal.werewolf.core.async.winningcondition.WerewolfWinningConditionAsync;
import io.github.hellinfernal.werewolf.core.async.winningcondition.WinningConditionAsync;
import io.github.hellinfernal.werewolf.core.player.Player;
import io.github.hellinfernal.werewolf.core.role.GameRole;
import io.github.hellinfernal.werewolf.core.role.SpecialRole;
import io.github.hellinfernal.werewolf.core.user.GlobalPrinter;
import io.github.hellinfernal.werewolf.core.user.User;
import io.github.hellinfernal.werewolf.core.vote.ImperativVotingMachine;
import io.github.hellinfernal.werewolf.core.vote.VotingMachine;
import io.github.hellinfernal.werewolf.core.winningcondition.WinningCondition;


/**
 * TODOS:
 * - Intro mit Geschichten erzählen zwischen den Runden
 * - Tag und Nacht sind die runden die gespielt werden, jede Rolle hat jeweils einen Zug
 */
public class GameAsync {
    private boolean isDay;

    private final List<PlayerAsync>           _playersPlayingTheGame = new ArrayList<>();
    private final List<WinningConditionAsync> _winConditions         = List.of(
            new WerewolfWinningConditionAsync(),
            new VillagerWinningConditionAsync()
    );
    private final List<GlobalPrinterAsync>         _globalPrinters;
    private final GameRoundAsync      _nightRound = new NightRoundAsync();
    private final GameRoundAsync      _dayRound   = new DayRoundAsync();

    private final GameMoveAsync _werewolfMove = new WerewolfMoveAsync(this);
    private final GameMoveAsync _villagerMove = new VillagerMoveAsync(this);

    private final GameMoveAsync _witchMove1 = new WitchMoveAsync1(this);
    private final GameMoveAsync _witchMove2 = new WitchMoveAsync2(this);

    private final GameMoveAsync _AmorMove = new AmorMoveAsync(this);

    private final List<GameMoveAsync> _movesQueue = new ArrayList<>();
    private static final Logger   LOGGER = LoggerFactory.getLogger(GameAsync.class);
    private final        Protocol protocol;


    public GameAsync(final List<UserAsync> usersThatWantToPlay, final List<GlobalPrinterAsync> globalPrinters) {
        _globalPrinters = globalPrinters;
        isDay = false;
        protocol = new Protocol("Game");
        final long amountOfWerewolfs = Werewolf.getAmount(usersThatWantToPlay.size());
        int werewolfsSelected = 0;
        Collections.shuffle(usersThatWantToPlay);

        for (final UserAsync user : usersThatWantToPlay) {
            GameRole gameRole;
            if (werewolfsSelected < amountOfWerewolfs) {
                gameRole = Werewolf;
                werewolfsSelected++;
            } else {
                gameRole = GameRole.Villager;
            }
            final PlayerAsync player = new GamePlayerAsync(this,gameRole, user);
            _playersPlayingTheGame.add(player);
        }
        LOGGER.debug("Game Created: \n " + toString());
    }

    /**
     *  @param usersThatWantToPlay
     * Contains users who have no prefered role
     * @param usersThatWantToBeWerewolfes
     * @param voteStrategy
     */
    public GameAsync(final List<UserAsync> usersThatWantToPlay, final List<UserAsync> usersThatWantToBeWerewolfes, final List<GlobalPrinterAsync> globalPrinters) {
        _globalPrinters = globalPrinters;
        isDay = false;
        protocol = new Protocol("Game");
        final long amountOfWerewolfs = Werewolf.getAmount(usersThatWantToPlay.size()+ usersThatWantToBeWerewolfes.size());
        int werewolfsSelected = 0;
        if (usersThatWantToBeWerewolfes.size() > amountOfWerewolfs){
            throw new RuntimeException("You need a bigger game");
        }
        for (final UserAsync user : usersThatWantToBeWerewolfes){
            GameRole gameRole;
            gameRole = Werewolf;
            werewolfsSelected++;
            final PlayerAsync player = new GamePlayerAsync(this,gameRole,user);
            _playersPlayingTheGame.add(player);
        }


        Collections.shuffle(usersThatWantToPlay);

        for (final UserAsync user : usersThatWantToPlay) {
            GameRole gameRole;
            if (werewolfsSelected < amountOfWerewolfs) {
                gameRole = Werewolf;
                werewolfsSelected++;
            } else {
                gameRole = GameRole.Villager;
            }
            final PlayerAsync player = new GamePlayerAsync(this,gameRole, user);
            _playersPlayingTheGame.add(player);
        }
        LOGGER.debug("Game Created: \n " + toString());

    }

    /**
     * Returns the players who are dead.
     * @return a list of killed Players
     */

    public List<Player> getKilledPlayers() {
        return _playersPlayingTheGame.stream().filter(Player::isDead).collect(Collectors.toList());
    }

    public PlayerAsync getLastKilledPlayer() {
        return _playersPlayingTheGame.stream()
                .filter(Player::isDead)
                .max(Comparator.comparing(k -> k.killed().get()))
                .orElse(null);
    }
    public void gameStart(){
        while ( playRound() == false){

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

    public boolean playRound() {
        
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
    private void playRoundAsync(){

        GameRoundAsync gameRoundAsync = getListOfPlayedMovesOfThisRound();
        _movesQueue.addAll(gameRoundAsync.getMoves());
        while (!_movesQueue.isEmpty()){
            playMoveInQueue();
        }

    }

    private void playMoveInQueue() {
        _movesQueue.sort(move -> m);
    }


    /**
     *
     * @return returns the round that is played
     */
    private GameRoundAsync getListOfPlayedMovesOfThisRound() {
        if (isDay){
            return _dayRound;
        }
        else {
            return _nightRound;
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

    public List<PlayerAsync> getPlayers() {
        return _playersPlayingTheGame;
    }

    public GameMoveAsync getVillagerMove() {
        return _villagerMove;
    }
    public GameMoveAsync getWerewolfMove(){
        return _werewolfMove;
    }

    public boolean isDay(){
        return isDay;
    }


    public List<PlayerAsync> getAlivePlayers() {
        return getPlayers().stream().filter(Player::isAlive).collect(Collectors.toList());
    }
    public List<PlayerAsync> getAliveWerewolfPlayers(){
        return getPlayers().stream().filter(player -> player.role() == Werewolf).filter(Player::isAlive).collect(Collectors.toList());
    }
    public List<PlayerAsync> getAliveVillagerPlayers(){
        return getPlayers().stream().filter(player -> player.role() == Villager).filter(Player::isAlive).collect(Collectors.toList());

    }

    /**
     *
     * @return a Amor move ;D
     */

    public GameMoveAsync getAmorMove() {
        return _AmorMove;
    }

    public Optional<PlayerAsync> getSpecialClassPlayer(SpecialRole role) {
        return getPlayers().stream().filter(player -> player.specialRoles().contains(role)).findFirst();
    }
    public Player getPlayer(User user){
        AtomicReference<Player> player = new AtomicReference<>();
        getPlayers().stream().filter(x -> x.user() == user).findFirst().ifPresent(x -> player.set(x));
        return player.get();
    }

    public GameMoveAsync getWitchMove1() {
        return _witchMove1;
    }

    public GameMoveAsync getWitchMove2() {
        return _witchMove2;
    }

    /**
    * Gets a Random Player. Shouldn't throw a Exception.
     */
    public PlayerAsync getRandomPlayer() {
        return getPlayers().stream().findAny().get();
    }

    /**
     * Gets a Random Player, filtered with an predicate. Shouldn't throw a Exception too, except if the predicate is too restrictive
     */
    public PlayerAsync getRandomPlayerWithCondition(Predicate<Player> predicate){
        return getPlayers().stream().filter(predicate).findAny().orElseThrow();
    }

    /**
     *
     * @return a list where all Players with the called role are
     */
    public List<PlayerAsync> getSpecialRolePlayers(SpecialRole specialRole){
        return getPlayers().stream()
              .filter(player -> player.specialRoles().contains(specialRole))
              .collect(Collectors.toList());

    }

    @Override
    public String toString() {
        return "Game{" +
                "isDay=" + isDay +
                ", _playersPlayingTheGame=" + _playersPlayingTheGame +
                ", _winConditions=" + _winConditions +
                ", _globalPrinters=" + _globalPrinters +
                ", _nightRound=" + _nightRound +
                ", _dayRound=" + _dayRound +
                ", _werewolfMove=" + _werewolfMove +
                ", _villagerMove=" + _villagerMove +
                ", _witchMove1=" + _witchMove1 +
                ", _witchMove2=" + _witchMove2 +
                ", _AmorMove=" + _AmorMove +
                ", _activeRound=" + _activeRound +
                ", _activeMove=" + _activeMove +
                '}';
    }

    public void acceptGlobalPrinterMethod(Consumer<GlobalPrinterAsync> action){
        _globalPrinters.forEach(action);
    }

    public VotingMachine getVoteStrategy(List<Player> voters, List<Player> playerSelection, BiFunction<Player,Collection<Player>,Player> votingFunction) {
        return new ImperativVotingMachine(voters,playerSelection,votingFunction);
    }
}
