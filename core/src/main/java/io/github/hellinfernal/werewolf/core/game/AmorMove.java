package io.github.hellinfernal.werewolf.core.game;

import io.github.hellinfernal.werewolf.core.Game;
import io.github.hellinfernal.werewolf.core.player.Player;
import io.github.hellinfernal.werewolf.core.role.SpecialRole;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class AmorMove implements GameMove{
    private Game _game;
    boolean alreadyDone = false;

    /**
     *
     */
    public AmorMove(Game game){
        _game = game;

    }



    @Override
    public void execute() {
        if (alreadyDone == false){
            Optional<Player> amor = _game.getSpecialClassPlayer(SpecialRole.Amor);
            if (amor.isPresent()){
                Player player1 = amor.get().user().requestLover(_game.getPlayers());
                Player player2 = amor.get().user().requestLover(_game.getPlayers().stream().filter(p -> p != player1).collect(Collectors.toList()));
                player1.grantSpecialRole(SpecialRole.Lover);
                player2.grantSpecialRole(SpecialRole.Lover);
                player1.user().informAboutFallingInLove(player2);
                player2.user().informAboutFallingInLove(player1);
                player1.addDeathTrigger(game -> deathTrigger(player2));
                player2.addDeathTrigger(game -> deathTrigger(player1));
                alreadyDone = true;
            }
        }
    }
    void deathTrigger(Player player){
        player.kill();

    }
}
