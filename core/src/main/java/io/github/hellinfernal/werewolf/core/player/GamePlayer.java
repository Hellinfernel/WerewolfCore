package io.github.hellinfernal.werewolf.core.player;

import java.util.Collections;
import java.util.EnumSet;

import io.github.hellinfernal.werewolf.core.role.GameRole;
import io.github.hellinfernal.werewolf.core.role.SpecialRole;
import io.github.hellinfernal.werewolf.core.user.User;

public class GamePlayer implements Player {
    private final GameRole _role;
    private final User _user;
    private boolean _alive;
    private EnumSet<SpecialRole> _specialRoles = EnumSet.noneOf(SpecialRole.class);

    public GamePlayer(final GameRole role, final User user) {
        _role = role;
        _user = user;
        _alive = true;
    }
    public GamePlayer(final GameRole role, final User user,EnumSet<SpecialRole> specialRoles) {
        _role = role;
        _user = user;
        _alive = true;
        _specialRoles.addAll(specialRoles);
    }
    public GamePlayer(final GameRole role, final User user,SpecialRole specialRoles) {
        _role = role;
        _user = user;
        _alive = true;
        _specialRoles.add(specialRoles);
    }

    @Override
    public GameRole role() {
        return _role;
    }

    @Override
    public EnumSet<SpecialRole> specialRoles() {
        return _specialRoles;
    }



    @Override
    public void grantSpecialRole( final SpecialRole role ) {
        _specialRoles.add(role);

    }

    @Override
    public void denySpecialRole( final SpecialRole role ) {
        _specialRoles.remove(role);


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
    public void revive() {
        _alive = true;
    }

    @Override
    public User user() {
        return _user;
    }
}
