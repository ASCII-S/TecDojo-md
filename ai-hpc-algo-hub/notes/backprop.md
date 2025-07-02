---
jupyter:
  jupytext:
    text_representation:
      extension: .md
      format_name: markdown
      format_version: '1.3'
      jupytext_version: 1.14.4
  kernelspec:
    display_name: C++
    language: c++
    name: xcpp11
---

# 反向传播算法 (BackPropagation) 学习笔记

## 1. 算法简介

反向传播（BackPropagation，简称BP）算法是神经网络中最常用的训练算法之一，用于计算损失函数对网络中每个权重的梯度。BP算法的核心思想是通过链式法则（微积分中的复合函数求导法则）逆向传播误差，从而调整网络权重，最小化预测值与真实值之间的误差。

**复合函数求导法则**：
$$
\frac{\partial z}{\partial x} = \frac{\partial z}{\partial y} \cdot \frac{\partial y}{\partial x}
$$

## 2. 算法原理

### 2.1 神经网络基础

一个基本的神经网络由输入层、隐藏层和输出层组成。每层之间通过权重连接，每个神经元接收加权输入，通过激活函数产生输出。

在代码中，神经网络结构定义如下：


```
/**
 * @struct BPNN
 * @brief 神经网络结构
 *
 * 神经网络结构定义
 * 包含输入层、隐藏层和输出层的神经元数量
 * 输入层、隐藏层和输出层的神经元
 * 隐藏层和输出层的误差
 * 目标值
 * 输入层到隐藏层的权重
 * 隐藏层到输出层的权重
 */
typedef struct {
  int input_n;                  /* 输入层神经元数量 */
  int hidden_n;                 /* 隐藏层神经元数量 */
  int output_n;                 /* 输出层神经元数量 */

  float *input_units;          /* 输入层神经元 */
  float *hidden_units;         /* 隐藏层神经元 */
  float *output_units;         /* 输出层神经元 */

  float *hidden_delta;         /* 隐藏层误差 */
  float *output_delta;         /* 输出层误差 */

  float *target;               /* 目标值 */

  float **input_weights;       /* 输入层到隐藏层的权重 */
  float **hidden_weights;      /* 隐藏层到输出层的权重 */

  float **input_prev_weights;  /* 输入层到隐藏层的上一次权重变化 */
  float **hidden_prev_weights; /* 隐藏层到输出层的上一次权重变化 */
} BPNN;
```

### 2.2 激活函数

本实现使用Sigmoid函数作为激活函数：

```
float squash(x)
float x;
{
  return (1.0 / (1.0 + exp(-x)));
}
```

Sigmoid函数将任意值映射到(0,1)范围内，具有平滑的梯度，适合于BP算法的梯度计算。

### 2.3 前向传播

前向传播是将输入数据从输入层传递到输出层的过程，计算每一层的输出值：


```
/**
 * 前向传播
 * @param l1 输入层
 * @param l2 输出层
 * @param conn 权重矩阵
 * @param n1 输入层神经元数量
 * @param n2 输出层神经元数量
 */
void bpnn_layerforward(l1, l2, conn, n1, n2)
float *l1, *l2, **conn;
int n1, n2;
{
  float sum;
  int j, k;

  /*** 设置阈值单元 ***/
  l1[0] = 1.0;
  #pragma omp parallel for shared(conn, n1, n2, l1) private(k, j) reduction(+: sum) schedule(static)
  /*** 对第二层的每个单元 ***/
  for (j = 1; j <= n2; j++) {
    /*** 计算其输入的加权和 ***/
    sum = 0.0;
    for (k = 0; k <= n1; k++) {	
      sum += conn[k][j] * l1[k]; 
    }
    l2[j] = squash(sum);
  }
}
```

### 2.4 计算误差

BP算法需要计算网络每一层的误差：


```
/**
 * 计算输出层误差
 * @param delta 输出层误差
 * @param target 目标值
 * @param output 输出层值
 * @param nj 输出层神经元数量
 * @param err 总误差
 */
void bpnn_output_error(delta, target, output, nj, err)  
float *delta, *target, *output, *err;
int nj;
{
  int j;
  float o, t, errsum;
  errsum = 0.0;
  for (j = 1; j <= nj; j++) {
    o = output[j];
    t = target[j];
    delta[j] = o * (1.0 - o) * (t - o);
    errsum += ABS(delta[j]);
  }
  *err = errsum;
}

/**
 * 计算隐藏层误差
 * @param delta_h 隐藏层误差
 * @param nh 隐藏层神经元数量
 * @param delta_o 输出层误差
 * @param no 输出层神经元数量
 * @param who 隐藏层到输出层的权重
 * @param hidden 隐藏层值
 * @param err 总误差
 */
void bpnn_hidden_error(delta_h, nh, delta_o, no, who, hidden, err)
float *delta_h, *delta_o, *hidden, **who, *err;
int nh, no;
{
  int j, k;
  float h, sum, errsum;

  errsum = 0.0;
  for (j = 1; j <= nh; j++) {
    h = hidden[j];
    sum = 0.0;
    for (k = 1; k <= no; k++) {
      sum += delta_o[k] * who[j][k];
    }
    delta_h[j] = h * (1.0 - h) * sum;
    errsum += ABS(delta_h[j]);
  }
  *err = errsum;
}
```

