
# BFS（广度优先搜索）算法学习笔记

## 1. 基本概念

**广度优先搜索（Breadth-First Search, BFS）** 是图和树结构中最常用的遍历与搜索算法之一。BFS的核心思想是"由近及远，逐层推进"，即从起始节点出发，先访问所有距离起点最近的节点，再访问距离更远的节点，直到遍历完所有节点或找到目标。

- BFS常用于无权图的最短路径查找、层次遍历、连通性判定等。
- 适用于有向图、无向图、树、网格等多种结构。

**直观理解**：
- 类似于"水波扩散"或"迷宫寻路"，每一层扩展到下一层。
- 需要借助队列（Queue）数据结构实现"先进先出"的遍历顺序。

---
[1](#test)
## 2. 算法原理与流程

### 2.1 基本流程
1. 选择起始节点，将其加入队列，并标记为已访问。
2. 不断从队列头部取出节点，访问其所有未访问的邻居节点，并将这些邻居加入队列。
3. 重复上述过程，直到队列为空或找到目标节点。

### 2.2 伪代码<a id="test"></a>

```
from collections import deque

def bfs(graph, start):
    visited = set()
    queue = deque([start])
    while queue:
        node = queue.popleft()
        if node not in visited:
            visited.add(node)
            print(node)  # 处理节点
            for neighbor in graph[node]:
                if neighbor not in visited:
                    queue.append(neighbor)
```

### 2.3 C++实现示例
```cpp
#include <iostream>
#include <vector>
#include <queue>
using namespace std;

void bfs(int start, const vector<vector<int>>& adj) {
    vector<bool> visited(adj.size(), false);
    queue<int> q;
    q.push(start);
    visited[start] = true;
    while (!q.empty()) {
        int node = q.front(); q.pop();
        cout << node << " ";
        for (int neighbor : adj[node]) {
            if (!visited[neighbor]) {
                visited[neighbor] = true;
                q.push(neighbor);
            }
        }
    }
}
```

---

## 3. 复杂度分析
- **时间复杂度**：O(V + E)
  - V为节点数，E为边数。每个节点和每条边最多被访问一次。
- **空间复杂度**：O(V)
  - 主要用于存储队列和访问标记数组。

---

## 4. 常见应用场景
1. **无权图最短路径**：BFS天然适合无权图的最短路径查找（如迷宫最短路、社交网络最短关系链）。
2. **层次遍历**：如树的层序遍历、分层输出。
3. **连通性判定**：判断图是否连通、求连通分量。
4. **Web爬虫**：按层级抓取网页。
5. **社交网络分析**：如推荐"二度好友"。
6. **棋盘/网格问题**：如"最短步数走出迷宫"、"腐烂的橘子"等。

---

## 5. 进阶与优化

### 5.1 双向BFS（Bidirectional BFS）
- 同时从起点和终点两侧进行BFS，遇到交汇时停止，能大幅减少搜索空间。
- 适合已知起点和终点的最短路径问题。

### 5.2 多源BFS（Multi-source BFS）
- 从多个起点同时扩展，常用于"多个源点到所有点的最短距离"问题。
- 典型如"多个火源蔓延最短时间"。

### 5.3 层次BFS与最短路径
- BFS天然支持分层（step变量记录层数），可用于求最短步数。

### 5.4 并行与分布式BFS
- 在大规模图计算中，BFS可并行化（如Pregel、Spark GraphX等平台）。

---

## 6. 典型题目与代码模板

### 6.1 LeetCode 题目
- [542. 01 矩阵](https://leetcode.cn/problems/01-matrix/)
- [994. 腐烂的橘子](https://leetcode.cn/problems/rotting-oranges/)
- [752. 打开转盘锁](https://leetcode.cn/problems/open-the-lock/)
- [127. 单词接龙](https://leetcode.cn/problems/word-ladder/)

### 6.2 BFS最短路径模板

```
from collections import deque

def bfs_shortest_path(graph, start, target):
    visited = set()
    queue = deque([(start, 0)])  # (节点, 距离)
    while queue:
        node, dist = queue.popleft()
        if node == target:
            return dist
        if node in visited:
            continue
        visited.add(node)
        for neighbor in graph[node]:
            if neighbor not in visited:
                queue.append((neighbor, dist + 1))
    return -1  # 未找到
```

---

## 7. 进阶案例与变体

### 7.1 双向BFS代码示例

```
def bidirectional_bfs(graph, start, end):
    if start == end:
        return 0
    visited_start = {start}
    visited_end = {end}
    queue_start = deque([(start, 0)])
    queue_end = deque([(end, 0)])
    while queue_start and queue_end:
        # 从起点扩展一层
        node, dist = queue_start.popleft()
        for neighbor in graph[node]:
            if neighbor in visited_end:
                return dist + 1
            if neighbor not in visited_start:
                visited_start.add(neighbor)
                queue_start.append((neighbor, dist + 1))
        # 从终点扩展一层
        node, dist = queue_end.popleft()
        for neighbor in graph[node]:
            if neighbor in visited_start:
                return dist + 1
            if neighbor not in visited_end:
                visited_end.add(neighbor)
                queue_end.append((neighbor, dist + 1))
    return -1
```

### 7.2 多源BFS代码示例

```
from collections import deque

def multi_source_bfs(graph, sources):
    visited = set(sources)
    queue = deque([(s, 0) for s in sources])
    while queue:
        node, dist = queue.popleft()
        # 处理节点
        for neighbor in graph[node]:
            if neighbor not in visited:
                visited.add(neighbor)
                queue.append((neighbor, dist + 1))
```

---

## 8. 学习建议与参考资料

### 学习建议
- 建议先理解队列、图的邻接表/邻接矩阵等基础数据结构。
- 多画图模拟BFS过程，体会"层层推进"的思想。
- 多刷LeetCode/牛客网相关题目，掌握BFS模板。
- 理解BFS与DFS的区别与适用场景。

### 参考资料
- [广度优先搜索(BFS）学习笔记（CSDN）](https://blog.csdn.net/ELovevne/article/details/139097269)
- [labuladong：BFS算法解题套路框架](https://labuladong.online/algo/essential-technique/bfs-framework/)
- [图的BFS算法（CSDN）](https://blog.csdn.net/qq_36159303/article/details/106983110)
- [LeetCode官方题解](https://leetcode.cn/tag/breadth-first-search/)
- [BFS算法（dev.to）](https://dev.to/khushi2488/breadth-first-search-bfs-algorithm-3lmh)

---
