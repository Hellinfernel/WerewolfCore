package io.github.hellinfernal.werewolf.core.role;

import io.github.hellinfernal.werewolf.core.player.Player;

public enum SpecialRole {
   Witch(GameRole.Villager){
      boolean hasHealPotion = true;
      boolean hasKillPotion = true;
      void useHealPotion(Player player){
         player.revive();
         hasHealPotion = false;
      }
   };

   public GameRole _linkedCoreRole;
   SpecialRole(GameRole linkedCoreRole){
      _linkedCoreRole =linkedCoreRole;
   }
}