### 2.5 权重更新

根据计算出的误差，通过梯度下降法更新网络权重：


```
/**
 * 调整权重
 * @param delta 误差
 * @param ndelta 误差元素数量
 * @param ly 输入层值
 * @param nly 输入层神经元数量
 * @param w 权重
 * @param oldw 上一次权重变化
 */
void bpnn_adjust_weights(delta, ndelta, ly, nly, w, oldw)
float *delta, *ly, **w, **oldw;
{
  float new_dw;
  int k, j;
  ly[0] = 1.0;
  
  #pragma omp parallel for shared(oldw, w, delta) private(j, k, new_dw) firstprivate(ndelta, nly) 
  for (j = 1; j <= ndelta; j++) {
    for (k = 0; k <= nly; k++) {
      new_dw = ((ETA * delta[j] * ly[k]) + (MOMENTUM * oldw[k][j]));
	  w[k][j] += new_dw;
	  oldw[k][j] = new_dw;
    }
  }
}
```

其中，ETA是学习率（0.3），MOMENTUM是动量因子（0.3），用于加速训练过程并改善收敛性。

## 3. OpenMP并行化

该实现使用OpenMP进行并行化处理，主要在以下位置：

1. 前向传播计算：
```
#pragma omp parallel for shared(conn, n1, n2, l1) private(k, j) reduction(+: sum) schedule(static)
```

2. 权重更新：
```
#pragma omp parallel for shared(oldw, w, delta) private(j, k, new_dw) firstprivate(ndelta, nly)
```

这些并行处理指令允许在多核处理器上同时执行多个计算任务，提高处理速度。

## 4. 算法完整流程

### 4.1 反向传播算法流程详解

反向传播算法的核心流程可以分为以下几个关键步骤：

1. **初始化**：创建神经网络结构，随机初始化权重
2. **前向传播**：计算每层神经元的输出
3. **计算误差**：根据目标值和实际输出计算误差
4. **反向传播误差**：从输出层向输入层传播误差
5. **更新权重**：根据误差和学习率调整网络权重
6. **重复迭代**：重复步骤2-5直到收敛或达到最大迭代次数

### 4.2 详细流程图解

神经网络训练的完整流程如下图所示：

```
输入层 -> 隐藏层 -> 输出层 -> 计算误差 -> 反向传播误差 -> 更新权重
  ^                                            |
  |                                            |
  +--------------------------------------------+
```

### 4.3 代码实现详解

在代码实现中，BP算法的完整流程被封装在`bpnn_train`函数中：


```
/**
 * 训练神经网络
 * @param net 神经网络
 * @param eo 输出层误差
 * @param eh 隐藏层误差
 */
void bpnn_train(net, eo, eh)
BPNN *net;
float *eo, *eh;
{
  int in, hid, out;
  float out_err, hid_err;

  // 获取网络结构信息
  in = net->input_n;
  hid = net->hidden_n;
  out = net->output_n;

  // 步骤1: 前向传播
  // 从输入层到隐藏层
  bpnn_layerforward(net->input_units, net->hidden_units,
      net->input_weights, in, hid);
  // 从隐藏层到输出层
  bpnn_layerforward(net->hidden_units, net->output_units,
      net->hidden_weights, hid, out);

  // 步骤2: 计算误差
  // 计算输出层误差
  bpnn_output_error(net->output_delta, net->target, net->output_units,
      out, &out_err);
  // 计算隐藏层误差 (反向传播误差)
  bpnn_hidden_error(net->hidden_delta, hid, net->output_delta, out,
      net->hidden_weights, net->hidden_units, &hid_err);
  // 保存误差值
  *eo = out_err;
  *eh = hid_err;

  // 步骤3: 更新权重
  // 更新隐藏层到输出层的权重
  bpnn_adjust_weights(net->output_delta, out, net->hidden_units, hid,
      net->hidden_weights, net->hidden_prev_weights);
  // 更新输入层到隐藏层的权重
  bpnn_adjust_weights(net->hidden_delta, hid, net->input_units, in,
      net->input_weights, net->input_prev_weights);
}
```

### 4.4 数学原理与代码对应关系

反向传播算法的核心是通过链式法则计算梯度，在代码中的体现如下：

1. **输出层误差计算**：
   ```C++
   delta[j] = o * (1.0 - o) * (t - o);
   ```
   这对应于损失函数对输出层加权输入的偏导数：
   $$\delta_j^o = \frac{\partial E}{\partial y_j} \cdot \frac{\partial y_j}{\partial net_j} = (t_j - o_j) \cdot o_j(1-o_j)$$
   
   其中：
   - $E$ 是损失函数
   - $y_j$ 是输出层第j个神经元的输出
   - $net_j$ 是输出层第j个神经元的加权输入
   - $t_j$ 是目标值
   - $o_j$ 是实际输出
   - $o_j(1-o_j)$ 是sigmoid函数的导数

