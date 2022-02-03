package io.github.hellinfernal.werewolf.core.game;

import io.github.hellinfernal.werewolf.core.Game;
import io.github.hellinfernal.werewolf.core.TestUser;
import io.github.hellinfernal.werewolf.core.player.Player;
import io.github.hellinfernal.werewolf.core.role.GameRole;
import io.github.hellinfernal.werewolf.core.user.User;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static io.github.hellinfernal.werewolf.core.role.GameRole.Werewolf;
import static org.assertj.core.api.Assertions.assertThat;

class RunningGameTest {



    @Test
    void testGame() {
        final List<User> usersThatWantToPlay = new ArrayList<>();
        usersThatWantToPlay.add(new TestUser());
        usersThatWantToPlay.add(new TestUser());
        usersThatWantToPlay.add(new TestUser());
        usersThatWantToPlay.add(new TestUser());

        final Game game = new Game(usersThatWantToPlay);

        assertThat(game.getPlayers().stream().filter(p -> p.role().equals(Werewolf)).count()).isEqualTo(1);
        assertThat(game.getPlayers().stream().filter(p -> p.role().equals(GameRole.Villager)).count()).isEqualTo(3);


    }

    @Test
    void testVillagerMove(){
        final List<User> usersThatWantToPlay = new ArrayList<>();
        final TestUser aleks = new TestUser("Aleks", v -> null);
        final TestUser kevin = new TestUser("Kevin", votes -> votes.stream().filter(p -> p.user() == aleks).findFirst().orElse(null));
        final TestUser peter = new TestUser("Peter", votes -> votes.stream().filter(p -> p.user() == kevin).findFirst().orElse(null));
        final TestUser lisa = new TestUser("Lisa", votes -> votes.stream().filter(p -> p.user() == kevin).findFirst().orElse(null));
        usersThatWantToPlay.add(kevin);
        usersThatWantToPlay.add(aleks);
        usersThatWantToPlay.add(peter);
        usersThatWantToPlay.add(lisa);

        final Game game = new Game(usersThatWantToPlay);
        final VillagerMove villagerMove = new VillagerMove(game);

        assertThat(game.getAlivePlayers()).extracting(Player::user).containsOnly(aleks, peter, lisa, kevin);
        villagerMove.execute();
        assertThat(game.getAlivePlayers()).extracting(Player::user).containsOnly(aleks, peter, lisa);
    }

    @Test
    void testVillagersWithSameVotes(){
        final List<User> usersThatWantToPlay = new ArrayList<>();
        final TestUser aleks = new TestUser("Aleks", v -> null);
        final TestUser kevin = new TestUser("Kevin", votes -> votes.stream().filter(p -> p.user() == aleks).findFirst().orElse(null));
        final TestUser peter = new TestUser("Peter", votes -> votes.stream().filter(p -> p.user() == kevin).findFirst().orElse(null));
        final TestUser lisa = new TestUser("Lisa", votes -> votes.stream().filter(p -> p.user() == aleks).findFirst().orElse(null));
        final TestUser sahra = new TestUser("Sahra", votes -> votes.stream().filter(p -> p.user() == kevin).findFirst().orElse(null));
        final TestUser tina = new TestUser("Tina",votes -> votes.stream().filter(p -> p.user() == peter).findFirst()
                .orElse(votes.stream().filter(p -> p.user() == kevin).findFirst().orElse(null)));


        usersThatWantToPlay.add(kevin);
        usersThatWantToPlay.add(aleks);
        usersThatWantToPlay.add(peter);
        usersThatWantToPlay.add(lisa);
        usersThatWantToPlay.add(sahra);
        usersThatWantToPlay.add(tina);

        final Game game = new Game(usersThatWantToPlay);
        final VillagerMove villagerMove = new VillagerMove(game);

        assertThat(game.getAlivePlayers()).extracting(Player::user).containsOnly(aleks, peter, lisa, kevin,sahra,tina);
        game.get_villagerMove().execute();
        assertThat(game.getAlivePlayers()).extracting(Player::user).containsOnly(aleks,peter,lisa,sahra,tina);

    }

