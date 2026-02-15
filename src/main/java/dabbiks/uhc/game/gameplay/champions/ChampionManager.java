package dabbiks.uhc.game.gameplay.champions;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ChampionManager {

    private final Map<String, Champion> champions = new LinkedHashMap<>();

    public ChampionManager() {
        register(new Miner());
        register(new Default());
    }

    private void register(Champion champion) {
        champions.put(champion.getId(), champion);
    }

    public List<Champion> getChampions() {
        return new ArrayList<>(champions.values());
    }

    public Champion getChampion(String id) {
        return champions.get(id);
    }
}