2. **隐藏层误差计算**：
   ```C++
   sum = 0.0;
   for (k = 1; k <= no; k++) {
     sum += delta_o[k] * who[j][k];
   }
   delta_h[j] = h * (1.0 - h) * sum;
   ```
   
   这对应于损失函数对隐藏层加权输入的偏导数：
   $$\delta_j^h = \frac{\partial E}{\partial h_j} \cdot \frac{\partial h_j}{\partial net_j^h} = \left(\sum_{k=1}^{n_o} \delta_k^o \cdot w_{jk}\right) \cdot h_j(1-h_j)$$
   
   其中：
   - $h_j$ 是隐藏层第j个神经元的输出
   - $net_j^h$ 是隐藏层第j个神经元的加权输入
   - $w_{jk}$ 是隐藏层第j个神经元到输出层第k个神经元的权重
   - $\delta_k^o$ 是输出层第k个神经元的误差
   - $h_j(1-h_j)$ 是sigmoid函数的导数

3. **权重更新**：
   ```C++
   new_dw = ((ETA * delta[j] * ly[k]) + (MOMENTUM * oldw[k][j]));
   w[k][j] += new_dw;
   ```
   
   这对应于梯度下降法的权重更新公式：
   $$\Delta w_{kj} = -\eta \frac{\partial E}{\partial w_{kj}} + \alpha \Delta w_{kj}^{old}$$
   $$w_{kj}^{new} = w_{kj}^{old} + \Delta w_{kj}$$
   
   其中：
   - $\eta$ 是学习率（代码中的ETA = 0.3）
   - $\alpha$ 是动量因子（代码中的MOMENTUM = 0.3）
   - $\frac{\partial E}{\partial w_{kj}} = -\delta_j \cdot y_k$，其中$y_k$是前一层的输出
   - $\Delta w_{kj}^{old}$ 是上一次的权重变化

### 4.5 完整训练过程

在facetrain.c文件中，可以看到完整的训练过程：


```
backprop_face()
{
  BPNN *net;
  int i;
  float out_err, hid_err;
  // 创建神经网络 (输入层大小可变, 隐藏层16个神经元, 输出层1个神经元)
  net = bpnn_create(layer_size, 16, 1);
  printf("Input layer size : %d\n", layer_size);
  // 加载训练数据
  load(net);
  // 开始训练
  printf("Starting training kernel\n");
  bpnn_train_kernel(net, &out_err, &hid_err);
  // 释放网络资源
  bpnn_free(net);
  printf("Training done\n");
}
```

而在backprop_kernel.c中，`bpnn_train_kernel`函数调用了`bpnn_train`函数执行实际的训练过程：


```
void bpnn_train_kernel(BPNN *net, float *eo, float *eh)
{
  int in, hid, out;
  float out_err, hid_err;
  
  in = net->input_n;
  hid = net->hidden_n;
  out = net->output_n;   
   
  printf("Performing CPU computation\n");
  // 执行前向传播
  bpnn_layerforward(net->input_units, net->hidden_units,net->input_weights, in, hid);
  bpnn_layerforward(net->hidden_units, net->output_units, net->hidden_weights, hid, out);
  
  // 计算误差并反向传播
  bpnn_output_error(net->output_delta, net->target, net->output_units, out, &out_err);
  bpnn_hidden_error(net->hidden_delta, hid, net->output_delta, out, net->hidden_weights, net->hidden_units, &hid_err);  
  
  // 更新权重
  bpnn_adjust_weights(net->output_delta, out, net->hidden_units, hid, net->hidden_weights, net->hidden_prev_weights);
  bpnn_adjust_weights(net->hidden_delta, hid, net->input_units, in, net->input_weights, net->input_prev_weights);
}
```

## 5. 反向传播算法小结

反向传播算法的核心优势：
1. 高效计算梯度：通过链式法则逐层传播误差，避免直接计算复杂导数
2. 适用性广：适用于各种神经网络架构
3. 易于实现：虽然数学原理复杂，但算法实现相对简单

局限性：
1. 可能陷入局部最小值
2. 训练深层网络时可能遇到梯度消失/爆炸问题
3. 需要大量训练数据和迭代才能获得好的结果

## 6. 实践技巧

1. 参数调整：学习率和动量因子的选择对收敛速度和稳定性至关重要
2. 初始化：权重初始化方法影响训练效果
3. 数据预处理：通常需要对输入数据进行归一化处理
4. 正则化：可以添加正则化项防止过拟合

## 7. 进阶学习方向

1. 深入理解链式法则及其在BP中的应用
2. 学习其他优化算法（如Adam, RMSprop等）
3. 探索应对梯度消失/爆炸的方法（如ReLU激活函数、批归一化等）
4. 学习更复杂的神经网络架构（CNN, RNN等）及其BP训练方法
