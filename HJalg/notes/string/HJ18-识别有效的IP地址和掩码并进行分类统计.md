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

## 题目描述
识别有效的IP地址和掩码并进行分类统计。需要处理IP地址和子网掩码，这两者均形如"*.*.*.*"，由四段数字组成，每段数字之间以点分隔。

我们定义五类IP地址：
- A类："1.0.0.0"-"126.255.255.255"
- B类："128.0.0.0"-"191.255.255.255"
- C类："192.0.0.0"-"223.255.255.255"
- D类："224.0.0.0"-"239.255.255.255"
- E类："240.0.0.0"-"255.255.255.255"

私有IP地址：
- "10.0.0.0"-"10.255.255.255"
- "172.16.0.0"-"172.31.255.255"
- "192.168.0.0"-"192.168.255.255"

合法的子网掩码：
- 将IP地址转换为二进制后，必须由若干个连续的1后跟若干个连续的0组成
- 全为1或全为0的子网掩码也是非法的

特别地："0.*.*.*"和"127.*.*.*"的IP地址不属于上述任意一类，也不属于不合法IP地址；一个IP地址既可以是私有IP地址，也可以是五类IP地址之一，计数时请分别计入。

## 输入描述
本题将会给出1≦T≦1000条地址信息，确切数字未知，需要一直读入直到文件结尾。每条地址信息描述如下：在一行上先输入一个字符串，代表IP地址；随后，在同一行输入一个字符串，代表子网掩码；使用" "分隔。

## 输出描述
在一行上输出七个整数，分别代表ABCDE类地址的数量、错误IP或错误子网掩码的数量、私有IP的数量。

## 示例1
输入：
```
10.70.44.68~1.1.1.5
1.0.0.1~255.0.0.0
192.168.0.2~255.255.255.0
19..0.~255.255.255.0
```

输出：
```
1 0 1 0 0 2 1
```

说明：
- 第一条地址信息，"10.70.44.68"是其IP地址，"255.254.255.0"是其子网掩码；该条地址的子网掩码非法。
- 第二条地址信息，IP地址和子网掩码均无误，且属于A类地址。
- 第三条地址信息，IP地址和子网掩码均无误，且属于C类地址，同时属于私有IP地址。
- 第四条地址信息，IP地址非法。

## 示例2
输入：
```
0.201.56.50~255.255.255.0
127.201.56.50~255.255.111.255
```

输出：
```
0 0 0 0 0 0 0
```

说明：
- 第一、二条地址信息，均属于特殊IP地址，不需要处理，直接跳过。
- 特别地，第二条地址的子网掩码是非法的。但是因为该条为特殊IP地址，此优先级更高，所以不进入统计。

## 解题思路
这道题需要我们处理IP地址和子网掩码的有效性，并进行分类统计。解题思路如下：

1. 解析输入的IP地址和子网掩码，检查格式是否正确
2. 判断IP地址是否为特殊IP（0开头或127开头），如是则跳过
3. 检查IP地址和子网掩码的合法性
4. 判断合法IP地址所属的类别（A、B、C、D、E）
5. 判断IP地址是否为私有IP
6. 统计并输出结果

## 代码实现

