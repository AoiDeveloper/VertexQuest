package com.github.aoideveloper.vertexQuest.example;

import com.github.aoideveloper.vertexQuest.model.QuestGraph;
import com.github.aoideveloper.vertexQuest.model.node.KillEntityNode;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class ExampleQuest implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        var killFivePigsQuest = new KillEntityNode("kill5pigs", 5, entity -> entity.getType() == EntityType.PIG);
        var graph = new QuestGraph.Builder("example-quest").addNode(killFivePigsQuest).build();
        var player = event.getPlayer();

    }
}
