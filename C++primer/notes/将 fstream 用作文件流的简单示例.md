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


```
\#include <iostream>
\#include <fstream>
\#include <string>
int main() {
    std::fstream file("example.txt", std::ios::in | std::ios::out | std::ios::app); // 打开文件进行读写并追加内容
    if (!file.is_open()) {
        std::cerr << "Failed to open the file!" << std::endl;
        return 1;
    }
    // 写入文件
    file << "This is a test line." << std::endl;
    // 回到文件开头
    file.seekg(0); // 设置读取位置到文件起始
    // 从文件读取内容
    std::string line;
    while (std::getline(file, line)) {
        std::cout << line << std::endl;
    }
    return 0; // 程序结束时，`file` 的析构函数会自动关闭文件
}
```
