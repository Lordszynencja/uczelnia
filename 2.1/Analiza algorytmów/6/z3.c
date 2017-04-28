#include <stdlib.h>
#include <stdio.h>
#include <time.h>
#include <limits.h>

#define hashSize 111000

int k[9] = {2, 3, 10, 20, 50, 100, 200, 400, 1000};

double* hashes;

int hashInHashes(double h, int p) {
	int i;
	for (i=0;i<p;i++) if (hashes[i] == h) return 1;
	return 0;
}

double get30bitRandom() {
	return (double)((rand()<<15)+rand());
}

void initHash() {
	hashes = malloc(sizeof(double)*hashSize);
	int i;
	double q = (double)((RAND_MAX<<15)+RAND_MAX);
	for (i=0;i<hashSize;i++) {
		double new_hash = get30bitRandom()/q;
		while (hashInHashes(new_hash, i) == 1) {
			new_hash = get30bitRandom()/q;
		}
		hashes[i] = new_hash;
	}
	printf("hash initialized\n");
}

double myhash1(int v) {
	if (v>=0 && v<hashSize) return hashes[v];
	return 0.0;
}

double myhash2(int v) {
	return v/(double)INT_MAX;
}

double myhash3(int v) {
	v = ((v >> 16) ^ v) * 0x45d9f3b;
    v = ((v >> 16) ^ v) * 0x45d9f3b;
    v = (v >> 16) ^ v;
    return v/(double)INT_MAX;
}

int xInTable(double x, double* table, int length) {
	int x1 = 0;
	int x2 = length-1;
	while (x2-x1 > 1) {
		int x0 = (x1+x2)/2;
		if (table[x0] == x) return 1;
		else if (table[x0] > x) x2 = x0;
		else x1 = x0;
	}
	if (table[x1] == x || table[x2] == x) return 1;
	return 0;
}

int counting(int k, int* multi, int multi_length) {
	double* M = malloc(sizeof(double)*k);
	int i;
	for (i=0;i<k;i++) M[i] = 1.0;
	
	for (i=0;i<multi_length;i++) {
		double h = myhash3(multi[i]);
		if (h<M[k-1] && xInTable(h, M, k) == 0) {
			M[k-1] = h;
			int j;
			for (j=k-1;j>0;j--) {
				if (M[j]<M[j-1]) {
					double tmp = M[j];
					M[j] = M[j-1];
					M[j-1] = tmp;
				} else j = 0;
			}
		}
	}
	if (M[k-1] == 1.0) {
		int count = 0;
		for (i=0;i<k;i++) {
			if (M[i] != 1.0) count++;
			else i = k;
		}
		free(M);
		return count;
	} else {
		int result = (int)((double)(k-1)/M[k-1]);
		free(M);
		return result;
	}
}

void count_100k() {
	int i;
	int size2 = 100000;
	int* multi = malloc(sizeof(int)*size2);
	for (i=0;i<size2;i++) multi[i] = i+1;
	for (i=0;i<9;i++) {
			int result = counting(k[i], multi, size2);
			printf("k=%d, %d\n", k[i], result);
	}
	free(multi);
}

int main(int argc, char** argv) {
	srand(time(NULL));
	initHash();
	count_100k();
	int* multi = malloc(sizeof(int)*10000);
	FILE* f = fopen("output.js", "w+");
	int i, j=0;
	for (i=0;i<10000;i++) multi[i] = i+1;
	fprintf(f, "var results = {\n");
	for (j=0;j<9;j++) {
		printf("\nk=%d\n", k[j]);
		fprintf(f, "   k%d : [", k[j]);
		for (i=0;i<10000;i++) {
			int result = counting(k[j], multi, i+1);
			if (i == 0) fprintf(f, "%d", result);
			else fprintf(f, ",%d", result);
			if (i%1000 == 0) printf("%d%%\n", i/100);
		}
		if (j != 8) fprintf(f, "],\n");
		else fprintf(f, "]\n};");
	}
	fclose(f);
	free(multi);
}
