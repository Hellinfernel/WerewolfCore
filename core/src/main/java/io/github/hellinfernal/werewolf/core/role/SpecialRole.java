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
   /** The Amor makes 2 People to Lovers. If they survive alone, They win
    *
    */
   Amor(Villager),
   /** The Lovers are the Role who is granted by The Amor
    * If one of the lovers Dies, the other does too :D
    */
   Lover(null),
   /** The Hunter can someone Kill if he Dies
    *
    */
   Hunter(Villager);



   public GameRole _linkedCoreRole;



   SpecialRole(GameRole linkedCoreRole){
      _linkedCoreRole =linkedCoreRole;
   }



}
