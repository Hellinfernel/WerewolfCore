package io.github.hellinfernal.werewolf.core.player;

import io.github.hellinfernal.werewolf.core.role.SpecialRole;

import java.util.List;

public interface PlayersInLove {
    final class InLove implements PlayersInLove {

        private final Player _lover1;
        private final Player _lover2;
        private final List<Player> _lovers;

        public InLove(final Player lover1, final Player lover2) {
            _lover1 = lover1;
            _lover2 = lover2;
            _lovers = List.of(_lover1, _lover2);
        }

        @Override
        public Player lover1() {
            return _lover1;
        }

        @Override
        public Player lover2() {
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

        void deathTrigger(Player player) {
            player.kill();
        }
    }

    Player lover1();

    Player lover2();

    void grantRole();

    void informAboutFallingInLove();

    void deaths();


}
