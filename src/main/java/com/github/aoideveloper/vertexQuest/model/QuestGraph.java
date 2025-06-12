package com.github.aoideveloper.vertexQuest.model;

import com.github.aoideveloper.vertexQuest.model.node.QuestNode;
import java.util.*;
import java.util.stream.Stream;
import javax.annotation.Nonnull;

/**
 * 一連のクエストラインの構造全体を定義するクラス.
 * 内部的にQuestNode同士の関係性をDAGで管理する.
 */
public class QuestGraph {
    /**
     * グラフをユニークに識別するためのID.
     * QuestManager中で一意である必要がある.
     */
    private final String graphId;

    /**
     * このグラフに属するすべてのノードを,ノードIDをキーとして格納します.
     */
    private final Map<String, QuestNode> nodes;

    /**
     * あるノードがどのノードに依存しているかを格納します.
     * Key: 子ノードのID
     * Value: その子ノードが依存する、親ノードのIDのリスト
     */
    private final Map<String, List<String>> dependencies;

    /**
     * 依存関係を持たない、クエストラインの開始点となるノードのリスト.
     */
    private final List<QuestNode> rootNodes;

    /**
     * 新しいクエストグラフを構築する.
     *
     * @param builder 構築元のビルダー
     */
    private QuestGraph(@Nonnull Builder builder) {
        this.graphId = Objects.requireNonNull(builder.graphId);
        this.nodes = builder.nodes;
        this.dependencies = builder.dependencies;
        this.rootNodes = builder.rootNodes;
    }

    /**
     * 新しいクエストグラフを構築するためのビルダークラス.
     */
    public static class Builder {
        private final String graphId;
        private final Map<String, QuestNode> nodes;
        private final Map<String, List<String>> dependencies;
        private final List<QuestNode> rootNodes;

        /**
         * QuestGraphを構築するためのビルダークラス.
         *
         * @param graphId 構築するグラフのID
         */
        public Builder(@Nonnull String graphId) {
            this.graphId = Objects.requireNonNull(graphId, "Graph ID must not be null");
            this.nodes = new HashMap<>();
            this.dependencies = new HashMap<>();
            this.rootNodes = new ArrayList<>();
        }

        /**
         * グラフに新しいクエストノードを追加します.
         * この際に、グラフに循環参照が発生しないかを検証します.
         *
         * @param node 追加する新しいノード
         * @param parents このノードが依存する親ノード
         * @throws IllegalArgumentException ノードIDの重複や、循環参照が発生する場合
         */
        public Builder addNode(@Nonnull QuestNode node, @Nonnull QuestNode... parents) {
            Objects.requireNonNull(node, "node must not be null");
            Objects.requireNonNull(parents, "parents array must not be null");

            if (nodes.containsKey(node.getNodeId())) {
                throw new IllegalArgumentException(
                        String.format("Node with ID '%s' already exists in this graph(GraphID: %s)", node.getNodeId(), graphId)
                );
            }

            // 親ノードに自分自身が含まれている場合、IllegalArgumentExceptionを発行する。
            if (Stream.of(parents).anyMatch(parent -> node == parent)) {
                throw new IllegalArgumentException(
                        String.format("Parent must not be a child(NodeID: %s)", node.getNodeId())
                );
            }

            // 循環参照チェック
            for (QuestNode parent : parents) {
                if (isReachable(parent, node)) {
                    throw new IllegalArgumentException(
                            String.format("Circular dependency detected: Adding '%s' as a parent of '%s' creates a loop.",
                                    parent.getNodeId(), node.getNodeId())
                    );
                }
            }

            nodes.put(node.getNodeId(), node);

            // 依存関係の情報を追加する。
            List<QuestNode> parentList = Arrays.asList(parents);
            if (parentList.isEmpty()) {
                rootNodes.add(node);
            } else {
                List<String> parentIds = parentList.stream()
                        .map(QuestNode::getNodeId)
                        .toList();
                dependencies.put(node.getNodeId(), parentIds);
            }

            // メソッドチェーンのためにthisを返す
            return this;
        }

        /**
         * 構築された情報から、不変なQuestGraphインスタンスを生成する.
         *
         * @return 新しいQuestGraphインスタンス
         */
        public QuestGraph build() {
            // TODO: 整合性チェックの追加
            return new QuestGraph(this);
        }

        private boolean isReachable(QuestNode startNode, QuestNode targetNode) {
            // 自分自身にはいつでも到達可能であるため。
            if (startNode == targetNode) {
                return true;
            }
            List<String> parentIds = dependencies.getOrDefault(startNode.getNodeId(), Collections.emptyList());
            for (String parentId : parentIds) {
                QuestNode parentNode = nodes.get(parentId);
                if (parentNode != null && isReachable(parentNode, targetNode)) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * {@code node}が{@code potentialAncestor}の祖先であるかを再帰的にチェックします.
     *
     * @param node 判定元のノード
     * @param potentialAncestor 祖先であるか調べる対象のノード
     * @return 祖先である場合 {@code True} ,そうでない場合 {@code False}
     */
    private boolean isAncestor(@Nonnull QuestNode node, @Nonnull QuestNode potentialAncestor) {
        List<String> parentIds = dependencies.getOrDefault(node.getNodeId(), Collections.emptyList());
        if (parentIds.contains(potentialAncestor.getNodeId())) {
            return true;
        }
        for (String parentId : parentIds) {
            QuestNode parentNode = nodes.get(parentId);
            if (parentNode != null && isAncestor(potentialAncestor, parentNode)) {
                return true;
            }
        }
        return false;
    }
}
