---
jupytext:
  formats: md:markdown,ipynb/ParaProgramBook/09-MPI.ipynb:notebook
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

# 简介

MPI 仅仅是一个接口定义，它已经被不同的开发者在不同的架构上实现。  
MPI 采用 **单程序多数据（SPMD）** 模式，将工作量分成多个任务，由不同的处理器并行执行。每个进程与本地内存关联，通过 `send` 和 `receive` 等通信例程进行数据交换。

- **现代架构的演变**:
    
    随着计算机架构的发展，主流计算集群逐渐转变为混合分布式内存和共享内存系统：
    
    - 每个节点包含多个 CPU 核和一个共享内存模块。
    - 节点之间通过网络连接。
- **混合方法与优化**:
    - 为充分利用现代硬件的计算能力，MPI 在混合架构中被优化：
        - 每个计算节点运行一个 MPI 进程，内部利用多线程或 CUDA 来并行化计算。
    - 这种方法能够有效利用 CPU 核和节点内的加速卡（如 GPU）。

---

# 本书中的mpi接口使用的是C++MPI接口.他是MPI-2标准的一部分, 但是在MPI-3中被废弃.
