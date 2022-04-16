package io.github.hellinfernal.werewolf.core.game;

import io.github.hellinfernal.werewolf.core.Game;
import io.github.hellinfernal.werewolf.core.player.Player;
import io.github.hellinfernal.werewolf.core.player.PlayersInLove;
import io.github.hellinfernal.werewolf.core.role.SpecialRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.Optional;

public class AmorMove implements GameMove{
    private final Logger LOGGER = LoggerFactory.getLogger(AmorMove.class);
    private Game _game;
    private PlayersInLove _playersInLove;

    public AmorMove(Game game){
        _game = game;

    }

    @Override
    public void execute() {
        if (_playersInLove != null) {
            return;
        }
        Optional<Player> amor = _game.getSpecialClassPlayer(SpecialRole.Amor);
        if (amor.isPresent()) {
            PlayersInLove playersInLove = amor.get().user().requestLovers(_game.getPlayers());
            if (playersInLove.lover1() == null || playersInLove.lover2() == null || playersInLove.lover1().user().equals(playersInLove.lover2().user())) {
                throw new IllegalStateException("lovers not unique");
            }
            playersInLove.grantRole();
            playersInLove.informAboutFallingInLove();
            playersInLove.deaths();
            _playersInLove = playersInLove;
            LOGGER.debug("The following Players fall in Love :" + playersInLove.toString());
        }
    }

}
