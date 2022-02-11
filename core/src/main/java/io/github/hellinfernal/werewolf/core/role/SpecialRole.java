package io.github.hellinfernal.werewolf.core.role;

import io.github.hellinfernal.werewolf.core.player.Player;

public enum SpecialRole {
   Witch(GameRole.Villager);

   public GameRole _linkedCoreRole;

   SpecialRole(GameRole linkedCoreRole){
      _linkedCoreRole =linkedCoreRole;
   }
}
