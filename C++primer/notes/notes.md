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
    cell_metadata_filter: -all
    formats: md,ipynb
    main_language: python
    text_representation:
      extension: .md
      format_name: markdown
      format_version: '1.3'
      jupytext_version: 1.14.7
---

# 碎片知识索引
[[StrBlob.h]]
## 🧠 核心概念
- [[左值和右值]] - C++中的值类别及其区别
- [[移动语义]] - C++11引入的高效资源转移机制
- [[完美转发]] - 保持参数类型信息的转发技术
- [[类的继承]] - C++面向对象编程中的继承机制
- [[虚函数]] - C++多态实现的基础
- [[模板]] - C++泛型编程基础

## 🛠️ 技术实现
- [[std--unique_ptr + std--make_unique]] - 智能指针实现
- [[shared_ptr]] - 共享所有权的智能指针
- [[make_unique]] - C++14引入的智能指针工厂函数
- [[placement new]] - 在预分配内存上构造对象
- [[memcpy]] - 内存复制操作
- [[memset]] - 内存设置操作
- [[reinterpret_cast]] - 底层类型转换
- [[原始指针迭代器]] - 使用原始指针作为迭代器

## ⚠️ 常见问题与陷阱
- [[拷贝vs移动]] - 拷贝与移动操作的性能对比
- [[拷贝构造函数]] - 对象拷贝的实现方法
- [[移动构造函数]] - 优化资源转移的构造函数
- [[noexcept]] - 异常说明符的使用
- [[string literal 和string,这两种类型有什么区别呢]] - 字符串字面值与string对象的差异
- [[如何查看复杂数组定义]] - 理解复杂数组类型声明

## 📊 示例与实践
- [[示例-分离式编译]] - C++多文件项目组织方式
- [[一个文件操作的完整示例]] - 文件读写完整流程
- [[将 fstream 用作文件流的简单示例]] - 文件流基本操作
- [[文件读取与异常处理]] - 文件操作中的异常处理方式
- [[示例 1：拼接字符串]] - 字符串连接操作示例
- [[示例 2：动态生成格式化字符串]] - 字符串格式化示例
- [[示例 1：按单词读取]] - 读取输入流中的单词
- [[示例 2：解析格式化数据]] - 解析特定格式数据

#C++ #学习笔记 
