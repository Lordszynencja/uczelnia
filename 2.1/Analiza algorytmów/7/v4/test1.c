#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <string.h>

int n;
int nMin1;
int nToN;
int** permutations;
int permNo;
int* positions;

void preparePositions();
int* copyOneBigger(int* a, int cs);
void preparePermutations(int level);
void printPermutations();
void printResult(int t, int result, int n);
void toRegistries(int number);
int toNumber(int* r);
int isOk(int* r);
int run(int* r, int i);
int calc(int number, int* r, int h);
int testForR(int number, int* r, int h);
int testFor(int number);
int test(int lvl, int number);

int* copyOneBigger(int* a, int cs) {
	int* c = malloc(sizeof(int)*cs);
	memcpy(c, a, (cs-1)*sizeof(int));
	return c;
}

void preparePermutations(int level) {
	if (level>1) {
		preparePermutations(level-1);
		int n1 = permNo*level;
		int** oldPerms = permutations;
		permutations = malloc(sizeof(int*)*n1);
		int i, j;
		for (i=0;i<level;i++) {
			for (j=0;j<permNo;j++) {
				int* p = copyOneBigger(oldPerms[j], level);
				p[level-1] = i;
				int k;
				for (k=0;k<level-1;k++) {
					if (p[k] == i) {
						p[k] = level-1;
						k = level;
					}
				}
				permutations[j*level+i] = p;
			}
		}
		for (i=0;i<permNo;i++) free(oldPerms[i]);
		free(oldPerms);
		permNo = n1;
	} else {
		permutations = malloc(sizeof(int*));
		permutations[0] = malloc(sizeof(int));
		permutations[0][0] = 0;
		permNo = 1;
	}
}

void printPermutations() {
	int i, j;
	for (i=0;i<permNo;i++) {
		printf("[%d", permutations[i][0]);
		for (j=1;j<n;j++) printf(",%d", permutations[i][j]);
		printf("]\n");
	}
}

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

int hash(int num) {
	int h = 0;
	int i;
	for (i=0;i<n;i++) {
		h = h*n+(num&15);
		num = num>>4;
	}
	return h;
}

void preparePositions() {
	positions = calloc(nToN, sizeof(int));
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

void toRegistries(int number) {
	int i;
	for (i=0;i<n;i++) {
		rMem[i] = number&15;
		number = number>>4;
	}
}

int toNumber(int* r) {
	int i;
	int num = 0;
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

int run(int* r, int i) {
	if (i == 0) {
		if (r[0] == r[nMin1]) {
			r[0] = (r[0]+1)%n;
			return 1;
		}
	} else {
		if (r[i] != r[i-1]) {
			r[i] = r[i-1];
			return 1;
		}
	}
	return 0;
}

int calc(int number, int* r, int h) {
	int max = 1;
	int i;
	int* r1 = stackForRegisters[stackPointer];
	stackPointer++;
	for (i=0;i<permNo;i++) {
		int* p = permutations[i];
		memcpy(r1, r, n*sizeof(int));
		
		int runs = 0;
		int j;
		for (j=0;j<n;j++) {
			runs += run(r1, p[j]);
		}
		
		int r1AsNumber = toNumber(r1);
		int h1 = hash(r1AsNumber);
		int steps = testForR(r1AsNumber, r1, h1) + runs;
		if (steps>max) max = steps;
	}
	stackPointer--;
	positions[hash(number)] = max+1;
	return max;
}

int testForR(int number, int* r, int h) {
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

int testFor(int number) {
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

int test(int lvl, int number) {
	int max = 0;
	int i;
	if (lvl == n-1) {
		for (i=0;i<n;i++) {
			int result = testFor((number<<4)+i);
			if (result > max) max = result;
		}
	} else if (lvl == 1) {
		printf("%d:", number&15);
		for (i=0;i<n;i++) {
			printf("%d,", i);
			int result = test(lvl+1, (number<<4)+i);
			if (result > max) max = result;
		}
		printf("\n");
	} else {
		for (i=0;i<n;i++) {
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
	nToN = 1;
	for (i=0;i<n;i++) nToN *= n;
	
	preparePermutations(n);
	preparePositions();
	int result = test(0, 0);
	printResult(time(NULL)-t, result, n);
	freePositions();
	return 1;
}