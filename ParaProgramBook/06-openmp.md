---
jupytext:
  formats: md:markdown,ipynb/ParaProgramBook/06-openmp.ipynb:notebook
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

[[并行程序设计]]

---

# # pragma omp制导语句

[[基本用法]]

## 向量加法

```
\#include <iostream>
\#include <cstdint>
\#include <vector>
\#include "../include/hpc_helpers.hpp" // 包含 TIMERSTART 和 TIMERSTOP，以及 no_init_t 模板

int main() {
    // 定义输入向量大小
    const uint64_t num_entries = 1UL << 30;

    // 使用 no_init_t 包装分配内存
    TIMERSTART(alloc);
    std::vector<no_init_t<uint64_t>> x(num_entries);
    std::vector<no_init_t<uint64_t>> y(num_entries);
    std::vector<no_init_t<uint64_t>> z(num_entries);
    TIMERSTOP(alloc);

    // 手动初始化向量 x 和 y
    TIMERSTART(init);
    \#pragma omp parallel for
    for (uint64_t i = 0; i < num_entries; i++) {
        x[i] = i;
        y[i] = num_entries - i;
    }
    TIMERSTOP(init);

    // 顺序计算 x + y = z
    TIMERSTART(add_seq);
    for (uint64_t i = 0; i < num_entries; i++) {
        z[i] = x[i] + y[i];
    }
    TIMERSTOP(add_seq);

    // 并行计算 x + y = z
    TIMERSTART(add);
    \#pragma omp parallel for
    for (uint64_t i = 0; i < num_entries; i++) {
        z[i] = x[i] + y[i];
    }
    TIMERSTOP(add);

    // 检查求和结果是否正确
    TIMERSTART(check);
    bool is_correct = true;
    \#pragma omp parallel for
    for (uint64_t i = 0; i < num_entries; i++) {
        if (z[i] != num_entries) {
            \#pragma omp critical
            {
                std::cout << "Error at position " << i << ", expected: " 
                          << num_entries << ", got: " << z[i] << std::endl;
            }
            is_correct = false;
        }
    }
    if (is_correct) {
        std::cout << "All results are correct!" << std::endl;
    }
    TIMERSTOP(check);

    return 0;
}
```

```
g++ -std=c++14 -fopenmp -O2 6.3omp_add.cpp
```

**隐式同步:**

**OpenMP 的** `**parallel for**`： 在一个 `**parallel for**` 块结束后，默认会隐含一个同步点，确保所有线程完成循环的所有迭代后再继续执行后续代码。

**避免不必要的隐含同步**：

如果可以，关闭隐含同步。例如，在 OpenMP 中使用 `**nowait**` 指令跳过隐含同步：

```
\#pragma omp parallel for nowait
for (int i = 0; i < 10; i++) {
    // 任务执行
}
// 不等待循环完成，直接继续执行
```

## 矩阵向量乘

```
\#include <iostream>
\#include <cstdint>
\#include <vector>
\#include "../include/hpc_helpers.hpp" // 包含 TIMERSTART, TIMERSTOP 和 no_init_t 模板

// 初始化矩阵 A 和向量 x
template <typename value_t, typename index_t>
void init(std::vector<value_t>& A, std::vector<value_t>& x, index_t m, index_t n) {
    for (index_t row = 0; row < m; row++) {
        for (index_t col = 0; col < n; col++) {
            A[row * n + col] = (row >= col) ? 1 : 0; // 下三角矩阵
        }
    }
    for (index_t col = 0; col < n; col++) {
        x[col] = col; // 初始化 x 为连续值
    }
}

// 矩阵向量乘法
template <typename value_t, typename index_t>
void mult(std::vector<value_t>& A, std::vector<value_t>& x, std::vector<value_t>& b, index_t m, index_t n, bool parallel) {
    \#pragma omp parallel for if (parallel)
    for (index_t row = 0; row < m; row++) {
        value_t accum = value_t(0);
        for (index_t col = 0; col < n; col++) {
            accum += A[row * n + col] * x[col];
        }
        b[row] = accum;
    }
}

int main() {
    const uint64_t n = 1UL << 15; // 矩阵列数
    const uint64_t m = 1UL << 15; // 矩阵行数

    TIMERSTART(overall);

    // 分配向量和矩阵的内存
    TIMERSTART(alloc);
    std::vector<no_init_t<uint64_t>> A(m * n); // 矩阵 A
    std::vector<no_init_t<uint64_t>> x(n);     // 向量 x
    std::vector<no_init_t<uint64_t>> b(m);     // 结果向量 b
    TIMERSTOP(alloc);

    // 初始化矩阵 A 和向量 x
    TIMERSTART(init);
    init(A, x, m, n);
    TIMERSTOP(init);

    // 顺序计算 A * x = b 三次
    for (uint64_t k = 0; k < 3; k++) {
        TIMERSTART(mult_seq);
        mult(A, x, b, m, n, false); // 串行计算
        TIMERSTOP(mult_seq);
    }

    // 并行计算 A * x = b 三次
    for (uint64_t k = 0; k < 3; k++) {
        TIMERSTART(mult_par);
        mult(A, x, b, m, n, true); // 并行计算
        TIMERSTOP(mult_par);
    }

    TIMERSTOP(overall);

    // 验证结果
    for (uint64_t index = 0; index < m; index++) {
        if (b[index] != index * (index + 1) / 2) {
            std::cout << "Error at position " << index
                      << ", value: " << b[index] << std::endl;
        }
    }

    return 0;
}
```

