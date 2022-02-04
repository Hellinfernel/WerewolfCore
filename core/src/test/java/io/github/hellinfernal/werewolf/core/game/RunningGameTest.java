package io.github.hellinfernal.werewolf.core.game;

import io.github.hellinfernal.werewolf.core.Game;
import io.github.hellinfernal.werewolf.core.TestUser;
import io.github.hellinfernal.werewolf.core.player.Player;
import io.github.hellinfernal.werewolf.core.role.GameRole;
import io.github.hellinfernal.werewolf.core.user.User;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

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
        final TestUser aleks = new TestUser("Aleks", v -> false);
        final TestUser kevin = new TestUser("Kevin", voteUser(aleks));
        final TestUser peter = new TestUser("Peter", voteUser(kevin));
        final TestUser lisa = new TestUser("Lisa", voteUser(kevin));
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

    public Predicate<Player> voteUser(TestUser testUser) {
        return p -> p.user().equals(testUser);
    }
    public Predicate<Player> voteUser(List<TestUser> testUsers){
        return p -> testUsers.stream().filter(u -> u.equals(p)).findFirst().isPresent();
    }

    /*@Test
    void testVillagersWithSameVotes(){
        final List<User> usersThatWantToPlay = new ArrayList<>();
        final TestUser aleks = new TestUser("Aleks", v -> false);
        final TestUser kevin = new TestUser("Kevin", voteUser(aleks));
        final TestUser peter = new TestUser("Peter", voteUser(kevin));
        final TestUser lisa = new TestUser("Lisa", voteUser(aleks));
        final TestUser sahra = new TestUser("Sahra", voteUser(kevin));
        final TestUser tina = new TestUser("Tina", voteUser(peter));


        usersThatWantToPlay.add(kevin);
        usersThatWantToPlay.add(aleks);
        usersThatWantToPlay.add(peter);
        usersThatWantToPlay.add(lisa);
        usersThatWantToPlay.add(sahra);
        usersThatWantToPlay.add(tina);

        final Game game = new Game(usersThatWantToPlay);

        assertThat(game.getAlivePlayers()).extracting(Player::user).containsOnly(aleks, peter, lisa, kevin,sahra,tina);
        game.getVillagerMove().execute();
        assertThat(game.getAlivePlayers()).extracting(Player::user).containsOnly(aleks,peter,lisa,sahra,tina);

    } */

    @Test
    void testWerewolfHunt(){
        final List<User> usersThatWantToPlay = new ArrayList<>();
        final List<User> usersThatWantToBeWerewolfes = new ArrayList<>();
        final TestUser nostradamus;
        final TestUser aleks = new TestUser("Aleks", v -> false);
        final TestUser kevin = new TestUser("Kevin", voteUser(aleks));
        final TestUser peter = new TestUser("Peter", voteUser(kevin));
        final TestUser lisa = new TestUser("Lisa", voteUser(peter));
        nostradamus = new TestUser("Nostradamus",voteUser(kevin));

        usersThatWantToPlay.add(kevin);
        usersThatWantToPlay.add(aleks);
        usersThatWantToPlay.add(peter);
        usersThatWantToPlay.add(lisa);
        usersThatWantToBeWerewolfes.add(nostradamus);

        final Game game = new Game(usersThatWantToPlay,usersThatWantToBeWerewolfes);
        assertThat(game.getAliveWerewolfPlayers()).hasSize(1);
        assertThat(game.getAliveVillagerPlayers()).hasSize(4);
        assertThat(game.getAlivePlayers()).extracting(Player::user).containsOnly(aleks, peter, lisa, kevin, nostradamus);
        assertThat(game.getAliveWerewolfPlayers()).extracting(Player::user).containsOnly(nostradamus);
        game.getWerewolfMove().execute();
        assertThat(game.getAlivePlayers()).extracting(Player::user).containsOnly(aleks, peter, lisa, nostradamus);


    }
    @Test
    void testSecondBallot(){
        //Tests if the double vote feature works.
        final List<User> usersThatWantToPlay = new ArrayList<>();
        final List<User> usersThatWantToBeWerewolfes = new ArrayList<>();

        final TestUser villager3 = new TestUser("Lisa");
        final TestUser villager1 = new TestUser("Aleks");
        final TestUser villager2 = new TestUser("Peter");
        final TestUser villager4 = new TestUser("Tina");
        final TestUser werewolf1 = new TestUser("Nostradamus");

        usersThatWantToBeWerewolfes.add(werewolf1);

        usersThatWantToPlay.add(villager1);
        usersThatWantToPlay.add(villager2);
        usersThatWantToPlay.add(villager3);
        usersThatWantToPlay.add(villager4);

        final Game game = new Game(usersThatWantToPlay,usersThatWantToBeWerewolfes);
        final List<TestUser> villager4VoteList = new ArrayList<>();
        villager4VoteList.add(villager2);
        villager4VoteList.add(werewolf1);
        werewolf1.changeVote(voteUser(villager1));
        villager1.changeVote(voteUser(werewolf1));
        villager2.changeVote(voteUser(villager1));
        villager3.changeVote(voteUser(werewolf1));
        villager4.changeVote(voteUser(villager4VoteList));
        game.getVillagerMove().execute();
        assertThat(game.getKilledPlayers()).extracting(Player::user).containsOnly(werewolf1);



    }

    @Test
    void testBaseGame(){
        final List<User> usersThatWantToPlay = new ArrayList<>();
        final List<User> usersThatWantToBeWerewolfes = new ArrayList<>();

        final TestUser villager3 = new TestUser("Lisa");
        final TestUser villager1 = new TestUser("Aleks");
        final TestUser villager2 = new TestUser("Peter");
        final TestUser villager4 = new TestUser("Tina");
        final TestUser werewolf1 = new TestUser("Kevin");
        final TestUser werewolf2 = new TestUser("Nostradamus");

        usersThatWantToBeWerewolfes.add(werewolf1);
        usersThatWantToBeWerewolfes.add(werewolf2);
        usersThatWantToPlay.add(villager1);
        usersThatWantToPlay.add(villager2);
        usersThatWantToPlay.add(villager3);
        usersThatWantToPlay.add(villager4);


        // test user erzeugen
        // wen voten die test user beim nächsten zug?
        // zug ausführen -> playround
        // wen voten die test user beim nächsten zug?
        // zug ausführen -> playround
        // wen voten die test user beim nächsten zug?

        final Game game = new Game(usersThatWantToPlay,usersThatWantToBeWerewolfes);

        // first round is played at night -> so only werewolfs are allowed to vote
        werewolf1.changeVote(voteUser(villager1));
        werewolf2.changeVote(voteUser(villager1));
        assertThat(game.playStandardRound())
              .describedAs("Game has finished too early.")
              .isFalse();
        assertThat(game.getKilledPlayers()).extracting(Player::user).containsOnly(villager1);

        // second round is played at day -> so all villagers are allowed to vote
        villager2.changeVote(voteUser(werewolf1));
        villager3.changeVote(voteUser(werewolf1));
        villager4.changeVote(voteUser(villager4));
        werewolf1.changeVote(voteUser(villager3));
        werewolf2.changeVote(voteUser(villager2));
        assertThat(game.playStandardRound())
              .describedAs("Game has finished too early.")
              .isFalse();
        assertThat(game.getKilledPlayers()).extracting(Player::user).containsOnly(villager1, werewolf1);

        //Third round is played at night -> so only werewolfs are allowed to vote

        assertThat(game.playStandardRound())
                .describedAs("Game has finished too early.")
                .isFalse();
        assertThat(game.getKilledPlayers()).extracting(Player::user).containsOnly(villager1, werewolf1,villager2);

        //Forth round is played at day -> so all villagers are allowed to vote
        //final round
        villager3.changeVote(voteUser(werewolf2));
        villager4.changeVote(voteUser(werewolf2));
        werewolf2.changeVote(voteUser(villager3));

        assertThat(game.playStandardRound())
                .describedAs("Game Has finished")
                .isTrue();

        assertThat(game.getAlivePlayers()).extracting(Player::user).containsOnly(villager3,villager4);




    }


}