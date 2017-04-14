#include <stdlib.h>
#include <stdio.h>

#define numbers 100000000

int key[32] = {2, 245, 254, 64,
	93, 104, 222, 105,
	74, 10, 8, 44,
	95, 97, 56, 111,
	95, 35, 222, 109,
	168, 98, 107, 88,
	1, 94, 257, 14,
	195, 123, 97, 32};
int keylength = 256;

int t = 256;
int n = 256;
int* S;

int shouldContinue(int* marked, int round, int bytes) {
	int i;
	int j = n-(bytes%n)-1;
	int markedAmount = 0;
	for (i=0;i<n;i++) markedAmount += marked[i];
	if (markedAmount<n/2) {
		if (marked[round] == 0 && marked[j]) marked[round] = 1;
	} else {
		if ((marked[round] == 0 && marked[j] == 1) || (marked[round] == 0 && round == j)) marked[round] = 1;
	}

	int allMarked = 1;
	for (i=0;i<n;i++) allMarked = allMarked & marked[i];
	
	return 1-allMarked;
}

void ksa_sst() {
	S = malloc(sizeof(int)*n);
	int* marked = malloc(sizeof(int)*n);
	int i;
	int j = 0;
	for (i=0;i<n;i++) {
		S[i] = i;
		marked[i] = 0;
	}
	marked[n-1] = 1;
	
	int round = 0;
	while (shouldContinue(marked, round%n, S[j])) {
		for (i=0;i<t;i++) {
			j = (j + S[i] + key[i % keylength])%n;
			int temp = S[i%n];
			S[i%n] = S[j];
			S[j] = temp;
		}
		round++;
	}
	printf("KSA-SST finished\n");
}

void prga(char* filename) {
	int i = 0;
	int j = 0;
	int k;
	int counter;
	
	FILE* f = fopen(filename, "w+");
	fprintf(f, "#===================================\n");
	fprintf(f, "# mygenerator RC4-SST\n");
	fprintf(f, "#===================================\n");
	fprintf(f, "type: d\n");
	fprintf(f, "count: %d\n", numbers);
	fprintf(f, "numbit: 32\n");
	
	int result;
	for (counter = 0;counter<numbers;counter++) {
		result = 0;
		for (k=0;k<4;k++) {
			i = (i+1)%n;
			j = (j+S[i])%n;
			int temp = S[i];
			S[i] = S[j];
			S[j] = temp;
			result = (result<<8)+S[(S[i]+S[j])%n];
		}
		fprintf(f, "%d\n", result);
		if (counter%(numbers/100) == 0 && counter != 0) printf("%d%%\n", counter/(numbers/100));
	}
	printf("PRGA finished");
	fclose(f);
}

void printHelp() {
	printf("usage: z2.exe <filename>\n");
}

int main(int argc, char** argv) {
	if (argc<2) {
		printHelp();
		system("PAUSE");
		return 0;
	}
	printf("RC4-SST\n");
	ksa_sst();
	prga(argv[1]);
	free(S);
}