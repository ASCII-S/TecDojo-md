---
jupytext:
  formats: "md:markdown,ipynb/ParaProgramBook/notes/c++\u591A\u7EBF\u7A0B\u8BED\u6CD5\
    -\u6765\u6E90\u83DC\u9E1F\u6559\u7A0B.ipynb:notebook"
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

[[第 4 章 c++多线程编程]]

---

## c++组件

- `**std::thread**`：用于创建和管理线程。在 `#include<thread>` 中声明
- `**std::mutex**`：用于线程之间的互斥，防止多个线程同时访问共享资源。
- `**std::lock_guard**` 和 `**std::unique_lock**`：用于管理锁的获取和释放。
- `**std::condition_variable**`：用于线程间的条件变量，协调线程间的等待和通知。
- `**std::future**` 和 `**std::promise**`：用于实现线程间的值传递和任务同步。

## 创建线程

```
\#include<thread>
std::thread thread_object(callable, args...);
```

- `**callable**`：可调用对象，可以是函数指针、函数对象、Lambda 表达式等。
- `**args...**`：传递给 `callable` 的参数列表。

```
g++ -std=c++11 test.cpp 
```

### 使用函数指针

直接传入函数的指针

```
\#include <iostream>
\#include <thread>

void printMessage(int count) {
    for (int i = 0; i < count; ++i) {
        std::cout << "Hello from thread (function pointer)!\n";
    }
}

int main() {
    std::thread t1(printMessage, 5); // 创建线程，传递函数指针和参数
    t1.join(); // 等待线程完成
    return 0;
}
```

### 使用函数对象-类中的函数

通过类中的 `operator()` 方法定义函数对象来创建线程：

[[operator是什么]]

```
\#include <iostream>
\#include <thread>

class PrintTask {
public:
    void operator()(int count) const {
        for (int i = 0; i < count; ++i) {
            std::cout << "Hello from thread (function object)!\n";
        }
    }
};

int main() {
    std::thread t2(PrintTask(), 5); // 创建线程，传递函数对象和参数
    t2.join(); // 等待线程完成
    return 0;
}
```

### 使用lambda表达式

[[lambda表达式是什么]]

[[lambda表达式是什么 2]]

```
\#include <iostream>
\#include <thread>

int main() {
    std::thread t3([](int count) {
        for (int i = 0; i < count; ++i) {
            std::cout << "Hello from thread (lambda)!\n";
        }
    }, 5); // 创建线程，传递 Lambda 表达式和参数
    t3.join(); // 等待线程完成
    return 0;
}
```

---

## 创建线程的传参

### **值传递**

参数可以通过值传递给线程：

```Plain
std::thread t(func, arg1, arg2);
```

### **引用传递**

如果需要传递引用参数，需要使用 `std::ref`：

```
\#include <iostream>
\#include <thread>

void increment(int& x) {
    ++x;
}

int main() {
    int num = 0;
    std::thread t(increment, std::ref(num)); // 使用 std::ref 传递引用
    t.join();
    std::cout << "Value after increment: " << num << std::endl;
    return 0;
}
```

---

## 线程管理

### **join()**

join() 用于等待线程完成执行。如果不调用 join() 或 detach() 而直接销毁线程对象，会导致程序崩溃。

```
t.join();
```

### **detach()**

detach() 将线程与主线程分离，线程在后台独立运行，主线程不再等待它。

```
t.detach();
```

---

## 线程同步与互斥

###  **互斥量（Mutex）**

`**std::mutex**`：用于保护共享资源，防止数据竞争。

```
std::mutex mtx;
mtx.lock();   // 锁定互斥锁
// 访问共享资源
mtx.unlock(); // 释放互斥锁
```

`**std::lock_guard**`和  `**std::unique_lock**`：自动管理锁的获取和释放。

```
std::lock_guard<std::mutex> lock(mtx); // 自动锁定和解锁
// 访问共享资源
```

互斥量的使用示例：

```
\#include <mutex>

std::mutex mtx; // 全局互斥量

void safeFunction() {
    mtx.lock(); // 请求锁定互斥量
    // 访问或修改共享资源
    mtx.unlock(); // 释放互斥量
}

int main() {
    std::thread t1(safeFunction);
    std::thread t2(safeFunction);
    t1.join();
    t2.join();
    return 0;
}
```

### **锁（Locks）**

常见的锁类型包括：

- `std::lock_guard`：作用域锁，当构造时自动锁定互斥量，当析构时自动解锁。
- `std::unique_lock`：与std::lock_guard类似，但提供了更多的灵活性，例如可以转移所有权和手动解锁。

### **条件变量（Condition Variable）**

条件变量用于线程间的协调，允许一个或多个线程等待某个条件的发生。它通常与互斥量一起使用，以实现线程间的同步。

`**std::condition_variable**`用于实现线程间的等待和通知机制。

条件变量的使用示例

```
\#include <mutex>
\#include <condition_variable>

std::mutex mtx;
std::condition_variable cv;
bool ready = false;

void workerThread() {
    std::unique_lock<std::mutex> lk(mtx);
    cv.wait(lk, []{ return ready; }); // 等待条件
    // 当条件满足时执行工作
}

void mainThread() {
    {
        std::lock_guard<std::mutex> lk(mtx);
        // 准备数据
        ready = true;
    } // 离开作用域时解锁
    cv.notify_one(); // 通知一个等待的线程
}
```

### **原子操作（Atomic Operations）**

原子操作的使用示例：

```
\#include <atomic>
\#include <thread>

std::atomic<int> count(0);

void increment() {
    count.fetch_add(1, std::memory_order_relaxed);
}

int main() {
    std::thread t1(increment);
    std::thread t2(increment);
    t1.join();
    t2.join();
    return count; // 应返回2
}
```

- `**fetch_add(1, std::memory_order_relaxed)**` 是一个原子操作，执行以下操作：
    - **增加** `**count**` 的值，增加的数量是 `**1**`。
    - **返回** 操作之前的值。
    - **内存序** 参数 `**std::memory_order_relaxed**` 表示对内存的访问没有顺序约束，即不需要保证其他线程对 `**count**` 的操作会在这个操作之前或之后发生。

### **线程局部存储（Thread Local Storage, TLS）**

线程局部存储允许每个线程拥有自己的数据副本。这可以通过thread_local关键字实现，避免了对共享资源的争用。

线程局部存储的使用示例：

```
\#include <iostream>
\#include <thread>

thread_local int threadData = 0;

void threadFunction() {
    threadData = 42; // 每个线程都有自己的threadData副本
    std::cout << "Thread data: " << threadData << std::endl;
}

int main() {
    std::thread t1(threadFunction);
    std::thread t2(threadFunction);
    t1.join();
    t2.join();
    return 0;
}
```

---

[[第 4 章 c++多线程编程]]
