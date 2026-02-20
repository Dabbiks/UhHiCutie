package dabbiks.uhc.cosmetics.chest.rewards;

import java.util.LinkedHashMap;
import java.util.Map;

public class RewardPool {
    private final Map<Reward, Double> rewards = new LinkedHashMap<>();
    private double totalWeight = 0.0;

    public void addReward(Reward reward, double weight) {
        rewards.put(reward, weight);
        totalWeight += weight;
    }

    public Reward draw() {
        if (rewards.isEmpty()) return null;
        double value = Math.random() * totalWeight;
        for (Map.Entry<Reward, Double> entry : rewards.entrySet()) {
            value -= entry.getValue();
            if (value <= 0) {
                return entry.getKey();
            }
        }
        return null;
    }
}