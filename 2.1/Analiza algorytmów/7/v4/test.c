#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <string.h>

int n;
int nMin1;
int statesNoToN;
int* positions;
int statesNo;

void preparePositions();
void printResult(int t, int result, int n);
void toRegistries(long long number);
long long toNumber(int* r);
int isOk(int* r);
int calc(long long number, int* r, int h);
int testForR(long long number, int* r, int h);
int testFor(long long number);
int test(int lvl, long long number);

void printResult(int t, int result, int n) {
	char* filename = malloc(sizeof(char)*6);
	filename[0] = n+'0';
	filename[1] = '.';
	filename[2] = 't';
	filename[3] = 'x';
	filename[4] = 't';
	filename[5] = 0;
	FILE* f = fopen(filename, "w+");
	fprintf(f, "time: %ds\n", t);
	fprintf(f, "result: %d", result);
	fclose(f);
}

//////////CALCULATIONS

int* rMem;//always one copy is used in testFor
int** stackForRegisters;
int stackSize;
int stackPointer = 0;

int hash(long long num) {
	int h = 0;
	int i;
	for (i=0;i<n;i++) {
		h = h*statesNo+(num&15);
		num = num>>4;
	}
	return h;
}

void preparePositions() {
	positions = calloc(statesNoToN, sizeof(int));
	rMem = malloc(sizeof(int)*n);
	stackSize = n*n*5;
	stackForRegisters = malloc(sizeof(int*)*stackSize);
	int i;
	for (i=0;i<stackSize;i++) {
		stackForRegisters[i] = malloc(sizeof(int)*n);
	}
}

void freePositions() {
	free(positions);
	free(rMem);
	int i;
	for (i=0;i<stackSize;i++) {
		free(stackForRegisters[i]);
	}
	free(stackForRegisters);
}

void toRegistries(long long number) {
	int i;
	for (i=0;i<n;i++) {
		rMem[i] = number&15;
		number = number>>4;
	}
}

long long toNumber(int* r) {
	int i;
	long long num = 0;
	for (i=0;i<n;i++) num = (num<<4)+r[i];
	return num;
}

int isOk(int* r) {
	if (n>1) {
		int ok = 0;
		if (r[0] == r[nMin1]) ok = 1;
		int i;
		for (i=1;i<n;i++) {
			if (r[i] != r[i-1]) {
				if (ok) return 0;
				else ok = 1;
			}
		}
		return ok;
	}
	return 1;
}

int calc(long long number, int* r, int h) {
	int max = 0;
	int i;
	int* r1 = stackForRegisters[stackPointer];
	stackPointer++;
	memcpy(r1, r, n*sizeof(int));
	if (r1[0] == r1[nMin1]) {
		r1[0] = (r1[0]+1)%(statesNo);
		long long r1AsNumber = toNumber(r1);
		int h1 = hash(r1AsNumber);
		int t = testForR(r1AsNumber, r1, h1)+1;
		if (t>max) max = t;
		r1[0] = r[0];
	}
	for (i=1;i<n;i++) {
		if (r1[i] != r1[i-1]) {
			r1[i] = r1[i-1];
			long long r1AsNumber = toNumber(r1);
			int h1 = hash(r1AsNumber);
			int t = testForR(r1AsNumber, r1, h1) + 1;
			if (t>max) max = t;
			r1[i] = r[i];
		}
	}
	stackPointer--;
	positions[h] = max+1;
	return max;
}

int testForR(long long number, int* r, int h) {
	if (positions[h]) {
		return positions[h]-1;
	} else {
		int i;
		if (isOk(r)) {
			positions[h] = 1;
			return 0;
		} else {
			return calc(number, r, h);
		}
	}
}

int testFor(long long number) {
	int h = hash(number);
	if (positions[h]) {
		return positions[h]-1;
	} else {
		toRegistries(number);
		int result;
		if (isOk(rMem)) {
			positions[h] = 1;
			result = 0;
		} else {
			result = calc(number, rMem, h);
		}
		return result;
	}
}

int test(int lvl, long long number) {
	int max = 0;
	int i;
	if (lvl == n-1) {
		for (i=0;i<statesNo;i++) {
			int result = testFor((number<<4)+i);
			if (result > max) max = result;
		}
	} else if (lvl == 1) {
		printf("%d:", number&15);
		for (i=0;i<statesNo;i++) {
			printf("%d,", i);
			int result = test(lvl+1, (number<<4)+i);
			if (result > max) max = result;
		}
		printf("\n");
	} else {
		for (i=0;i<statesNo;i++) {
			int result = test(lvl+1, (number<<4)+i);
			if (result > max) max = result;
		}
	}
	return max;
}

int main(int argc, char** argv) {
	int t = time(NULL);
	if (argc <= 1) {
		printf("no n given");
		system("PAUSE");
		return 0;
	}
	n = atoi(argv[1]);
	nMin1 = n-1;
	if (n < 3 && n > 0) {
		printResult(0, 0, n);
		return 1;
	} else if (n < 1 || n > 10) {
		printf("wrong n: %s", argv[1]);
		system("PAUSE");
		return 0;
	}
	
	int i;
	statesNo = n-1;
	statesNoToN = 1;
	for (i=0;i<n;i++) statesNoToN *= statesNo;
	
	preparePositions();
	int result = test(0, 0LL);
	printResult(time(NULL)-t, result, n);
	freePositions();
	return 1;
}