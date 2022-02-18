package io.github.hellinfernal.werewolf.core.player;

import java.time.Instant;
import java.util.*;
import java.util.function.Consumer;

import io.github.hellinfernal.werewolf.core.Game;
import io.github.hellinfernal.werewolf.core.role.GameRole;
import io.github.hellinfernal.werewolf.core.role.SpecialRole;
import io.github.hellinfernal.werewolf.core.user.User;

public class GamePlayer implements Player {
    private final GameRole _role;
    private final User _user;
    private final Game _game;

    private EnumSet<SpecialRole> _specialRoles = EnumSet.noneOf(SpecialRole.class);
    private Optional<Instant>    _killed = Optional.empty();
    private List<Consumer<Game>> _deathTriggers = new ArrayList<>();

    public GamePlayer(final Game game,final GameRole role, final User user) {
        _role = role;
        _user = user;
        _game = game;
    }
    public GamePlayer(final Game game,final GameRole role, final User user,EnumSet<SpecialRole> specialRoles) {
        _role = role;
        _user = user;
        _specialRoles.addAll(specialRoles);
        _game = game;
    }
    public GamePlayer(final Game game,final GameRole role, final User user,SpecialRole specialRoles) {
        _role = role;
        _user = user;
        _specialRoles.add(specialRoles);
        _game = game;
    }

    public EnumSet<SpecialRole> get_specialRoles() {
        return _specialRoles;
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
        if (!_killed.isPresent()) {
            _killed = Optional.of(Instant.now());
            _deathTriggers.stream().forEach(consumer -> consumer.accept(_game));

            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
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

    @Override
    public void addDeathTrigger(Consumer<Game> consumer) {
        _deathTriggers.add(consumer);

    }

    @Override
    public String toString(){
        return user().toString();
    }
}
