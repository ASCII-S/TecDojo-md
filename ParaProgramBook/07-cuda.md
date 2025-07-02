---
jupytext:
  formats: md:markdown,ipynb/ParaProgramBook/07-cuda.ipynb:notebook
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

# helloworld

```
\#include <stdio.h> // printf
\#include <cuda_runtime.h> // CUDA runtime API

__global__ void hello_kernel() {
    // calculate global thread identifier
    const int thid = blockIdx.x * blockDim.x + threadIdx.x;
    
    // print a greeting message
    printf("Hello from thread %d\n", thid);
}

int main(int argc, char *argv[]) {
    // set the ID of the CUDA device
    cudaSetDevice(0); // Using the first device (device 0)
    
    // invoke kernel using 4 threads executed in 1 thread block
    hello_kernel<<<1, 4>>>();
    
    // synchronize the GPU preventing premature termination
    cudaDeviceSynchronize();
    
    return 0;
}
```

---

## `cudaSetDevice()`

在多 GPU 系统中，指定当前主机线程使用的 GPU 设备。

- 将 CUDA API 调用绑定到指定的 GPU 上。

```
cudaError_t cudaSetDevice(int device);
```

- `device`: 指定 GPU 的设备编号（从 `0` 开始）。

**返回值**

- `cudaSuccess`: 成功绑定设备。
- 其他错误码:
    - `cudaErrorInvalidDevice`: 请求的设备编号无效或设备不可用。

---

## 启动内核函数

这是一段 CUDA 程序中用来启动**内核函数**（kernel）的代码。它使用了 CUDA 的 **执行配置语法** `<<<GridSize, BlockSize>>>` 来定义内核函数的执行参数。

### **线程的组织结构**

CUDA 将线程组织为二维结构：

1. **Grid（网格）**：
    - 包含多个线程块（Block）。
    - `<<<GridSize, BlockSize>>>` 中的 `GridSize` 定义了 Grid 中 Block 的数量。
2. **Block（线程块）**：
    - 包含多个线程。
    - `BlockSize` 定义了每个 Block 中的线程数量。

### `hello_kernel<<<1, 4>>>();`

- `**hello_kernel**`:
    - 这是一个用 `__global__` 修饰的内核函数，运行在 GPU 上。
    - 由 CUDA 设备执行，主机代码通过调用它来启动 GPU 上的计算。
- `**<<<1, 4>>>**`:
    - CUDA 的**执行配置语法**，用于定义 GPU 上线程的组织结构。
    - `1` 表示 **Grid 的大小**，即网格中有 1 个 Block。
    - `4` 表示 **Block 的大小**，即每个 Block 有 4 个线程。

---

## 限定符

---

`__global__`

- **功能**：声明一个核函数（kernel），该函数在设备（GPU）上执行。
- **调用方式**：可以从主机（host）调用
- **返回类型**：必须是 `void`，不能有返回值。
- **限制**：不能是类成员函数。
- **执行配置**：调用 `__global__` 函数时，必须指定执行配置（如线程块数、每个线程块的线程数等）。
- **异步执行**：函数调用是异步的，意味着它会立即返回，而设备的执行会在后台继续进行。

---

`__device__`

- **作用**：`__device__` 限定符表示该函数仅能在 **GPU 设备代码** 中调用，即只能在 GPU 上执行，且不能直接从主机（CPU）代码中调用。
- **使用场景**：用于在设备代码中执行的函数，如设备内的计算，或者从其他设备函数调用。
- **语法示例**：

```
__device__ void deviceFunction() {
    // 设备上的代码，不能从主机调用
}
```

---

`__host__`

- **作用**：`__host__` 限定符表示该函数仅能在 **主机代码** 中调用，即只能在 CPU 上执行，不能在 GPU 上执行。
- **使用场景**：用于 CPU 上执行的函数，用来执行不需要 GPU 的部分。

---

`__noinline__` 和 `__forceinline__`

- `**__noinline__**`：建议编译器不要内联此函数。
- `**__forceinline__**`：强制编译器内联此函数。

---

`__inline_hint__`

- **功能**：允许编译器进行更积极的内联优化。与 `__forceinline__` 不同，`__inline_hint__` 只是提示编译器优化内联，而不强制编译器一定要内联该函数。
- **用途**：在使用链接时间优化（LTO）时，可以提高跨模块的内联效率。
- **冲突**：不能与 `__noinline__` 或 `__forceinline__` 一起使用

---

## 线程标识计算

- `**blockIdx.x**`: 当前线程**所在 Block** 在总block中的索引（在此例中为 0，因为只有 1 个 Block）。
- `**threadIdx.x**`: 当前**线程**在 Block 中的索引（范围为 `0, 1, 2, 3`）。
- `**blockDim.x**`: 每个 Block 中的线程数（在此例中为 4）。

