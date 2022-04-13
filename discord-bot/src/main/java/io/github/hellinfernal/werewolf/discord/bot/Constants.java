package io.github.hellinfernal.werewolf.discord.bot;

import io.github.hellinfernal.werewolf.core.role.SpecialRole;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Constants {
    public static final Set<SpecialRole> ALL_SPECIALROLES = new HashSet<>
            (Collections.unmodifiableCollection(
                    Arrays.asList(
                            SpecialRole.Witch,
                            SpecialRole.Hunter,
                            SpecialRole.Amor)));
}
