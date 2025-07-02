# const函数签名与匹配规则

const在函数签名中的不同位置有不同的含义，影响函数重载和匹配规则。

## const成员函数

```cpp
class TestClass {
public:
    void foo() { std::cout << "non-const version\n"; }
    void foo() const { std::cout << "const version\n"; }
    
    int getValue() const { return value; }  // const成员函数
    void setValue(int v) { value = v; }     // 非const成员函数
    
private:
    int value = 0;
};
```

## const参数

```cpp
void func(const int& param) { /* 参数不可修改 */ }
void func(int param) { /* 可以修改参数副本 */ }
```

## 函数匹配规则

### const对象只能调用const成员函数
```cpp
const TestClass const_obj;
const_obj.foo();       // 调用const版本
// const_obj.setValue(5); // 错误：不能调用非const函数
```

### 非const对象优先选择非const版本
```cpp
TestClass obj;
obj.foo();             // 调用非const版本
obj.getValue();        // 可以调用const版本
```

## 隐式转换规则

```cpp
void process(const std::string& str) { /* ... */ }

process("hello");      // const char* -> std::string (隐式转换)
process(std::string("world")); // 直接匹配
``` 