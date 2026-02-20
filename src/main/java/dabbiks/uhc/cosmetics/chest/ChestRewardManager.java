package dabbiks.uhc.cosmetics.chest;

import dabbiks.uhc.cosmetics.chest.rewards.Reward;
import dabbiks.uhc.cosmetics.chest.rewards.RewardPool;
import dabbiks.uhc.cosmetics.chest.rewards.coins.MediumCoinReward;
import dabbiks.uhc.cosmetics.chest.rewards.coins.MiniCoinReward;
import dabbiks.uhc.cosmetics.chest.rewards.coins.SmallCoinReward;

import java.util.EnumMap;
import java.util.Map;

public class ChestRewardManager {
    private static final Map<ChestType, RewardPool> rewardPools = new EnumMap<>(ChestType.class);

    static {
        loadPools();
    }

    public static void loadPools() {
        rewardPools.clear();

        RewardPool commonPool = new RewardPool();
        commonPool.addReward(new MiniCoinReward(), 70.0);
        commonPool.addReward(new SmallCoinReward(), 30.0);
        rewardPools.put(ChestType.COMMON, commonPool);

        RewardPool rarePool = new RewardPool();
        rarePool.addReward(new MiniCoinReward(), 40.0);
        rarePool.addReward(new SmallCoinReward(), 50.0);
        rarePool.addReward(new MediumCoinReward(), 10.0);
        rewardPools.put(ChestType.RARE, rarePool);

        RewardPool epicPool = new RewardPool();
        epicPool.addReward(new MiniCoinReward(), 10.0);
        epicPool.addReward(new SmallCoinReward(), 60.0);
        epicPool.addReward(new MediumCoinReward(), 30.0);
        rewardPools.put(ChestType.EPIC, epicPool);

        RewardPool mythicPool = new RewardPool();
        mythicPool.addReward(new SmallCoinReward(), 40.0);
        mythicPool.addReward(new MediumCoinReward(), 60.0);
        rewardPools.put(ChestType.MYTHIC, mythicPool);

        RewardPool legendaryPool = new RewardPool();
        legendaryPool.addReward(new SmallCoinReward(), 10.0);
        legendaryPool.addReward(new MediumCoinReward(), 90.0);
        rewardPools.put(ChestType.LEGENDARY, legendaryPool);
    }

    public static Reward drawReward(ChestType type) {
        RewardPool pool = rewardPools.get(type);
        return pool != null ? pool.draw() : null;
    }
}