---

## **主机（Host）和设备（Device）同步**`**cudaDeviceSynchronize()**`

`cudaDeviceSynchronize()` 确保设备上所有的任务都完成后，主机才继续执行后续代码。

- **主机和设备的执行模型**：
    - CUDA 内核函数调用和许多设备操作（如数据拷贝）是 **异步** 的。
    - 异步执行意味着主机代码会立即返回，不等待设备上的操作完成。
- **同步作用**：
    - 调用 `cudaDeviceSynchronize()` 后，主机会暂停，直到设备完成所有之前启动的操作，包括：
        - 所有内核函数的执行。
        - 所有异步内存拷贝操作。

```
cudaError_t cudaDeviceSynchronize(void);
```

- **返回值**：
    - 如果所有设备操作成功完成，则返回 `cudaSuccess`。
    - 如果设备上存在错误（例如非法内存访问），则返回对应的错误代码。

---

## 序列化执行

在 CUDA 中，线程的执行和打印顺序与传统的多线程程序不同，原因在于 **Warp** 的执行机制和 **线程块的组织结构**。

### **1. Warp 的概念**

- 在 CUDA 中，**一个线程块中的线程被分组为 Warps**，每个 Warp 包含 32 个连续的线程。
- **Warp 的执行方式**：
    - Warp 是 GPU 执行的基本单位。
    - 一个 Warp 中的所有线程通常是 **SIMD（单指令多数据）** 模式下并行执行。
    - 对于某些操作（如 I/O 操作），Warp 中线程的行为可能会被序列化。

### **2. 线程 Warp 的序列化规则**

- 当一个线程块中的线程调用 `printf` 时：
    1. **一个 Warp 内部**的所有线程的打印操作被 **序列化**，逐个打印。
    2. **不同的 Warp** 的打印顺序可能没有严格的时间先后顺序，具体由硬件调度决定。

---

# 硬件架构

## 主机和设备的互连

### **1. 主机与设备的定义**

- **主机（Host）：** 通常指运行 CPU 的主机系统，负责程序的控制和管理。
- **设备（Device）：** 通常指 GPU，它负责并行执行高性能计算任务。

---

### **2. 主机与设备之间的互连模型**

主机和设备通过高速互连总线（如 PCIe,NVlink）进行通信。互连模型分为以下几个方面：

- **内存模型：**
    - 主机内存（Host Memory）：由 CPU 管理的系统内存。
    - 设备内存（Device Memory）：由 GPU 管理的显存(VRAM)。
    - 主机和设备各自拥有独立的内存地址空间。
- **数据传输模型：**
    - 数据从主机内存传输到设备内存。
    - 计算结果从设备内存传回主机内存。
    - 数据传输是通过 PCIe 总线完成的，速度远低于 GPU 内部存储的带宽。
- **执行模型：**
    - 主机负责管理设备，启动设备上的内核（kernel）。
    - 设备执行内核，处理数据并返回结果。

---

## 计算资源的组织

### **编程中的层次结构**

- **线程（Thread）**
    - **基本单位：** 每个线程是 CUDA 程序的执行单元，执行指定的内核函数（Kernel）。
    - **独立性：** 每个线程有自己的程序计数器、寄存器和本地内存。
    - **线程索引：** 线程通过其 ID 确定工作任务，可以使用 `threadIdx` 访问。
- **线程块（Thread Block）**
    - **线程的集合：** 一个线程块包含若干线程，线程数由开发者定义。
    - **共享内存：** 同一线程块内的线程可以通过共享内存（Shared Memory）通信。
    - **同步：** 可以使用 `__syncthreads()` 在块内实现线程同步。
    - **线程块维度：** 可以是 1D、2D 或 3D（如 `dim3(16, 16)` 表示二维块）。
    - **索引：** 线程块通过 `blockIdx` 和 `blockDim` 访问线程块和线程的维度和索引。
- **网格（Grid）**
    - **线程块的集合：** 一个网格由多个线程块组成，用于分配更大规模的任务。
    - **无共享内存：** 网格中的线程块不能直接通信。
    - **网格维度：** 可以是 1D、2D 或 3D（如 `dim3(32, 32)` 表示二维网格）。
    - **索引：** 网格中线程块通过 `gridDim` 和 `blockIdx` 获取维度和索引。

### `dim3` 结构体

- `dim3` 是 CUDA 提供的一个数据类型，用于表示最多 **三维** 的线程块（block）或线程网格（grid）的大小。
- **grid_dim**：定义网格（grid）的大小。
    - `dim3 grid_dim(1, 2, 3);`
