package io.github.hellinfernal.werewolf.core.role;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GameRoleTest {
    @Test
    void testAmount() {
        assertThat(GameRole.Werewolf.getAmount(100)).isEqualTo(25);
        assertThat(GameRole.Werewolf.getAmount(5)).isEqualTo(1);
    }
}