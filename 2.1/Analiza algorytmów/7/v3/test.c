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

long long simple = 0;
long long calc = 0;

void preparePositions() {
	positions = calloc(nToN, sizeof(int));
}

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

printPermutations() {
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

int* toRegistries(int number) {
	int* r = malloc(sizeof(int)*n);
	int i;
	for (i=0;i<n;i++) {
		r[i] = number%n;
		number /= n;
	}
	return r;
}

int toNumber(int* r) {
	int i;
	int num = 0;
	for (i=0;i<n;i++) num = num*n+r[i];
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

int testForR(int number, int* r) {
	if (number>nToN) printf("%d\n", number);
	if (positions[number]) {
		return positions[number]-1;
	} else {
		int i;
		if (isOk(r)) {
			positions[number] = 1;
			return 0;
		} else {
			int max = 1;
			int i;
			for (i=0;i<permNo;i++) {
				int* p = permutations[i];
				int* r1 = memcpy(malloc(sizeof(int)*n), r, n*sizeof(int));
				
				int runs = 0;
				int j;
				for (j=0;j<n;j++) {
					runs += run(r1, p[j]);
				}
				
				int r1AsNumber = toNumber(r1);
				int steps = testForR(r1AsNumber, r1) + runs;
				if (steps>max) max = steps;
				free(r1);
			}
			positions[number] = max+1;
			return max;
		}
	}
}

int testFor(int number) {
	if (number>nToN) printf("%d\n", number);
	if (positions[number]) {
		return positions[number]-1;
	} else {
		int* r = toRegistries(number);
		if (isOk(r)) {
			free(r);
			positions[number] = 1;
			return 0;
		} else {
			int max = 0;
			int i;
			int* r1 = malloc(sizeof(int)*n);
			for (i=0;i<permNo;i++) {
				int* p = permutations[i];
				int j;
				//for (j=0;j<n;j++) r1[j] = r[j];
				memcpy(r1, r, n*sizeof(int));
				
				int runs = 0;
				for (j=0;j<n;j++) {
					runs += run(r1, p[j]);
				}
				
				int r1AsNumber = toNumber(r1);
				int steps = testForR(r1AsNumber, r1) + runs;
				if (steps>max) max = steps;
			}
			free(r1);
			free(r);
			positions[number] = max+1;
			return max;
		}
	}
}

int test() {
	int max = 0;
	int number = 0;
	int i,j;
	int logTime = nToN/100;
	for (i=0;i<100;i++) {
		for (j=0;j<logTime;j++) {
			int t = testFor(number);
			if (max<t) max = t;
			number++;
		}
		printf("%d%%\n", i);
	}
	for (;number<nToN;number++) {
		int t = testFor(number);
		if (max<t) max = t;
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
	if (n < 1 || n > 10) {
		printf("wrong n: "+n);
		system("PAUSE");
		return 0;
	}
	printf("n: %d\n", n);
	
	int i;
	nToN = 1;
	for (i=0;i<n;i++) nToN *= n;
	
	preparePermutations(n);
	preparePositions();
	int result = test();
	printResult(time(NULL)-t, result, n);
	return 1;
}