```cpp
#include <iostream>
#include <array>
#include <bitset>
#include <string>
using namespace std;

bool parseIpMasked(const string line, array<int,4>& ip, array<int,4>& masked){
    string strnum = "";
    bool ipflag = false;
    int index = 0;
    for (char c:line){
        // 读一小段数
        if (!(c=='.' || c=='~')){
            strnum += c;
        }
        // 存一小段数
        else {
            // 区分ip和子网掩码的部分
            if (c=='~'){
                ipflag = true;
                if (strnum == "") return false;
                ip[3] = stoi(strnum);
                index = 0;
            }
            // 区分地址的各字段
            else if(c=='.'){
                if (strnum == "") return false;
                if(!ipflag) ip[index++] = stoi(strnum);
                else masked[index++] = stoi(strnum);
            }
            strnum = "";
        }
    }
    if (strnum == "") return false;
    masked[3] = stoi(strnum);

    // 验证IP和掩码每段都在0-255范围内
    for(int i = 0; i < 4; i++) {
        if(ip[i] < 0 || ip[i] > 255 || masked[i] < 0 || masked[i] > 255)
            return false;
    }

    return true;
}

bool isBadMasked(const array<int,4>& masked){
    string maskedline="";
    // 将子网掩码所有部分转化成二进制字符串
    for (int i:masked){
        bitset<8> bit(i);
        string bitstr = bit.to_string();
        maskedline += bitstr;
    }

    // 判断二进制字符串是否符合合法子网掩码规定
    bool flag0 = false;
    for (char c: maskedline){
        if (c=='0' && !flag0)   flag0 = true;
        if (flag0){
            if (c == '1')   return true;
        }
    }

    if (maskedline[0] == '0' || flag0 == false) return true;
    
    return false;
}

char isFiveIp(const array<int,4>& ip){
    int head = ip[0];
    if(head >= 1 && head <= 126)    return 'A';
    if(head >= 128 && head <= 191)    return 'B'; // 修正B类范围
    if(head >= 192 && head <= 223)    return 'C';
    if(head >= 224 && head <= 239)    return 'D';
    if(head >= 240 && head <= 255)    return 'E';
    return 'X';
}

bool isPrivateIp(const array<int,4>& ip){
    if (ip[0]==10)  return true;
    if (ip[0]==172 && (ip[1]>=16 && ip[1]<=31)) return true;
    if (ip[0]==192 && ip[1]==168)   return true;
    return false;
}

bool isLocalIp(const array<int,4>& ip){
    return ip[0]==0 || ip[0]==127;
}

bool isBadIp(const array<int,4>& ip){
    return isFiveIp(ip) == 'X' && !isLocalIp(ip);
}

void resUpdate(const array<int,4>& ip, const array<int,4>& masked, array<int,7>& rescount){
    // 特殊IP直接跳过
    if(isLocalIp(ip)) {
        return;
    }
    
    // 检查IP和掩码是否合法
    if (isBadMasked(masked) || isBadIp(ip)){
        rescount[5]++;
        return;
    }
    
    // 统计IP类型
    char fClass = isFiveIp(ip);
    if (fClass != 'X'){
        rescount[(fClass - 'A')]++;
    }
    
    // 统计私有IP
    if (isPrivateIp(ip)){
        rescount[6]++;
    }
}

void printRes(const array<int,7>& rescount){
    for(int i=0; i<rescount.size(); i++){
        cout << rescount[i];
        if (i!=rescount.size()-1){
            cout << ' ';
        }
    }
}

int main() {
    array<int,7> rescount = {0};
    string line;
    while(getline(cin, line)){ // 修改为getline读取整行
        if(line.empty()) continue;
        
        array<int,4> ip = {0};
        array<int,4> masked = {0};
        bool parseSuccess = parseIpMasked(line,ip,masked);
        if (!parseSuccess){
            rescount[5]++;
            continue;
        }
        resUpdate(ip,masked,rescount);
    }
    printRes(rescount);
    return 0;
}
```
## 涉及的标准库及对应函数
- `<iostream>`: 用于输入输出操作（cin, cout, endl）
- `<string>`: 字符串处理（string类型，substr, find等方法）
- `<vector>`: 动态数组容器（vector类型，push_back等方法）
- `<sstream>`: 字符串流处理（istringstream类，getline方法）

函数：
- `stoi()`: 将字符串转换为整数
- `isdigit()`: 检查字符是否为数字
- `getline()`: 读取流中的一行或者分隔符之前的内容
  - 使用分割方式: `getline(iss, segment, '.')`
## 解题模板

### 子网掩码验证模板

