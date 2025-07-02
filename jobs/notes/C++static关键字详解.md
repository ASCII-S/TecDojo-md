# C++static关键字详解

static关键字在C++中有多种用法，主要用于控制变量/函数的生命周期、作用域和链接属性。

## static的基本用法

### 1. 类内static成员
static成员属于类本身，不属于某个特定对象，所有对象共享一份。

```cpp
class Counter {
private:
    static int count;  // 静态成员变量声明
    int id;
    
public:
    Counter() : id(++count) {
        std::cout << "Creating object " << id << ", total: " << count << std::endl;
    }
    
    ~Counter() {
        --count;
        std::cout << "Destroying object " << id << ", remaining: " << count << std::endl;
    }
    
    static int getCount() {  // 静态成员函数
        return count;
        // 注意：静态成员函数不能访问非静态成员
        // return id;  // 错误：无法访问非静态成员
    }
    
    void printInfo() {
        std::cout << "Object " << id << " of " << count << " total" << std::endl;
    }
};

// 静态成员变量定义（必须在类外定义）
int Counter::count = 0;

void test_static_members() {
    Counter obj1;  // count = 1
    Counter obj2;  // count = 2
    
    std::cout << "Total objects: " << Counter::getCount() << std::endl;  // 通过类名调用
    std::cout << "Total objects: " << obj1.getCount() << std::endl;      // 也可通过对象调用
}
```

### 2. 函数内static变量
函数内的static变量只初始化一次，生命周期贯穿程序始终。

```cpp
int generateID() {
    static int id = 0;  // 只在第一次调用时初始化
    return ++id;
}

void test_static_local() {
    std::cout << "ID 1: " << generateID() << std::endl;  // 1
    std::cout << "ID 2: " << generateID() << std::endl;  // 2
    std::cout << "ID 3: " << generateID() << std::endl;  // 3
}

// 线程安全的单例模式（C++11及以后）
class Singleton {
public:
    static Singleton& getInstance() {
        static Singleton instance;  // C++11保证线程安全
        return instance;
    }
    
    void doSomething() {
        std::cout << "Singleton doing something..." << std::endl;
    }
    
private:
    Singleton() = default;
    Singleton(const Singleton&) = delete;
    Singleton& operator=(const Singleton&) = delete;
};
```

### 3. 文件作用域static
限制变量/函数只在当前编译单元（文件）可见。

```cpp
// file1.cpp
static int internal_counter = 0;  // 内部链接，只在本文件可见

static void internal_function() {  // 内部链接函数
    ++internal_counter;
    std::cout << "Internal counter: " << internal_counter << std::endl;
}

void public_function() {  // 外部链接，其他文件可见
    internal_function();
}

// file2.cpp
// static int internal_counter = 100;  // 可以，这是不同的变量
// void internal_function();  // 错误：无法访问file1.cpp中的静态函数
extern void public_function();  // 可以访问file1.cpp中的公共函数
```

## static成员深入分析

### static成员的内存布局
```cpp
class MemoryAnalysis {
public:
    int regular_member;
    static int static_member;
    
    void showAddresses() {
        std::cout << "Object address: " << this << std::endl;
        std::cout << "Regular member address: " << &regular_member << std::endl;
        std::cout << "Static member address: " << &static_member << std::endl;
    }
};

int MemoryAnalysis::static_member = 42;

void analyze_memory_layout() {
    MemoryAnalysis obj1;
    MemoryAnalysis obj2;
    
    obj1.showAddresses();
    obj2.showAddresses();
    
    // 可以看到：
    // 1. obj1和obj2的地址不同
    // 2. 它们的regular_member地址不同
    // 3. static_member的地址相同（全局唯一）
}
```

### static成员的初始化顺序
```cpp
class InitOrder {
public:
    static int first;
    static int second;
    
    InitOrder() {
        std::cout << "Constructor: first=" << first << ", second=" << second << std::endl;
    }
};

// 静态成员按照定义顺序初始化
int InitOrder::first = (std::cout << "Initializing first\n", 1);
int InitOrder::second = (std::cout << "Initializing second\n", 2);

// 不同编译单元的静态变量初始化顺序是未定义的（静态初始化顺序惨案）
```

