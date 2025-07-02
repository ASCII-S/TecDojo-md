---
jupytext:
  formats: "md:markdown,ipynb/ParaProgramBook/notes/MPI\u96C6\u4F53\u901A\u4FE1.ipynb:notebook"
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

在并行计算中，集体通信是多个进程之间协同数据交换的重要机制。MPI 提供了多种集体通信操作，用于高效地在所有或部分进程之间分发、收集或规约数据。

---

## 3.1 集体通信的基本概念

- **全体进程参与**：集体通信操作必须由通信域内的所有进程调用。
- **同步或半同步**：集体通信通常会同步参与的进程，确保数据一致性。
- **隐式拓扑结构**：通过通信域组织进程（如树形结构或环形结构）。

---

## 3.2 常见的集体通信操作

1. **广播（**`**MPI_Bcast**`**）**
    
    将数据从一个进程发送到所有其他进程。
    
2. **收集（**`**MPI_Gather**` **和** `**MPI_Allgather**`**）**
    
    从所有进程收集数据到一个或所有进程。
    
3. **分散（**`**MPI_Scatter**`**）**
    
    从一个进程向所有其他进程分发数据。
    
4. **规约（**`**MPI_Reduce**` **和** `**MPI_Allreduce**`**）**
    
    将所有进程的数据进行规约操作（如求和、最大值）。
    
5. **屏障同步（**`**MPI_Barrier**`**）**
    
    使所有进程同步，确保它们在同一时间点继续执行。
    

---

## 3.3 每个操作的函数原型与示例

### 3.3.1 广播（`MPI_Bcast`）

函数原型

```C
int MPI_Bcast(void *buffer, int count, MPI_Datatype datatype, int root, MPI_Comm comm);
```

- **参数**：
    - `buffer`：广播的数据缓冲区（发送或接收）。
    - `count`：数据元素的数量。
    - `datatype`：数据类型（如 `MPI_INT`）。
    - `root`：广播数据的源进程编号。
    - `comm`：通信域。

示例代码

```C
\#include <mpi.h>
\#include <stdio.h>
int main(int argc, char *argv[]) {
    int rank, data = 0;

    MPI_Init(&argc, &argv);
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);

    if (rank == 0) {
        data = 42;  // Root 进程初始化数据
    }

    MPI_Bcast(&data, 1, MPI_INT, 0, MPI_COMM_WORLD);
    printf("Rank %d received data %d\n", rank, data);

    MPI_Finalize();
    return 0;
}

```

---

### 3.3.2 收集（`MPI_Gather`）

函数原型

```C
int MPI_Gather(const void *sendbuf, int sendcount, MPI_Datatype sendtype,
               void *recvbuf, int recvcount, MPI_Datatype recvtype,
               int root, MPI_Comm comm);

```

- **参数**：
    - `sendbuf`：每个进程发送的数据缓冲区。
    - `sendcount`：每个进程发送的数据元素数量。
    - `sendtype`：发送数据类型。
    - `recvbuf`：接收所有数据的缓冲区（仅 root 进程）。
    - `recvcount`：每个进程接收的数据元素数量。
    - `recvtype`：接收数据类型。
    - `root`：接收数据的目标进程编号。
    - `comm`：通信域。

示例代码

```C
\#include <mpi.h>
\#include <stdio.h>
int main(int argc, char *argv[]) {
    int rank, size, send_data, recv_data[4];

    MPI_Init(&argc, &argv);
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);
    MPI_Comm_size(MPI_COMM_WORLD, &size);

    send_data = rank + 1;  // 每个进程的数据

    MPI_Gather(&send_data, 1, MPI_INT, recv_data, 1, MPI_INT, 0, MPI_COMM_WORLD);

    if (rank == 0) {
        printf("Rank 0 gathered data: ");
        for (int i = 0; i < size; i++) {
            printf("%d ", recv_data[i]);
            //MPI_Gather 会将所有 rank 的 send_data 数据汇集到 root 的 recv_data 中。
            //对于非 root 的进程，recv_data 参数在调用 MPI_Gather 时是 无意义的，即使分配了空间，也不会被 MPI 填充任何内容。
        }
        printf("\n");
    }

    MPI_Finalize();
    return 0;
}
```