# 基本并行归约

## 最近邻分类

最近邻分类是一种简单而直观的**监督学习**算法，用于分类问题。其基本思想是：**给定一个待分类样本，根据其与训练集中样本的距离，选择最近的一个或多个样本的类别进行预测**。

```
\#include <iostream>       // for std::cout
\#include <limits>         // for std::numeric_limits
\#include <vector>         // for std::vector
\#include "../include/hpc_helpers.hpp"  // for TIMERSTART and TIMERSTOP
\#include "../include/binary-IO.hpp"    // for load_binary function

// 计算全对距离（all-vs-all distance）
template <typename value_t, typename index_t>
void all_vs_all(value_t* test, value_t* train, value_t* delta, 
                index_t num_test, index_t num_train, index_t num_features, 
                bool parallel) {
    \#pragma omp parallel for collapse(2) if (parallel)
    for (index_t i = 0; i < num_test; i++) {
        for (index_t j = 0; j < num_train; j++) {
            value_t accum = value_t(0); // 累加器
            for (index_t k = 0; k < num_features; k++) {
                const value_t residue = test[i * num_features + k] 
                                      - train[j * num_features + k];
                accum += residue * residue; // 计算欧几里得距离平方
            }
            delta[i * num_train + j] = accum; // 存储结果
        }
    }
}
```

`**collapse(2)**` **的含义**

在 OpenMP 中，`**collapse(2)**` 是一个**修饰符**，用于告诉编译器将**两层嵌套循环**合并成一个循环，并进行并行化。这有助于提高多线程的效率，尤其是在处理多维循环时。

**优点**

1. **提高并行效率**：
    - 在外层循环迭代次数较少时（例如 _N_ 很小），单独对外层循环进行并行化可能无法充分利用线程。
2. **均匀负载分配**：
    - 通过将多层循环展开为一个单层循环，任务可以更均匀地分配给多个线程，减少负载不均的问题。

**使用限制**

1. **循环必须是规整的（rectangular loops）**：
    - 每层循环的范围必须是已知的固定值。
2. **可能增加开销**：
    - 当循环体中的计算量很小，线程切换的开销可能抵消并行化的优势。

**细粒度优化:**

适合外层循环少(减少创建线程池次数),内层循环计算任务多(并行后加速效果大).

```
value_t accum = value_t(0); // 累加器

for (index_t k = 0; k < num_features; k++) {
    accum += some_value
}
```

**产生的问题: accum的在多个线程中的竞争问题**

**解决策略:**

1. 把accum设置成原子结构: 开销很大
2. 用锁机制: 开销很大
3. 最有效的方法:`#pragma omp parallel for reduction(+:accum)`
    
    OpenMP 提供了 `**reduction**` 指令，用于在多个线程之间安全地累加变量。`**reduction**` 会为每个线程创建一个 `**accum**` 的私有副本，线程完成计算后将结果合并到全局变量中。
    

---

## 并行标签预测

**1. 完全配对的距离矩阵**

我们之前通过计算每个测试样本和每个训练样本之间的距离，得到了一个完全配对的距离矩阵 D_D_。矩阵的形状为：num_test×num_train

- 每行对应一个测试样本与所有训练样本之间的距离。
- 每列对应一个训练样本与所有测试样本之间的距离。

通过扫描矩阵 D_D_ 的每一行，可以找到与测试样本最接近的训练样本（即最小距离值对应的索引）。

**2. 使用独热编码的标签**

独热编码是一种常见的分类标签表示方式，每个类别用一个长度为 C_C_的二进制向量表示，其中 C是类别总数：

- 只有一个位置为 1，其他位置为 0。
- 例如，在 MNIST 数据集中，数字 3 的标签用 (0,0,0,1,0,0,0,0,0,0) 表示。

**3. 分类器准确性的计算**

分类准确性（accuracy）是一个常见的分类器性能指标，定义如下：

Accuracy=$\frac{正确分类的样本数量测试集中样本总数}{测试集中样本总数正确分类的样本数量}$

计算过程：

1. 比较每个测试样本的预测标签 和真实标签。
2. 统计预测正确的样本数量。
3. 用正确分类的样本数量除以测试集的总样本数，得到分类准确性。

```
template <typename label_t, typename value_t, typename index_t>
value_t accuracy(
    const label_t* label_test,
    const label_t* label_train,
    const value_t* delta,
    index_t num_test,
    index_t num_train,
    index_t num_classes,
    bool parallel
) {
    index_t counter = 0;

    \#pragma omp parallel for reduction(+:counter) if (parallel)
    for (index_t i = 0; i < num_test; i++) {
        value_t bsf = std::numeric_limits<value_t>::max(); // 初始化最近距离
        index_t jst = std::numeric_limits<index_t>::max(); // 最近邻索引初始化

        // 找到最近邻
        for (index_t j = 0; j < num_train; j++) {
            const value_t value = delta[i * num_train + j];
            if (value < bsf) {
                bsf = value;
                jst = j;
            }
        }

        // 比较标签
        bool match = true;
        for (index_t k = 0; k < num_classes; k++) {
            match &&= label_test[i * num_classes + k] == label_train[jst * num_classes + k];
        }

        counter += match; // 如果匹配，计数器加 1
    }

    return value_t(counter) / value_t(num_test); // 返回分类准确性
}
```

