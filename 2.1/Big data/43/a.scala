import scala.util.hashing;
import scala.math.pow;
import scala.math.log;
import scala.io.Source;

def alpha(m : Int) : Double = {
	if (m <= 16) return 0.673;
	if (m <= 32) return 0.697;
	if (m <= 64) return 0.709;
	return 0.7213/(1+1.079/m);
}

def h(s : String) : Int = {
	return scala.util.hashing.MurmurHash3.stringHash(s);
}

def p(x : Int) : Int = {
	if (x <= 0) return 32;
	return p(x/2)-1;
}

def hyperLogLog(l : Iterator[String], b : Int) : Double = {
	var m = 1 << b;
	var b2 = 32-b;
	val M = new Array[Int](m);
	for (i <- Range(0, m)) {
		M(i) = 0;
	}
	var counter = 0;
	for (i <- l) {
		if (counter%100000 == 0) println(counter);
		val x = h(i);
		val j = x >>> b2;
		val w = x % (1 << b2);
		val v = p(w);
		if (M(j)<v) M(j) = v;
		counter = counter+1;
	}
	
	var Z = 0.0;
	for (i <- Range(0, m)) {
		Z += pow(2, -M(i));
	}
	
	val E = alpha(m)*m*m/Z;
	
	if (E <= 5*m/2.0) {
		val c = M.count(_ == 0)*1.0;
		if (c > 0) return m*log(m/c);
		return E;
	}
	if (E > pow(2, 32)/30.0) {
		return -pow(2, 32)*log(1-E/pow(2, 32));
	}
	return E;
}

def do1(filename : String, b : Int) : Unit = {
	val list = getSrcDestAndPairs(filename);
	println();
	println("src: " + hyperLogLog(Source.fromFile(filename).getLines().map(line => line.split(" ")).map(line => line(1)), b));
	println("dst: " + hyperLogLog(Source.fromFile(filename).getLines().map(line => line.split(" ")).map(line => line(2)), b));
	println("pair: " + hyperLogLog(Source.fromFile(filename).getLines().map(line => line.split(" ")).map(line => line(1) + " -> " + line(2)), b));
}

do1("test.txt", 16);