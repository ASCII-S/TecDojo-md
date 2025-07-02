---
jupytext:
  formats: "md:markdown,ipynb/ParaProgramBook/notes/\u5177\u4F53\u4EE3\u7801-gpt.ipynb:notebook"
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

# threadpool.hpp

```
\#ifndef THREADPOOL_HPP
\#define THREADPOOL_HPP

\#include <iostream>
\#include <thread>
\#include <functional>
\#include <vector>
\#include <queue>
\#include <future>
\#include <mutex>
\#include <condition_variable>

// 线程池类定义
class ThreadPool {
public:
    // 构造函数：创建指定数量的线程
    ThreadPool(size_t num_threads);
    
    // 析构函数：停止线程池并等待线程完成
    ~ThreadPool();
    
    // 向线程池中添加任务
    template <typename F, typename... Args>
    auto enqueue(F&& f, Args&&... args) -> std::future<typename std::result_of<F(Args...)>::type>;

private:
    // 线程池中的工作线程
    void worker();

    // 任务队列
    std::queue<std::function<void()>> tasks;

    // 线程池的线程
    std::vector<std::thread> workers;

    // 互斥锁，确保任务队列线程安全
    std::mutex queue_mutex;
    
    // 条件变量，用于通知线程任务已就绪
    std::condition_variable condition;

    // 停止线程标志
    bool stop;
};

// 构造函数：初始化线程池，创建工作线程
ThreadPool::ThreadPool(size_t num_threads) : stop(false) {
    for (size_t i = 0; i < num_threads; ++i) {
        workers.emplace_back(&ThreadPool::worker, this);
    }
}

// 析构函数：停止线程池
ThreadPool::~ThreadPool() {
    {
        std::unique_lock<std::mutex> lock(queue_mutex);
        stop = true;
    }
    condition.notify_all(); // 通知所有线程停止
    for (auto& worker : workers) {
        worker.join(); // 等待所有线程完成
    }
}

// 向线程池添加任务
template <typename F, typename... Args>
auto ThreadPool::enqueue(F&& f, Args&&... args) -> std::future<typename std::result_of<F(Args...)>::type> {
    using return_type = typename std::result_of<F(Args...)>::type;

    auto task = std::make_shared<std::packaged_task<return_type()>>(std::bind(std::forward<F>(f), std::forward<Args>(args)...));

    std::future<return_type> res = task->get_future();

    {
        std::unique_lock<std::mutex> lock(queue_mutex);
        if (stop) {
            throw std::runtime_error("enqueue on stopped ThreadPool");
        }
        tasks.emplace([task]() { (*task)(); }); // 将任务加入队列
    }

    condition.notify_one(); // 通知线程处理任务
    return res;
}

// 工作线程的执行函数
void ThreadPool::worker() {
    while (true) {
        std::function<void()> task;

        {
            std::unique_lock<std::mutex> lock(queue_mutex);
            condition.wait(lock, [this]() { return !tasks.empty() || stop; });

            if (stop && tasks.empty()) {
                return;
            }

            task = std::move(tasks.front());
            tasks.pop();
        }

        task(); // 执行任务
    }
}

\#endif // THREADPOOL_HPP
```

## 解析代码

## auto enqueue(F&& f, Args&&... args) -> std::future<typename std::result_of<F(Args...)>::type>;

这是一个函数模板，带有右值引用（`**&&**`）和参数包（`**...**`），并且使用了 C++14 引入的尾返回类型（Trailing Return Type）语法。以下是逐部分的解释：

### **1.** `**F&& f**` **和** `**Args&&... args**`

- `**F&&**` 是对函数类型 `**F**` 的右值引用，这使得 `**enqueue**` 可以接受任意类型的可调用对象（如普通函数、lambda 表达式、函数对象等）。
- `**Args&&... args**` 是一个参数包，表示任意数量的参数（包括它们的类型），可以传递给函数 `**f**`。这里的 `**Args**` 是函数 `**f**` 需要的参数类型。

通过这种方式，`**enqueue**` 可以接受任何类型的可调用对象（例如，函数指针、lambda 表达式等）以及该可调用对象所需的任意参数。

### **2.** `**typename std::result_of<F(Args...)>::type**`

- `**std::result_of<F(Args...)>**` 用于获取函数 `**F**` 调用后返回的类型。`**F(Args...)**` 是指调用 `**F**` 并传入 `**Args...**` 类型的参数。
- `**std::result_of<F(Args...)>::type**` 就是上面类型的定义，它表示返回类型。

换句话说，`**std::result_of<F(Args...)>::type**` 用来推导函数 `**f**` 执行后的返回值类型。

### **3.** `**std::future<typename std::result_of<F(Args...)>::type>**`

- `**std::future**` 是一个模板类，它代表一个异步操作的结果，可以在将来某个时刻获取该结果。
- 由于 `**std::result_of<F(Args...)>::type**` 是 `**f**` 返回的类型，因此 `**enqueue**` 函数会返回一个 `**std::future**`，用于获取 `**f**` 执行后的结果。

### **4.** `**auto**` **和尾返回类型**

- `**auto**` 表示返回类型会由编译器推导出来，具体类型是 `**std::future<typename std::result_of<F(Args...)>::type>**`。
- 尾返回类型（Trailing Return Type）语法使得我们可以在 `**auto**` 后面指定返回类型。这里的尾返回类型语法用于指定返回 `**std::future**` 类型，它是 `**f**` 执行后返回类型的包装。

## std::queue<std::function<void()>> tasks;

### **1.** `**std::queue**`

`**std::queue**` 是一个 C++ 标准库容器适配器，基于双端队列（`**std::deque**`）或列表（`**std::list**`）来实现队列数据结构。它遵循先进先出（FIFO，First-In-First-Out）原则。

### **2.** `**std::function<void()>**`

`**std::function<void()>**` 是 C++ 标准库中的一个模板类，它封装了可以调用的对象。`**std::function**` 提供了一个类型擦除机制，使得你可以存储任意类型的可调用对象（如普通函数、lambda 表达式、函数指针、成员函数指针、仿函数等）作为一个统一的接口。

- `**void()**` 表示无返回值、无参数的函数类型（即返回类型为 `**void**`，参数为空）。

### **3.** `**tasks**`

`**tasks**` 是一个 `**std::queue<std::function<void()>>**` 类型的变量，表示一个任务队列。这个队列中的每个元素都是一个无参数、无返回值的可调用对象（即 `**std::function<void()>**`）。任务可以是任何函数或可调用对象，只要它符合 `**void()**` 类型。

- **作用**：`**tasks**` 队列的作用通常是用于存储待执行的任务。这些任务可以是函数、lambda 表达式或其他可调用对象，队列的处理流程是一个线程或多个线程从队列中取出任务并执行。