```
int main(int argc, char* argv[]) {
    // 是否并行执行
    const bool parallel = argc > 1;
    std::cout << "running " << (parallel ? "in parallel" : "sequentially") << std::endl;

    // 数据维度定义
    const uint64_t num_features = 28 * 28;
    const uint64_t num_classes = 10;
    const uint64_t num_entries = 65000;
    const uint64_t num_train = 55000;
    const uint64_t num_test = num_entries - num_train;

    // 数据分配
    std::vector<float> input(num_entries * num_features);  // 图像数据
    std::vector<float> label(num_entries * num_classes);   // 标签数据
    std::vector<float> delta(num_test * num_train);        // 距离矩阵

    // 从磁盘加载数据
    load_binary(input.data(), input.size(), "./data/X.bin");
    load_binary(label.data(), label.size(), "./data/Y.bin");

    // 计算全对距离矩阵
    TIMERSTART(all_vs_all);
    const uint64_t inp_off = num_train * num_features;
    all_vs_all(
        input.data() + inp_off,   // 测试集数据
        input.data(),             // 训练集数据
        delta.data(),             // 距离矩阵
        num_test, num_train,      // 测试集和训练集大小
        num_features, parallel    // 是否并行
    );
    TIMERSTOP(all_vs_all);

    // 分类并计算准确率
    TIMERSTART(classify);
    const uint64_t lbl_off = num_train * num_classes;
    auto acc = accuracy(
        label.data() + lbl_off,   // 测试集标签
        label.data(),             // 训练集标签
        delta.data(),             // 距离矩阵
        num_test, num_train,      // 测试集和训练集大小
        num_classes, parallel     // 是否并行
    );
    TIMERSTOP(classify);

    // 输出准确率
    std::cout << "test accuracy: " << acc << std::endl;

    return 0;
}
```

全部程序由6.5+6.6+6.7组成

---

# 不平衡循环调度

## 负载失衡问题引入

问题引入:计算矩阵内积是常用的计算方式, 这种计算的特点是沿主对角线呈对称形式,因此在计算时只要计算下/上三角即可. 对于计算矩阵内积,简单在外层使用omp制导语句会导致负载失衡,一些线程比其他线程计算更多的元素值.

## OpenMP的guided调度模式

在 OpenMP 中，调度模式决定了如何将循环的迭代任务分配给线程。调度的目标是实现线程负载平衡和性能优化。

|   |   |   |   |   |
|---|---|---|---|---|
|**模式**|**分配方式**|**默认块大小**|**空闲发生场景**|**适用场景**|
|**static**|固定大小块，循环分配|n/p|如果线程完成任务早，需等待其他线程完成|均匀任务分布，工作量相等|
|**dynamic**|动态分配，线程空闲时获取新任务|1|仅在最后一个块任务时可能空闲|不均匀任务分布，工作量差异较大|
|**guided**|动态分配，块大小递减|n/p|同动态调度|初始任务多，后期任务少|
|**auto**|编译器或运行时决定|−−|依赖编译器行为|不确定任务特性，依赖系统优化|
|**runtime**|通过 `**OMP_SCHEDULE**` 决定|−−|取决于运行时设定|灵活配置调试，运行时可更改模式|

---

### **静态调度 (static)**

- **特点**：
    - 将n次迭代分成 n/c个**固定大小的块**，每个块包含 _c_ 次迭代。
    - 按照循环方式（循环块分配），将块分配给线程组。
    - **默认块大小**：
        - 如果未指定块大小 _c_，则块大小为总迭代次数 _n_ 除以线程数 _p_（即纯块分配）。
    - **线程状态**：
        - 如果一个线程完成了其分配的任务，它将处于**空闲等待**状态，直到所有线程完成工作。
- **适用场景**：任务分布均匀，迭代工作量相等。

---

### **动态调度 (dynamic)**

- **特点**：
    - 迭代也分为块，但块任务是动态分配的，即每次将下一个块分配给空闲线程。
    - 动态调度避免了线程空闲，因为线程完成后可以立即获取新任务。
    - **默认块大小**：如果未指定块大小，默认大小为 1（即纯循环分配）。块大小为c时,表示为线程一次分配连续的c个迭代任务.
- **线程状态**：
    - 除非一个线程执行的是最后一个任务块，否则不会出现空闲。
- **适用场景**：
    
    任务分布不均，工作量差异较大，需要动态负载平衡。
    

---

### **引导调度 (guided)**

- **特点**：
    - 块大小以递减方式分配：开始时较大，随着任务的进行逐渐变小。
    - 动态分配线程执行块，类似动态调度。
    - **默认块大小**：初始块大小等于总迭代次数，最小块大小默认为 1。
- **线程状态**：
    - 同动态调度，线程尽量保持忙碌。
