---
jupytext:
  formats: "md:markdown,ipynb/ParaProgramBook/notes/MPI \u6570\u636E\u5206\u6563\u4E0E\
    \u5168\u5C40\u64CD\u4F5C.ipynb:notebook"
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

# 4.1 数据分散（`MPI_Scatter`）

### 功能

`MPI_Scatter` 将一个进程的数据缓冲区分割，并分发给所有进程（包括自身）。

### 函数原型

```C
int MPI_Scatter(const void *sendbuf, int sendcount, MPI_Datatype sendtype,
                void *recvbuf, int recvcount, MPI_Datatype recvtype,
                int root, MPI_Comm comm);
```

### 参数详解

- `**sendbuf**`：发送缓冲区，包含要分发的数据（仅 root 进程提供）。
- `**sendcount**`：发送给每个进程的数据数量。
- `**sendtype**`：发送数据的类型。
- `**recvbuf**`：接收缓冲区，用于存储接收到的数据。
- `**recvcount**`：每个进程接收的元素数量。
- `**recvtype**`：接收数据的类型。
- `**root**`：分发数据的源进程编号。
- `**comm**`：通信域。

---

### 示例代码：分发数组元素到各个进程

以下代码将数组分发给每个进程，每个进程接收数组中的一个元素：

```C
\#include <mpi.h>
\#include <stdio.h>
int main(int argc, char *argv[]) {
    int rank, size;
    int send_data[4] = {10, 20, 30, 40}; // Root 进程的数据
    int recv_data; // 每个进程接收的单个元素

    MPI_Init(&argc, &argv);
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);
    MPI_Comm_size(MPI_COMM_WORLD, &size);

    // 使用 MPI_Scatter 分发数据
    MPI_Scatter(send_data, 1, MPI_INT, &recv_data, 1, MPI_INT, 0, MPI_COMM_WORLD);

    printf("Rank %d received data: %d\n", rank, recv_data);

    MPI_Finalize();
    return 0;
}
```

### 示例练习:结和MPI_Gather计算平均数

核心思想: 计算n个数的平均数,可以将n个数用MPI_scatter分发给p个进程, 这p个进程计算n/p个数的平均数, 结果用MPI_Gather交还给根进程继续计算各部分的平均数

---

# 4.2 全局收集（`MPI_Allgather`）

### 功能

`MPI_Allgather` 从所有进程收集数据，并将所有收集到的数据分发到每个进程。

### 函数原型

```C
int MPI_Allgather(const void *sendbuf, int sendcount, MPI_Datatype sendtype,
                  void *recvbuf, int recvcount, MPI_Datatype recvtype,
                  MPI_Comm comm);
```

### 参数详解

- `**sendbuf**`: 指向发送缓冲区的指针，包含当前进程要共享的数据。
- `**sendcount**`: 每个进程发送的数据元素数量。
- `**sendtype**`: 发送数据的类型（如 `MPI_INT`）。
- `**recvbuf**`: 指向接收缓冲区的指针，用于存储所有进程发送的数据。
- `**recvcount**`: 每个进程接收到的数据元素数量（应等于 `sendcount`）。
- `**recvtype**`: 接收数据的类型（如 `MPI_INT`）。
- `**comm**`: 通信域（通常为 `MPI_COMM_WORLD`）。

### 示例代码：所有进程共享数据

以下代码让每个进程共享其排名（`rank`）：

```C
\#include <mpi.h>
\#include <stdio.h>
int main(int argc, char *argv[]) {
    int rank, size;
    int send_data, recv_data[4];

    MPI_Init(&argc, &argv);
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);
    MPI_Comm_size(MPI_COMM_WORLD, &size);

    send_data = rank; // 每个进程发送自己的 rank

    // 使用 MPI_Allgather 收集所有进程的数据
    MPI_Allgather(&send_data, 1, MPI_INT, recv_data, 1, MPI_INT, MPI_COMM_WORLD);

    printf("Rank %d received: ", rank);
    for (int i = 0; i < size; i++) {
        printf("%d ", recv_data[i]);
    }
    printf("\n");

    MPI_Finalize();
    return 0;
}
```

---

# 4.3 全局规约（`MPI_Allreduce`）

### 功能

`MPI_Allreduce` 对所有进程的数据进行规约操作，并将结果广播到所有进程。

### 函数原型

```C
int MPI_Allreduce(const void *sendbuf, void *recvbuf, int count, MPI_Datatype datatype,
                  MPI_Op op, MPI_Comm comm);
```

### 参数详解

- `**sendbuf**`: 指向发送缓冲区的指针，包含当前进程参与规约的数据。
- `**recvbuf**`: 指向接收缓冲区的指针，用于存储规约操作的结果（在所有进程中均相同）。
- `**count**`: 要参与规约操作的数据元素数量。
- `**datatype**`: 数据类型（如 `MPI_INT`, `MPI_FLOAT`）。
- `**op**`: 规约操作符（如 `MPI_SUM`, `MPI_MAX`，可自定义操作）。
- `**comm**`: 通信域（通常为 `MPI_COMM_WORLD`）。

### 示例代码：计算所有进程的和

以下代码让所有进程计算其 `rank` 的和，并共享结果：

```C
\#include <mpi.h>
\#include <stdio.h>
int main(int argc, char *argv[]) {
    int rank, size, sum;

    MPI_Init(&argc, &argv);
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);
    MPI_Comm_size(MPI_COMM_WORLD, &size);

    // 每个进程发送自己的 rank
    MPI_Allreduce(&rank, &sum, 1, MPI_INT, MPI_SUM, MPI_COMM_WORLD);

    printf("Rank %d calculated total sum: %d\n", rank, sum);

    MPI_Finalize();
    return 0;
}
```
