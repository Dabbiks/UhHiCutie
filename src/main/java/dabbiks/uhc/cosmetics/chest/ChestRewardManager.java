package dabbiks.uhc.cosmetics.chest;

import dabbiks.uhc.cosmetics.CosmeticTier;
import dabbiks.uhc.cosmetics.chest.rewards.Reward;
import dabbiks.uhc.cosmetics.chest.rewards.RewardPool;
import dabbiks.uhc.cosmetics.chest.rewards.loot.*;

import java.util.EnumMap;
import java.util.Map;

public class ChestRewardManager {
    private static final Map<ChestType, RewardPool> rewardPools = new EnumMap<>(ChestType.class);

    static {
        loadPools();
    }

    public static void loadPools() {
        rewardPools.clear();
        for (ChestType type : ChestType.values()) {
            RewardPool pool = new RewardPool();
            switch (type) {
                case COMMON -> setupCommon(pool);
                case RARE -> setupRare(pool);
                case EPIC -> setupEpic(pool);
                case MYTHIC -> setupMythic(pool);
                case LEGENDARY -> setupLegendary(pool);
            }
            rewardPools.put(type, pool);
        }
    }

    private static void setupCommon(RewardPool p) {
        addCoins(p, 100, 40, 3, 0.1, 0.005);
        addPowder(p, 80, 30, 3, 0.1, 0.005);
        addSwords(p, 3.0, 0.5, 0.025, 0.01, 0.001);
        addCages(p, 3.0, 0.5, 0.025, 0.01, 0.001);
        addKillSounds(p, 1.5, 0.5, 0.02, 0.005, 0.001);
        addKeys(p, 10, 3, 1, 1, 1);
        addChampions(p, 0.3, 2.3);
        addChests(p, 10, 3, 1, 1, 1);
    }

    private static void setupRare(RewardPool p) {
        addCoins(p, 65, 85, 12, 1, 0.05);
        addPowder(p, 60, 70, 8, 0.5, 0.05);
        addSwords(p, 4.0, 1.5, 0.125, 0.01, 0.005);
        addCages(p, 4.0, 1.5, 0.125, 0.01, 0.005);
        addKillSounds(p, 2.5, 1.5, 0.1, 0.05, 0.005);
        addKeys(p, 8, 12, 3, 1, 1);
        addChampions(p, 0.6, 3.6);
        addChests(p, 8, 12, 3, 1, 1);
    }

    private static void setupEpic(RewardPool p) {
        addCoins(p, 60, 90, 12, 1.5, 0.1);
        addPowder(p, 50, 80, 15, 2, 0.1);
        addSwords(p, 4.5, 2.5, 0.5, 0.05, 0.01);
        addCages(p, 4.5, 2.5, 0.5, 0.05, 0.01);
        addKillSounds(p, 3.0, 2.0, 0.4, 0.08, 0.01);
        addKeys(p, 6, 12, 6, 2, 1);
        addChampions(p, 1.5, 3.5);
        addChests(p, 6, 12, 6, 2, 1);
    }

    private static void setupMythic(RewardPool p) {
        addCoins(p, 70, 75, 12, 2.5, 0.2);
        addPowder(p, 60, 65, 15, 3, 0.2);
        addSwords(p, 4.0, 3.5, 1.2, 0.25, 0.05);
        addCages(p, 4.0, 3.5, 1.2, 0.25, 0.05);
        addKillSounds(p, 2.5, 3.0, 1.0, 0.2, 0.05);
        addKeys(p, 5, 10, 12, 4, 2);
        addChampions(p, 3.0, 5.0);
        addChests(p, 5, 10, 12, 4, 2);
    }

    private static void setupLegendary(RewardPool p) {
        addCoins(p, 70, 70, 20, 5.5, 0.5);
        addPowder(p, 60, 60, 25, 6, 0.5);
        addSwords(p, 3.5, 4.5, 2.5, 0.75, 0.25);
        addCages(p, 3.5, 4.5, 2.5, 0.75, 0.25);
        addKillSounds(p, 2.0, 3.5, 2.2, 0.6, 0.2);
        addKeys(p, 4, 8, 15, 10, 5);
        addChampions(p, 5.0, 8.0);
        addChests(p, 4, 8, 15, 10, 5);
    }

