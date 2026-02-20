package dabbiks.uhc.cosmetics.chest;

import dabbiks.uhc.cosmetics.chest.rewards.Reward;
import dabbiks.uhc.cosmetics.chest.rewards.RewardPool;
import dabbiks.uhc.cosmetics.chest.rewards.coins.*;

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
        commonPool.addReward(MiniCoinReward::new, 70.0);
        commonPool.addReward(SmallCoinReward::new, 30.0);
        commonPool.addReward(MediumCoinReward::new, 5.0);
        commonPool.addReward(BigCoinReward::new, 0.2);
        rewardPools.put(ChestType.COMMON, commonPool);

        RewardPool rarePool = new RewardPool();
        rarePool.addReward(MiniCoinReward::new, 40.0);
        rarePool.addReward(SmallCoinReward::new, 50.0);
        rarePool.addReward(MediumCoinReward::new, 10.0);
        rarePool.addReward(BigCoinReward::new, 1.0);
        rewardPools.put(ChestType.RARE, rarePool);

        RewardPool epicPool = new RewardPool();
        epicPool.addReward(MiniCoinReward::new, 10.0);
        epicPool.addReward(SmallCoinReward::new, 60.0);
        epicPool.addReward(MediumCoinReward::new, 10.0);
        rarePool.addReward(BigCoinReward::new, 2.0);
        rarePool.addReward(HugeCoinReward::new, 0.1);
        rewardPools.put(ChestType.EPIC, epicPool);

        RewardPool mythicPool = new RewardPool();
        mythicPool.addReward(SmallCoinReward::new, 40.0);
        mythicPool.addReward(MediumCoinReward::new, 60.0);
        mythicPool.addReward(MiniCoinReward::new, 5.0);
        rarePool.addReward(BigCoinReward::new, 4.0);
        rarePool.addReward(HugeCoinReward::new, 0.5);
        rewardPools.put(ChestType.MYTHIC, mythicPool);

        RewardPool legendaryPool = new RewardPool();
        legendaryPool.addReward(SmallCoinReward::new, 10.0);
        legendaryPool.addReward(MediumCoinReward::new, 90.0);
        rarePool.addReward(BigCoinReward::new, 10.0);
        rarePool.addReward(HugeCoinReward::new, 5.0);
        rewardPools.put(ChestType.LEGENDARY, legendaryPool);
    }

    public static Reward drawReward(ChestType type) {
        RewardPool pool = rewardPools.get(type);
        return pool != null ? pool.draw() : null;
    }
}