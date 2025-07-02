#include<iostream>
using namespace std;
int main () {
	int currVal,cnt,Val;
	cnt = 1;
	if (cin >> currVal){
		while (cin >> Val){
			if (Val == currVal){
				cnt ++;
			}
			else {
				cout << currVal << "\toccurs\t" << cnt << "\ttimes" << endl;
				currVal = Val;
				cnt = 1 ;
			}
		}
	cout << currVal << "\toccurs\t" << cnt << "\ttimes" << endl;
	}
	return 0;
}