    @Test
    void testWerewolfHunt(){
        final List<User> usersThatWantToPlay = new ArrayList<>();
        final List<User> usersThatWantToBeWerewolfes = new ArrayList<>();
        final TestUser nostradamus;
        final TestUser aleks = new TestUser("Aleks", v -> null);
        final TestUser kevin = new TestUser("Kevin", votes -> votes.stream().filter(p -> p.user() == aleks).findFirst().orElse(null));
        final TestUser peter = new TestUser("Peter", votes -> votes.stream().filter(p -> p.user() == kevin).findFirst().orElse(null));
        final TestUser lisa = new TestUser("Lisa", votes -> votes.stream().filter(p -> p.user() == peter).findFirst().orElse(null));
        nostradamus = new TestUser("Nostradamus",votes -> votes.stream().filter(p -> p.user() == kevin).findFirst().orElse(null));




        usersThatWantToPlay.add(kevin);
        usersThatWantToPlay.add(aleks);
        usersThatWantToPlay.add(peter);
        usersThatWantToPlay.add(lisa);
        usersThatWantToBeWerewolfes.add(nostradamus);

        final Game game = new Game(usersThatWantToPlay,usersThatWantToBeWerewolfes);
        assertThat(game.getPlayers().stream().filter(p -> p.role().equals(Werewolf)).count()).isEqualTo(1);
        assertThat(game.getPlayers().stream().filter(p -> p.role().equals(GameRole.Villager)).count()).isEqualTo(4);
        assertThat(game.getAlivePlayers()).extracting(Player::user).containsOnly(aleks, peter, lisa, kevin,nostradamus);
        assertThat(game.getAlivePlayers().stream().filter(player -> player.user() == nostradamus)).extracting(Player::role).contains(Werewolf);
        game.get_werewolfMove().execute();
        assertThat(game.getAlivePlayers()).extracting(Player::user).containsOnly(aleks, peter, lisa,nostradamus);


    }
    @Test
    void testBaseGame(){
        final List<User> usersThatWantToPlay = new ArrayList<>();
        final List<User> usersThatWantToBeWerewolfes = new ArrayList<>();
        final TestUser nostradamus;
        // he is the Werewolf, Dies at the second Day
        final TestUser aleks;
        // dies in the first night
        final TestUser kevin;
        //Dies in the first Vote, is a werewolf too
        final TestUser peter;
        //Survives
        final TestUser lisa;
        //Survives
        final TestUser tina;
        //Dies in the second Night
        lisa = new TestUser("Lisa");
        aleks = new TestUser("Aleks");
        kevin = new TestUser("Kevin");
        peter = new TestUser("Peter");
        nostradamus = new TestUser("Nostradamus");

        tina = new TestUser("Tina");
        nostradamus.set_votedPlayer(votes -> votes.stream().filter(p -> p.user() == aleks).findFirst().orElse(votes.stream().filter(p -> p.user() == tina).findFirst().orElse(votes.stream().filter(p -> p.user() == lisa).findFirst().orElse(null))));
        kevin.set_votedPlayer(votes -> votes.stream().filter(p -> p.user() == aleks).findFirst().orElse(votes.stream().filter(p -> p.user() == tina).findFirst().orElse(null)));
        aleks.set_votedPlayer(votes -> votes.stream().filter(p -> p.user() == nostradamus).findFirst().orElse(null));
        lisa.set_votedPlayer(votes -> votes.stream().filter(p -> p.user() == kevin).findFirst().orElse(votes.stream().filter(p -> p.user() == nostradamus).findFirst().orElse(null)));
        peter.set_votedPlayer(votes -> votes.stream().filter(p -> p.user() == kevin).findFirst().orElse(votes.stream().filter(p -> p.user() == nostradamus).findFirst().orElse(null)));
        tina.set_votedPlayer(votes -> votes.stream().filter(p -> p.user() == kevin).findFirst().orElse(votes.stream().filter(p -> p.user() == peter).findFirst().orElse(null)));




        usersThatWantToPlay.add(aleks);
        usersThatWantToBeWerewolfes.add(kevin);
        usersThatWantToPlay.add(peter);
        usersThatWantToPlay.add(lisa);
        usersThatWantToPlay.add(tina);
        usersThatWantToBeWerewolfes.add(nostradamus);

        final Game game = new Game(usersThatWantToPlay,usersThatWantToBeWerewolfes);
        assertThat(game.getPlayers().stream().filter(p -> p.role().equals(Werewolf)).count()).isEqualTo(2);
        assertThat(game.getPlayers().stream().filter(p -> p.role().equals(GameRole.Villager)).count()).isEqualTo(4);
        assertThat(game.getAlivePlayers()).extracting(Player::user).containsOnly(aleks, peter, lisa, kevin,nostradamus,tina);
        assertThat(game.getAliveWerewolfPlayers()).extracting(Player::user).containsOnly(kevin,nostradamus);
        game.playStandardRound();
        assertThat(game.getAlivePlayers()).extracting(Player::user).containsOnly(lisa, peter);

    }


}