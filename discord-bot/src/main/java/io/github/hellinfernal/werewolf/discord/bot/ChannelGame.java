package io.github.hellinfernal.werewolf.discord.bot;

import discord4j.common.util.Snowflake;
import discord4j.core.object.component.Button;
import discord4j.core.object.entity.Member;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ChannelGame {
    public final Snowflake channelId;
    public final Instant time;
    private final String customButtonId = UUID.randomUUID().toString();
    private final AtomicLong members = new AtomicLong();
    public Button registerButton = Button.primary(customButtonId.toString(), "Join");

    public ChannelGame(final Snowflake channelId) {
        this.channelId = channelId;
        this.time = Instant.now();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ChannelGame that = (ChannelGame) o;
        return Objects.equals(channelId, that.channelId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(channelId);
    }

    public boolean join(final Member member) {
        members.incrementAndGet();
        registerButton = Button.primary(customButtonId.toString(), "Join (" + members.get() + ")");
        return true;
    }
}
