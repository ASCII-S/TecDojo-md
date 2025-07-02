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
MP3 Player因为屏幕较小，显示歌曲列表的时候每屏只能显示几首歌曲，用户要通过上下键才能浏览所有的歌曲。为了简化处理，假设每屏只能显示4首歌曲，光标初始的位置为第1首歌。

现在要实现通过上下键控制光标移动来浏览歌曲列表，控制逻辑如下：

1. 歌曲总数<=4的时候，不需要翻页，只是挪动光标位置。
   - 光标在第一首歌曲上时，按Up键光标挪到最后一首歌曲；光标在最后一首歌曲时，按Down键光标挪到第一首歌曲。
   - 其他情况下用户按Up键，光标挪到上一首歌曲；用户按Down键，光标挪到下一首歌曲。

2. 歌曲总数大于4的时候（以一共有10首歌为例）：
   - 特殊翻页：屏幕显示的是第一页（即显示第1 – 4首）时，光标在第一首歌曲上，用户按Up键后，屏幕要显示最后一页（即显示第7-10首歌），同时光标放到最后一首歌上。同样的，屏幕显示最后一页时，光标在最后一首歌曲上，用户按Down键，屏幕要显示第一页，光标挪到第一首歌上。
   - 一般翻页：屏幕显示的不是第一页时，光标在当前屏幕显示的第一首歌曲时，用户按Up键后，屏幕从当前歌曲的上一首开始显示，光标也挪到上一首歌曲。光标当前屏幕的最后一首歌时的Down键处理也类似。
   - 其他情况，不用翻页，只是挪动光标就行。

## 输入描述
1. 输入歌曲数量
2. 输入命令 U或者D

## 输出描述
1. 输出当前列表
2. 输出当前选中歌曲

## 示例1
输入：
```
10
UUUU
```

输出：
```
7 8 9 10
7
```

## 解题思路
这是一个模拟题，我们需要模拟MP3光标的移动逻辑。主要分为以下几种情况：

1. 当歌曲总数小于等于4时，不需要翻页：
   - 只需要考虑光标位置的变化
   - 边界情况：按Up键从第一首跳到最后一首，按Down键从最后一首跳到第一首

2. 当歌曲总数大于4时，需要考虑页面显示和光标位置的同步变化：
   - 维护一个表示当前页面第一首歌曲的索引和当前光标位置的索引
   - 根据不同的按键和光标位置情况，更新这两个索引

关键是处理各种边界情况，尤其是需要翻页的情况。

## 代码实现
```cpp
#include <iostream>
#include <string>
#include <vector>
using namespace std;

// 处理光标移动函数
void processCursorMovement(int n, const string& commands) {
    const int PAGE_SIZE = 4; // 每页显示的歌曲数
    int cursorPos = 1;       // 光标位置，从1开始
    int pageStart = 1;       // 当前页面起始歌曲编号，从1开始

    for (char cmd : commands) {
        if (cmd == 'U') {
            // 向上移动光标
            if (cursorPos == 1) {
                // 光标在第一首歌曲，特殊处理
                cursorPos = n;
                // 计算最后一页的起始位置
                pageStart = max(1, n - PAGE_SIZE + 1);
            } else {
                // 光标上移
                cursorPos--;
                // 检查是否需要翻页
                if (cursorPos < pageStart) {
                    pageStart = cursorPos;
                }
            }
        } else if (cmd == 'D') {
            // 向下移动光标
            if (cursorPos == n) {
                // 光标在最后一首歌曲，特殊处理
                cursorPos = 1;
                pageStart = 1;
            } else {
                // 光标下移
                cursorPos++;
                // 检查是否需要翻页
                if (cursorPos > pageStart + PAGE_SIZE - 1) {
                    pageStart = cursorPos - PAGE_SIZE + 1;
                }
            }
        }
    }

    // 输出当前页面显示的歌曲列表
    int pageEnd = min(pageStart + PAGE_SIZE - 1, n);
    for (int i = pageStart; i <= pageEnd; i++) {
        cout << i;
        if (i < pageEnd) cout << " ";
    }
    cout << endl;
    
    // 输出当前光标位置的歌曲
    cout << cursorPos << endl;
}

int main() {
    int n;
    string commands;
    
    // 输入歌曲数量和命令
    cin >> n >> commands;
    
    // 处理光标移动
    processCursorMovement(n, commands);
    
    return 0;
}
```

```cpp
#include <iostream>
#include <array>
#include <vector>
using namespace std;

void toLastPage(array<int, 4>& page, array<int, 4>& lastpage, int& curCursor, int total){
    page = lastpage;
    curCursor = total>4?3:total-1;
}

void toFirstPage(array<int, 4>& page, array<int, 4>& firstpage, int& curCursor){
    page = firstpage;
    curCursor = 0;
}

void rollPage(array<int, 4>& page, int offset){
    for (int &songnum:page){
        songnum += offset;
    }
}

int main() {
    int total;
    string insline;
    cin >> total;
    cin >> insline;
    int curCursor = 0;
    array<int, 4> page;
    array<int, 4> firstpage;
    array<int, 4> lastpage;
    
    if (total < 4) {
        for (int i = 0; i < total; i++) {
            page[i] = i + 1;
        }
        firstpage = lastpage = page;
    } 
    else {
        firstpage = page = {1, 2, 3, 4};
        lastpage = {total-3,total-2,total-1,total};
    }

    // 开始接受指令
    for (char ins : insline) {
        if (ins == 'U') {
            if (page[curCursor] == 1) {
                toLastPage(page, lastpage, curCursor, total);
            }
            else if (curCursor == 0) {
                rollPage(page, -1);
            }
            else {
                curCursor --;
            }
        }
        if (ins == 'D') {
            if (page[curCursor] == total) {
                toFirstPage(page, firstpage, curCursor);
            }
            else if (curCursor == 3) {
                rollPage(page, 1);
            }
            else{
                curCursor ++;
            }
        }
        // cout << ins << endl;
        // cout << "curCursor:\t" << curCursor << endl;
        // cout << "page:\t" ;
        // for (int i:page)    cout << i << ' ';
        // cout <<"\n\n";
    }

    int pageMaxCursor = min(4,total);
    for(int i =0; i< pageMaxCursor; i++){
        cout << page[i];
        if (i!=pageMaxCursor-1)   cout << ' ';
    }
    cout << endl << page[curCursor];
}
// 64 位输出请用 printf("%lld")
```
## 涉及的标准库及对应函数
1. `<iostream>`: 用于输入输出
   - `cin`: 标准输入
   - `cout`: 标准输出
   - `endl`: 换行符

2. `<string>`: 用于字符串处理
   - `string`: 字符串类

3. `<vector>`: 用于动态数组（虽然代码中没有直接使用，但在思考过程中可能会考虑）

4. 常用的函数：
   - `max()`: 返回两个值中的较大值
   - `min()`: 返回两个值中的较小值

## 解题模板
本题是一个经典的模拟题，可以提取以下解题模板：

1. **界面翻页与光标移动模拟**：
   - 维护当前页面的起始位置和光标位置
   - 处理各种边界情况，特别是循环移动和翻页
   
2. **状态转移逻辑**：
   - 根据当前状态和输入命令，确定下一个状态
   - 分类讨论不同情况下的状态转移规则

这类题目的关键在于理清各种情况，尤其是边界情况的处理。还需要注意索引的管理，如从0开始还是从1开始。
