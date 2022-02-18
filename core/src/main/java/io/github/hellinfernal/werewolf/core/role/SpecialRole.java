package io.github.hellinfernal.werewolf.core.role;

import io.github.hellinfernal.werewolf.core.player.Player;


import javax.lang.model.element.AnnotationMirror;

import java.util.function.Consumer;

import static io.github.hellinfernal.werewolf.core.role.GameRole.Villager;

public enum SpecialRole {

   /** the Witch has 2 potions, one heal potion, and one kill potion.
    *  she can use them in the night after WerewolfMove
    */
   Witch(Villager),
   Amor(Villager),
   Lover(null);



   public GameRole _linkedCoreRole;



   SpecialRole(GameRole linkedCoreRole){
      _linkedCoreRole =linkedCoreRole;
   }



}
