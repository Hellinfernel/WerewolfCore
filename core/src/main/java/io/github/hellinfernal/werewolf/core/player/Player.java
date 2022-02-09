package io.github.hellinfernal.werewolf.core.player;

import java.util.EnumSet;

import io.github.hellinfernal.werewolf.core.role.GameRole;
import io.github.hellinfernal.werewolf.core.role.SpecialRole;
import io.github.hellinfernal.werewolf.core.user.User;

public interface Player {
    GameRole role();
    EnumSet<SpecialRole> specialRoles();



    void grantSpecialRole(final SpecialRole role );
    void denySpecialRole(final SpecialRole role );

    boolean isAlive();

    default boolean isDead() {
        return !isAlive();
    }

    void kill();

    void revive();
    //Sets isAlive to true

    User user();


}
