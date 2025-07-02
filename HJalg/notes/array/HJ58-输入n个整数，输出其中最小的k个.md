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

## 题目描述
对于输入的n个整数，升序输出其中最小的k个。

## 输入描述
第一行输入两个整数n,k(1≦n≦1000; 1≦k≦n)。
第二行输入n个整数a₁,a₂,...,aₙ(1≦aᵢ≦10⁴)代表给定的数字。

## 输出描述
在一行中升序输出最小的k个整数。

## 示例1
输入：
```
5 2
1 3 5 7 2
```

输出：
```
1 2
```

## 解题思路
1. 这是一个典型的TopK问题，即找出一组数据中最小的k个数。
2. 有多种方法可以解决：
   - 直接排序法：将所有数排序后取前k个
   - 局部排序法：只对前k个数排序
   - 堆排序法：维护一个大小为k的最大堆
   - 快速选择法：基于快速排序的partition思想

3. 由于数据规模较小(n≤1000)，可以直接使用排序算法。如果数据量更大，可考虑使用堆排序或快速选择算法。

## 代码实现
```cpp
#include <iostream>
#include <vector>
#include <algorithm>
using namespace std;

// 方法一：直接排序
void findKSmallest_sort(vector<int>& nums, int k) {
    sort(nums.begin(), nums.end());
    for (int i = 0; i < k; i++) {
        cout << nums[i];
        if (i < k - 1) cout << " ";
    }
    cout << endl;
}

// 方法二：使用堆
void findKSmallest_heap(vector<int>& nums, int k) {
    vector<int> result;
    // STL中的优先队列默认是最大堆，这里使用堆排序
    make_heap(nums.begin(), nums.end(), less<int>());
    for (int i = 0; i < k; i++) {
        // 每次弹出堆顶（最大值），调整堆
        pop_heap(nums.begin(), nums.end() - i, less<int>());
    }
    // 取末尾k个元素（即最小的k个），并进行排序
    sort(nums.end() - k, nums.end());
    for (int i = 0; i < k; i++) {
        cout << nums[nums.size() - k + i];
        if (i < k - 1) cout << " ";
    }
    cout << endl;
}

int main() {
    int n, k;
    cin >> n >> k;
    
    vector<int> nums(n);
    for (int i = 0; i < n; i++) {
        cin >> nums[i];
    }
    
    // 使用直接排序方法
    findKSmallest_sort(nums, k);
    
    return 0;
}
```

## 涉及的标准库及对应函数
1. `<iostream>` - 输入输出流
   - `cin` - 标准输入
   - `cout` - 标准输出
2. `<vector>` - 动态数组
   - `push_back()` - 添加元素
   - `begin()`, `end()` - 获取迭代器
3. `<algorithm>` - 算法库
   - `sort()` - 排序
   - `make_heap()` - 构建堆
   - `pop_heap()` - 弹出堆顶元素并调整

## 解题模板
1. **TopK问题模板**：找出数组中最小的k个数或最大的k个数
   - 直接排序：O(nlogn)时间复杂度
   - 堆排序：O(nlogk)时间复杂度，适合处理大数据量
   - 快速选择：平均O(n)时间复杂度，最坏O(n²)

2. **排序相关操作**：
   - 升序排序：`sort(nums.begin(), nums.end())`
   - 降序排序：`sort(nums.begin(), nums.end(), greater<int>())`

3. **堆操作**：
   - 建立最大堆：`make_heap(nums.begin(), nums.end())`
   - 建立最小堆：`make_heap(nums.begin(), nums.end(), greater<int>())`
