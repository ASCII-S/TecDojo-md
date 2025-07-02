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


# HJ9 提取不重复的整数

## 题目描述
对于给定的正整数n，按照从右向左的阅读顺序，返回一个不含重复数字的新的整数。

## 输入描述
在一行上输入一个正整数n(1≦n≦10^8)代表给定的整数。
保证n的最后一位不为0。

## 输出描述
在一行上输出一个整数，代表处理后的数字。

## 示例1
输入：
```
9876673
```
输出：
```
37689
```
说明：
在这个样例中，先将数字倒序，得到3766789，然后去除重复数字，得到37689。

## 示例2
输入：
```
12345678
```
输出：
```
87654321
```

## 解题思路
1. 将输入的整数转为字符串，方便处理每一位数字
2. 从后向前遍历字符串（即从右向左读取数字）
3. 使用哈希表（unordered_set）记录已经出现过的数字，避免重复
4. 如果当前数字没有出现过，则添加到结果中

## 代码实现
```cpp
#include <iostream>
#include <string>
#include <unordered_set>
using namespace std;

int extractUniqueDigits(int num) {
    string numStr = to_string(num);
    string result = "";
    unordered_set<char> seen;
    
    // 从右向左遍历数字
    for (int i = numStr.length() - 1; i >= 0; i--) {
        char digit = numStr[i];
        // 如果这个数字还没有出现过
        if (seen.find(digit) == seen.end()) {
            result += digit;
            seen.insert(digit);
        }
    }
    
    // 将结果转回整数
    return stoi(result);
}

int main() {
    int n;
    cin >> n;
    
    int result = extractUniqueDigits(n);
    cout << result << endl;
    
    return 0;
}
```

```cpp
#include <iostream>
#include <algorithm>
#include <array>
using namespace std;

int main() {
    string line;
    getline(cin,line);
    reverse(line.begin(),line.end());
    array<int,10> visited{0};
    string strresult = "";
    for(char c:line){
        int i = c - '0';
        // cout << visited[i];
        if (!visited[i]){
            visited[i] = 1;
            strresult += c;
        }
    }
    cout << strresult;
    // cout << stoi(strresult) << endl;
    return 0;
}
// 64 位输出请用 printf("%lld")
```
## 涉及的标准库及对应函数
1. `<iostream>`: 用于输入输出，提供`cin`和`cout`
2. `<string>`: 用于字符串处理
   - `to_string()`: 将数字转换为字符串
   - `stoi()`: 将字符串转换为整数
3. `<unordered_set>`: 提供哈希集合数据结构
   - `find()`: 查找元素
   - `insert()`: 插入元素

## 解题模板
本题可以提取为"从右向左读数并去重"的模板，适用于需要按特定顺序处理数字并去除重复元素的场景。哈希表去重的思路也可以应用到其他需要判断元素是否重复出现的问题中。
