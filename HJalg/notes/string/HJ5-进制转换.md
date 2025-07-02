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

# HJ5 进制转换

## 题目描述

对于给定的十六进制数，输出其对应的十进制表示。

在本题中，十六进制数的格式为：`0x`开头，后跟若干个十六进制数字（`0-9`和`A-F`）。其中，`A-F`依次代表`10-15`。

## 题目难度

- 难度：简单
- 通过率：36.85%
- 时间限制：1秒
- 空间限制：32M

## 知识点

- 字符串处理
- 进制转换

## 输入描述

在一行上输入一个十六进制数`s`，代表待转换的十六进制数。
保证`s`转化得到的十进制数`x`的范围为`1≦x≦2^31−1`。

## 输出描述

在一行上输出一个整数，代表`s`对应的十进制数。

## 示例

### 示例1

输入：
```
0xFA93
```

输出：
```
64147
```

### 说明

回忆十六进制转化为十进制的方法：从右往左，将第`i`位乘以`16^i`，然后求和。

在这个样例中，`0xFA93`的第`0`位是`3`，第`1`位是`9`，第`2`位是`A`，第`3`位是`F`，因此：
`0xFA93 = 3×16^0 + 9×16^1 + 10×16^2 + 15×16^3 = 64147`

## 解题思路

### 问题分析
- 输入：一个以"0x"开头的十六进制字符串
- 输出：对应的十进制数值
- 转换规则：从右往左，第i位上的数字乘以16^i，然后求和

### 模块化解决方案

#### 1. 输入处理模块
- 读取输入字符串
- 去除"0x"前缀

#### 2. 字符转数值模块
- 将十六进制字符转换为对应的数值
- '0'-'9' 对应 0-9
- 'A'-'F'/'a'-'f' 对应 10-15

#### 3. 进制转换模块
- 对每一位字符执行转换
- 累加乘以权重后的结果

#### 4. 输出模块
- 输出最终的十进制结果

## 代码实现

```cpp
#include <iostream>
#include <string>
#include <cmath>
using namespace std;

// 模块1：输入处理
string getHexString() {
    string hexStr;
    cin >> hexStr;
    
    // 去掉"0x"前缀
    if (hexStr.substr(0, 2) == "0x") {
        hexStr = hexStr.substr(2);
    }
    
    return hexStr;
}

// 模块2：字符转数值
int hexCharToDecimal(char c) {
    if (c >= '0' && c <= '9') {
        return c - '0';
    } else if (c >= 'A' && c <= 'F') {
        return c - 'A' + 10;
    } else if (c >= 'a' && c <= 'f') {
        return c - 'a' + 10;
    }
    return 0; // 出错情况，正常不会出现
}

// 模块3：进制转换
int hexStringToDecimal(const string& hexStr) {
    int result = 0;
    int len = hexStr.length();
    
    for (int i = 0; i < len; i++) {
        // 从左到右处理，result乘以16相当于左移一位
        result = result * 16 + hexCharToDecimal(hexStr[i]);
    }
    
    return result;
}

// 主函数
int main() {
    string hexStr = getHexString();
    int decimal = hexStringToDecimal(hexStr);
    cout << decimal << endl;
    return 0;
}
```

## 涉及的标准库函数

| 头文件 | 函数 | 功能 |
|-------|------|-----|
| `<iostream>` | `cin`, `cout`, `endl` | 用于输入输出 |
| `<string>` | `string`, `substr()`, `length()` | 字符串处理，提取子串，获取长度 |

## 解题模板

### 任意进制转十进制模板

```cpp
// 字符转数值通用函数
int charToValue(char c, int base) {
    if (c >= '0' && c <= '9') {
        return c - '0';
    } else if (c >= 'A' && c <= 'Z') {
        return c - 'A' + 10;
    } else if (c >= 'a' && c <= 'z') {
        return c - 'a' + 10;
    }
    return 0;
}

// 任意进制转十进制
int anyBaseToDecimal(const string& str, int base) {
    int result = 0;
    
    for (char c : str) {
        result = result * base + charToValue(c, base);
    }
    
    return result;
}
```

### 十进制转任意进制模板

```cpp
// 十进制转任意进制
string decimalToAnyBase(int num, int base) {
    if (num == 0) return "0";
    
    string result;
    char digits[] = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    
    while (num > 0) {
        result = digits[num % base] + result;
        num /= base;
    }
    
    return result;
}
```

## 解题要点与优化

1. **时间复杂度**: O(n)，其中n为输入字符串的长度
2. **空间复杂度**: O(1)，只使用了常数额外空间

3. **优化点**:
   - 可以直接使用C++标准库提供的`stoi()`函数，指定基数为16
   - 例如：`int decimal = stoi(hexStr, nullptr, 16);`
     - stoi的各参数：
       - 第一个参数：待转换的字符串
       - 第二个参数：转换失败时返回的指针，一般用nullptr
       - 第三个参数：基数，这里为16

4. **扩展思路**:
   - 本题可以扩展为任意进制之间的转换
   - 通常先将源进制转为十进制，再从十进制转为目标进制
