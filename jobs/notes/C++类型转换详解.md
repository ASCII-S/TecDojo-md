# C++类型转换详解

## 概述

C++提供了四种显式类型转换操作符，每种都有特定的用途和安全保证。相比于C风格的强制转换，这些操作符提供了更好的类型安全性和代码可读性。

## 1. static_cast

### 用途和特点
- **编译时检查**：在编译期进行类型检查，运行时无额外开销
- **类型安全转换**：用于已知安全的类型转换
- **适用场景**：
  - 基本数据类型之间的转换
  - 类层次结构中的上行转换（派生类到基类）
  - void*指针与具体类型指针的转换
  - 消除const属性（不推荐）

### 代码示例

```cpp
#include <iostream>

class Base {
public:
    virtual void show() { std::cout << "Base" << std::endl; }
};

class Derived : public Base {
public:
    void show() override { std::cout << "Derived" << std::endl; }
    void derivedMethod() { std::cout << "Derived method" << std::endl; }
};

int main() {
    // 基本类型转换
    double d = 3.14;
    int i = static_cast<int>(d);  // 3
    
    // 指针类型转换
    Derived* derived = new Derived();
    Base* base = static_cast<Base*>(derived);  // 上行转换，安全
    
    // void*转换
    void* vptr = derived;
    Derived* back = static_cast<Derived*>(vptr);
    
    delete derived;
    return 0;
}
```

## 2. dynamic_cast

### 用途和特点
- **运行时类型检查**：使用RTTI（运行时类型信息）进行安全转换
- **多态类型转换**：主要用于类层次结构中的安全下行转换
- **失败处理**：
  - 指针转换失败返回nullptr
  - 引用转换失败抛出std::bad_cast异常
- **要求**：只能用于包含虚函数的多态类

### 代码示例

```cpp
#include <iostream>
#include <typeinfo>

class Base {
public:
    virtual ~Base() = default;  // 虚析构函数
    virtual void show() { std::cout << "Base" << std::endl; }
};

class Derived : public Base {
public:
    void show() override { std::cout << "Derived" << std::endl; }
    void derivedMethod() { std::cout << "Derived method" << std::endl; }
};

class Another : public Base {
public:
    void show() override { std::cout << "Another" << std::endl; }
};

int main() {
    Base* base1 = new Derived();
    Base* base2 = new Another();
    
    // 成功的下行转换
    Derived* derived1 = dynamic_cast<Derived*>(base1);
    if (derived1) {
        derived1->derivedMethod();  // 成功执行
    }
    
    // 失败的下行转换
    Derived* derived2 = dynamic_cast<Derived*>(base2);
    if (!derived2) {
        std::cout << "Conversion failed" << std::endl;  // 会执行
    }
    
    // 引用转换（失败时抛异常）
    try {
        Derived& derivedRef = dynamic_cast<Derived&>(*base2);
    } catch (const std::bad_cast& e) {
        std::cout << "Reference cast failed: " << e.what() << std::endl;
    }
    
    delete base1;
    delete base2;
    return 0;
}
```

## 3. const_cast

### 用途和特点
- **const属性操作**：用于添加或移除const、volatile属性
- **唯一用途**：只能改变类型的const/volatile限定符
- **危险操作**：移除const可能导致未定义行为
- **应用场景**：与C API交互、临时移除const限制

### 代码示例

```cpp
#include <iostream>
#include <cstring>

void legacyFunction(char* str) {
    // 老式C函数，参数不是const，但实际不修改数据
    std::cout << "Legacy function: " << str << std::endl;
}

int main() {
    const char* constStr = "Hello World";
    
    // 移除const（危险操作）
    char* nonConstStr = const_cast<char*>(constStr);
    legacyFunction(nonConstStr);  // 合法调用
    
    // 注意：修改字符串字面量是未定义行为
    // nonConstStr[0] = 'h';  // 危险！可能崩溃
    
    // 添加const
    char buffer[] = "Modifiable";
    const char* constBuffer = const_cast<const char*>(buffer);
    
    // 安全的使用方式：只是为了调用接口
    const int value = 42;
    int* modifiable = const_cast<int*>(&value);
    // *modifiable = 43;  // 未定义行为，因为原始对象是const
    
    return 0;
}
```

