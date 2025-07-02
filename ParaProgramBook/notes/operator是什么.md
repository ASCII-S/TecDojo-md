---
jupytext:
  formats: "md:markdown,ipynb/ParaProgramBook/notes/operator\u662F\u4EC0\u4E48.ipynb:notebook"
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

在这段代码中，使用 `**operator()**` 是为了定义一个**函数对象**（functor）。函数对象是一种重载了函数调用运算符 `**()**` 的类，可以像函数一样被调用。具体来说，`**PrintTask**` 类通过重载 `**operator()**` 运算符，将其实例化对象的行为变成了一个可以被调用的函数。

### **为什么使用** `**operator()**`**：**

1. **函数对象的优势**：
    - **状态保存**：函数对象可以携带状态，即类中的成员变量。这使得它们比普通的函数指针或lambda表达式更强大。
    - **可扩展性**：可以方便地扩展，添加更多成员函数或成员变量。
    - **内联优化**：在某些情况下，编译器可以对函数对象进行更好的内联优化，从而提高性能。
2. **在** `**std::thread**` **中使用函数对象**：
    - 在创建线程时，可以传递函数对象作为参数。`**std::thread**` 会将这个对象当作一个可调用对象来执行。通过重载 `**operator()**`，你可以直接将对象本身作为线程的任务函数。
    - 这样可以使得代码更加灵活和清晰，不仅可以传递参数，还可以通过类成员变量保持状态。

### **工作原理**

在这段代码中：

- `**PrintTask()**` 创建了一个匿名对象，该对象是一个函数对象，因为它重载了 `**operator()**`。
- `**std::thread t2(PrintTask(), 5);**` 创建了一个线程，并将这个函数对象和参数 `**5**` 传递给它。
- 当线程启动时，它会调用这个函数对象，并使用参数 `**5**` 作为 `**count**` 的值。

### **等价形式**

这段代码使用函数对象实现的功能，也可以用lambda表达式或普通的函数来实现。例如，可以使用lambda表达式来达到类似的效果：

```
std::thread t2([](int count) {
    for (int i = 0; i < count; ++i) {
        std::cout << "Hello from thread (lambda)!\n";
    }
}, 5);
```

两者在功能上是等价的，但函数对象的优势在于它可以携带状态，适用于更复杂的场景。