### static成员函数的特点
```cpp
class StaticMemberFunc {
private:
    int value;
    static int static_value;
    
public:
    StaticMemberFunc(int v) : value(v) {}
    
    static void staticFunc() {
        // 可以访问静态成员
        static_value = 10;
        
        // 不能访问非静态成员
        // value = 5;  // 错误：no object
        // this->value = 5;  // 错误：静态函数没有this指针
        
        // 可以访问静态函数
        anotherStaticFunc();
    }
    
    static void anotherStaticFunc() {
        std::cout << "Another static function" << std::endl;
    }
    
    void instanceFunc() {
        // 实例函数可以访问一切
        value = 1;
        static_value = 2;
        staticFunc();
    }
};

int StaticMemberFunc::static_value = 0;
```

## MPI程序中的static变量

### 进程独立性
```cpp
#include <mpi.h>

class MPIExample {
public:
    static int process_counter;
    static int total_messages;
    
    static void incrementCounter() {
        ++process_counter;
    }
    
    static void sendMessage(int dest) {
        ++total_messages;
        // MPI发送逻辑
    }
};

int MPIExample::process_counter = 0;
int MPIExample::total_messages = 0;

int main(int argc, char** argv) {
    MPI_Init(&argc, &argv);
    
    int rank, size;
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);
    MPI_Comm_size(MPI_COMM_WORLD, &size);
    
    // 每个进程都有自己的static变量副本
    MPIExample::incrementCounter();
    std::cout << "Process " << rank << " counter: " << MPIExample::process_counter << std::endl;
    
    // 进程间不会自动共享static变量的值
    // 每个进程的process_counter都是1，而不是进程总数
    
    MPI_Finalize();
    return 0;
}
```

### 进程间数据共享策略
```cpp
class MPIDataSharing {
public:
    static int local_count;  // 每个进程独立
    
    // 收集所有进程的数据
    static int getGlobalCount() {
        int global_count;
        MPI_Allreduce(&local_count, &global_count, 1, MPI_INT, MPI_SUM, MPI_COMM_WORLD);
        return global_count;
    }
    
    // 广播数据到所有进程
    static void broadcastCount(int root_rank) {
        MPI_Bcast(&local_count, 1, MPI_INT, root_rank, MPI_COMM_WORLD);
    }
    
    // 点对点通信共享数据
    static void exchangeWithNeighbor(int neighbor_rank) {
        int neighbor_count;
        MPI_Sendrecv(&local_count, 1, MPI_INT, neighbor_rank, 0,
                     &neighbor_count, 1, MPI_INT, neighbor_rank, 0,
                     MPI_COMM_WORLD, MPI_STATUS_IGNORE);
        
        std::cout << "My count: " << local_count 
                  << ", Neighbor count: " << neighbor_count << std::endl;
    }
};

int MPIDataSharing::local_count = 0;
```

### 共享内存方式
```cpp
#include <sys/mman.h>
#include <unistd.h>

class SharedMemoryExample {
public:
    static int* shared_counter;  // 指向共享内存的指针
    
    static void initSharedMemory() {
        // 创建共享内存区域
        shared_counter = static_cast<int*>(
            mmap(nullptr, sizeof(int), PROT_READ | PROT_WRITE,
                 MAP_SHARED | MAP_ANONYMOUS, -1, 0)
        );
        
        if (shared_counter == MAP_FAILED) {
            perror("mmap failed");
            exit(1);
        }
        
        *shared_counter = 0;
    }
    
    static void incrementShared() {
        if (shared_counter) {
            ++(*shared_counter);
        }
    }
    
    static int getSharedValue() {
        return shared_counter ? *shared_counter : -1;
    }
    
    static void cleanupSharedMemory() {
        if (shared_counter && shared_counter != MAP_FAILED) {
            munmap(shared_counter, sizeof(int));
        }
    }
};

int* SharedMemoryExample::shared_counter = nullptr;

// 注意：这种方式需要进程间同步机制（如信号量）来避免竞态条件
```

## 性能和线程安全考虑

