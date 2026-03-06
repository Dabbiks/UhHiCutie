package dabbiks.uhc.tasks.tasks;

import dabbiks.uhc.lobby.LobbyTopManager;
import dabbiks.uhc.tasks.Task;

public class LobbyTopTask extends Task {

    private int currentIndex = 0;

    public LobbyTopTask() {
        LobbyTopManager.loadTops();
        LobbyTopManager.displayTop(LobbyTopManager.TopCategory.values()[currentIndex]);
    }

    @Override
    protected long getPeriod() {
        return 200;
    }

    @Override
    protected void tick() {
        currentIndex++;
        if (currentIndex >= LobbyTopManager.TopCategory.values().length) {
            currentIndex = 0;
            LobbyTopManager.loadTops();
        }
        LobbyTopManager.displayTop(LobbyTopManager.TopCategory.values()[currentIndex]);
    }
}