#include<stdio.h>
#include<stdlib.h>
#include<time.h>

#define MAX 200000
#define samples_number 300

int seed = 1;
long long a = 16807LL;
long long m = 2147483647;

int pointer = 344;
int r[MAX];

int previous[31];

void generate() {
  int i;
  r[0] = seed;
  for (i=1; i<31; i++) {
    r[i] = (a * r[i-1]) % m;
    if (r[i] < 0) {
      r[i] += m;
    }
  }
  r[31] = r[0];
  r[32] = r[1];
  r[33] = r[2];
  for (i=34; i<344; i++) {
    r[i] = r[i-31] + r[i-3];
  }
  for (i=344; i<MAX; i++) {
    r[i] = r[i-31] + r[i-3];
  }
}

int getRandom() {
	return r[pointer++]>>1;
}

int getCracked() {
	int value = previous[0]+previous[28];
	int i;
	for (i=0;i<30;i++) previous[i] = previous[i+1];
	previous[30] = value;
	return value>>1;
}

void testCracked() {
	int correct = 0;
	int tries;
	for (tries=0;tries<100000;tries++) {
		if (getCracked() == getRandom()) correct++;
	}
	printf("%d/%d correct\n", correct, tries);
}

void crack(int try) {
	if (try>10) {
		printf("cracking failed\n");
		return;
	}
	int i;
	for (i=0;i<100;i++) {
		getRandom();
	}
	unsigned int samples[samples_number];
	int samples_accurate[samples_number];
	
	for (i=0;i<samples_number;i++) {
		samples[i] = getRandom()<<1;
		samples_accurate[i] = 0;
	}
	for (i=0;i<samples_number-31;i++) {
		if (samples[i]+samples[i+28] == samples[i+31]-2) {
			samples[i]++;
			samples[i+28]++;
			samples_accurate[i] = 1;
			samples_accurate[i+28] = 1;
			samples_accurate[i+31] = 1;
		}
	}
	printf("\n");
	
	for (i=0;i<samples_number-31;i++) {
		if (samples[i]+samples[i+28] == samples[i+31] && samples_accurate[i+31]) {
			samples_accurate[i] = 1;
			samples_accurate[i+28] = 1;
		} else if (samples_accurate[i] && samples_accurate[i+28] && samples_accurate[i+31] == 0) {
			samples[i+31] = samples[i]+samples[i+28];
			samples_accurate[i+31] = 1;
		} else if (samples[i]+samples[i+28] == samples[i+31]-1) {
			if (samples_accurate[i]) {
				samples[i+28]++;
				samples_accurate[i+28] = 1;
			} else if (samples_accurate[i+28]) {
				samples[i]++;
				samples_accurate[i] = 1;
			}
		}
	}
	
	int correct_in_row = 0;
	int correct_position = -1;
	
	for (i=0;i<samples_number;i++) {
		if (samples_accurate[i]) correct_in_row++;
		else correct_in_row = 0;
		if (correct_in_row>30) correct_position = i;
	}
	for (i=0;i<31;i++) {
		previous[i] = samples[correct_position-31+i];
	}
	for (i=correct_position;i<samples_number;i++) getCracked();
	
	if (correct_position != -1) {
		printf("cracking succeeded, testing\n");
		testCracked();
	} else {
		printf("cracking failed, trying again\n");
		crack(try+1);
	}
}

int main(int argc, char** argv) {
	srand(time(NULL));
	printf("command list:\n");
	printf("n-next value\n");
	printf("c-crack\n");
	printf("a-approximate next value\n");
	printf("e-exit\n");
	char command = 'a';
	generate();
	
	while (command != 'e') {
		printf(":");
		scanf("%c", &command);
		if (command == 'n') printf("next value = %d\n", getRandom());
		if (command == 'c') crack(0);
		if (command == 'a') printf("approximate next value = %d\n", getCracked());
	}
}