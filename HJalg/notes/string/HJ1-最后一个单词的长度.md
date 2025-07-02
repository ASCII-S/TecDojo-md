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

HJ1 字符串最后一个单词的长度
===

## 题目描述

对于给定的若干个单词组成的句子，每个单词均由大小写字母混合构成，单词间使用单个空格分隔。输出最后一个单词的长度。

## 输入描述

在一行上输入若干个字符串，每个字符串代表一个单词，组成给定的句子。

除此之外，保证每个单词非空，由大小写字母混合构成，且总字符长度不超过10^3。

## 输出描述

在一行上输出一个整数，代表最后一个单词的长度。

## 示例

### 示例1

输入：
```
HelloNowcoder
```

输出：
```
13
```

说明：在这个样例中，最后一个单词是"HelloNowcoder"，长度为13。

### 示例2

输入：
```
A B C D
```

输出：
```
1
```

## 解题思路

这是一道简单的字符串处理题目，解题思路如下：
1. 读取输入的字符串
2. 按空格分割字符串，获取单词数组
3. 取出最后一个单词并计算其长度
4. 输出结果

## 代码实现

### C++实现

```cpp
#include <iostream>
#include <string>
#include <sstream>
#include <vector>
using namespace std;

int main() {
    string line;
    getline(cin, line);
    
    istringstream iss(line);
    vector<string> words;
    string word;
    
    // 按空格分割字符串
    while (iss >> word) {
        words.push_back(word);
    }
    
    // 如果有单词，输出最后一个单词的长度
    if (!words.empty()) {
        cout << words.back().length() << endl;
    } else {
        cout << 0 << endl;
    }
    
    return 0;
}
```

## 复杂度分析

- 时间复杂度：O(n)，其中n为输入字符串的长度，需要遍历整个字符串
- 空间复杂度：O(n)，存储分割后的单词数组需要的空间

