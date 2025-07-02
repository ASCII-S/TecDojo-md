---
jupytext:
  formats: md:markdown,ipynb/ai-hpc-algo-hub/GCN.ipynb:notebook
  text_representation:
    extension: .md
    format_name: markdown
    format_version: '1.3'
    jupytext_version: 1.13.8
kernelspec:
  display_name: C++
  language: C++
  name: xcpp
language_info:
  codemirror_mode: text/x-c++src
  file_extension: .cpp
  mimetype: text/x-c++src
  name: c++
  version: '11'
---

# GCN（图卷积网络）算法学习笔记

## 1. 背景与发展动机

在现实世界中，许多数据天然以图结构存在，如社交网络、分子结构、知识图谱等。传统的深度学习（如CNN）主要处理规则的欧式数据（如图片、序列），难以直接应用于不规则的图结构。为了解决这一问题，**图神经网络（GNN）**应运而生，而**图卷积网络（GCN, Graph Convolutional Network）**是其中最具代表性的模型之一。

GCN的提出主要是为了解决：
- 如何在图结构上实现"卷积"操作，提取节点的结构与特征信息？
- 如何让节点的表示融合邻居的信息，实现节点分类、图分类等任务？

GCN最早由Kipf和Welling于2017年提出，论文《Semi-Supervised Classification with Graph Convolutional Networks》是该领域的奠基之作。

---

## 2. GCN核心原理

### 2.1 谱域推导（频域视角）

GCN的理论基础之一是**谱图理论**。谱方法将图的卷积定义为：

\[
Z = U g_\theta U^T X
\]

- $U$：图拉普拉斯矩阵的特征向量矩阵（傅里叶基）
- $g_\theta$：可学习的滤波器参数
- $X$：节点特征矩阵

但谱方法计算复杂度高，不适合大规模图。为此，Kipf等人采用了切比雪夫多项式近似，并进一步简化，最终得到高效的GCN层表达式。

### 2.2 空域推导（邻域聚合视角）

GCN的核心思想是：**每个节点的表示由自身和邻居节点的特征加权平均得到**。

#### 公式推导

GCN一层的传播规则为：

\[
H^{(l+1)} = \sigma( \tilde{D}^{-1/2} \tilde{A} \tilde{D}^{-1/2} H^{(l)} W^{(l)} )
\]

- $\tilde{A} = A + I$：邻接矩阵加自环
- $\tilde{D}$：$\tilde{A}$的度矩阵
- $H^{(l)}$：第$l$层的节点特征（$H^{(0)} = X$）
- $W^{(l)}$：第$l$层的可学习权重
- $\sigma$：激活函数（如ReLU）

#### 直观解释
- $\tilde{A}$加自环，保证节点自身信息被保留。
- $\tilde{D}^{-1/2} \tilde{A} \tilde{D}^{-1/2}$实现对邻居特征的归一化加权平均，防止度数不同带来的影响。
- 多层GCN可实现多阶邻居信息的融合。

#### 信息流动
- 一层GCN：每个节点聚合一阶邻居信息。
- 两层GCN：每个节点聚合二阶邻居信息（邻居的邻居）。

---

## 3. 代码实现

### 3.1 PyTorch实现（简化版）

```
import torch
import torch.nn as nn
import torch.nn.functional as F

class GCNLayer(nn.Module):
    def __init__(self, in_feats, out_feats):
        super().__init__()
        self.linear = nn.Linear(in_feats, out_feats)
    def forward(self, x, adj):
        # adj为归一化后的邻接矩阵
        x = torch.mm(adj, x)
        return self.linear(x)

class GCN(nn.Module):
    def __init__(self, in_feats, hidden_feats, out_feats):
        super().__init__()
        self.gcn1 = GCNLayer(in_feats, hidden_feats)
        self.gcn2 = GCNLayer(hidden_feats, out_feats)
    def forward(self, x, adj):
        x = F.relu(self.gcn1(x, adj))
        x = self.gcn2(x, adj)
        return x
```

#### 邻接矩阵归一化

```
import numpy as np

def normalize_adj(adj):
    adj = adj + np.eye(adj.shape[0])  # 加自环
    degree = np.sum(adj, axis=1)
    d_inv_sqrt = np.power(degree, -0.5)
    d_inv_sqrt[np.isinf(d_inv_sqrt)] = 0.
    d_mat_inv_sqrt = np.diag(d_inv_sqrt)
    return d_mat_inv_sqrt @ adj @ d_mat_inv_sqrt
```

### 3.2 PyTorch Geometric实现

PyG库极大简化了GCN的实现：

```
from torch_geometric.nn import GCNConv
import torch.nn.functional as F

class GCN(torch.nn.Module):
    def __init__(self, in_channels, hidden_channels, out_channels):
        super().__init__()
        self.conv1 = GCNConv(in_channels, hidden_channels)
        self.conv2 = GCNConv(hidden_channels, out_channels)
    def forward(self, x, edge_index):
        x = F.relu(self.conv1(x, edge_index))
        x = self.conv2(x, edge_index)
        return x
```

---

## 4. 典型应用场景

- **节点分类**：如论文引用网络（Cora、Citeseer）、社交网络用户分类。
- **图分类**：如分子性质预测、蛋白质功能预测。
- **链路预测**：如社交网络好友推荐、知识图谱补全。
- **推荐系统**：利用用户-物品图建模兴趣传播。
- **知识图谱推理**：实体关系预测。

---

## 5. 优缺点与发展方向

### 优点
- 能直接处理非欧式结构数据，适应性强。
- 能有效融合结构与特征信息，捕捉节点间复杂依赖。
- 参数量少，适合大规模图。

### 缺点
- **过平滑（Over-smoothing）**：层数过多时，节点表示趋于一致，区分度下降。
- **依赖图结构**：对图的连通性、稀疏性较敏感。
- **难以处理动态图、异质图**。

### 发展方向
- 深层GCN的训练（如残差连接、DropEdge等）
- 图注意力机制（GAT）、采样式聚合（GraphSAGE）
- 动态图、异质图、超图的建模
- 与自监督、元学习等结合

---

## 6. 学习建议与参考资料

### 学习建议
- 先理解图的基本概念（节点、边、邻接矩阵、度矩阵等）。
- 多画图模拟GCN的邻域聚合过程。
- 用小数据集（如Cora）动手实践，调试GCN代码。
- 理解GCN与传统CNN的异同。
- 关注GCN的变体与实际应用案例。

### 参考资料
- [GCN原论文](https://arxiv.org/abs/1609.02907)
- [深入浅出了解GCN原理（CSDN）](https://blog.csdn.net/qq_26593695/article/details/109393775)
- [图卷积网络(Graph Convolutional Networks, GCN)详细介绍（CSDN）](https://blog.csdn.net/qq_43787862/article/details/113830925)
- [PyTorch Geometric官方文档](https://pytorch-geometric.readthedocs.io/)
- [知乎：如何理解GCN](https://zhuanlan.zhihu.com/p/71200936)
- 《深入浅出图神经网络：GNN原理解析》

---

> **小结**：GCN是图神经网络的基础模型，掌握其原理和实现对后续学习GNN变体和图数据分析非常重要。建议多动手实践，结合实际案例加深理解。
