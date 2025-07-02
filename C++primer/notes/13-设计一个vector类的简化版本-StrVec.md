
# 动态内存管理

## 设计一个vector类的简化版本-StrVec

### vector类设计背景

* `vector` 类通过 **动态内存管理** 实现元素的添加和扩展。
* 为了性能，`vector` 会 **预留多于当前元素的空间**，避免频繁的内存分配。
* 元素添加过程：

  * 检查是否还有空间；
  * 有空间：直接在未使用的位置构造新对象；
  * 无空间：重新分配更大的内存，将已有元素移动过去，释放旧内存。


### StrVec 类的内存管理策略

StrVec 使用了类似 `vector` 的策略，但 **借助 `allocator<string>` 来手动管理内存与对象生命周期**。

**指针成员变量（三个）：**

* `elements`：指向 **分配内存的首元素**；
* `first_free`：指向 **当前最后一个有效元素之后的位置**；
* `cap`：指向 **分配内存的尾部**（即整个可用空间的末端）。

> 图示（模拟内存状态）：
```
elements               first_free         cap
    ↓                      ↓               ↓
| 元素0 | 元素1 | 元素2 | 未构造 | 未构造 |
```

**静态成员**

* `alloc`：类型为 `allocator<string>`，用于所有内存分配操作。


**工具函数（4个）**

1. **`alloc_n_copy(begin, end)`**

   * 分配一段足够的内存；
   * 拷贝 `[begin, end)` 范围内的元素到新内存；
   * 返回新的首尾指针。

2. **`free()`**

   * 逐个调用 `destroy` 销毁已构造的元素；
   * 使用 `deallocate` 释放内存。

3. **`chk_n_alloc()`**

   * 检查是否还有空间容纳新元素；
   * 如果没有，调用 `reallocate()` 重新分配更大空间。

4. **`reallocate()`**

   * 通常会分配当前容量的两倍；
   * 移动已有元素到新内存；
   * 析构原有元素并释放旧内存；
   * 更新 `elements`, `first_free`, `cap` 指针。

---

### 提供的接口

* StrVec 实现了类似 `vector` 的接口（如 `push_back` 等），用于向外暴露功能；
* 这些接口内部会调用上述工具函数来管理内存和元素构造/销毁。

## StrVec类定义

```cpp

class StrVec {
public:
    StrVec();                                 // 默认构造函数
    StrVec(const StrVec&);                    // 拷贝构造函数
    StrVec &operator=(const StrVec&);         // 拷贝赋值运算符
    ~StrVec();                                // 析构函数

    void push_back(const std::string&);       // 添加元素

    size_t size() const;                      // 当前元素个数
    size_t capacity() const;                  // 当前容量
    std::string *begin() const;               // 指向首元素
    std::string *end() const;                 // 指向尾后元素

private:
    static std::allocator<std::string> alloc; // 分配内存的工具

    // 检查是否需要扩容
    void chk_n_alloc();

    // 工具函数：内存分配 + 拷贝
    std::pair<std::string*, std::string*> alloc_n_copy(const std::string*, const std::string*);

    void free();         // 销毁并释放内存
    void reallocate();   // 分配更大内存，搬移已有元素

    std::string *elements;     // 指向内存首位置
    std::string *first_free;   // 指向已构造元素的尾后位置
    std::string *cap;          // 指向内存的末尾位置
};


```

## `StrVec::push_back` 成员函数

### push\_back 的作用

* 向 `StrVec` 添加一个新的 `string` 元素。
* 需要确保有空间，并在未构造区域手动构造一个新对象。

### 函数实现

```cpp
void StrVec::push_back(const std::string& s) {
    chk_n_alloc();                    // 保证有空间可放置新元素
    alloc.construct(first_free++, s); // 在 first_free 所指位置构造 s 的副本，并前置递增指针
}
```

### `construct` 的调用说明

#### 1. 为什么使用 `construct`

* 使用 `allocator.allocate(n)` 分配的是 **未构造的原始内存**；
* 不能直接赋值，必须通过 `construct(p, args...)` 在该位置 **显式构造对象**

#### 2. 参数解释

```cpp
alloc.construct(first_free++, s);
```

* `first_free`：StrVec成员，指向第一个未构造的位置，作为构造对象的地址；
* `s`：传入的 `string` 参数，会调用 `string` 的 **拷贝构造函数**；
* 构造完成后，`first_free` 前置递增，指向下一个可构造位置。

### 构造过程图示（内存操作顺序）

```text
初始状态:
elements     first_free     cap
   ↓             ↓           ↓
| s0 | s1 | 未构造 | 未构造 | ...

执行 push_back("s2") 后:
- chk_n_alloc 确保有空间
- construct(first_free, "s2")
- first_free++

结果状态:
elements         first_free       cap
   ↓                 ↓              ↓
| s0 | s1 | s2 | 未构造 | ...
```


