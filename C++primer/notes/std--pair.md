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
Created time: 2025-04-15T15:16
加入带背: false
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
太好了！你问的 `std::pair` 是 C++ 标准库中一个非常常用、非常实用的工具，用于 **打包两个数据值在一起**。
我们一步一步来解释 👇
---
## 🎯 什么是 `std::pair`？

> std::pair<T1, T2> 是一个模板类，用来将两个不同类型的值组合成一个对象，就像是一个小型结构体。
你可以把它当作：

> “装着两个值的小盒子”
---
## ✅ 常见用途
|   |   |
|---|---|
|用途|举例|
|传递多个返回值|比如函数返回 `(x, y)` 坐标|
|作为键值对（key-value）|用于 `std::map` 中|
|存储临时数据组合|比如图的边：`<src, dst>`|
---
## 🧩 使用方法
### 1. 创建 pair


```
std::pair<int, std::string> p(1, "apple");
```

或者使用 C++11 的推荐写法：


```
auto p = std::make_pair(1, "apple");
```

---
### 2. 访问成员


```
std::cout << p.first << " " << p.second;  // 输出: 1 apple
```

---
### 3. 用作函数返回值


```
std::pair<int, int> getMinMax() {
    return {2, 99};
}
auto result = getMinMax();
std::cout << result.first << ", " << result.second;
```

---
### 4. 用于 STL 容器（如 `std::map`）


```
std::map<std::string, int> fruitCount;
fruitCount["apple"] = 3;
for (const std::pair<std::string, int>& entry : fruitCount) {
    std::cout << entry.first << ": " << entry.second << "\n";
}
```

---
## 🧠 pair 的结构定义（简化版）


```
template<typename T1, typename T2>
struct pair {
    T1 first;
    T2 second;
};
```

---
## 🔄 支持比较运算


```
std::pair<int, int> a = {1, 2};
std::pair<int, int> b = {1, 3};
if (a < b) {
    std::cout << "a 小于 b";  // 按字典序比较 first，再比较 second
}
```

---
## 📝 快速笔记模板
```Plain
# std::pair 使用笔记
✅ 定义：
template<typename T1, typename T2>
struct pair {
    T1 first;
    T2 second;
};
📌 创建方式：
std::pair<int, std::string> p(1, "apple");
auto p = std::make_pair(1, "apple");
🎯 用途：
- 多值返回
- map/set 中的键值对
- 表示坐标、边等二元结构
⚠️ 注意：
- 支持比较操作（按 first -> second 排序）
- 建议用 auto + make_pair 简化类型
```
---
如果你愿意，我可以帮你写一段小 demo，展示 `std::pair` 在图结构、坐标系统、数据映射等场景下的实际用法，要不要？
