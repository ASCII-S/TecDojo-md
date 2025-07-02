#include<iostream>

int main(){
	int a,b;
	std::cin >> a >> b ;
	std::cout << "end input" << std::endl;
	while (a != b){
		std::cout << a << '\n' ;
		if (a < b) {
			a ++ ;
		}
		else {
			a--;
		}
	}
	std::cout<< a << std::endl;
}
