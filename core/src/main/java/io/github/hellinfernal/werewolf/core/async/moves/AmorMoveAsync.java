package io.github.hellinfernal.werewolf.core.async.moves;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.hellinfernal.werewolf.core.Game;
import io.github.hellinfernal.werewolf.core.async.GameAsync;
import io.github.hellinfernal.werewolf.core.player.Player;
import io.github.hellinfernal.werewolf.core.player.PlayersInLove;
import io.github.hellinfernal.werewolf.core.role.SpecialRole;

public class AmorMoveAsync implements GameMoveAsync {
    private final Logger        LOGGER = LoggerFactory.getLogger(AmorMoveAsync.class);
    private       GameAsync     _game;
    private       PlayersInLove _playersInLove;

    public AmorMoveAsync(GameAsync game){
        _game = game;

    }

    @Override
    public MovePriority movePriority() {
        return MovePriority.AMOR_MOVE;
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

    @Override
    public void start() {

    }

}