---

### 3.3.3 规约（`MPI_Reduce`）

函数原型

```C
int MPI_Reduce(const void *sendbuf, void *recvbuf, int count, MPI_Datatype datatype,
               MPI_Op op, int root, MPI_Comm comm);
```

- **参数**：
    - `sendbuf`：每个进程发送的数据缓冲区。
    - `recvbuf`：存储规约结果的缓冲区（仅 root 进程）。
    - `count`：规约的数据元素数量。
    - `datatype`：数据类型。
    - `op`：规约操作（如 `MPI_SUM`）。
        
        |   |   |
        |---|---|
        |操作符|描述|
        |`**MPI_MAX**`|计算所有元素的最大值。|
        |`**MPI_MIN**`|计算所有元素的最小值。|
        |`**MPI_SUM**`|计算所有元素的和。|
        |`**MPI_PROD**`|计算所有元素的积。|
        |`**MPI_LAND**`|按位逻辑与（`AND`）。|
        |`**MPI_BAND**`|按位与（`bitwise AND`）。|
        |`**MPI_LOR**`|按位逻辑或（`OR`）。|
        |`**MPI_BOR**`|按位或（`bitwise OR`）。|
        |`**MPI_LXOR**`|按位逻辑异或（`XOR`）。|
        |`**MPI_BXOR**`|按位异或（`bitwise XOR`）。|
        |`**MPI_MAXLOC**`|查找最大值及其对应的进程 rank。|
        |`**MPI_MINLOC**`|查找最小值及其对应的进程 rank。|
        
    - `root`：接收结果的目标进程编号。
    - `comm`：通信域。

示例代码

```C
\#include <mpi.h>
\#include <stdio.h>
int main(int argc, char *argv[]) {
    int rank, size, send_data, result;

    MPI_Init(&argc, &argv);
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);
    MPI_Comm_size(MPI_COMM_WORLD, &size);

    send_data = rank + 1;  // 每个进程的数据

    MPI_Reduce(&send_data, &result, 1, MPI_INT, MPI_SUM, 0, MPI_COMM_WORLD);

    if (rank == 0) {
        printf("Sum of all ranks: %d\n", result);
    }

    MPI_Finalize();
    return 0;
}
```

---

### 3.3.4 屏障同步（`MPI_Barrier`）

函数原型

```C
int MPI_Barrier(MPI_Comm comm);
```

- **参数**：
    - `comm`：通信域。

示例代码

```C
\#include <mpi.h>
\#include <stdio.h>
\#include <unistd.h> // for sleep
int main(int argc, char *argv[]) {
    int rank;
    int size;
    char recv_msgs[50][50];

    MPI_Init(&argc, &argv);
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);
    MPI_Comm_size(MPI_COMM_WORLD, &size);
    
    //由于直接使用printf的缓冲模式,会导致无法看出进程顺序,因此这里采用将所有进程的输出都归并到0进程中,保证顺序再打印出来
    char msg[50];
    sprintf(msg, "Rank %d reached the barrier.\n", rank);
    MPI_Gather(msg, 50, MPI_CHAR, recv_msgs, 50, MPI_CHAR, 0, MPI_COMM_WORLD);

    if (rank == 0) {
        for (int i = 0; i < size; i++) {
            printf("%s", recv_msgs[i]);
        }
    }
    
    MPI_Barrier(MPI_COMM_WORLD);

    sprintf(msg, "Rank %d passed the barrier.\n", rank);
    MPI_Gather(msg, 50, MPI_CHAR, recv_msgs, 50, MPI_CHAR, 0, MPI_COMM_WORLD);

    if (rank == 0) {
        for (int i = 0; i < size; i++) {
            printf("%s", recv_msgs[i]);
        }
    }


    MPI_Finalize();
    return 0;
}
```

---
