package com.github.aoideveloper.vertexQuest.event;

import com.github.aoideveloper.vertexQuest.model.node.QuestNode;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class QuestCompleteEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    public final Player player;
    public final QuestNode quest;

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public QuestCompleteEvent(Player player, QuestNode quest) {
        this.player = player;
        this.quest = quest;
    }
}
