package io.github.hellinfernal.werewolf.core.player;

import java.time.Instant;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Optional;

import io.github.hellinfernal.werewolf.core.role.GameRole;
import io.github.hellinfernal.werewolf.core.role.SpecialRole;
import io.github.hellinfernal.werewolf.core.user.User;

public class GamePlayer implements Player {
    private final GameRole _role;
    private final User _user;
    private EnumSet<SpecialRole> _specialRoles = EnumSet.noneOf(SpecialRole.class);
    private Optional<Instant>    _killed = Optional.empty();

    public GamePlayer(final GameRole role, final User user) {
        _role = role;
        _user = user;
    }
    public GamePlayer(final GameRole role, final User user,EnumSet<SpecialRole> specialRoles) {
        _role = role;
        _user = user;
        _specialRoles.addAll(specialRoles);
    }
    public GamePlayer(final GameRole role, final User user,SpecialRole specialRoles) {
        _role = role;
        _user = user;
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
        return _killed.isEmpty();
    }

    @Override
    public void kill() {
        _killed = Optional.of(Instant.now());
    }

    @Override
    public Optional<Instant> killed() {
        return _killed;
    }

    @Override
    public void revive() {
        _killed = Optional.empty();
    }

    @Override
    public User user() {
        return _user;
    }
}
