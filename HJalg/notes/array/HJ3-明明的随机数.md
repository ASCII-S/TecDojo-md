---
jupytext:
  formats: "md:markdown,ipynb/HJalg/notes/array/HJ3-\u660E\u660E\u7684\u968F\u673A\
    \u6570.ipynb:notebook"
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

# HJ3 明明的随机数

## 题目描述
对于明明生成的 n 个 1 到 500 之间的随机整数，你需要帮助他完成以下任务：
- 删去重复的数字，即相同的数字只保留一个，把其余相同的数去掉；
- 然后再把这些数从小到大排序，按照排好的顺序输出。

你只需要输出最终的排序结果。

## 输入描述
第一行输入一个整数 n (1≦n≦1000)，代表明明生成的数字个数。
此后 n 行，第 i 行输入一个整数 ai (1≦ai≦500)，代表明明生成的随机整数。

## 输出描述
输出若干行，每行输出一个整数，代表输入数据排序后的结果。第一行输出最小的数字。

## 示例1
输入：
```
3
2
2
1
```

输出：
```
1
2
```

## 解题思路
这道题目的解题思路主要分为两步：
1. 去重：使用集合（set）数据结构自动去除重复元素
2. 排序：利用set的自动排序特性获得有序结果

由于C++的set容器本身就具有去重和自动排序的特性，因此可以直接使用set来解决这个问题。

## 代码实现
```cpp
#include <iostream>
#include <set>
using namespace std;

// 函数：读取输入并存入集合
void readInput(set<int>& numSet, int n) {
    int num;
    for (int i = 0; i < n; i++) {
        cin >> num;
        numSet.insert(num);
    }
}

// 函数：输出结果
void printResult(const set<int>& numSet) {
    for (int num : numSet) {
        cout << num << endl;
    }
}

int main() {
    int n;
    cin >> n;
    
    // 使用set去重和排序
    set<int> numSet;
    
    // 读取输入
    readInput(numSet, n);
    
    // 输出结果
    printResult(numSet);
    
    return 0;
}
```

## 涉及的标准库及对应函数
1. `<iostream>`: 
   - `cin`: 用于从标准输入读取数据
   - `cout`: 用于向标准输出写入数据
   - `endl`: 插入换行符并刷新缓冲区

2. `<set>`: 
   - `set<int>`: 创建一个整数集合，自动实现去重和排序
   - `insert()`: 向集合中插入元素
   - 使用范围for循环遍历集合

## 解题模板
这道题目涉及的核心操作是：
1. 集合去重
2. 元素排序

可以作为以下问题的解题模板：
- 需要对数据去重并排序的问题
- 需要查找不重复元素的问题
- 统计不同元素个数的问题

标准库set是一个非常有用的数据结构，它基于红黑树实现，能在O(log n)时间复杂度内完成插入、删除和查找操作，同时自动维护元素的有序性和唯一性。