## `StrVec::alloc_n_copy` 成员函数

### 函数作用

* 在 **拷贝构造** 或 **拷贝赋值** `StrVec` 时调用；
* 功能：从给定范围 `[b, e)` 中拷贝元素：

  * **分配新内存**；
  * **在新内存中构造这些元素的副本**；
  * 返回一个 pair，表示新内存的起始与尾后位置。

### 函数定义

```cpp
std::pair<std::string*, std::string*>
StrVec::alloc_n_copy(const std::string *b, const std::string *e) {
    auto data = alloc.allocate(e - b);                       // 分配足够的原始内存
    return {data, std::uninitialized_copy(b, e, data)};      // 构造拷贝，返回首尾指针
}
```


### 关键操作详解

#### 1. `allocate(n)`

* 使用 `allocator` 分配 `n` 个未构造的 `string` 类型内存块；
* `n = e - b`，即元素个数。

#### 2. `std::uninitialized_copy(b, e, data)`

* 将 `[b, e)` 范围内的每个元素 **拷贝构造** 到以 `data` 开始的位置；
* 会调用每个元素的拷贝构造函数；
* 返回指向最后一个构造对象“尾后位置”的指针。

#### 3. 返回值 `std::pair`

* `first`: `data`，即起始地址；
* `second`: `uninitialized_copy` 的返回值，尾后地址；
* 可用于设置 `elements` 和 `first_free`。


## `StrVec::free` 成员函数

### 函数作用

`free()` 是 `StrVec` 的资源释放工具函数，作用有两个：

1. **销毁构造过的元素**（调用 `allocator::destroy`）；
2. **释放分配的内存空间**（调用 `allocator::deallocate`）。

### 函数定义

```cpp
void StrVec::free() {
    if (elements) { // 检查是否有分配过内存
        for (auto p = first_free; p != elements; /* 空 */)
            alloc.destroy(--p); // 逆序销毁所有已构造元素
        alloc.deallocate(elements, cap - elements); // 释放内存
    }
}
```


### 实现细节解析

#### 1. 空检查

* `elements == nullptr` 表示未分配内存；
* `deallocate` 不接受空指针，因此需要先判断。

#### 2. 逆序销毁

```cpp
for (auto p = first_free; p != elements; )
    alloc.destroy(--p);
```

* 迭代器从尾后 `first_free` 开始；
* 每次前移（`--p`）再销毁对应对象；
* 保证与构造顺序相反，**符合析构安全性要求**。

#### 3. 内存释放

```cpp
alloc.deallocate(elements, cap - elements);
```

* 释放之前 `allocate` 得到的那一段原始内存；
* 第二个参数为释放的元素个数（容量）。


## StrVec 拷贝控制成员函数

### 拷贝控制成员的目的

为了实现 **类值语义（value-like behavior）**，StrVec 需要显式管理：

* **拷贝构造**：构造新对象时拷贝已有对象的数据；
* **拷贝赋值运算符**：将一个已存在对象赋值为另一个；
* **析构函数**：释放资源，避免内存泄漏；

这些操作通过 `alloc_n_copy()` 和 `free()` 实现资源的正确管理。

### 拷贝构造函数

```cpp
StrVec::StrVec(const StrVec &s) {
    auto newdata = alloc_n_copy(s.begin(), s.end()); // 分配 + 构造
    elements = newdata.first;
    first_free = cap = newdata.second;
}
```

#### 说明：

* 使用 `alloc_n_copy` 拷贝 `[s.begin(), s.end())` 中的元素；
* `first_free` 和 `cap` 都指向尾后位置，因该内存仅容纳拷贝内容，无预留空间。

---

### 析构函数

```cpp
StrVec::~StrVec() {
    free();
}
```

#### 说明：

* 使用 `free()` 逐个销毁元素并释放内存；
* 保证所有资源在对象生命周期结束时释放，符合 RAII 原则。

---

### 拷贝赋值运算符

```cpp
StrVec &StrVec::operator=(const StrVec &rhs) {
    auto data = alloc_n_copy(rhs.begin(), rhs.end()); // 拷贝新内容
    free();                                            // 释放旧资源
    elements = data.first;
    first_free = cap = data.second;
    return *this;
}
```

#### 实现步骤：

1. 先调用 `alloc_n_copy` 创建 **新资源**；
2. 再调用 `free()` 销毁旧资源；
3. 最后更新指针成员；
4. 返回 `*this` 支持链式赋值。

#### 注意事项：

* **自赋值安全**：拷贝新数据 *先于* 释放原数据


## reallocate：为何使用移动而非拷贝

### reallocate 的作用

当 StrVec 的空间不够时，`reallocate()` 需要：

