package dabbiks.uhc.tasks.tasks;

import dabbiks.uhc.game.gameplay.items.data.attributes.AttributeType;
import dabbiks.uhc.player.data.session.SessionData;
import dabbiks.uhc.player.data.session.SessionDataManager;
import dabbiks.uhc.player.data.session.SessionTags;
import dabbiks.uhc.tasks.Task;
import org.bukkit.entity.Player;

import static dabbiks.uhc.Main.*;

public class RegenerationTask extends Task {

    protected long getPeriod() {
        return 20;
    }

    protected void tick() {
        if (timeU.getTime() % 15 != 0) return;
        for (Player player : playerListU.getPlayingPlayers()) {
            double regen = 1.0 + attributeManager.getAttributeValue(player, AttributeType.REGENERATION, 1);

            SessionData sessionData = SessionDataManager.getData(player.getUniqueId());
            if (sessionData != null && sessionData.hasTag(SessionTags.ADDITIONAL_REGENERATION)) {
                regen += 1.0;
            }

            playerU.addHealth(player, regen);
        }
    }
}