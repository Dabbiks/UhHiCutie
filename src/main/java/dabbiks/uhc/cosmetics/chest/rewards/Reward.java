package dabbiks.uhc.cosmetics.chest.rewards;

import dabbiks.uhc.player.data.persistent.PersistentData;

public abstract class Reward {

    public RewardType getType(RewardType rewardType) {
        return rewardType;
    }

    public abstract void addReward(PersistentData persistentData);

    public abstract String getName();
}
