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

## HJ2 计算某字符出现次数

### 题目描述

对于给定的由大小写字母、数字和空格混合构成的字符串s，统计字符c在其中出现的次数。具体来说：
- 若c为大写或者小写字母，统计其大小写形态出现的次数和；
- 若c为数字，统计其出现的次数。

保证字符c仅为大小写字母或数字。

### 输入描述

第一行输入一个长度1≤length(s)≤10³，由大小写字母、数字和空格混合构成的字符串s。保证首尾不为空格。

第二行输入一个字符c，代表需要统计的字符。

### 输出描述

在一行上输出一个整数，代表字符c在字符串s中出现的次数。

### 示例1

输入：
```
HELLONowcoder123
o
```

输出：
```
3
```

说明：由于o为小写字母，因此统计其小写形态和大写形态出现的次数和，即3。

### 示例2

输入：
```
H E L L O Nowcoder123
1
```

输出：
```
1
```

### C++解答

```cpp
#include <iostream>
#include <string>
#include <cctype>
using namespace std;

int main() {
    string s;
    char c;
    
    // 读取输入
    getline(cin, s);
    cin >> c;
    
    int count = 0;
    
    // 统计字符出现次数
    for (char ch : s) {
        if (isalpha(c)) {  // 如果c是字母，不区分大小写比较
            if (tolower(ch) == tolower(c)) {
                count++;
            }
        } else {  // 如果c是数字，直接比较
            if (ch == c) {
                count++;
            }
        }
    }
    
    cout << count << endl;
    return 0;
}
```

### 解题要点

1. **字符串处理**：使用getline读取可能包含空格的字符串。
   
2. **大小写不敏感**：对于字母字符，需要进行大小写不敏感的比较。
   - 可以使用C++的`tolower()`或`toupper()`函数将字符转换为小写或大写再比较。
   - 也可以使用ASCII码特性：大小写字母ASCII码差为32，'A'和'a'的ASCII码差为32。

3. **字符类型判断**：使用`isalpha()`函数判断是否为字母。

4. **时间复杂度**：O(n)，其中n为字符串长度。

5. **空间复杂度**：O(1)，只需常数空间。

6. **边界情况**：
   - 空格也是字符串的一部分，需要正确处理。
   - 如果字符串中没有出现目标字符，输出0。

### 头文件函数对应关系

1. \<iostream>
- 功能：提供输入输出流功能
- 使用的函数/对象：
  - cin：标准输入流对象，用于从控制台读取输入
  - cout：标准输出流对象，用于向控制台输出结果
  - endl：用于插入换行符并刷新输出缓冲区
2. \<string>
- 功能：提供字符串操作功能
- 使用的函数/对象：
  - string：字符串类，用于存储和处理字符串
  - getline(cin, s)：从输入流中读取一行文本并存储到字符串s中
3. \<cctype>
- 功能：提供字符处理函数
- 使用的函数：
  - isalpha(c)：检查字符c是否为字母（A-Z或a-z）
  - tolower(ch)：将字符ch转换为小写形式（如果是字母）

函数关系总结：
  - getline(cin, s)：从标准输入读取一行文本到字符串s中
  - cin >> c：从标准输入读取一个字符到变量c中
  - isalpha(c)：判断字符c是否为字母
  - tolower(ch)和tolower(c)：将字符转换为小写，用于不区分大小写的比较
  - cout << count << endl：输出统计结果并换行