- **适用场景**：
    
    当任务分布不均，但前期任务更多、后期任务减少时（例如指数递减工作量）
    

---

### **自动调度 (auto)**

- **特点**：
    - 由编译器或运行时系统决定使用哪种调度模式。
    - 允许编译器根据工作量和硬件架构自动选择最优的调度方式。
- **适用场景**：
    
    不需要手动干预调度模式，依赖编译器进行优化。
    

---

### **运行时调度 (runtime)**

- **特点**：
    - 调度模式由环境变量 `**OMP_SCHEDULE**` 决定。
    - 运行时选择的模式可以是上述模式之一。
- **适用场景**：
    
    调试或灵活设置时有用，通过运行时环境变量控制调度行为。
    

---

```
\#include <iostream> // std::cout
\#include <vector>   // std::vector
\#include <omp.h>    // OpenMP for parallelism
\#include "../include/hpc_helpers.hpp" // TIMERSTART and TIMERSTOP macros
\#include "../include/binary_IO.hpp"   // load_binary function

// Define the scheduling mode (can be modified)
\#define MODE static
\#define MODE static ,1 
\#define MODE static ,32
\#define MODE dynamic 
\#define MODE dynamic , 32


// Function to compute the inner product (symmetric matrix computation)
template <typename value_t, typename index_t>
void inner_product(value_t* data, value_t* delta, index_t num_entries, index_t num_features, bool parallel) {
    \#pragma omp parallel for schedule(MODE) if (parallel)
    for (index_t i = 0; i < num_entries; i++) {
        for (index_t j = i; j < num_entries; j++) { // Upper triangle of the matrix
            value_t accum = value_t(0); // Initialize accumulator
            for (index_t k = 0; k < num_features; k++) {
                accum += data[i * num_features + k] * data[j * num_features + k];
            }
            // Fill both upper and lower triangle due to symmetry
            delta[i * num_entries + j] = accum;
            delta[j * num_entries + i] = accum;
        }
    }
}
```

```
int main(int argc, char* argv[]) {
    // 如果有任何命令行参数，则以并行方式运行
    const bool parallel = argc > 1;
    std::cout << "Running " << (parallel ? "in parallel" : "sequentially") << std::endl;

    // 定义数据矩阵的维度
    const uint64_t num_features = 28 * 28; // 每个样本的特征数
    const uint64_t num_entries = 65000;    // 样本总数

    // 分配内存
    TIMERSTART(alloc)
    std::vector<float> input(num_entries * num_features); // 输入数据矩阵
    std::vector<float> delta(num_entries * num_entries);  // 内积矩阵
    TIMERSTOP(alloc)

    // 从磁盘读取数据
    TIMERSTART(read_data)
    load_binary(input.data(), input.size(), "./data/X.bin");
    TIMERSTOP(read_data)

    // 计算内积矩阵
    TIMERSTART(inner_product)
    inner_product(input.data(), delta.data(), num_entries, num_features, parallel);
    TIMERSTOP(inner_product)

    std::cout << "Inner product computation complete!" << std::endl;

    return 0;
}
```

# 高级归约

## MNIST 数据集上的 SOFTMAX 回归分类器

### 预测阶段

```
\#include "./include/hpc_helpers.hpp"  // timers
\#include "./include/binary-IO.hpp"     // load images
\#include <limits>
\#include <vector>
\#include <cmath>
\#include <algorithm> // For std::max

// Numerical limits of data types
template <typename value_t, typename index_t>
void softmax_regression(
    value_t* input, 
    value_t* output, 
    value_t* weights, 
    value_t* bias, 
    index_t n_input, 
    index_t n_output
) {
    // Step 1: Compute the weighted sum of inputs for each output class
    for (index_t j = 0; j < n_output; j++) {
        value_t accum = value_t(0);
        for (index_t k = 0; k < n_input; k++) {
            accum += weights[j * n_input + k] * input[k];  // Corrected multiplication
        }
        output[j] = accum + bias[j];  // Add bias to the weighted sum
    }

    // Step 2: Find the maximum value in the output (for numerical stability)
    value_t mu = std::numeric_limits<value_t>::lowest(); // Initialize to the lowest possible value
    for (index_t index = 0; index < n_output; index++) {
        mu = std::max(mu, output[index]);
    }

    // Step 3: Compute exp(z_j - mu) for numerical stability
    for (index_t j = 0; j < n_output; j++) {
        output[j] = std::exp(output[j] - mu);  // Apply exp function after subtracting mu
    }

    // Step 4: Compute the normalization factor Z = sum_j exp(z_j)
    value_t norm = value_t(0);
    for (index_t j = 0; j < n_output; j++) {
        norm += output[j];  // Sum of exponentiated values
    }

    // Step 5: Normalize the output values to get probabilities
    for (index_t j = 0; j < n_output; j++) {
        output[j] /= norm;  // Normalize by dividing each element by the sum
    }
}
```

```
template <typename value_t, typename index_t>
index_t argmax(value_t* neurons, index_t n_units) {
    index_t arg = 0;
    value_t max = std::numeric_limits<value_t>::lowest();  // Initialize to lowest value

    for (index_t j = 0; j < n_units; j++) {
        const value_t val = neurons[j];
        if (val > max) {
            arg = j;
            max = val;
        }
    }

    return arg;  // Return index of the maximum value
}
```

