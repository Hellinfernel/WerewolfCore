package io.github.hellinfernal.werewolf.core;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.function.Consumer;

public abstract class GameRole { //Every Player needs a role.
    public abstract boolean WinningCondition(WerewolfGame werewolfGame); //Every Role has a target. if this target is fulfilled, all who have this role Win.
    public abstract int howMuchOfThisRole(WerewolfGame werewolfGame); //Every Role has also a amount of Players who can take this role in one Game.
    public void sequenceLinkedToRole(){} //Usually, roles have sequences linked to their role. For example, the witch, who will be later added, has a own sequence in the night where she can save a killed villager and kill someone with a potion. But i am myself unsure how to solve it if someone does not have such a sequence.

}
