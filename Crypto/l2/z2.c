#include <stdlib.h>
#include <stdio.h>

#define numbers 100000000

int key[8] = {2, 245, 254, 64,
	93, 104, 222, 105};
int keylength;

int t;
int n;
int* S;

void ksa() {
	S = malloc(sizeof(int)*n);
	int i;
	for (i=0;i<n;i++) S[i] = i;
	
	int j = 0;
	for (i=0;i<t;i++) {
		j = (j + S[i] + key[i % keylength])%n;
		int temp = S[i%n];
		S[i%n] = S[j%n];
		S[j%n] = temp;
	}
	printf("KSA finished\n");
}

int getKeyBit(int bit) {
	int a = key[bit/8];
	int b = 1<<(bit%8);
	return a & b;
}

void ksa_rs() {
	S = malloc(sizeof(int)*n);
	int i;
	for (i=0;i<n;i++) S[i] = i;
	
	int r;
	for (r=0;r<t;r++) {
		int* array = malloc(sizeof(int)*n);
		int top = n-1;
		int bottom = 0;
		for (i=0;i<n;i++) {
			if (getKeyBit((r*n+i)%(keylength*8)) == 0) {
				array[top] = i;
				top--;
			} else {
				array[bottom] = i;
				bottom++;
			}
		}
		
		int* newS = malloc(sizeof(int)*n);
		for (i=n-1;i>top;i--) {
			newS[n-i-1] = S[array[i]];
		}
		for (i=0;i<bottom;i++) {
			newS[n-top-1+i] = S[array[i]];
		}
		free(array);
		free(S);
		S = newS;
	}
	printf("KSA-RS finished\n");
}

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
	while (shouldContinue(marked, round, S[j])) {
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

void prga(char* filename, int type) {
	int i = 0;
	int j = 0;
	int k;
	int counter;
	
	FILE* f = fopen(filename, "w+");
	fprintf(f, "#===================================\n");
	fprintf(f, "# mygenerator %s\n", type == 1 ? "RC4" : type == 2 ? "RC4-RS" : "RC4-SST");
	fprintf(f, "#===================================\n");
	fprintf(f, "type: d\n");
	fprintf(f, "count: %d\n", numbers);
	fprintf(f, "numbit: 32\n");
	
	int result;
	for (counter = 0;counter<numbers;counter++) {
		result = 0;
		for (k=0;k<8;k++) {
			i = (i+1)%n;
			j = (j+S[i])%n;
			int temp = S[i];
			S[i] = S[j];
			S[j] = temp;
			result = (result<<4)+S[(S[i]+S[j])%n];
		}
		fprintf(f, "%d\n", result);
	}
	printf("PRGA finished");
	fclose(f);
}

void printHelp() {
	printf("usage: z2.exe <filename> <mode> <keylength> <N> <T>\n");
	printf("modes:\n");
	printf("    1 - RC4\n");
	printf("    2 - RC4-RS\n");
	printf("    3 - RC4-SST\n");
	printf("keylength: 8/16/24/32/40/64\n");
}

int main(int argc, char** argv) {
	if (argc<4) {
		printHelp();
		system("PAUSE");
		return;
	}
	int option = atoi(argv[2]);
	keylength = atoi(argv[3]);
	n = atoi(argv[4]);
	if (option == 1) {
		printf("RC4 selected\n");
		if (argc<5) {
			printHelp();
			system("PAUSE");
			return;
		}
		t = atoi(argv[5]);
		ksa();
	} else if (option == 2) {
		printf("RC4-RS selected\n");
		if (argc<5) {
			printHelp();
			system("PAUSE");
			return;
		}
		t = atoi(argv[5]);
		ksa_rs();
	} else if (option == 3) {
		printf("RC4-SST selected\n");
		t = atoi(argv[5]);
		ksa_sst();
	}
	prga(argv[1], option);
	free(S);
}