```
template <typename value_t, typename index_t>
value_t accuracy(
    value_t *input,
    value_t *label,
    value_t *weights,
    value_t *bias,
    index_t num_entries,
    index_t num_features,
    index_t num_classes) {

    index_t counter = index_t(0);

    \#pragma omp parallel for reduction(+: counter)
    for (index_t i = 0; i < num_entries; i++) {
        value_t output[num_classes];  // Output for each class

        const uint64_t input_off = i * num_features;
        const uint64_t label_off = i * num_classes;

        // Perform softmax regression
        softmax_regression(input + input_off, output, weights, bias, num_features, num_classes);

        // Compare predicted class with the true class
        counter += argmax(output, num_classes) == argmax(label + label_off, num_classes);
    }

    return value_t(counter) / value_t(num_entries);  // Return the accuracy as a fraction
}
```

### 训练阶段

```
template <typename value_t, typename index_t>
void train(
    value_t *input,
    value_t *label,
    value_t *weights,
    value_t *bias,
    index_t num_entries,
    index_t num_features,
    index_t num_classes,
    index_t num_iters = 32,
    value_t epsilon = 1e-1) {

    // Allocate memory for the gradients
    value_t *grad_bias = new value_t[num_classes];
    value_t *grad_weights = new value_t[num_features * num_classes];

    // Spawn the team of threads once
    \#pragma omp parallel
    for (uint64_t iter = 0; iter < num_iters; iter++) {
        // Zero the gradients
        \#pragma omp single
        for (index_t j = 0; j < num_classes; j++) {
            grad_bias[j] = value_t(0);
        }

        \#pragma omp for collapse(2)
        for (index_t j = 0; j < num_classes; j++) {
            for (index_t k = 0; k < num_features; k++) {
                grad_weights[j * num_features + k] = value_t(0);
            }
        }

        // Compute softmax contributions
        \#pragma omp for reduction(+:grad_bias[0:num_classes]) reduction(+:grad_weights[0:num_classes * num_features])
        for (index_t i = 0; i < num_entries; i++) {
            const index_t inp_off = i * num_features;
            const index_t out_off = i * num_classes;

            // Allocate memory for output
            value_t *output = new value_t[num_classes];

            // Perform softmax regression
            softmax_regression(input + inp_off, output, weights, bias, num_features, num_classes);

            // Compute gradients for bias and weights
            for (index_t j = 0; j < num_classes; j++) {
                const index_t out_ind = out_off + j;
                const value_t lbl_res = output[j] - label[out_ind];
                grad_bias[j] += lbl_res;

                const index_t wgt_off = j * num_features;
                for (index_t k = 0; k < num_features; k++) {
                    const index_t wgt_ind = wgt_off + k;
                    const index_t inp_ind = inp_off + k;
                    grad_weights[wgt_ind] += lbl_res * input[inp_ind];
                }
            }

            // Free the output memory
            delete[] output;
        }

        // Adjust bias vector
        \#pragma omp single
        for (index_t j = 0; j < num_classes; j++) {
            bias[j] -= epsilon * grad_bias[j] / num_entries;
        }

        // Adjust weight matrix
        \#pragma omp for collapse(2)
        for (index_t j = 0; j < num_classes; j++) {
            for (index_t k = 0; k < num_features; k++) {
                weights[j * num_features + k] -= epsilon * grad_weights[j * num_features + k] / num_entries;
            }
        }
    }

    // Free gradient memory
    delete[] grad_bias;
    delete[] grad_weights;
}
```

```
int main() {
    const uint64_t num_features = 28 * 28; // 每个图像的特征数量 (28x28像素)
    const uint64_t num_classes = 10;       // 分类的数量 (0-9)
    const uint64_t num_entries = 65000;   // 数据集中的样本总数

    // 初始化输入数据、标签、权重和偏置
    std::vector<float> input(num_entries * num_features);  // 图像数据
    std::vector<float> label(num_entries * num_classes);   // 标签数据
    std::vector<float> weights(num_classes * num_features); // 权重矩阵
    std::vector<float> bias(num_classes);                  // 偏置向量

    // 从二进制文件加载数据
    load_binary(input.data(), input.size(), "./data/X.bin");
    load_binary(label.data(), label.size(), "./data/Y.bin");

    // 进入训练和测试循环
    while (true) {
        // 训练阶段
        TIMERSTART(training);
        train(
            input.data(),         // 输入数据
            label.data(),         // 标签数据
            weights.data(),       // 权重矩阵
            bias.data(),          // 偏置向量
            55000UL,              // 训练样本数量
            num_features,         // 每个样本的特征数
            num_classes           // 分类数
        );
        TIMERSTOP(training);

        // 测试阶段
        const uint64_t off_inp = 55000 * num_features;  // 测试集偏移量（从训练集后开始）
        const uint64_t off_lbl = 55000 * num_classes;   // 标签偏移量

        TIMERSTART(accuracy);
        auto acc = accuracy(
            input.data() + off_inp, // 测试集数据
            label.data() + off_lbl, // 测试集标签
            weights.data(),         // 训练后的权重
            bias.data(),            // 训练后的偏置
            10000UL,                // 测试样本数量
            num_features,           // 每个样本的特征数
            num_classes             // 分类数
        );
        TIMERSTOP(accuracy);

        // 输出测试集的准确率
        std::cout << "Accuracy on test set: " << acc << std::endl;
    }

    return 0;
}
```

