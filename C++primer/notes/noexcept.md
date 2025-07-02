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
Created time: 2025-03-25T17:21
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
非常棒的问题！你遇到的这个：


```
GraphData(const GraphData& other) noexcept;
```

里面的 `noexcept` 是 C++11 引入的一个关键字，用于**声明函数在运行时不会抛出异常**。
---
## ✅ 一句话理解

> noexcept 表示：这个函数承诺不会抛出异常（throw），如果真的抛了，会导致程序终止（std::terminate()）。
---
## ✨ 为什么要用 `noexcept`？
- 编译器可以进行**更好的优化**（比如移动代替拷贝）
- 有助于**构建强异常安全保证**的代码
- STL 容器（如 `std::vector`）内部在判断是否使用移动构造时，会优先选择 `noexcept` 的构造函数！
---
## 🧠 用法讲解
### ✅ 一般函数


```
void foo() noexcept;
```

表示 `foo()` 函数保证不会抛出异常。
---
### ✅ 构造函数 / 析构函数


```
MyClass(const MyClass& other) noexcept;
MyClass(MyClass&& other) noexcept;
```

这告诉 STL 或调用者：你可以放心地移动/拷贝我，不会崩。
---
### ✅ 条件 noexcept（高级）


```
template<typename T>
void bar() noexcept(std::is_nothrow_copy_constructible<T>::value);
```

这表示：只有当 `T` 的拷贝构造是 noexcept 的时候，这个函数才是 noexcept 的。
---
## 📌 示例：为什么重要？


```
struct A {
    A() {}
    A(const A&) noexcept { std::cout << "copy\n"; }
    A(A&&) noexcept { std::cout << "move\n"; }
};
std::vector<A> v;
v.push_back(A());  // 如果 move 是 noexcept，vector 会更倾向于用 move 构造，提高性能
```

---
## 🚨 如果没有写 noexcept，会怎样？
- 默认认为函数 **可能会抛异常**
- 在某些场景（如容器扩容）会退化成使用 `copy` 而不是 `move`，影响性能
- 如果写了 `noexcept`，却抛出了异常：程序 **直接崩溃终止**
---
## 📝 学习笔记模板
```Plain
# noexcept 学习笔记
✅ 作用：
- 表示函数不会抛出异常，提高安全性和优化空间
📌 写法：
void foo() noexcept;
T::T(const T& other) noexcept;
🎯 用途：
- 提高 STL 容器性能（如 vector 移动）
- 提升函数的异常安全性
⚠️ 注意：
- 如果声明 noexcept，却真的抛异常，会调用 std::terminate() 崩溃
```
---
需要我在你项目中找一个带 `noexcept` 的构造函数来具体讲讲它为什么加、有没有效果？我可以帮你查下 👍
