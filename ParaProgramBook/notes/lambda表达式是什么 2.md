---
jupytext:
  formats: "md:markdown,ipynb/ParaProgramBook/notes/lambda\u8868\u8FBE\u5F0F\u662F\
    \u4EC0\u4E48 2.ipynb:notebook"
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

[[c++多线程语法-来源菜鸟教程]]

---

Lambda表达式（或匿名函数）是一种在 C++ 中用来定义**短小函数**的语法。它们通常用于需要临时定义函数的场合，例如作为回调函数、用于多线程编程、算法中的自定义操作等。Lambda 表达式的一个显著特点是它不需要像普通函数那样有一个名字。

### **语法结构**

Lambda 表达式的基本结构如下：

```
[capture](parameters) -> return_type {
    // function body
}
```

- `**[capture]**`：捕获列表，用来指定哪些变量可以在 lambda 表达式中使用。这些变量可以通过值或引用传递。
- `**(parameters)**`：参数列表，与普通函数类似，可以指定函数的参数。
- `**> return_type**`：返回类型（可选），用于指定 lambda 表达式的返回类型。编译器通常能自动推断出返回类型，因此可以省略。
- `**{ function body }**`：函数体，包含 lambda 表达式的代码逻辑。

### **捕获列表的用法**

Lambda 表达式可以访问在它创建时**所在作用域中的变量**，这些变量通过捕获列表指定。常见的捕获方式包括：

1. **按值捕获（**`**=**`**）**：捕获外部变量的副本。
2. **按引用捕获（**`**&**`**）**：捕获外部变量的引用，使得 lambda 内部的修改会影响到外部变量。
3. **混合捕获**：可以分别对不同的变量使用按值或按引用捕获。

```
int x = 10;
int y = 20;

// 按值捕获 x，按引用捕获 y
auto lambda = [x, &y]() {
    std::cout << "x = " << x << ", y = " << y << std::endl;
};

lambda(); // 输出：x = 10, y = 20
```

在这个例子中，`**x**` 被按值捕获，所以 lambda 内部不能修改 `**x**`。而 `**y**` 是按引用捕获的，所以 lambda 可以直接修改 `**y**`。