---

## 定制归约操作符

### 归约操作的数学性质

**结合律**

结合律是指在二进制运算中，操作的顺序不会影响结果. 结合律确保在并行归约中，无论如何划分或组合计算，最终结果都不会改变。这是并行归约算法正确性的核心条件。

**幺半群的性质**

幺半群的定义确保归约操作适用于特定集合 M上的所有元素：

1. **结合性**：运算满足结合律。
2. **封闭性**：对于 M 中的任何两个元素，运算结果仍在 M 中。
3. **中性元素 e**：满足 $a∘e=e∘a=a$，例如加法的中性元素是 0，乘法的中性元素是 1。

当归约运算满足这些性质时，可以安全地在并行环境中执行。

**扩展数组的必要性**

在实际实现中，当数组长度不是 2 的幂时，为了便于采用树结构归约，我们需要将数组扩展到最近的 2 的幂次。例如：

- 数组长度为 10 时，可以扩展为 16，并将新添加的元素设置为中性元素（如 0）。
- 数学意义：扩展操作确保算法可以使用标准的二叉树拓扑结构。

---

### 声明定制并行归约

`#pragma omp declare reduction` 是 OpenMP 中用于自定义归约操作的语句。

```
\#pragma omp declare reduction (reduction-name: type: combiner-function) initializer(initializer)
```

- **reduction-name**：为归约操作起一个名字（这只是用于在 OpenMP 并行区域内进行引用）。例如，可以是 `sum`, `min`, `max`，或者你自定义的名字。
- **type**：定义了归约操作中使用的数据类型。例如，对于一个累加和归约操作，类型可能是 `int` 或 `double`。
- **combiner-function**：是一个用户自定义的函数，它定义了如何将两个线程的结果合并起来,一般这个函数是结构体内的成员函数。这个函数必须是一个二元函数，即它接收两个输入，并返回合并后的结果。例如，累加操作的函数是 `a + b`，而乘法操作是 `a * b`。
- **initializer**（可选）：这个函数用于在并行区域内为每个线程的归约变量设置初始值。通常，归约操作的初始值是中性元素，比如累加操作的初始值是 0，累乘操作的初始值是 1。

```
\#include <iostream>
\#include <cstdint>
\#include <cmath>
\#include <random>
\#include <immintrin.h> // AVX intrinsics
struct avxop {
    __m256 neutral;  // 一个 256 位的 AVX 寄存器

    avxop() : neutral(_mm256_set1_ps(-INFINITY)) {}  // 构造函数初始化为 -INFINITY

    __m256 operator() (const __m256& lhs, const __m256& rhs) const {
        return _mm256_max_ps(lhs, rhs);  // 对应最大值操作
    }
};

void init(float *data, uint64_t length) {
    std::mt19937 engine(42);  // 使用 Mersenne Twister 随机数生成器
    std::uniform_real_distribution<float> density(-1L<<28, 1L<<28);  // 均匀分布范围
    for (uint64_t i = 0; i < length; i++)
        data[i] = density(engine);  // 填充数据
}

inline float hmax_sse3(__m128 v) {
    __m128 shuf = _mm_movehdup_ps(v);  // 复制高位到低位
    __m128 maxs = _mm_max_ps(v, shuf);  // 计算最大值
    shuf = _mm_movehl_ps(shuf, maxs);  // 高低交换
    maxs = _mm_max_ss(maxs, shuf);  // 继续计算最大值
    return _mm_cvtss_f32(maxs);  // 转换为浮点数
}

inline float hmax_avx(__m256 v) {
    __m128 hi = _mm256_extractf128_ps(v, 1);  // 提取高位
    __m128 lo = _mm256_castps256_ps128(v);  // 提取低位
    lo = _mm_max_ps(lo, hi);  // 计算低位和高位的最大值
    return hmax_sse3(lo);  // 使用 SSE3 计算最终的最大值
}

int main() {
    const uint64_t num_entries = 1UL << 28;  // 设置数据长度为 2^28
    const uint64_t num_bytes = num_entries * sizeof(float);  // 计算字节数
    auto data = static_cast<float*>(_mm_malloc(num_bytes, 32));  // 分配内存，要求 32 字节对齐
    init(data, num_entries);  // 填充数据

    \#pragma omp declare reduction(avxmax: __m256: omp_out = avxop() (omp_out, omp_in)) \
        initializer(omp_priv = avxop().neutral)
    auto result = avxop().neutral;  // 初始化归约结果为中性元素

    \#pragma omp parallel for reduction(avxmax: result)  // 并行归约操作
    for (uint64_t i = 0; i < num_entries; i += 8)
        result = avxop()(result, _mm256_load_ps(data + i));  // 使用 AVX 加载数据并进行归约

    std::cout << hmax_avx(result) << std::endl;  // 计算最终结果的最大值并输出
    _mm_free(data);  // 释放内存
}
```

