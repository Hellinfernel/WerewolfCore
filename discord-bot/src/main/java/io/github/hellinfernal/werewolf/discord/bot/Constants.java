package io.github.hellinfernal.werewolf.discord.bot;

import io.github.hellinfernal.werewolf.core.role.SpecialRole;

import java.lang.reflect.Array;
import java.util.*;

public class Constants {
    public static final Set<SpecialRole> ALL_SPECIALROLES = new HashSet<>
            (Collections.unmodifiableCollection(
                    Arrays.asList(
                            SpecialRole.Witch,
                            SpecialRole.Hunter,
                            SpecialRole.Amor)));
    public static final Set<String> KIUSERNAMES = new HashSet<>
            (Collections.unmodifiableCollection(
                    Arrays.asList(
                            "Saskia",
                            "Marcel",
                            "Maduro",
                            "Arko",
                            "Nocki",
                            "Butthead"
                    )));
}