    private static void addCoins(RewardPool p, double w1, double w2, double w3, double w4, double w5) {
        p.addReward(() -> new CoinReward(CoinReward.Tier.MINI), w1);
        p.addReward(() -> new CoinReward(CoinReward.Tier.SMALL), w2);
        p.addReward(() -> new CoinReward(CoinReward.Tier.MEDIUM), w3);
        p.addReward(() -> new CoinReward(CoinReward.Tier.BIG), w4);
        p.addReward(() -> new CoinReward(CoinReward.Tier.HUGE), w5);
    }

    private static void addPowder(RewardPool p, double w1, double w2, double w3, double w4, double w5) {
        p.addReward(() -> new PowderReward(PowderReward.Tier.MINI), w1);
        p.addReward(() -> new PowderReward(PowderReward.Tier.SMALL), w2);
        p.addReward(() -> new PowderReward(PowderReward.Tier.MEDIUM), w3);
        p.addReward(() -> new PowderReward(PowderReward.Tier.BIG), w4);
        p.addReward(() -> new PowderReward(PowderReward.Tier.HUGE), w5);
    }

    private static void addSwords(RewardPool p, double w1, double w2, double w3, double w4, double w5) {
        p.addReward(() -> new PvpSwordReward(CosmeticTier.COMMON), w1);
        p.addReward(() -> new PvpSwordReward(CosmeticTier.RARE), w2);
        p.addReward(() -> new PvpSwordReward(CosmeticTier.EPIC), w3);
        p.addReward(() -> new PvpSwordReward(CosmeticTier.MYTHIC), w4);
        p.addReward(() -> new PvpSwordReward(CosmeticTier.LEGENDARY), w5);
    }

    private static void addCages(RewardPool p, double w1, double w2, double w3, double w4, double w5) {
        p.addReward(() -> new CageReward(CosmeticTier.COMMON), w1);
        p.addReward(() -> new CageReward(CosmeticTier.RARE), w2);
        p.addReward(() -> new CageReward(CosmeticTier.EPIC), w3);
        p.addReward(() -> new CageReward(CosmeticTier.MYTHIC), w4);
        p.addReward(() -> new CageReward(CosmeticTier.LEGENDARY), w5);
    }

    private static void addKillSounds(RewardPool p, double w1, double w2, double w3, double w4, double w5) {
        p.addReward(() -> new KillSoundReward(CosmeticTier.COMMON), w1);
        p.addReward(() -> new KillSoundReward(CosmeticTier.RARE), w2);
        p.addReward(() -> new KillSoundReward(CosmeticTier.EPIC), w3);
        p.addReward(() -> new KillSoundReward(CosmeticTier.MYTHIC), w4);
        p.addReward(() -> new KillSoundReward(CosmeticTier.LEGENDARY), w5);
    }

    private static void addKeys(RewardPool p, double w1, double w2, double w3, double w4, double w5) {
        p.addReward(() -> new KeyReward(KeyType.COMMON), w1);
        p.addReward(() -> new KeyReward(KeyType.RARE), w2);
        p.addReward(() -> new KeyReward(KeyType.EPIC), w3);
        p.addReward(() -> new KeyReward(KeyType.MYTHIC), w4);
        p.addReward(() -> new KeyReward(KeyType.LEGENDARY), w5);
    }

    private static void addChampions(RewardPool p, double wUnlock, double wUpgrade) {
        p.addReward(ChampionReward::new, wUnlock);
        p.addReward(ChampionUpgradeReward::new, wUpgrade);
    }

    private static void addChests(RewardPool p, double w1, double w2, double w3, double w4, double w5) {
        p.addReward(() -> new ChestReward(ChestType.COMMON), w1);
        p.addReward(() -> new ChestReward(ChestType.RARE), w2);
        p.addReward(() -> new ChestReward(ChestType.EPIC), w3);
        p.addReward(() -> new ChestReward(ChestType.MYTHIC), w4);
        p.addReward(() -> new ChestReward(ChestType.LEGENDARY), w5);
    }

    public static Reward drawReward(ChestType type) {
        RewardPool pool = rewardPools.get(type);
        return pool != null ? pool.draw() : null;
    }
}