1. 分配更大的新内存空间；
2. 将旧内存中的元素转移到新内存中；
3. 销毁原内存中的元素；
4. 释放原内存。

### 默认行为：拷贝

在最初的实现中：

* 使用 `alloc_n_copy()` 或 `uninitialized_copy()`，会 **逐个拷贝 string 元素**；
* string 是一个类值对象，拷贝意味着：

  * 分配新内存保存字符；
  * 拷贝每个字符；
  * 新旧 string 相互独立。

#### 问题：

* 这种 **深拷贝** 成本较高；
* 明明旧对象马上就要销毁，**却要拷贝一份数据出来再删掉旧的**，效率低。


### 改进：使用移动构造

在 `reallocate()` 中，所有元素都将**从旧空间迁移到新空间，并立刻销毁旧副本**，这正好适合使用 **移动语义**：

* `std::move(*p)`：将资源的所有权从旧对象转移到新对象；
* 移动构造 string：

  * 不再拷贝字符数组；
  * 转移指针，原对象置空；
* 避免重复内存分配与释放，大幅提升效率。


## 移动构造函数与 `std::move` 

### 背景

为了提高性能，尤其在 `reallocate` 中避免不必要的拷贝，C++11 引入了两项关键机制：

* **移动构造函数**
* **`std::move` 函数**

这些机制可以显著降低资源转移的成本，特别是对类如 `std::string` 等拥有动态资源的类型。


### 移动构造函数的核心思想

* 与拷贝构造不同，**移动构造**不复制资源，而是\*\*“窃取”资源所有权\*\*。
* 对于 `std::string`，可以假设其内部结构为：

  ```cpp
  class string {
      char* data;
      size_t size;
      // ...
  };
  ```

#### 传统拷贝构造：

```cpp
string(const string& other); // 分配新内存，复制字符
```

#### 移动构造：

```cpp
string(string&& other); // 只“拷贝指针”，并将原指针置空
```

* 移动后对象（moved-from object）仍是 **合法的、可析构的**，但其内容不再使用。

---

### std::move 的作用

```cpp
#include <utility>
// move定义在utility头文件中
std::move(expr)
// 将 expr 转换为右值引用类型
```

#### 关键理解：

* `std::move` 并不**移动**任何东西；
* 它只是**将左值强制转换为右值引用类型**，使其能够绑定到移动构造函数上。

#### 举例说明：

```cpp
std::string s = "hello";
std::string t = std::move(s); // 调用 string 的移动构造函数
```

* `s` 的资源被转移给 `t`，`s` 进入“移后状态”，但仍然可以析构。

---

### reallocate 中为何必须使用 std::move

```cpp
alloc.construct(dest++, std::move(*src++));
```

如果省略 `std::move`：

```cpp
alloc.construct(dest++, *src++); // 会调用拷贝构造函数
```

* 默认表达式是左值 → 拷贝；
* 只有加上 `std::move`，编译器才认为我们**允许转移资源**。


## StrVec::reallocate 成员函数

### 函数目的

`reallocate()` 在当前空间不足时被调用，用于：

1. **分配更大容量的内存**；
2. **将已有元素移动到新空间**；
3. **释放旧内存资源**；
4. **更新指针成员维护结构状态**。


### 完整函数实现

```cpp
void StrVec::reallocate() {
    // 计算新容量（为原来的两倍，空时为1）
    auto newcapacity = size() ? 2 * size() : 1;

    // 分配新内存
    auto newdata = alloc.allocate(newcapacity);

    // 移动旧元素到新内存
    auto dest = newdata;
    auto elem = elements;
    for (size_t i = 0; i != size(); ++i)
        alloc.construct(dest++, std::move(*elem++));

    // 释放旧内存
    free();

    // 更新指针
    elements = newdata;
    first_free = dest;
    cap = elements + newcapacity;
}
```

### 实现要点详解

#### 1. 容量计算

```cpp
auto newcapacity = size() ? 2 * size() : 1;
```

* 空容器初次扩展 → 分配 1 个元素；
* 之后每次扩容 → 容量加倍。

#### 2. 元素迁移（使用移动构造）

```cpp
alloc.construct(dest++, std::move(*elem++));
```

* `std::move` 将 `*elem` 转为右值引用；
* 触发 `std::string` 的 **移动构造函数**：

  * 仅转移字符数组指针；
  * 避免重复分配与拷贝；
* 结果：新对象接管资源，旧对象留为空壳。

#### 3. 销毁旧元素 + 释放旧内存

```cpp
free();
```

* 即便旧对象已被“掏空”，其析构仍然安全；
* `free()` 执行逆序 `destroy` 和 `deallocate`。

#### 4. 更新指针

```cpp
elements = newdata;
first_free = dest;
cap = elements + newcapacity;
```

* `first_free` 更新为最后一个构造元素之后；
* `cap` 更新为新容量的尾后位置。

---