- **block_dim**：定义每个线程块（block）的大小。
    - `dim3 block_dim(4, 5, 6);`
- 每个线程的唯一标识符
    
    ```C++
    int thread_id =
    threadIdx.x 
    + threadIdx.y * (blockDim.x) 
    + threadIdx.z * blockDim.x * (blockDim.y)
    + blockIdx.x * blockDim.x * blockDim.y * (blockDim.z)
    + blockIdx.y * (gridDim.x) * blockDim.x * blockDim.y * blockDim.z
    + blockIdx.z * gridDim.x * (gridDim.y) * blockDim.x * blockDim.y * blockDim.z;
    ```
    
- **定义网格和线程块维度**：
    - `kernel<<<grid_dim, block_dim>>>();`

```
\#include <stdio.h> // printf

// CUDA kernel function
__global__ void kernel() {
    // Print grid and block dimensions and identifiers
    printf(
        "Hello from thread (%d %d %d) "
        "in a block of dimension (%d %d %d) "
        "with block identifier (%d %d %d) "
        "spawned in a grid of shape (%d %d %d)\n",
        threadIdx.x, threadIdx.y, threadIdx.z,
        blockDim.x, blockDim.y, blockDim.z,
        blockIdx.x, blockIdx.y, blockIdx.z,
        gridDim.x, gridDim.y, gridDim.z
    );
}

int main(int argc, char *argv[]) {
    // Set the ID of the CUDA device
    cudaSetDevice(0);

    // Define a grid of 1 * 2 * 3 = 6 blocks
    // Each block contains 4 * 5 * 6 = 120 threads
    // Altogether 720 threads
    dim3 grid_dim(1, 2, 3);
    dim3 block_dim(4, 5, 6);

    // Invoke the kernel
    kernel<<<grid_dim, block_dim>>>();

    // Synchronize the GPU, preventing premature termination
    cudaDeviceSynchronize();

    return 0;
}
```

---

### 计算资源的组织与映射

- **线程块与 SM：**
    - 每个线程块分配到一个 SM 中执行。
    - 一个 SM 可以同时容纳多个线程块（具体数量取决于硬件限制，例如寄存器和共享内存的需求）。
- **线程与 CUDA 核心：**
    - SM 内部的 CUDA 核心负责执行线程块中的线程。
    - 每个线程分配到一个 CUDA 核心。

---

# 内存访问模式

---

主成分分析 (Principal Component Analysis, PCA ) 是一种线性降维技术，用于通过投影将数据从高维空间映射到低维空间，同时保留尽可能多的信息。在 CUDA 上实现 PCA，可以利用 GPU 的并行计算能力，加速计算过程。

**PCA 工作原理**

1. **数据标准化**：将数据集中的每一列（特征）中心化，使其均值为 0。
2. **协方差矩阵计算**：计算标准化数据的协方差矩阵，用来表示特征之间的相关性。
3. **特征值分解**：对协方差矩阵进行特征值分解，获取主成分（特征向量）和对应的权重（特征值）。
4. **降维**：根据特征值大小选择前 k 个主成分，并将原始数据投影到这些主成分上。

---

## CelebA 数据集

下面以celeba数据集来进行PCA的步骤实现.

CelebA 数据集中的 202 599 张图像，以形如 178 x 218 的 RGB 值矩阵格式存储。

**预处理,压缩数据集:**

- 颜色通道合并成灰度
    - 颜色信息对某些任务（如人脸识别或分类）可能并不重要，而亮度信息通常包含了图像的主要结构和特征。
- 因子为4的下采样
    - 下采样保留了图像的整体结构，适合 PCA 等方法提取全局特征。

---

## 内存管理语句

### `cudaDeviceReset()`

`cudaDeviceReset()` 是 CUDA 提供的一个函数，用于重置当前 GPU 设备的状态。其主要功能如下：

1. **释放设备内存**:
    - 释放由 CUDA 分配的设备内存（通过 `cudaMalloc` 分配的内存）。
    - 释放设备上的所有资源，例如上下文、内存和流等。
2. **清理 CUDA 上下文**:
    - 清理 CUDA 上的运行状态（如已分配的流、事件和内存）。
    - 结束与当前线程关联的 CUDA 上下文。
3. **恢复设备到初始状态**:
    - 将 GPU 恢复到程序启动时的默认状态。

---

### `**cudaMallocHost()**`

`cudaMallocHost()` 是 CUDA 提供的一种函数，用于在主机端（CPU）分配可固定内存（pinned memory）。这种内存的特点是：

