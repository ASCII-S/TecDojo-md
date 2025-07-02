#include <iostream>
#include <memory>

class A
{

    explicit A(int a)
    {
        a++;
    }
    A(A &&rvalue)
    {
        a = rvalue.a;
    }
    A &operator=(A &&rvalue)
    {
        a = rvalue.a;
        return *this;
    }

private:
    int a;
};

class B
{
};

int main()
{
    B b;
    std::shared_ptr<B> b_ptr = std::make_shared<B>(b);
}