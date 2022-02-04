package io.github.hellinfernal.werewolf.core.player;

import java.util.EnumSet;

import io.github.hellinfernal.werewolf.core.role.GameRole;
import io.github.hellinfernal.werewolf.core.role.SpecialRole;
import io.github.hellinfernal.werewolf.core.user.User;

public class GamePlayer implements Player {
    private final GameRole _role;
    private final User _user;
    private boolean _alive;

    public GamePlayer(final GameRole role, final User user) {
        _role = role;
        _user = user;
        _alive = true;
    }

    @Override
    public GameRole role() {
        return _role;
    }

    @Override
    public EnumSet<SpecialRole> specialRoles() {
        return null;
    }

    @Override
    public void grantSpecialRole( final SpecialRole role ) {

    }

    @Override
    public void denySpecialRole( final SpecialRole role ) {

    }

    @Override
    public boolean isAlive() {
        return _alive;
    }

    @Override
    public void kill() {
        _alive = false;
    }

    @Override
    public User user() {
        return _user;
    }
}