## openmp高级归约

### 归约的总体执行流程

1. **创建线程组，确定每个线程需要执行的迭代集合**

- 在并行归约操作中，首先会创建一个线程组。每个线程负责计算某一部分的归约任务。通过合理的任务划分，可以保证每个线程的负载均衡，避免某些线程空闲，而其他线程过于繁忙。
- 任务的分配可以通过不同的调度策略来实现，如静态、动态、引导（guided）等。

2. **每个线程声明一个归约变量的私有化变量，初始化为幺半群的中性元素**

- 每个线程都有一个私有的归约变量，通常是归约操作中的一个中间结果。例如，在求和操作中，私有化变量会初始化为 0；在求最大值操作中，则初始化为 `INFINITY` 或数据类型的最小值。
- 这个私有化变量用来保存每个线程在其负责的迭代集合上的局部计算结果。

3. **所有线程执行迭代，而不管它们是否或如何涉及私有化变量的更新**

- 所有线程并行执行归约操作。每个线程根据其被分配的任务区间，独立计算归约变量的局部值。由于是并行计算，线程之间的操作是独立的，它们各自更新自己的私有化变量，而不直接干涉其他线程。
- 在这一步骤中，线程只是计算局部结果，并未进行全局合并。

4. **部分结果通过串行归约计算合并，并写回到全局变量**

- 一旦所有线程完成了局部计算，接下来会通过串行归约将这些部分结果合并成最终结果。这个步骤通常是在最后一个线程执行完任务后，进行局部结果的合并。
- 如果是二叉树结构的归约，多个局部结果将两两合并，直到最终得到全局结果。这个过程可以是递归的，也可以是通过循环实现。
- 最后，最终的归约结果会被写回到全局变量，确保每个线程的局部计算结果得到了合并。

---

### 归约映射的可交换性

归约操作的**可交换性**对于并行计算至关重要，它使得我们可以灵活地调整计算的顺序，并且保证最终结果的一致性。只有满足可交换性和结合性的操作才能在并行计算中得到正确的结果。对于不可交换的操作，我们需要确保操作顺序的合理性，以避免由于并行执行引发的错误。

### 隐式屏障

### 隐式屏障的局限性

OpenMP的隐式屏障只保证在并行区域内所有线程完成任务后才退出，但它并不能保证在归约操作时，所有线程的局部结果都被正确地同步到全局变量 `x`。因此，如果使用并行归约时依赖于隐式屏障，可能会导致以下问题：

- 局部归约结果未被正确更新到全局变量。
- 由于线程的执行顺序和任务划分的不同，结果的不确定性会增加。

# 任务并行

## 树遍历

### `#pragma omp task`

`#pragma omp task` 是 OpenMP 中的一个并行化指令，用于将任务标记为可并行执行的部分。它的作用是将某段代码块指定为独立的任务，这些任务可以由多个线程并行执行。

```
\#pragma omp task [clause]
{
    // code to be executed as a task
}
```

**作用**

- 将任务放入任务队列，并等待线程池中的线程来执行这些任务。任务队列中的任务将会在可用线程上调度执行。
- 每个任务在其所在的线程中独立执行，与其他任务之间没有数据依赖关系，除非通过共享变量显式地传递数据。

**常见用途**

- **递归**：在树结构的遍历中，子问题的解决可以通过并行任务来执行。
- **分治算法**：将一个大问题分解为小任务，使用并行化加速处理。

### 树节点遍历函数

```
\#include <iostream>
\#include <cstdint>
\#include <cmath>
\#include "../include/hpc_helpers.hpp"
\#define VALUE_T double

template <typename value_t>
class Node {
public:
    value_t value_;
    Node<value_t>* left_;
    Node<value_t>* right_;

    Node() {}
    Node(value_t value) : value_(value) {}
    Node(value_t value, Node<value_t>* left, Node<value_t>* right)
        : value_(value), left_(left), right_(right) {}

    // In-order tree traversal, calling func with each value
    void traverse(auto func, bool parallel=false) {
        if (this->left_) {
            \#pragma omp task if (parallel)
            this->left_->traverse(func, parallel);
        }
        func(this->value_);
        if (this->right_) {
            \#pragma omp task if (parallel)
            this->right_->traverse(func, parallel);
        }
    }

    void traverse() {
        traverse([](auto &val) { std::cout << val << std::endl; });
    }
};
```

