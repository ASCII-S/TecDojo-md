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

## HJ4 字符串分隔

### 题目描述

对于给定的由小写字母和数字混合构成的字符串s，你需要按每8个字符换一行的方式书写它，具体地：
- 书写前8个字符，换行；
- 书写接下来的8个字符，换行；
- ……
- 重复上述过程，直到字符串被完全书写。

特别地，如果最后一行不满8个字符，则需要在字符串末尾补充0，直到长度为8。

### 输入描述

在一行上输入一个长度1≤length(s)≤100，由小写字母和数字混合构成的字符串s。

### 输出描述

输出若干行，每行输出8个字符，代表按题意书写的结果。

### 示例1

输入：
```
hellonowcoder
```

输出：
```
hellonow
coder000
```

说明：在这个样例中，字符串长度为13，因此需要在第二行末尾补充3个0。

### 示例2

输入：
```
0
```

输出：
```
00000000
```

### C++解答

```cpp
#include <iostream>
using namespace std;

int main() {
    string line;
    getline(cin,line);
    int len = line.length();
    // 格式化字符串为8长度
    int tail_lack = 8 - len%8;
    while (tail_lack < 8 && tail_lack > 0){
        line += '0';
        tail_lack--;
    }
    // 格式化打印
    int row = line.length()/8;
    for (int i=0; i < row; i++){
        cout << line.substr(i*8,8)<<endl;
    }
    return 0;
}
```

### 解题要点

1. **字符串分割**：需要将输入字符串按每8个字符分割成多行。

2. **末尾补0**：如果最后一行不足8个字符，需要在末尾补充0直到长度为8。

3. **分割处理方式**：
   - 本题采用了先补齐字符串长度到8的倍数，再进行等长分割的方法。
   - 先计算末尾需要补充的0的个数，然后直接在原字符串后追加0。
   - 最后按每8个字符输出一行。

4. **行数计算**：补齐后的总行数 = 字符串长度 / 8。

5. **时间复杂度**：O(n)，其中n为字符串长度。

6. **空间复杂度**：O(n)，需要存储补齐后的字符串。

7. **边界情况**：
   - 空字符串输入：按题意应输出一行8个0。
   - 字符串长度正好是8的倍数：不需要补充0。
   - 字符串长度为1：需要补充7个0。

### 头文件函数对应关系

1. \<iostream>
   - 功能：提供输入输出流功能
   - 使用的函数/对象：
     - cin：标准输入流对象，用于从控制台读取输入
     - cout：标准输出流对象，用于向控制台输出结果
     - endl：用于插入换行符并刷新输出缓冲区
     - getline(cin, line)：从输入流读取一整行到字符串line中

2. \<string>（隐式包含）
   - 功能：提供字符串操作功能
   - 使用的函数/对象：
     - string：字符串类，用于存储和处理字符串
     - line.length()：获取字符串line的长度
     - line.substr(start, length)：从字符串line的start位置截取length长度的子串
     - line += '0'：在字符串line末尾追加字符'0'
