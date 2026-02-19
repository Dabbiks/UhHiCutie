package dabbiks.uhc.menu.cosmetics;

import dabbiks.uhc.player.data.persistent.PersistentData;
import fr.mrmicky.fastinv.FastInv;
import org.bukkit.entity.Player;

public class PvpSwordMenu extends FastInv {

    private final Player player;
    private final PersistentData persistentData;

    public PvpSwordMenu(Player player, PersistentData persistentData) {
        super(36, "Wikipedia");
        this.player = player;
        this.persistentData = persistentData;

        render();
    }

    private void render() {

        int[] slots = {
                10, 11, 12, 13, 14, 15, 16,
                19, 20, 21, 22, 23, 24, 25
        };


    }
}
