---
jupytext:
  formats: "md:markdown,ipynb/ParaProgramBook/notes/mpi\u57FA\u7840.ipynb:notebook"
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

[[第 9.0 章 MPI入门]]

---

# 什么是MPI？

MPI（Message Passing Interface）是一个标准化的库接口，用于在多进程间实现高效的数据通信。它主要用于分布式计算环境，支持在超级计算机、计算集群或多节点系统上执行并行程序。

### 特点

- **可移植性**：适用于多种硬件和操作系统。
- **高性能**：优化了数据通信，适合高性能计算（HPC）。
- **扩展性**：可以扩展到数千甚至数百万个进程。

### 应用场景

- 数值模拟（如天气预报、分子动力学）
- 大规模科学计算（如矩阵运算）
- 数据密集型计算（如机器学习、基因组学）

## 环境准备

### 安装MPI

在Linux环境中，可以使用以下命令安装MPI库（以OpenMPI或MPICH为例）：

```Shell
sudo apt-get install mpich
# 或
sudo apt-get install openmpi-bin openmpi-common libopenmpi-dev

```

### 验证安装

检查安装是否成功：

```Shell
mpicc --version  # 检查MPI编译器版本
mpirun --version # 检查MPI运行环境版本
```

# Hello MPI

```
\#include <mpi.h>
\#include <stdio.h>

int main(int argc, char *argv[]) {
    int rank, size;

    // 初始化MPI环境
    MPI_Init(&argc, &argv);

    // 获取当前进程的数量
    MPI_Comm_size(MPI_COMM_WORLD, &size);

    // 获取当前进程的编号
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);

    // 每个进程打印自己的编号和总进程数量
    printf("Hello from rank %d of %d\n", rank, size);

    // 结束MPI环境
    MPI_Finalize();
    return 0;
}
```

---

### 编译与运行

1. **编译程序**  
    使用  
    `mpicc`（MPI编译器）编译程序：
    
    ```Shell
    mpicc -o mpi_hello mpi_hello.cpp
    ```
    
2. **运行程序**  
    使用  
    `mpirun`启动程序，指定要运行的进程数量：
    
    ```Shell
    mpirun -np 4 ./mpi_hello
    ```
    
    ```Shell
    mpirun -np 4 mpi_hello
    # 在使用 mpirun -np 4 mpi_hello 时，MPI 在系统的 PATH 环境变量中查找可执行文件 mpi_hello。如果 mpi_hello 不在 PATH 中，或者没有指定其完整路径，MPI 就会找不到它，从而报错
    ```
    

---

## 语法解释

---

- **MPI_Init**
    
    初始化MPI环境，所有MPI程序都必须以此开始。
    
- **MPI_Finalize**
    
    终止MPI环境，所有MPI程序都必须以此结束。
    
- **MPI_Comm_size**
    
    获取总进程数量。
    
    ```C
    MPI_Comm_size(MPI_COMM_WORLD, &size);
    ```
    
    - `MPI_COMM_WORLD`：默认通信域，表示所有进程。
    - `size`：变量，用于存储总进程数量。
- **MPI_Comm_rank**
    
    获取当前进程的编号（Rank）。
    
    ```C
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);
    ```
    
    - `rank`：变量，用于存储当前进程的编号。
- **MPI_COMM_WORLD**
    
    默认通信域，表示所有进程的通信组。
