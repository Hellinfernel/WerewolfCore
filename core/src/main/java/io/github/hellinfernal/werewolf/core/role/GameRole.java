package io.github.hellinfernal.werewolf.core.role;

public enum GameRole {
    Werewolf(0.25d),
    Villager(0.75d),
    ;

    private final double _percentageAmount;

    GameRole(final double percentageAmount) {
        _percentageAmount = percentageAmount;
    }

    public double getPercentageAmount() {
        return _percentageAmount;
    }

    public boolean isWerewolf() {
        return this == Werewolf;
    }

    public boolean isVillager() {
        return this == Villager;
    }

    public long getAmount(final int playerSize) {
        return Math.round(_percentageAmount * ((double) playerSize));
    }
}