```

int main() {
    const uint64_t m = 15;
    const uint64_t n = (1UL << m) - 1; // Total number of nodes
    const uint64_t iterations = 1UL << 12; // Number of iterations within each task

    TIMERSTART(overall)
    TIMERSTART(alloc)
    Node<VALUE_T> nodes[n];
    TIMERSTOP(alloc)

    TIMERSTART(init)
    for (uint64_t i = (n-1); i > 0; --i) {
        if (i > ((n / 2) - 1)) {
            nodes[i] = Node<VALUE_T>(i);//叶子节点初始化
        } else {
            nodes[i] = Node<VALUE_T>(i, &nodes[2*i+1], &nodes[2*i+2]);//非叶子节点初始化,需要子节点的指针
        }
    }
    Node<VALUE_T> tree(0, &nodes[1], &nodes[2]);//根节点
    TIMERSTOP(init)

    TIMERSTART(sum)
    VALUE_T sum = 0;
    tree.traverse([&](auto &val) { sum += val; });
    std::cout << (n * (n - 1) / 2) << " ?= " << sum << std::endl;
    TIMERSTOP(sum)

    auto func = [](auto &val) {
        for (uint64_t i = 0; i < iterations; ++i)
            val = std::pow(val, 1.1);
    };

    TIMERSTART(sequential)
    tree.traverse(func);
    TIMERSTOP(sequential)

    TIMERSTART(parallel)
    \#pragma omp parallel
    {
        \#pragma omp single
        {
            \#pragma omp task
            tree.traverse(func, true);
        }
    }
    TIMERSTOP(parallel)
    TIMERSTOP(overall)
}
```

## 循环中生成任务

`#pragma omp taskloop` 是 OpenMP 中的一种指令，用于并行化循环，并将循环迭代分配给不同的任务（task）。它与常规的 OpenMP 循环指令 `#pragma omp for` 的主要区别在于，`taskloop` 会将每个迭代作为独立的任务执行，而不是简单地将迭代分配给线程。

```
\#pragma omp taskloop [clause]
for (int i = 0; i < N; i++) {
    // loop body
}
```

- `**grainsize**`：
    - `grainsize` 用于控制任务的大小（即每个任务应执行的迭代数目）。通过设置合理的 `grainsize`，可以减少任务创建和调度的开销，同时避免任务过小带来的负担。它帮助平衡任务的分配，使得每个任务的工作量接近，并且不会因为任务过小而造成过多的调度开销。
    - **示例**：`#pragma omp taskloop grainsize(5)` 表示每个任务包含 5 个迭代。
- `**num_tasks**`：
    - `num_tasks` 用于指定生成的任务的数量。在某些情况下，你可能希望控制生成的任务的数量而不是迭代的粒度。这有助于更好地管理任务调度，避免任务过多导致性能下降。
    - **示例**：`#pragma omp taskloop num_tasks(100)` 表示创建 100 个任务。

---

# omp中启动simd向量化

```
\#include <iostream>
\#include <cstdint>
\#include <vector>
// hpc_helpers contains the TIMERSTART and TIMERSTOP macros
// and the template that disables implicit type initialization
\#include "../include/hpc_helpers.hpp"

int main() {
    // memory allocation for the three vectors x, y, and z
    // with the no_init_t template as a wrapper for the actual type
    TIMERSTART(alloc)
    const uint64_t num_entries = 1UL << 30; // corrected left-shift operator
    std::vector<no_init_t<uint64_t>> x(num_entries); // corrected type and variable name
    std::vector<no_init_t<uint64_t>> y(num_entries); // corrected type and variable name
    std::vector<no_init_t<uint64_t>> z(num_entries); // corrected type and variable name
    TIMERSTOP(alloc)

    // manually initialize the input vectors x and y
    TIMERSTART(init)
    \#pragma omp parallel for
    for (uint64_t i = 0; i < num_entries; i++) { // corrected loop variable and typo
        x[i] = i;
        y[i] = num_entries - i;
    }
    TIMERSTOP(init)

    // compute x + y = z sequentially
    TIMERSTART(add_seq)
    for (uint64_t i = 0; i < num_entries; i++) {
        z[i] = x[i] + y[i];
    }
    TIMERSTOP(add_seq)

    // compute x + y = z vectorized
    TIMERSTART(add_vec)
    \#pragma omp simd
    for (uint64_t i = 0; i < num_entries; i++) {
        z[i] = x[i] + y[i]; // corrected index and syntax
    }
    TIMERSTOP(add_vec)

    // compute x + y = z in parallel
    TIMERSTART(add_par)
    \#pragma omp parallel for
    for (uint64_t i = 0; i < num_entries; i++) {
        z[i] = x[i] + y[i];
    }
    TIMERSTOP(add_par)

    // compute x + y = z in parallel *and* vectorized
    TIMERSTART(add)
    \#pragma omp parallel for simd
    for (uint64_t i = 0; i < num_entries; i++) {
        z[i] = x[i] + y[i];
    }
    TIMERSTOP(add)

    // check if summation is correct
    TIMERSTART(check)
    \#pragma omp parallel for
    for (uint64_t i = 0; i < num_entries; i++) {
        if (z[i] != num_entries) { // corrected check
            std::cout << "error at position " << i << std::endl;
        }
    }
    TIMERSTOP(check)

    return 0;
}
```

## 数据依赖

**1. 数据依赖的范围必须大于向量化块的大小**：

- 向量化依赖于数据之间的距离，如果两个迭代之间的依赖关系过小（例如相邻的两个元素），则向量化可能无法成功。为了有效地向量化，依赖必须跨越多个数据块，这样才能确保每个数据块可以独立地进行处理。
- 例如，如果你有一个包含许多数据元素的数组，且每个元素的计算需要依赖于距离较远的元素，那么你可以利用这一点来分配数据到向量寄存器中进行并行计算。

可以使用safelen来指定安全长度.`#pragma omp simd safelen(L)`

---

[[并行程序设计]]
