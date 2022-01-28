package io.github.hellinfernal.werewolf.core.player;

import io.github.hellinfernal.werewolf.core.role.GameRole;
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
    public boolean isAlive() {
        return _alive;
    }

    @Override
    public User user() {
        return _user;
    }
}
