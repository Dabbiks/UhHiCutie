package dabbiks.uhc.tasks;

import dabbiks.uhc.tasks.tasks.*;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static dabbiks.uhc.Main.plugin;

public class TaskManager extends BukkitRunnable {

    private static final List<Task> executors = new CopyOnWriteArrayList<>();

    public TaskManager() {
        executors.add(new StartTask());
        executors.add(new BorderTask());
        executors.add(new TimeTask());
        executors.add(new ScoreboardTask());
        executors.add(new SegmentTask());
        executors.add(new RegenerationTask());
        executors.add(new BossBarTask());
        executors.add(new LobbyTopTask());
        executors.add(new PlaytimeCoinsTask());
        executors.add(new LobbyTeleportTask());
        executors.add(new PvpSwordTask());
        executors.add(new DaylightTask());
        executors.add(new HeartbeatTask());
        executors.add(new GammaTask());

        runTaskTimer(plugin, 0, 1);
    }

    public static void addTask(Task task) {
        executors.add(task);
    }

    public static void removeTask(Task task) {
        executors.remove(task);
    }

    @Override
    public void run() {
        for (Task task : executors) {
            if (Bukkit.getCurrentTick() % task.getPeriod() != 0) continue;
            task.tick();
        }
    }
}