package dabbiks.uhc;

import dabbiks.uhc.player.data.persistent.PersistentDataJson;
import dabbiks.uhc.utils.*;
import dabbiks.uhc.utils.managers.AttributeManager;
import dabbiks.uhc.utils.managers.IndicatorManager;
import dabbiks.uhc.utils.managers.TabManager;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    public static Plugin plugin;
    public static Main INSTANCE;

    public static MessageUtils messageU;
    public static PlayerUtils playerU;
    public static SoundUtils soundU;
    public static StateUtils stateU;
    public static SymbolUtils symbolU;
    public static TimeUtils timeU;
    public static TitleUtils titleU;
    public static ParryUtils parryU;
    public static PlayerListUtils playerListU;

    public static TabManager tabManager;
    public static AttributeManager attributeManager;
    public static IndicatorManager indicatorManager;

    public static PersistentDataJson persistentDataJson;

    @Override
    public void onEnable() {
        // Plugin startup logic

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
