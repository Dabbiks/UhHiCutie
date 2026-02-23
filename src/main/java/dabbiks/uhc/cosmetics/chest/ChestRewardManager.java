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
        addCoins(p, 100, 40, 1.25, 0.0625, 0.001);
        addPowder(p, 80, 30, 1.0, 0.05, 0.001);
        addSwords(p, 2.0, 0.5, 0.025, 0.00125, 0.0001);
        addKillSounds(p, 1.5, 0.4, 0.02, 0.00062, 0.0001);
        addTrails(p, 1.0, 0.3, 0.0125, 0.00025, 0.0001);
        addKeys(p, 5, 1, 0.05, 0.00625, 0.001);
        addChampions(p, 0.5, 0.2);
        addChests(p, 0.5, 0.1, 0.0125, 0.00125, 0.0001);
    }

    private static void setupRare(RewardPool p) {
        addCoins(p, 40, 100, 5.0, 0.25, 0.01);
        addPowder(p, 30, 80, 3.75, 0.187, 0.005);
        addSwords(p, 1.66, 5.0, 0.208, 0.01, 0.0008);
        addKillSounds(p, 1.33, 4.16, 0.166, 0.004, 0.0003);
        addTrails(p, 0.83, 3.33, 0.125, 0.002, 0.0001);
        addKeys(p, 2, 8, 0.25, 0.0125, 0.002);
        addChampions(p, 1.5, 0.8);
        addChests(p, 2.0, 0.5, 0.025, 0.0025, 0.0005);
    }

    private static void setupEpic(RewardPool p) {
        addCoins(p, 15, 40, 25.0, 1.875, 0.1);
        addPowder(p, 10, 30, 20.0, 1.25, 0.08);
        addSwords(p, 1.25, 3.75, 10.0, 0.156, 0.0125);
        addKillSounds(p, 1.0, 3.0, 8.75, 0.093, 0.005);
        addTrails(p, 0.75, 2.5, 7.5, 0.062, 0.0025);
        addKeys(p, 1, 4, 2.5, 0.125, 0.02);
        addChampions(p, 5.0, 3.0);
        addChests(p, 5.0, 2.0, 0.25, 0.0125, 0.002);
    }

    private static void setupMythic(RewardPool p) {
        addCoins(p, 5, 15, 10.0, 12.5, 1.5);
        addPowder(p, 4, 10, 7.5, 10.0, 1.2);
        addSwords(p, 0.5, 2.0, 5.0, 1.56, 0.2);
        addKillSounds(p, 0.25, 1.5, 3.75, 1.25, 0.125);
        addTrails(p, 0.25, 1.25, 3.0, 1.09, 0.1);
        addKeys(p, 0.5, 2, 1.25, 1.875, 0.3);
        addChampions(p, 10.0, 8.0);
        addChests(p, 8.0, 5.0, 0.75, 0.187, 0.05);
    }

    private static void setupLegendary(RewardPool p) {
        addCoins(p, 1, 5, 3.75, 6.25, 12.0);
        addPowder(p, 1, 4, 3.12, 5.0, 10.0);
        addSwords(p, 0.125, 0.5, 2.5, 0.93, 2.0);
        addKillSounds(p, 0.05, 0.25, 2.0, 0.78, 1.5);
        addTrails(p, 0.025, 0.2, 1.5, 0.62, 1.25);
        addKeys(p, 0.1, 0.5, 0.5, 1.25, 2.5);
        addChampions(p, 15.0, 12.0);
        addChests(p, 10.0, 8.0, 1.5, 0.5, 0.2);
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

    private static void addKillSounds(RewardPool p, double w1, double w2, double w3, double w4, double w5) {
        p.addReward(() -> new KillSoundReward(CosmeticTier.COMMON), w1);
        p.addReward(() -> new KillSoundReward(CosmeticTier.RARE), w2);
        p.addReward(() -> new KillSoundReward(CosmeticTier.EPIC), w3);
        p.addReward(() -> new KillSoundReward(CosmeticTier.MYTHIC), w4);
        p.addReward(() -> new KillSoundReward(CosmeticTier.LEGENDARY), w5);
    }

    private static void addTrails(RewardPool p, double w1, double w2, double w3, double w4, double w5) {
        p.addReward(() -> new TrailReward(CosmeticTier.COMMON), w1);
        p.addReward(() -> new TrailReward(CosmeticTier.RARE), w2);
        p.addReward(() -> new TrailReward(CosmeticTier.EPIC), w3);
        p.addReward(() -> new TrailReward(CosmeticTier.MYTHIC), w4);
        p.addReward(() -> new TrailReward(CosmeticTier.LEGENDARY), w5);
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