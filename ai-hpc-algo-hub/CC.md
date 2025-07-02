
# CC（Connected Components，连通分量）算法学习笔记

## 1. 基本概念

**连通分量（Connected Components, CC）** 是图论中的一个基本概念。对于一个无向图，连通分量指的是极大连通子图，也就是图中任意两个顶点都可以通过路径连通，并且不能再添加其他顶点使其保持连通。

- **无向图**：每个连通分量是一个极大连通子图。
- **有向图**：通常讨论强连通分量（Strongly Connected Components, SCC），即任意两点互相可达。

**应用场景**：
- 社交网络分析（找社群）
- 关系网络中的团伙检测
- 图像分割
- 互联网爬虫中的站点聚类
- 计算机网络中的子网划分

---

## 2. 算法原理

### 2.1 基本思路

连通分量的核心任务是：给定一个图，找出所有的连通子图，并为每个节点分配一个"分量编号"或"标签"。

### 2.2 常见实现方法

#### 1. 深度优先搜索（DFS）/广度优先搜索（BFS）
- 遍历每个未访问节点，从该节点出发进行DFS/BFS，将所有可达节点标记为同一分量。
- 时间复杂度：O(N+M)，N为节点数，M为边数。

**伪代码：**

```
component_id = 0
for v in all_nodes:
    if not visited[v]:
        component_id += 1
        DFS(v, component_id)

def DFS(v, component_id):
    visited[v] = True
    label[v] = component_id
    for u in neighbors(v):
        if not visited[u]:
            DFS(u, component_id)
```

#### 2. 并查集（Union-Find）
- 适合动态合并、查询连通性。
- 先将每个节点初始化为独立集合，遍历每条边，合并两个端点。
- 最终每个集合代表一个连通分量。

**伪代码：**

```
# 初始化
for v in all_nodes:
    parent[v] = v

def find(v):
    if parent[v] != v:
        parent[v] = find(parent[v])
    return parent[v]

def union(u, v):
    parent[find(u)] = find(v)

for (u, v) in all_edges:
    union(u, v)

# 统计分量
components = {}
for v in all_nodes:
    root = find(v)
    components.setdefault(root, []).append(v)
```

#### 3. 标签传播法（Label Propagation）
- 适合大规模分布式图计算。
- 每个节点初始标签为自身ID，每轮迭代将标签更新为邻居中最小的标签，直到收敛。
- 典型应用：Spark GraphX、Pregel等大数据平台。

**伪代码：**

```
for v in all_nodes:
    label[v] = v

changed = True
while changed:
    changed = False
    for v in all_nodes:
        min_label = min([label[u] for u in neighbors(v)] + [label[v]])
        if min_label < label[v]:
            label[v] = min_label
            changed = True
```

---

## 3. 工程实践：Spark GraphX的connectedComponents实现

Spark GraphX的`connectedComponents`函数是大数据场景下的经典实现，采用了**标签传播法**思想，底层基于Pregel模型迭代。

**核心流程：**
1. 每个节点初始标签为自身ID。
2. 每轮迭代，每个节点将自己的标签与邻居标签比较，更新为最小值。
3. 迭代若干轮，直到所有节点标签不再变化（收敛）。

**代码示例（Scala，简化版）：**

```
%%scala
val cc = graph.connectedComponents()
cc.vertices.foreach(println)
```

输出：每个节点对应其所属连通分量的最小节点ID。

**底层原理简述：**
- 利用Pregel模型，节点间通过消息传递不断传播最小标签。
- 每轮迭代后，节点属性更新为收到的最小标签。
- 迭代次数可设定，默认足够大以保证收敛。

**工程应用举例：**
- 金融风控：识别团伙欺诈（同一分量内的用户高度关联）
- 社交网络：社群发现

---

## 4. 典型代码实现

### Python并查集实现

```
def connected_components(n, edges):
    parent = list(range(n))
    def find(x):
        if parent[x] != x:
            parent[x] = find(parent[x])
        return parent[x]
    def union(x, y):
        parent[find(x)] = find(y)
    for u, v in edges:
        union(u, v)
    # 统计分量
    from collections import defaultdict
    comps = defaultdict(list)
    for i in range(n):
        comps[find(i)].append(i)
    return list(comps.values())
```

### C++ DFS实现
```cpp
void dfs(int v, int cid, vector<vector<int>>& adj, vector<int>& comp) {
    comp[v] = cid;
    for (int u : adj[v]) {
        if (comp[u] == -1) dfs(u, cid, adj, comp);
    }
}

vector<vector<int>> connected_components(int n, vector<pair<int,int>>& edges) {
    vector<vector<int>> adj(n);
    for (auto& e : edges) {
        adj[e.first].push_back(e.second);
        adj[e.second].push_back(e.first);
    }
    vector<int> comp(n, -1);
    int cid = 0;
    for (int i = 0; i < n; ++i) {
        if (comp[i] == -1) dfs(i, cid++, adj, comp);
    }
    // 分组输出
    vector<vector<int>> res(cid);
    for (int i = 0; i < n; ++i) res[comp[i]].push_back(i);
    return res;
}
```

---

## 5. 学习建议与参考资料

### 学习建议
- 建议先理解图的基本概念（节点、边、邻接表/矩阵等）。
- 先用小规模手动画图，模拟DFS/BFS/并查集过程。
- 理解分布式标签传播思想，尝试用大数据平台（如Spark GraphX）跑一遍真实数据。
- 多刷LeetCode/牛客网相关题目（如"朋友圈"、"省份数量"等）。

### 参考资料
- [连通分量算法详解（知乎）](https://zhuanlan.zhihu.com/p/34907316)
- [Spark GraphX官方文档](https://spark.apache.org/docs/latest/graphx-programming-guide.html)
- [LeetCode 547. 省份数量](https://leetcode.cn/problems/number-of-provinces/)
- [HPC-Notes: 并行计算与图算法笔记](https://github.com/XiaoSong9905/HPC-Notes)
- [数据结构与算法：图与并查集](https://github.com/CCC1003/Algorithm)

---