```cpp
/**
 * 验证子网掩码是否合法
 * 合法的子网掩码：二进制表示中必须是连续的1后跟连续的0，且不能全0或全1
 */
bool isValidMask(const vector<int>& mask) {
    // 转换为32位整数
    unsigned int binary = 0;
    for (int i = 0; i < 4; i++) {
        binary = (binary << 8) | mask[i];
    }
    
    // 方法1：直接检查
    bool foundZero = false;
    bool foundOne = false;
    
    for (int i = 31; i >= 0; i--) {
        bool bit = (binary & (1u << i)) != 0;
        if (bit) { // 当前位是1
            foundOne = true;
            if (foundZero) { // 如果之前已经有0，现在又出现1，则不合法
                return false;
            }
        } else { // 当前位是0
            foundZero = true;
        }
    }
    
    // 全0或全1的掩码不合法
    return foundOne && foundZero;
    
    /* 方法2：使用位运算技巧
    // 全0和全1的特殊情况
    if (binary == 0 || binary == 0xFFFFFFFF) return false;
    
    // 检查是否是连续的1后跟连续的0
    // binary+1后，与binary按位与，如果结果为0，则说明是合法的子网掩码
    return ((binary + 1) & binary) == 0;
    */
}
```

### IP地址解析处理模板

```cpp
/**
 * 解析IP地址字符串为四个整数
 * 返回值：是否解析成功
 */
bool parseIPAddress(const string& ipStr, vector<int>& segments) {
    segments.clear();
    istringstream iss(ipStr);
    string segment;
    
    while (getline(iss, segment, '.')) {
        // 检查段是否为空
        if (segment.empty()) return false;
        
        // 检查是否都是数字
        for (char c : segment) {
            if (!isdigit(c)) return false;
        }
        
        // 转换为整数并验证范围
        int value;
        try {
            value = stoi(segment);
            if (value < 0 || value > 255) return false;
        } catch (...) {
            return false;
        }
        
        segments.push_back(value);
    }
    
    // 必须有4段
    return segments.size() == 4;
}

/**
 * 判断IP地址类型
 * 返回值：'A'-'E'表示相应类别，'X'表示不属于这五类
 */
char getIPClass(const vector<int>& ip) {
    int firstOctet = ip[0];
    
    if (firstOctet >= 1 && firstOctet <= 126) return 'A';
    if (firstOctet >= 128 && firstOctet <= 191) return 'B';
    if (firstOctet >= 192 && firstOctet <= 223) return 'C';
    if (firstOctet >= 224 && firstOctet <= 239) return 'D';
    if (firstOctet >= 240 && firstOctet <= 255) return 'E';
    
    return 'X'; // 特殊IP或非法IP
}

/**
 * 判断是否为私有IP
 */
bool isPrivateIP(const vector<int>& ip) {
    // 10.0.0.0 - 10.255.255.255
    if (ip[0] == 10) return true;
    
    // 172.16.0.0 - 172.31.255.255
    if (ip[0] == 172 && ip[1] >= 16 && ip[1] <= 31) return true;
    
    // 192.168.0.0 - 192.168.255.255
    if (ip[0] == 192 && ip[1] == 168) return true;
    
    return false;
}

/**
 * 判断是否为特殊IP (0.*.*.* 或 127.*.*.*)
 */
bool isSpecialIP(const vector<int>& ip) {
    return ip[0] == 0 || ip[0] == 127;
}
```

### 位运算操作
- ip地址的位运算:
  - `binary = (binary << 8) | mask[i];`
  - 左移运算 (<< 8)​:
    - binary << 8 将变量 binary 的当前值向左移动8位
    - 这相当于将原值乘以2⁸ (256)
    - 移动后，最低的8位变为0
  - 按位或运算 (|)​:
    - | mask[i] 将左移后的值与 mask[i] 进行按位或操作
    - mask[i] 应该是一个8位值(0-255)
    - 这个操作将 mask[i] 的值填充到 binary 的最低8位

### 处理分段数据的通用模式

对于像IP地址这样的分段数据，通常采用以下处理模式：

1. **分割策略**：
   - 使用字符串流和getline按分隔符分割
   - 手动迭代字符串，寻找分隔符

2. **验证策略**：
   - 检查每个段的格式（非空、只包含数字等）
   - 将段转换为目标类型（如整数）
   - 验证数值范围

3. **异常处理**：
   - 处理转换过程中可能出现的异常
   - 对无效输入提供适当的错误处理

4. **特殊情况处理**：
   - 对特殊格式或值进行专门处理
   - 根据业务需求实现特定的验证逻辑

这些模板可以方便地扩展到其他需要处理分段数据的场景，如MAC地址、日期时间格式等。