### 线程安全的static初始化
```cpp
#include <thread>
#include <mutex>

class ThreadSafeStatic {
public:
    static ThreadSafeStatic& getInstance() {
        // C++11及以后：局部静态变量的初始化是线程安全的
        static ThreadSafeStatic instance;
        return instance;
    }
    
    static int getNextID() {
        static std::mutex id_mutex;
        static int next_id = 0;
        
        std::lock_guard<std::mutex> lock(id_mutex);
        return ++next_id;
    }
    
private:
    ThreadSafeStatic() = default;
};

void test_thread_safety() {
    std::vector<std::thread> threads;
    std::vector<int> ids(10);
    
    for (int i = 0; i < 10; ++i) {
        threads.emplace_back([&ids, i]() {
            ids[i] = ThreadSafeStatic::getNextID();
        });
    }
    
    for (auto& t : threads) {
        t.join();
    }
    
    // 检查是否有重复ID
    std::sort(ids.begin(), ids.end());
    for (int i = 0; i < 10; ++i) {
        std::cout << "Thread " << i << " got ID: " << ids[i] << std::endl;
    }
}
```

### static变量的性能影响
```cpp
#include <chrono>

class PerformanceTest {
public:
    static int static_var;
    int instance_var;
    
    PerformanceTest() : instance_var(0) {}
    
    void incrementStatic() {
        ++static_var;
    }
    
    void incrementInstance() {
        ++instance_var;
    }
};

int PerformanceTest::static_var = 0;

void compare_performance() {
    const int iterations = 100000000;
    PerformanceTest obj;
    
    // 测试静态变量访问
    auto start = std::chrono::high_resolution_clock::now();
    for (int i = 0; i < iterations; ++i) {
        obj.incrementStatic();
    }
    auto end = std::chrono::high_resolution_clock::now();
    auto static_time = std::chrono::duration_cast<std::chrono::nanoseconds>(end - start);
    
    // 测试实例变量访问
    start = std::chrono::high_resolution_clock::now();
    for (int i = 0; i < iterations; ++i) {
        obj.incrementInstance();
    }
    end = std::chrono::high_resolution_clock::now();
    auto instance_time = std::chrono::duration_cast<std::chrono::nanoseconds>(end - start);
    
    std::cout << "Static variable access time: " << static_time.count() << " ns\n";
    std::cout << "Instance variable access time: " << instance_time.count() << " ns\n";
}
```

## 最佳实践

### 1. 避免全局状态
```cpp
// 不好的做法
class BadDesign {
public:
    static std::vector<std::string> global_data;  // 全局状态，难以测试和维护
    
    static void addData(const std::string& data) {
        global_data.push_back(data);
    }
};

// 更好的做法：依赖注入
class GoodDesign {
private:
    std::vector<std::string>& data_ref;
    
public:
    explicit GoodDesign(std::vector<std::string>& data) : data_ref(data) {}
    
    void addData(const std::string& data) {
        data_ref.push_back(data);
    }
};
```

### 2. 合理使用静态工厂函数
```cpp
class Configuration {
private:
    std::string config_file;
    std::map<std::string, std::string> settings;
    
    Configuration(const std::string& file) : config_file(file) {
        loadSettings();
    }
    
    void loadSettings() {
        // 加载配置文件
    }
    
public:
    static std::unique_ptr<Configuration> fromFile(const std::string& filename) {
        return std::unique_ptr<Configuration>(new Configuration(filename));
    }
    
    static std::unique_ptr<Configuration> withDefaults() {
        auto config = std::unique_ptr<Configuration>(new Configuration(""));
        config->settings["debug"] = "false";
        config->settings["port"] = "8080";
        return config;
    }
    
    std::string get(const std::string& key) const {
        auto it = settings.find(key);
        return it != settings.end() ? it->second : "";
    }
};
```

## 总结

static关键字的核心特点：

1. **生命周期**：static变量生命周期贯穿程序运行期
2. **作用域**：可以限制变量/函数的可见性
3. **共享性**：类静态成员被所有对象共享
4. **进程独立**：在MPI等多进程环境中，每个进程有独立的static变量副本
5. **线程安全**：C++11后局部static变量初始化是线程安全的

正确使用static关键字可以有效管理全局状态、实现单例模式、优化性能，但要注意避免过度使用全局状态，保持代码的可测试性和维护性。 