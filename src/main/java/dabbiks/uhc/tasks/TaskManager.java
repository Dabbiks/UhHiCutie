package dabbiks.uhc.tasks;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

import static dabbiks.uhc.Main.plugin;

public class TaskManager extends BukkitRunnable {

    private final List<Task> executors;

    public TaskManager() {
        executors = new ArrayList<>();

        //executors.add(new GameStart());


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
