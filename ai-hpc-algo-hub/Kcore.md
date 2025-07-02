# Kcore算法学习笔记

## 1. 基本概念

**K-core（K核）** 是图论中的一个重要概念。对于一个无向图或有向图，K-core是指这样一个最大子图：其中每个节点的度（无向图为节点的边数，有向图可分为入度和出度）都大于等于K。

- K-core可以帮助我们发现图中"紧密连接"的核心结构。
- K值越大，K-core越小，代表越核心、越稠密的子结构。
- K-core分解（K-core decomposition）是将图分层，得到不同K值下的核心子图。

**相关术语：**
- K-shell：所有核数等于K的节点集合。
- 核数（coreness）：每个节点属于的最大K值。

---

## 2. 算法原理与流程

K-core算法的核心思想是**迭代剪枝**：不断移除度小于K的节点，直到所有剩余节点的度都大于等于K。

### 算法流程（以无向图为例）：
1. 计算每个节点的度。
2. 找出所有度小于K的节点，将其从图中移除。
3. 移除后，更新剩余节点的度。
4. 重复步骤2-3，直到没有度小于K的节点。
5. 剩下的子图即为K-core。

**注意：**每一轮剪枝后，原本度大于等于K的节点可能因邻居被删而度数下降，所以需要多轮迭代。

---

## 3. 代码实现

### Python实现示例

```
import networkx as nx

def k_core_subgraph(G, k):
    # G为无向图，k为核数
    G = G.copy()
    while True:
        remove = [node for node, degree in dict(G.degree()).items() if degree < k]
        if not remove:
            break
        G.remove_nodes_from(remove)
    return G

# 示例
G = nx.Graph()
G.add_edges_from([(0,1),(1,2),(2,3),(3,4),(4,0),(1,3),(2,4)])
core = k_core_subgraph(G, 3)
print(core.nodes())  # 输出K-core的节点
```

### NetworkX内置方法

```
import networkx as nx
G = nx.karate_club_graph()
k = 3
core = nx.k_core(G, k)
print(core.nodes())
```

---

## 4. 应用场景
- **社交网络分析**：发现核心社群、影响力人物。
- **金融风控**：识别高风险团伙、洗钱网络。
- **生物信息学**：分析蛋白质相互作用网络中的核心模块。
- **互联网结构分析**：识别互联网骨干节点。
- **推荐系统**：筛选高活跃用户群体。

---

## 5. 进阶与工程实践

- K-core分解可用于节点重要性排序（核数越大越重要）。
- 大规模图可用分布式/并行K-core算法（如Spark GraphX、TuGraph等）。
- 有向图K-core可分为入核、出核。
- K-core分解结果常用于可视化，展示网络分层结构。

---

## 6. 学习建议与参考资料

### 学习建议
- 建议先理解图的基本概念（节点、边、度、子图等）。
- 用小图手动画出K-core分解过程，帮助理解。
- 多用NetworkX等工具实际操作。
- 结合实际应用场景思考K-core的意义。

### 参考资料
- [K-core算法原理与应用（CSDN）](https://blog.csdn.net/TuGraph/article/details/132676159)
- [K-core decomposition（Wikipedia, 英文）](https://en.wikipedia.org/wiki/Degeneracy_(graph_theory)#k-core)
- [NetworkX官方文档：k_core](https://networkx.org/documentation/stable/reference/generated/networkx.algorithms.core.k_core.html)
- [复杂网络中的K核分解（知乎）](https://zhuanlan.zhihu.com/p/34907316)
- [K-core算法原理与代码实现（简书）](https://www.jianshu.com/p/2e7e6e2e7e6e)

---
