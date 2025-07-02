---
jupyter:
  jupytext:
    text_representation:
      extension: .md
      format_name: markdown
      format_version: '1.3'
      jupytext_version: 1.14.4
  kernelspec:
    display_name: C++
    language: c++
    name: xcpp11
---

---
Created time: 2025-04-02T15:58
加入带背: true
jupyter:
  jupytext:
    cell_metadata_filter: language,-all
    formats: md,ipynb
    main_language: c++
    text_representation:
      extension: .md
      format_name: markdown
      format_version: '1.3'
      jupytext_version: 1.14.7
---
非常好的问题！你这行代码中的 `std::make_unique<BFSLocalGraph>()` 是 **C++14 引入的** 一个 **智能指针工厂函数**，它用于 **创建一个** `**std::unique_ptr**` **智能指针**，并且**自动帮你 new 对象，避免手动写** `**new**`。
---
## 🎯 一句话解释

> std::make_unique<T>() = 创建一个 T 类型对象，并用 unique_ptr 管理它
---
## 📄 具体分析
### 这句代码：


```
localGraph = std::make_unique<BFSLocalGraph>();
```

相当于：


```
localGraph = std::unique_ptr<BFSLocalGraph>(new BFSLocalGraph());
```

但更 **简洁、安全、推荐**。
---
## 🧩 语法


```
std::unique_ptr<T> ptr = std::make_unique<T>(constructor_args...);
```

它会：
- 自动 **调用** `**new T(constructor_args...)**`
- 返回 `**std::unique_ptr<T>**`
- 避免手动写 `new`，更**安全**（不会忘记 `delete`）
---
## 🚀 为什么推荐使用？
|   |   |
|---|---|
|优点|说明|
|✅ **更简洁**|一行搞定，不用写 `new`|
|✅ **异常安全**|如果构造函数抛异常，`make_unique` 会正确释放资源|
|✅ **避免裸指针**|不需要手动管理内存|
|✅ **现代 C++最佳实践**|C++14 后推荐写法|
---
## 🧩 示例


```
struct Node {
    int val;
    Node(int v) : val(v) {}
};
int main() {
    auto p = std::make_unique<Node>(10);
    std::cout << p->val;  // 输出 10
}
```

---
## 🔥 和 `std::make_shared` 区别
|   |   |   |
|---|---|---|
|函数|返回类型|所属智能指针|
|`make_unique`|`std::unique_ptr<T>`|**独占所有权**，不能共享|
|`make_shared`|`std::shared_ptr<T>`|**共享所有权**，引用计数|
---
## 📝 学习笔记模板
```Plain
# std::make_unique<T>()
✅ 功能：
创建 T 类型对象并返回 unique_ptr 智能指针。
📌 语法：
auto ptr = std::make_unique<T>(args...);
🎯 优点：
- 简洁安全
- 异常安全
- 避免手动写 new/delete
⚠️ 注意：
- C++14 引入
- 独占所有权，不能复制，只能移动
```
---
如果你想，我可以帮你补充一套 **unique_ptr + make_unique 常见用法 & 注意事项**，让你快速掌握智能指针最佳实践。要不要？
