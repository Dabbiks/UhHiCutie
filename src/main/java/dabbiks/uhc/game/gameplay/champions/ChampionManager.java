package dabbiks.uhc.game.gameplay.champions;

import dabbiks.uhc.game.gameplay.champions.alchemist.Alchemist;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ChampionManager {

    private final Map<String, Champion> champions = new LinkedHashMap<>();

    public ChampionManager() {
        register(new Default());
        register(new Miner());
        register(new Archer());
        register(new Pikeman());
        register(new Fisherman());
        register(new Hunter());
        register(new Alchemist());
        register(new Geologist());
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