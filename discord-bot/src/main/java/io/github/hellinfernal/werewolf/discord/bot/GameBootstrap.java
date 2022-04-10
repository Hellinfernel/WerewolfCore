package io.github.hellinfernal.werewolf.discord.bot;

import discord4j.common.util.Snowflake;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.component.LayoutComponent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.reaction.ReactionEmoji;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class GameBootstrap {
    private final List<Member> _members = new ArrayList<>();
    private final Instant _started = Instant.now();
    private final Button _registerButton = Button.primary(UUID.randomUUID().toString(), "Join");
    private final Button _leaveButton = Button.danger(UUID.randomUUID().toString(), "Leave");
    private final Button _configButton = Button.secondary(UUID.randomUUID().toString(), ReactionEmoji.codepoints("U+2699"));
    private final Set<String> _buttonIds = Set.of(_registerButton.getCustomId().get(), _leaveButton.getCustomId().get(), _configButton.getCustomId().get());
    private final Snowflake channelId;


    public GameBootstrap(final Snowflake channelId) {
        this.channelId = channelId;
    }


    public LayoutComponent configureButtons() {
        if (_members.isEmpty()) {
            return ActionRow.of(_registerButton, _leaveButton, _configButton);
        }
        Button registerButtonWithMembers = Button.primary(_registerButton.toString(), "Join (" + _members.size() + ")");
        return ActionRow.of(registerButtonWithMembers, _leaveButton, _configButton);
    }

    public boolean hasButton(final String customId) {
        return _buttonIds.contains(customId);
    }

    public boolean hasClickedRegister(final String customId) {
        return _registerButton.getCustomId().get().equalsIgnoreCase(customId);
    }

    public boolean join(final Member member) {
        if (_members.contains(member)) {
            return false;
        }

        _members.add(member);
        return true;
    }

    public LayoutComponent disableRegisterButton() {

        return ActionRow.of(_registerButton, _leaveButton, _configButton);
    }

    public boolean hasClickedLeave(final String customId) {
        return _leaveButton.getCustomId().get().equalsIgnoreCase(customId);
    }

    public boolean leave(final Member member) {
        if (!_members.contains(member)) {
            return false;
        }
        _members.remove(member);
        return true;
    }
}
