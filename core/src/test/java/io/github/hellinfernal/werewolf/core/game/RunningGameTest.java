package io.github.hellinfernal.werewolf.core.game;

import io.github.hellinfernal.werewolf.core.Game;
import io.github.hellinfernal.werewolf.core.TestUser;
import io.github.hellinfernal.werewolf.core.player.Player;
import io.github.hellinfernal.werewolf.core.role.GameRole;
import io.github.hellinfernal.werewolf.core.user.User;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

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

        assertThat(game.getPlayers().stream().filter(p -> p.role().equals(GameRole.Werewolf)).count()).isEqualTo(1);
        assertThat(game.getPlayers().stream().filter(p -> p.role().equals(GameRole.Villager)).count()).isEqualTo(3);

        game.playStandardRound();
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
        final TestUser lisa = new TestUser("Lisa", votes -> votes.stream().filter(p -> p.user() == peter).findFirst().orElse(null));
        usersThatWantToPlay.add(kevin);
        usersThatWantToPlay.add(aleks);
        usersThatWantToPlay.add(peter);
        usersThatWantToPlay.add(lisa);

        final Game game = new Game(usersThatWantToPlay);
        final VillagerMove villagerMove = new VillagerMove(game);

        assertThat(game.getAlivePlayers()).extracting(Player::user).containsOnly(aleks, peter, lisa, kevin);
        villagerMove.execute();
        assertThat(game.getAlivePlayers()).extracting(Player::user).containsOnly(lisa);
    }
}