#include <iostream>
#include <typeinfo>

int main(){
	int i = 0;
	const int ci = i;
	std::cout << typeid(ci).name() << std::endl;
	auto ai = i;
	ai = 10;
	std::cout << ai << std::endl;
	
}
