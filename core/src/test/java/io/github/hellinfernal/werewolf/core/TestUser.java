package io.github.hellinfernal.werewolf.core;

import io.github.hellinfernal.werewolf.core.user.User;

import java.util.UUID;

public class TestUser implements User {
    private final String _name;

    public TestUser() {
        _name = UUID.randomUUID().toString();
    }

    @Override
    public String name() {
        return _name;
    }

    @Override
    public void tell(final String whatTodo) {

    }
}
