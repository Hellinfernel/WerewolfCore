package io.github.hellinfernal.werewolf.core.player;

import io.github.hellinfernal.werewolf.core.Game;
import io.github.hellinfernal.werewolf.core.role.GameRole;
import io.github.hellinfernal.werewolf.core.role.SpecialRole;
import io.github.hellinfernal.werewolf.core.user.User;

import java.time.Instant;
import java.util.EnumSet;
import java.util.Optional;
import java.util.function.Consumer;

public interface Player {
    GameRole role();

    EnumSet<SpecialRole> specialRoles();

    void grantSpecialRole(final SpecialRole role);

    void denySpecialRole(final SpecialRole role);

    boolean isAlive();

    default boolean isDead() {
        return !isAlive();
    }

    void kill();
    Optional<Instant> killed();

    void revive();
    //Sets isAlive to true

    User user();
    void addDeathTrigger(Consumer<Game> consumer);



}
