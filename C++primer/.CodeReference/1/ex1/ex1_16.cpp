#include<iostream>

int main(){
	float sum, value;
	while(std::cin >> value){
		sum += value;
	}
	std::cout << sum << std::endl;
	return 0;
}
