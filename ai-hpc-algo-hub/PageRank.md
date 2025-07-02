# PageRank算法学习笔记

## 1. 基本概念

**PageRank** 是Google创始人拉里·佩奇和谢尔盖·布林于1997年提出的网页排名算法。它通过分析网页之间的超链接关系，评估每个网页的重要性。PageRank的核心思想是：
- 如果一个网页被很多网页链接，说明它很重要。
- 如果一个网页被重要网页链接，它也会变得重要。

PageRank不仅用于网页排名，也广泛应用于社交网络分析、学术论文引用分析、推荐系统等领域。

---

## 2. 算法原理

### 2.1 随机游走模型

PageRank可以理解为"随机冲浪者模型"：
- 假设有一个用户在网页之间随机点击链接浏览网页。
- 用户有一定概率（通常为85%）点击当前页面的某个出链，也有一定概率（15%）随机跳转到任意页面。
- 每个页面的PageRank值，表示随机游走者在无限次点击后停留在该页面的概率。

### 2.2 数学公式

设网页总数为N，d为阻尼系数（damping factor，通常取0.85），PR(A)为页面A的PageRank值，T1...Tn为所有指向A的页面，C(Ti)为页面Ti的出链数。

公式如下：

$$
PR(A) = (1-d) + d \sum_{i=1}^n \frac{PR(T_i)}{C(T_i)}
$$

- (1-d)：表示随机跳转到任意页面的概率。
- d：表示继续点击链接的概率。
- $\frac{PR(T_i)}{C(T_i)}$：页面Ti将其PageRank值平均分配给它的所有出链页面。

### 2.3 迭代计算

1. 所有页面初始PageRank值设为$\frac{1}{N}$。
2. 按照公式迭代更新每个页面的PageRank值。
3. 当所有页面的PageRank值变化小于某个阈值（如0.0001）时，认为收敛，停止迭代。

---

## 3. 代码实现

### 3.1 Python简易实现

```
import numpy as np

def pagerank(M, num_iterations=100, d=0.85):
    N = M.shape[1]
    v = np.ones((N, 1)) / N  # 初始PageRank向量
    M_hat = d * M + (1 - d) / N * np.ones((N, N))
    for _ in range(num_iterations):
        v = M_hat @ v
    return v
```

- M为N×N的转移概率矩阵，M[i][j]表示j页面指向i页面的概率。
- 迭代num_iterations次后，v即为各页面的PageRank值。

### 3.2 NetworkX库实现

NetworkX是Python常用的图计算库，内置了PageRank算法。

```
import networkx as nx
G = nx.DiGraph()
G.add_edges_from([(0,1),(1,2),(2,0),(2,3)])
pagerank = nx.pagerank(G, alpha=0.85)
print(pagerank)
```

- alpha参数即阻尼系数d。
- 输出为每个节点的PageRank值。

---

## 4. 算法细节与常见问题

### 4.1 排名泄露（Dangling Node）
- 如果某个页面没有出链（死链），会导致PageRank值"泄露"。
- 解决方法：将无出链页面视为指向所有页面（包括自身），即均匀分配其PageRank值。

### 4.2 阻尼系数的作用
- 防止算法陷入循环或死链。
- 反映用户随机跳转网页的行为。
- 通常取值0.85。

### 4.3 收敛性
- PageRank算法通常在30-100次迭代后收敛。
- 可以设置收敛阈值（如所有节点PageRank变化小于1e-6）。

---

## 5. 应用场景

- **搜索引擎**：网页排名（Google、百度等）。
- **社交网络分析**：识别影响力人物。
- **学术论文引用分析**：评估论文/作者影响力（如Google Scholar）。
- **推荐系统**：如YouTube视频推荐、商品推荐。
- **自然语言处理**：TextRank用于关键词提取、文本摘要。

---

## 6. 工程实践与优化

- **大规模计算**：PageRank适合分布式计算（如MapReduce、Spark GraphX）。
- **稀疏矩阵优化**：实际网页链接非常稀疏，适合用稀疏矩阵存储和运算。
- **增量更新**：网页变化时可只对部分节点重新计算。

---

## 7. 学习建议与参考资料

### 学习建议
- 建议先理解有向图、邻接矩阵、概率转移矩阵等基础知识。
- 用小规模网页手动画图，模拟PageRank迭代过程。
- 多用NetworkX等工具实际操作，观察收敛过程。
- 结合实际应用场景思考PageRank的意义。

### 参考资料
- [PageRank算法原理与实现（CSDN）](https://blog.csdn.net/qq_35357274/article/details/109630125)
- [PageRank学习笔记（CSDN）](https://blog.csdn.net/m0_72410588/article/details/130549357)
- [Wikipedia: PageRank](https://en.wikipedia.org/wiki/PageRank)
- [NetworkX官方文档：pagerank](https://networkx.org/documentation/stable/reference/generated/networkx.algorithms.link_analysis.pagerank_alg.pagerank.html)
- [PageRank Algorithm Explained (Medium)](https://medium.com/biased-algorithms/pagerank-algorithm-explained-5f5c6a8c6696)

---

> **小结**：PageRank是图计算领域最经典的算法之一，理解其原理和实现对学习大数据、搜索引擎、社交网络分析等领域非常有帮助。建议多动手实践，结合实际案例加深理解。
