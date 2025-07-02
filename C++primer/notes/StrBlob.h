#ifndef STRBLOB_H
#define STRBLOB_H

//=============================================================================
// 【学习笔记】StrBlob 类与智能指针的使用
//=============================================================================
// 本文件实现了两个类：
// 1. StrBlob：管理字符串集合的类，使用 shared_ptr 共享底层数据
// 2. StrBlobPtr：类似迭代器，使用 weak_ptr 避免循环引用问题
//
// 主要展示的 C++11 特性：
// - 智能指针（shared_ptr 和 weak_ptr）
// - 初始化列表
// - 类型别名（typedef）
// - 友元类和友元函数
// - 异常处理
//=============================================================================

#include <vector>
#include <string>
#include <initializer_list>
#include <memory>           // 智能指针的头文件
#include <stdexcept>        // 异常处理的头文件

// 前向声明，使 StrBlob 能够将 StrBlobPtr 声明为友元
class StrBlobPtr;

//-----------------------------------------------------------------------------
// StrBlob 类：管理字符串集合
//-----------------------------------------------------------------------------
class StrBlob {
	friend class StrBlobPtr;  // 友元类声明，允许 StrBlobPtr 访问 StrBlob 的私有成员
public:
    // 类型别名，简化代码
    typedef std::vector<std::string>::size_type size_type;

	// constructors
    StrBlob() : data(std::make_shared<std::vector<std::string>>()) { }
    StrBlob(std::initializer_list<std::string> il);

	// size operations
    size_type size() const { return data->size(); }
    bool empty() const { return data->empty(); }

    // add and remove elements
    void push_back(const std::string &t) { data->push_back(t); }
    void pop_back();

    // element access
    std::string& front();
    std::string& back();

	// interface to StrBlobPtr
	StrBlobPtr begin();  // can't be defined until StrBlobPtr is
    StrBlobPtr end();
private:
    // 核心数据成员：指向 vector<string> 的共享指针
    // 使用 shared_ptr 让多个 StrBlob 对象可以共享相同的 vector
    std::shared_ptr<std::vector<std::string>> data; 
    
    // 辅助函数：检查索引是否有效，无效则抛出异常
    void check(size_type i, const std::string &msg) const;
};

// constructor
inline
StrBlob::StrBlob(std::initializer_list<std::string> il): 
              data(std::make_shared<std::vector<std::string>>(il)) { }

// StrBlobPtr throws an exception on attempts to access a nonexistent element 
class StrBlobPtr {
	friend bool eq(const StrBlobPtr&, const StrBlobPtr&);
public:
    StrBlobPtr(): curr(0) { }
    StrBlobPtr(StrBlob &a, size_t sz = 0): wptr(a.data), curr(sz) { }

    std::string& deref() const;
    StrBlobPtr& incr();       // prefix version
    StrBlobPtr& decr();       // prefix version
private:
    // 检查函数：返回一个指向 vector 的 shared_ptr
    std::shared_ptr<std::vector<std::string>> 
        check(std::size_t, const std::string&) const;

    // 成员变量
    // 使用 weak_ptr 避免循环引用，StrBlob 被销毁后，StrBlobPtr 不会保持底层 vector 的生命周期
    std::weak_ptr<std::vector<std::string>> wptr;  
    std::size_t curr;      // 当前位置
};

inline
std::string& StrBlobPtr::deref() const
{
    auto p = check(curr, "dereference past end"); 
    return (*p)[curr];  // (*p) 是该对象指向的 vector
}

inline
std::shared_ptr<std::vector<std::string>> 
StrBlobPtr::check(std::size_t i, const std::string &msg) const
{
    auto ret = wptr.lock();   // 尝试获取 shared_ptr，检查 vector 是否还存在
    if (!ret)
        throw std::runtime_error("unbound StrBlobPtr");  // vector 已被销毁

    if (i >= ret->size()) 
        throw std::out_of_range(msg);  // 索引越界
    return ret;  // 返回指向 vector 的 shared_ptr
}

// prefix: return a reference to the incremented object
inline
StrBlobPtr& StrBlobPtr::incr()
{
    // 检查是否已经指向容器末尾
    check(curr, "increment past end of StrBlobPtr");
    ++curr;       // 推进一个位置
    return *this;
}

inline
StrBlobPtr& StrBlobPtr::decr()
{
    // 先递减，再检查有效性
    --curr;       // 后退一个位置
    check(-1, "decrement past begin of StrBlobPtr");
    return *this;
}

// begin and end members for StrBlob
inline
StrBlobPtr
StrBlob::begin() 
{
	return StrBlobPtr(*this);
}

inline
StrBlobPtr
StrBlob::end() 
{
	auto ret = StrBlobPtr(*this, data->size());
    return ret; 
}

// named equality operators for StrBlobPtr
inline
bool eq(const StrBlobPtr &lhs, const StrBlobPtr &rhs)
{
	auto l = lhs.wptr.lock(), r = rhs.wptr.lock();
	// if the underlying vector is the same 
	if (l == r) 
		// then they're equal if they're both null or 
		// if they point to the same element
		return (!r || lhs.curr == rhs.curr);
	else
		return false; // if they point to difference vectors, they're not equal
}

inline
bool neq(const StrBlobPtr &lhs, const StrBlobPtr &rhs)
{
	return !eq(lhs, rhs); 
}
#endif