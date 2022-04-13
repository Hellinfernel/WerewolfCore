package io.github.hellinfernal.werewolf.discord.bot;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class EventManager<T> {
    List<Consumer<T>> functions = new ArrayList<>();

    public EventManager() {
    }

    void addFunction(Consumer<T> function) {
        this.functions.add(function);
    }

    public void trigger(T t) {
        this.functions.forEach((function) -> {
            function.accept(t);
        });
    }
}

