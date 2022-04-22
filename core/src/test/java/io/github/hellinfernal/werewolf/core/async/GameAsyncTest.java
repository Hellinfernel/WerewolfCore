package io.github.hellinfernal.werewolf.core.async;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

import io.github.hellinfernal.werewolf.core.TestUser;
import io.github.hellinfernal.werewolf.core.user.ConsolePrinter;


class GameAsyncTest {

   @Test
   void testGame() {
      final TestUser nostradamus = new TestUser("Nostradamus");
      final TestUser aleks = new TestUser("Aleks");
      final TestUser kevin = new TestUser("Kevin");
      final TestUser peter = new TestUser("Peter");
      final TestUser lisa = new TestUser("Lisa");

      final GameAsync game = new GameAsync(List.of(nostradamus, aleks, kevin, peter, lisa) , List.of(new ConsolePrinter()));

      assertThat(game.getAliveWerewolfPlayers()).hasSize(1);
      assertThat(game.getAliveVillagerPlayers()).hasSize(4);

      game.playRound();
   }
}