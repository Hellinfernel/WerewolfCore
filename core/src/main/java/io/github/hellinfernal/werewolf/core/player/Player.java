package io.github.hellinfernal.werewolf.core.player;

import java.util.EnumSet;

import io.github.hellinfernal.werewolf.core.role.GameRole;
import io.github.hellinfernal.werewolf.core.role.SpecialRole;
import io.github.hellinfernal.werewolf.core.user.User;

public interface Player {
    GameRole role();
    EnumSet<SpecialRole> specialRoles();

    default boolean isWitch() {
        return specialRoles().stream().anyMatch(e -> e.equals(SpecialRole.Witch));
    }

    void grantSpecialRole(final SpecialRole role );
    void denySpecialRole(final SpecialRole role );

    boolean isAlive();

    default boolean isDead() {
        return !isAlive();
    }

    void kill();

    User user();
}
