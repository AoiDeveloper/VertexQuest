package com.github.aoideveloper.vertexQuest.model.node;

import java.util.Objects;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

/**
 * すべてのクエストタスクの基盤となる抽象クラス.
 * 各ノードは、有効化(activate)された際に自身をイベントリスナーとして登録し,
 * 完了条件を監視する責務を持つ.
 */
public abstract class QuestNode implements Listener {
    private final String nodeId;
    private boolean isActive;

    /**
     * 新しいQuestNodeを作成します.
     *
     * @param nodeId ノードのユニークなID
     */
    public QuestNode(String nodeId) {
        this.nodeId = nodeId;
        this.isActive = false;
    }

    public String getNodeId() {
        return nodeId;
    }

    public boolean isActive() {
        return isActive;
    }

    /**
     * このノードを特定のプレイヤーのために有効化します.
     * QuestManagerによって呼び出されます.
     *
     * @param player 対象とするプレイヤー
     */
    public void activate(Player player) {
        if (isActive) {
            return;
        }
        onActivate(player);
        isActive = true;
    }

    /**
     * このノードを特定のプレイヤーのために無効化します.
     * QuestManagerによって呼び出されます.
     *
     * @param player 対象とするプレイヤー
     */
    public void deactivate(Player player) {
        if (!isActive) {
            return;
        }
        onDeactivate(player);
        isActive = false;
    }

    /**
     * このノードが有効化された際に、サブクラスが独自の処理を実行するためのメソッド.
     *
     * @param player 対象プレイヤー
     */
    public abstract void onActivate(Player player);

    /**
     * このノードが有効化された際に、サブクラスが独自の処理を実行するためのメソッド.
     *
     * @param player 対象プレイヤー
     */
    public abstract void onDeactivate(Player player);

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        QuestNode questNode = (QuestNode) o;
        return nodeId.equals(questNode.nodeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodeId);
    }
}
