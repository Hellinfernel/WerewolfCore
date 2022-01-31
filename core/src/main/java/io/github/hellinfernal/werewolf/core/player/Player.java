package io.github.hellinfernal.werewolf.core.player;

import io.github.hellinfernal.werewolf.core.role.GameRole;
import io.github.hellinfernal.werewolf.core.user.User;

public interface Player {
    GameRole role();

    boolean isAlive();

    void kill();

    User user();
}
