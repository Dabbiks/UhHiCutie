package dabbiks.uhc.tasks;

import dabbiks.uhc.tasks.tasks.BorderTask;
import dabbiks.uhc.tasks.tasks.ScoreboardTask;
import dabbiks.uhc.tasks.tasks.StartTask;
import dabbiks.uhc.tasks.tasks.TimeTask;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

import static dabbiks.uhc.Main.plugin;

public class TaskManager extends BukkitRunnable {

    private final List<Task> executors;

    public TaskManager() {
        executors = new ArrayList<>();

        executors.add(new StartTask());
        executors.add(new BorderTask());
        executors.add(new TimeTask());
        executors.add(new ScoreboardTask());

        runTaskTimer(plugin, 0, 1);
    }

    @Override
    public void run() {
        for (Task task : executors) {
            if (Bukkit.getCurrentTick() % task.getPeriod() != 0) continue;
            task.tick();
        }
    }

}
