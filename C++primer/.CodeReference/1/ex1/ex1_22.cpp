#include<iostream>
#include"Sales_item.h"

int main(){
	Sales_item item,curritem,sum_item;
	if(std::cin >> curritem){
		item = curritem;
		sum_item = item;
	while (std::cin >> curritem){
		//std::cout << "current:\t" << curritem <<std::endl;
		if (curritem.isbn() == item.isbn())
			sum_item += curritem;
		else {
			std::cout << "sum_item:\t" << sum_item << std::endl;
			item = curritem;
			//std::cout << "item:\t" << item <<std::endl;
			sum_item = item;
			//std::cout << "new sum_item:\t" << sum_item <<std::endl;
		}
	}
	std::cout << "sum_item:\t" << sum_item << std::endl;
	}
	return 0;
}