## 4. reinterpret_cast

### 用途和特点
- **底层类型重解释**：将一种类型的位模式重新解释为另一种类型
- **最危险的转换**：几乎没有安全检查
- **适用场景**：
  - 指针与整数之间的转换
  - 不相关类型指针之间的转换
  - 函数指针转换
  - 底层内存操作

### 代码示例

```cpp
#include <iostream>

struct Point {
    int x, y;
};

int main() {
    // 指针与整数转换
    int value = 42;
    int* ptr = &value;
    
    // 指针转为整数
    uintptr_t addr = reinterpret_cast<uintptr_t>(ptr);
    std::cout << "Address: 0x" << std::hex << addr << std::endl;
    
    // 整数转为指针
    int* newPtr = reinterpret_cast<int*>(addr);
    std::cout << "Value: " << *newPtr << std::endl;  // 42
    
    // 不相关类型指针转换
    Point point = {10, 20};
    int* intPtr = reinterpret_cast<int*>(&point);
    std::cout << "First int: " << *intPtr << std::endl;      // 10
    std::cout << "Second int: " << *(intPtr + 1) << std::endl; // 20
    
    // 函数指针转换
    void (*funcPtr)() = reinterpret_cast<void(*)()>(addr);
    // 注意：调用funcPtr是危险的，这里只是演示
    
    return 0;
}
```

## 类型转换的内存行为

### 内存地址变化分析

```cpp
#include <iostream>

class Base {
public:
    virtual ~Base() = default;
    int baseData = 1;
};

class Derived : public Base {
public:
    int derivedData = 2;
};

int main() {
    Derived obj;
    std::cout << "Original object address: " << &obj << std::endl;
    
    // static_cast - 地址可能变化（虚函数表指针调整）
    Base* basePtr = static_cast<Base*>(&obj);
    std::cout << "static_cast address: " << basePtr << std::endl;
    
    // dynamic_cast - 地址可能变化
    Base* basePtrDynamic = dynamic_cast<Base*>(&obj);
    std::cout << "dynamic_cast address: " << basePtrDynamic << std::endl;
    
    // reinterpret_cast - 地址不变
    Base* basePtrReinterpret = reinterpret_cast<Base*>(&obj);
    std::cout << "reinterpret_cast address: " << basePtrReinterpret << std::endl;
    
    return 0;
}
```

### 关键点说明

1. **static_cast和dynamic_cast**：
   - 在复杂继承层次中可能调整指针值
   - 虚继承时地址调整更明显
   - 编译器自动处理偏移量

2. **const_cast**：
   - 不改变对象地址
   - 只改变类型的const/volatile属性

3. **reinterpret_cast**：
   - 永远不改变地址值
   - 只是重新解释内存中的位模式

## 性能对比

| 转换类型         | 编译时开销 | 运行时开销     | 安全性 | 推荐使用场景           |
| ---------------- | ---------- | -------------- | ------ | ---------------------- |
| static_cast      | 低         | 无             | 中等   | 已知安全的类型转换     |
| dynamic_cast     | 低         | 高（RTTI检查） | 高     | 多态类型的安全下行转换 |
| const_cast       | 无         | 无             | 低     | 与C API交互            |
| reinterpret_cast | 无         | 无             | 极低   | 底层内存操作           |

## 最佳实践

1. **优先级顺序**：static_cast > dynamic_cast > const_cast > reinterpret_cast
2. **避免C风格转换**：`(Type)value`不如显式转换安全
3. **dynamic_cast配合智能指针**：
   ```cpp
   if (auto derived = std::dynamic_pointer_cast<Derived>(basePtr)) {
       // 安全使用derived
   }
   ```
4. **const_cast谨慎使用**：确保不修改原本const的数据
5. **reinterpret_cast最后选择**：只在必要的底层操作中使用

## 面试重点

- **类型安全性**：static_cast编译期检查，dynamic_cast运行期检查
- **性能差异**：dynamic_cast有RTTI开销，其他基本无开销  
- **内存地址**：cast主要改变类型解释，地址变化取决于继承关系
- **使用场景**：根据安全性需求选择合适的转换方式 