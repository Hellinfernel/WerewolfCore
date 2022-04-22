package io.github.hellinfernal.werewolf.core.async.player;

import java.util.List;

import io.github.hellinfernal.werewolf.core.role.SpecialRole;

public interface PlayersInLoveAsync {
    final class InLoveAsync implements PlayersInLoveAsync {

        private final PlayerAsync       _lover1;
        private final PlayerAsync       _lover2;
        private final List<PlayerAsync> _lovers;

        public InLoveAsync(final PlayerAsync lover1, final PlayerAsync lover2) {
            _lover1 = lover1;
            _lover2 = lover2;
            _lovers = List.of(_lover1, _lover2);
        }

        @Override
        public PlayerAsync lover1() {
            return _lover1;
        }

        @Override
        public PlayerAsync lover2() {
            return _lover2;
        }

        @Override
        public void grantRole() {
            _lovers.forEach(p -> p.grantSpecialRole(SpecialRole.Lover));
        }

        @Override
        public void informAboutFallingInLove() {
            _lover1.user().informAboutFallingInLove(_lover2);
            _lover2.user().informAboutFallingInLove(_lover1);
        }

        @Override
        public void deaths() {
            _lover1.addDeathTrigger(game -> deathTrigger(_lover2));
            _lover2.addDeathTrigger(game -> deathTrigger(_lover1));
        }

        void deathTrigger( PlayerAsync player) {
            player.kill();
        }
    }

    PlayerAsync lover1();

    PlayerAsync lover2();

    void grantRole();

    void informAboutFallingInLove();

    void deaths();


}
