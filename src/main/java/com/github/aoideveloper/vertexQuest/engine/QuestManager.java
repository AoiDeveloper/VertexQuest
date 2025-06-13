package com.github.aoideveloper.vertexQuest.engine;

import com.github.aoideveloper.vertexQuest.VertexQuest;
import com.github.aoideveloper.vertexQuest.event.QuestCompleteEvent;
import com.github.aoideveloper.vertexQuest.model.QuestGraph;
import com.github.aoideveloper.vertexQuest.model.node.QuestNode;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QuestManager {

    private final Player player;
    private final QuestGraph graph;

    private final List<QuestNode> activeNodes;
    private final List<QuestNode> completeNodes;

    /**
     * クエストの進行度を管理し、依存関係を処理します.
     *
     * @param player 対象のプレイヤー
     * @param graph クエストの構造を表すグラフ
     */
    public QuestManager(Player player, QuestGraph graph) {
        this.player = player;
        this.graph = graph;

        this.activeNodes = new ArrayList<>();
        this.completeNodes = new ArrayList<>();
    }

    public void activateScenario() {
        this.activeNodes.clear();
        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onQuestComplete(QuestCompleteEvent event) {
                if (!activeNodes.contains(event.quest)) {
                    return;
                }
                activeNodes.remove(event.quest);
                completeNodes.add(event.quest);
                // クエストが完了したとき、開始しなければならない可能性のあるクエストIDのリスト
                var dependentQuests = graph.dependencies.entrySet().stream()
                        .filter(entry -> entry.getValue().contains(event.quest.getNodeId()))
                        .map(Map.Entry::getKey)
                        .toList();
                for (var quest : dependentQuests) {
                    final var fullFillCondition = graph.dependencies.get(quest)
                            .stream()
                            .allMatch(node -> completeNodes.contains(graph.nodes.get(node)));
                    if (fullFillCondition) {
                        activeNodes.add(graph.nodes.get(quest));
                    }
                }
            }
        }, VertexQuest.plugin);
    }
}
