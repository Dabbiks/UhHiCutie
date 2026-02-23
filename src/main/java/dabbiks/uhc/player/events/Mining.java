package dabbiks.uhc.player.events;

import dabbiks.uhc.player.data.persistent.PersistentData;
import dabbiks.uhc.player.data.persistent.PersistentDataManager;
import dabbiks.uhc.player.data.session.SessionData;
import dabbiks.uhc.player.data.session.SessionDataManager;
import dabbiks.uhc.player.data.session.SessionTags;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class Mining implements Listener {

    Random random = new Random();

    @EventHandler
    public void onMine(BlockBreakEvent event) {
        Player player = event.getPlayer();
        SessionData sessionData = SessionDataManager.getData(player.getUniqueId());
        PersistentData persistentData = PersistentDataManager.getData(player.getUniqueId());
        Block block = event.getBlock();

        if (block.getType().toString().contains("_ORE")) {
            player.setExp(player.getExp() + 5);
        }

        if (sessionData.hasTag(SessionTags.MINER)) {
            assert persistentData != null;
            int level = persistentData.getChampionLevel("miner");
            double chance = 0.02 * level;

            if (random.nextDouble() > chance) return;
            for (ItemStack item : block.getDrops()) {
                block.getWorld().dropItemNaturally(block.getLocation(), item);
            }
        }
    }

}
