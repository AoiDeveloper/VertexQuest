package com.github.aoideveloper.vertexQuest.model.node;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import com.github.aoideveloper.vertexQuest.VertexQuest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDeathEvent;

public class KillEntityNode extends QuestNode {
    private final Function<Entity, Boolean> filter;
    private final int requiredAmount;
    private final Map<UUID, Integer> killCounts = new HashMap<>();

    /**
     * 新しいKillEntityNodeを作成します.
     *
     * @param nodeId ノードのユニークなID
     */
    public KillEntityNode(String nodeId, int requiredAmount, Function<Entity, Boolean> filter) {
        super(nodeId);

        this.requiredAmount = requiredAmount;
        this.filter = filter;
    }

    @Override
    public void onActivate(Player player) {
        VertexQuest.server.getPluginManager().registerEvents(this, VertexQuest.plugin);
    }

    @Override
    public void onDeactivate(Player player) {
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onDeathEntity(EntityDeathEvent event) {
        if (event.getEntity().getKiller() == null) {
            return;
        }
        if (!filter.apply(event.getEntity())) {
            return;
        }
        Player killer = event.getEntity().getKiller();
        // このタスクがこのプレイヤーにとってアクティブでない場合は無視する。
        if (!killCounts.containsKey(killer.getUniqueId())) {
            return;
        }

        int currentCount = killCounts.computeIfPresent(killer.getUniqueId(), (uuid, count) -> count + 1);
        killer.sendMessage("討伐した！");

        if (currentCount >= requiredAmount) {
            deactivate(killer);
        }
    }
}