1. **固定（Pinned）**: 分配的内存不会被操作系统分页调度，从而提高主机和设备之间的数据传输效率。
2. **高效的数据传输**: 固定内存允许主机和设备之间使用异步内存复制（如 `cudaMemcpyAsync`），从而提高数据传输速度。
3. **直接访问（Zero-Copy）**: 在某些硬件支持下，设备可以直接访问主机内存。

```
cudaError_t cudaMallocHost(void **ptr, size_t size);
```

- `**ptr**`: 指向分配的固定内存的指针地址。
- `**size**`: 要分配的字节数。
- **返回值**: `cudaSuccess` 表示成功；否则返回错误码。

---

### `**cudaMalloc()**`

`cudaMalloc()` 是 CUDA 中用于在设备端（GPU）分配内存的函数。分配的内存位于设备的全局内存空间，可以在设备端使用。这种内存通常用于在主机（CPU）和设备（GPU）之间传输数据，或者在核函数中处理计算。

```
cudaError_t cudaMalloc(void **devPtr, size_t size);
```

- `**devPtr**`: 指向设备内存的指针地址。`cudaMalloc` 会将分配的设备内存地址存储到该指针中。
- `**size**`: 要分配的内存字节数。
- **返回值**:
    - `cudaSuccess` 表示分配成功。
    - 返回其他错误码（如 `cudaErrorMemoryAllocation`），表示分配失败。

---

### `**cudaMemcpy**`

`cudaMemcpy` 是 CUDA 中用于在主机（CPU）和设备（GPU）之间，或者设备内存之间复制数据的函数。它的作用是将数据从一个位置复制到另一个位置。

```
cudaError_t cudaMemcpy(void* dst, const void* src, size_t count, cudaMemcpyKind kind);
```

- `**dst**`: 目标地址，数据将被复制到此位置。
- `**src**`: 源地址，数据将从此位置复制。
- `**count**`: 数据大小（以字节为单位）。
- `**kind**`: 指定内存复制的方向，有以下几种类型：
    - `**cudaMemcpyHostToDevice**`: 从主机内存复制到设备内存。
    - `**cudaMemcpyDeviceToHost**`: 从设备内存复制到主机内存。
    - `**cudaMemcpyDeviceToDevice**`: 在设备内存之间复制。
    - `**cudaMemcpyHostToHost**`: 在主机内存之间复制。
- **返回值**:
    - 返回 `cudaSuccess` 表示复制成功。
    - 如果出错，会返回相应的错误码。

---

### `**SDIV(rows * cols, 1024)**`

`SDIV` 是一种安全整数除法（Safe Division）的宏或函数，用于计算 `rows * cols` 被 `1024` 整除后的商，通常用于 CUDA 中确定网格配置时的块数（blocks）计算。

```
\#define SDIV(a, b) (((a) + (b) - 1) / (b))
```

在 CUDA 程序中，`SDIV(rows * cols, 1024)` 用于确定网格（grid）的尺寸。

1. **参数解释**:
    - `rows * cols`: 总的计算任务数（如总像素数或矩阵总元素数）。
    - `1024`: 每个线程块中线程的数量（通常为 CUDA 的最大线程数 `1024`）。
    - `SDIV(rows * cols, 1024)`: 计算网格需要的块数量。

---

### `**cudaFreeHost(pinnedMemory)**`

- **释放通过** `**cudaMallocHost**` **分配的主机内存**。
- `cudaMallocHost` 分配的内存是页锁定（pinned）内存，这种内存用于优化主机和设备之间的数据传输。释放该内存后，不再可以使用指针 `pinnedMemory`，否则会导致未定义行为。
- 当你不再需要之前通过 `cudaMallocHost` 分配的主机内存时，使用 `cudaFreeHost` 释放它，避免内存泄漏。

---

### `**cudaFree(Data)**`

- **释放通过** `**cudaMalloc**` **分配的设备内存**。
- `cudaMalloc` 分配的内存位于设备端（GPU），是 CUDA 程序中主要用于数据存储的内存空间。调用 `cudaFree` 后，内存将被释放并归还给 CUDA 内存管理器。
- 当程序完成设备上的计算，或者该设备内存已经不再需要时，调用 `cudaFree` 释放内存，避免设备内存耗尽。

---

### `**CUERR**`

`CUERR` 是一个宏，用于检查 CUDA 调用是否成功。它通常定义为：

```
cpp
复制代码
\#define CUERR { \
    cudaError_t err = cudaGetLastError(); \
    if (err != cudaSuccess) { \
        std::cerr << "CUDA error: " << cudaGetErrorString(err) \
                  << " at " << __FILE__ << ":" << __LINE__ << std::endl; \
        exit(err); \
    } \
}

```

它的作用是确保调用 `cudaFreeHost` 或 `cudaFree` 等 CUDA API 时没有发生错误。

---
