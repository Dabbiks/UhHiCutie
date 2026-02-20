package dabbiks.uhc.cosmetics.chest.rewards;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class RewardPool {
    private final Map<Supplier<Reward>, Double> rewards = new LinkedHashMap<>();
    private double totalWeight = 0.0;

    public void addReward(Supplier<Reward> rewardSupplier, double weight) {
        rewards.put(rewardSupplier, weight);
        totalWeight += weight;
    }

    public Reward draw() {
        if (rewards.isEmpty()) return null;
        double value = Math.random() * totalWeight;
        for (Map.Entry<Supplier<Reward>, Double> entry : rewards.entrySet()) {
            value -= entry.getValue();
            if (value <= 0) {
                return entry.getKey().get();
            }
        }
        return null;
    }
}