package io.github.hellinfernal.werewolf.discord.bot;

import discord4j.common.util.Snowflake;
import discord4j.core.object.component.Button;
import discord4j.core.object.entity.Member;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.discordjson.json.EmojiData;
import io.github.hellinfernal.werewolf.core.Game;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class ChannelGame {
    public final Snowflake channelId;
    public final Instant time;
    private final String customButtonId = UUID.randomUUID().toString();
    private final String custom1ButtonId = UUID.randomUUID().toString();
    private final String custom2ButtonId = UUID.randomUUID().toString();
    //ReactionEmoji gear = ReactionEmoji.of(962406566265511946L, "\U+FE0F",false);

    ReactionEmoji gear = ReactionEmoji.codepoints("U+2699");
    private final List<Member> members = new ArrayList<>();
    public Button registerButton = Button.primary(customButtonId.toString(), "Join");
    public Button leaveButton = Button.danger(custom1ButtonId, "Leave");
    public Button configButton = Button.secondary(custom2ButtonId, gear);

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
        if (!members.contains(member)){
            members.add(member);
            registerButton = Button.primary(customButtonId.toString(), "Join (" + members.size() + ")");
            return true;
        }
        else return false;
    }
    public boolean leave(final Member member){
        if (members.contains(member)){
            members.remove(member);
            registerButton = Button.primary(customButtonId.toString(), "Join (" + members.size() + ")");
            return true;
        }
        else return false;
    }

    public Game initiate() {
        return new Game(members.stream().flatMap(member -> new DiscordWerewolfUser(member)))
    }
}
