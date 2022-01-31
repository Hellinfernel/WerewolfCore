package io.github.hellinfernal.werewolf.core.game;

import io.github.hellinfernal.werewolf.core.Game;
import io.github.hellinfernal.werewolf.core.TestUser;
import io.github.hellinfernal.werewolf.core.TestUserKevin;
import io.github.hellinfernal.werewolf.core.TestUserWithFixVictim;
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
        usersThatWantToPlay.add(new TestUserWithFixVictim("Kevin"));
        usersThatWantToPlay.add(new TestUserWithFixVictim("Kevin"));
        usersThatWantToPlay.add(new TestUserWithFixVictim("Kevin"));
        usersThatWantToPlay.add(new TestUserKevin());

        final Game game = new Game(usersThatWantToPlay);
        VillagerMove villagerMove = new VillagerMove(game);
        villagerMove.execute();
        assertThat(game.getPlayers().stream().filter(player -> player.user().name() != "Kevin").findFirst().get().isAlive() == false);
    }
}