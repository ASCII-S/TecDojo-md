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

在C++中，**string literal**（字符串字面量）和 `**std::string**` 是两种不同的概念，尽管它们都与字符串相关。它们的主要区别体现在类型、存储、特性和用法上。
### 1. **string literal（字符串字面量）**：
- **类型**：字符串字面量的类型是 `const char[]`，也就是说它是一个常量字符数组。它的大小由字符串的字符数+1（为末尾的空字符 `'\\0'`）决定。
- **存储方式**：字符串字面量通常存储在程序的只读内存区域，因此它是不可修改的。如果尝试修改，会导致未定义行为。
- **用法**：字符串字面量直接写在代码中，例如：  
    这里的  
    `"Hello, World!"` 就是一个字符串字面量，它的类型是 `const char[14]`，因为它包含13个字符加上一个空字符。
    
    ```C++
    const char* strLiteral = "Hello, World!";
    ```
    
### 2. `**std::string**`**（C++标准库的字符串类型）**：
- **类型**：`std::string` 是 C++ 标准库中定义的类，属于 `std` 命名空间。它是动态分配的字符串类型，可以根据需要调整大小。
- **存储方式**：`std::string` 在堆上动态分配内存，存储实际的字符数据。它的大小可以动态变化，支持多种字符串操作（如拼接、插入、删除等）。
- **可修改性**：`std::string` 对象可以修改，允许你添加、删除或修改字符内容。
- **用法**：`std::string` 提供了很多有用的成员函数，方便处理字符串。例如：
    
    ```C++
    std::string str = "Hello, World!";
    str += " How are you?";  // 拼接字符串
    ```
    
### 3. **主要区别**：
|   |   |   |
|---|---|---|
|特性|string literal|`std::string`|
|**类型**|`const char[]` / `const char*`|`std::string`|
|**存储位置**|通常存储在只读内存区域|动态内存分配，存储在堆上|
|**是否可修改**|不可修改|可修改（支持增加、删除、修改字符）|
|**大小可变性**|固定大小，编译时决定|动态大小，运行时可调整|
|**常用操作**|基本数组操作（例如访问单个字符）|提供丰富的成员函数（拼接、查找、替换等）|
|**末尾空字符**|自动添加 `'\\0'`|自动管理字符末尾（不需要手动添加 `'\\0'`）|
### 4. **示例比较**：


```
\#include <iostream>
\#include <string>
int main() {
    // 字符串字面量
    const char* strLiteral = "Hello";
    std::cout << "String Literal: " << strLiteral << std::endl;
    // std::string
    std::string str = "Hello";
    std::cout << "std::string: " << str << std::endl;
    // 修改std::string
    str += ", World!";
    std::cout << "Modified std::string: " << str << std::endl;
    // 修改字符串字面量 (错误)
    // strLiteral[0] = 'h';  // 编译器可能不允许，或导致运行时错误
}
```

在这个例子中，`std::string` 可以被修改和拼接，而字符串字面量则是常量，不允许修改。
### 总结：
- **字符串字面量**：是 `const char[]` 类型，通常存储在只读内存中，无法修改。
- `**std::string**`：是C++标准库中的动态字符串类，提供丰富的字符串操作功能，大小可以动态变化，内容可修改。
它们的选择取决于你是否需要动态字符串操作。如果你只需要一个常量字符串，字符串字面量可能就够了；而如果你需要修改字符串或者进行复杂的字符串操作，`std::string` 是更合适的选择。
