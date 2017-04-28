#include<stdio.h>
#include<stdlib.h>
#include<time.h>

long long seed = 1347381L;
long long gen_a = 85734845L;
long long gen_b = 47625347L;
long long gen_m = 0x100000000L;

long long calc_a = 1L;
long long calc_b = 1L;
long long calc_m = 1L;

long long mul_inv(long long a, long long b) {
	long long b0 = b;
	long long t, q;
	long long x0 = 0;
	long long x1 = 1;
	
	if (b == 1) return 1;
	
	while (a > 1) {
		if (b == 0) return x1;
		q = a / b;
		t = b;
		b = a % b;
		a = t;
		t = x0;
		x0 = x1 - q * x0;
		x1 = t;
	}
	if (x1 < 0) x1 += b0;
	return x1;
}

long long z1generator() {
	seed = (gen_a*seed+gen_b)%gen_m;
	return seed;
}

long long z1approx() {
	return (calc_a*seed+calc_b)%calc_m;
}

long long gcd(long long a, long long b) {
	if (b > 0) return gcd(b, a%b);
	return a;
}

void find_m() {
	int t_l = 100;
	int i;
	int previous = z1generator();
	long long t[3];
	for (i=0;i<3;i++) {
		int next = z1generator();
		t[i] = next-previous;
		previous = next;
	}
	calc_m = t[0]*t[2]-t[1]*t[1];
	if (calc_m<0) calc_m = -calc_m;
	
	for (i=0;i<t_l;i++) {
		int next = z1generator();
		while (next == 0) next = z1generator();
		int new_t = next-previous;
		previous = next;
		t[0] = t[1];
		t[1] = t[2];
		t[2] = new_t;
		long long u = t[0]*t[2]-t[1]*t[1];
		if (u<0) u = -u;
		calc_m = gcd(calc_m, u);
	}
	
}

void crackz1() {
	find_m();
	
	long long v0 = z1generator();
	long long v1 = z1generator();
	long long v2 = z1generator();
	
	long long l1 = v1-v2;
	if (l1<0) l1 += calc_m;
	
	long long diff = v0-v1;
	if (diff<0) diff += calc_m;
	
	long long l2 = mul_inv(diff, calc_m);
	
	calc_a = (l1*l2)%calc_m;
	if (calc_a<0) calc_a += calc_m;
	
	calc_b = v1-(v0*calc_a)%calc_m;
	if (calc_b<0) calc_b += calc_m;
	printf("calc_a = %lld, gen_a = %lld\ncalc_b = %lld, gen_b = %lld\ncalc_m = %lld, gen_m = %lld\nvalues are equal? %s\n", calc_a, gen_a, calc_b, gen_b, calc_m, gen_m, (calc_a == gen_a && calc_b == gen_b && calc_m == gen_m ? "true" : "false"));
}

int interesting[1][3] = {
	{3332, 2576, 3579}
};

void set_interesting(int i) {
	gen_a = interesting[i][0];
	gen_b = interesting[i][1];
	gen_m = interesting[i][2];
}

int main(int argc, char** argv) {
	srand(time(NULL));
	gen_m = rand();
	if (gen_m<0) gen_m = -gen_m;
	seed = rand()%gen_m;
	if (seed<0) seed = -seed;
	
	gen_a = gen_a%gen_m;
	gen_b = gen_b%gen_m;
	
	set_interesting(0);
	z1generator();
	z1generator();
	z1generator();
	crackz1();
	
	printf("command list:\n");
	printf("t-set new seed\n");
	printf("c-crack\n");
	printf("a-approximate next value\n");
	printf("n-next value\n");
	printf("e-exit\n");
	printf("values are: a - %lld, b - %lld, m - %lld, seed - %lld\n", gen_a, gen_b, gen_m, seed);
	char command = 'a';
	
	while (command != 'e') {
		printf(":");
		scanf("%c", &command);
		if (command == 't') {
			scanf("%lld", &seed);
		}
		if (command == 'n') printf("next value = %lld\n", z1generator());
		if (command == 'c') crackz1();
		if (command == 'a') printf("approximate next value = %lld\n", z1approx());
	}
}