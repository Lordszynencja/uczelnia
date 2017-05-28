import Jama._;
import scala.io._;
import java.io._;
import java.nio.charset._;

def matrixPower(M : Matrix, p : Int) : Matrix = {
	if (p == 0) return Matrix.identity(M.getRowDimension, M.getColumnDimension);
	if (p == 1) return M;
	var M2 = matrixPower(M, p/2);
	if (p%2 == 1) return M2.times(M2).times(M);
	return M2.times(M2);
}

def prepareMarkovMatrix(A : Matrix) : Matrix = {
	var S = A.copy;
	var SArray = S.getArray;
	var N = A.getRowDimension;
	for (i <- Range(0, N)) {
		var count = SArray(i).count(a => 1.0 == a);
		if (count == 0) {
			for (j <- Range(0, N)) {
				SArray(i)(j) = 1.0/N;
			}
		} else {
			for (j <- Range(0, N)) {
				SArray(i)(j) /= count;
			}
		}
	}
	return S;
}

def prepareGoogleMatrix(A : Matrix, alpha : Double) : Matrix = {
	var S = prepareMarkovMatrix(A);
	var G = S.copy;
	var N = S.getRowDimension;
	for (i <- Range(0, N)) {
		for (j <- Range(0, N)) {
			G.set(i, j, alpha*G.get(i, j) + (1-alpha)/N);
		}
	}
	return G;
}

def siteMapToAdjMatrix(m : Map[String, Array[String]], sites : Array[String]) : Matrix = {
	var N = sites.size;
	var mx = new Matrix(N, N, 0);
	for (i <- Range(0, N)) {
		val si = sites(i);
		val listOption = m.get(si);
		val list = if (listOption == None) Array[String]() else listOption.get;
		for (j <- Range(0, N)) {
			val sj = sites(j);
			if (list.contains(sj)) mx.set(i, j, 1);
		}
	}
	return mx;
}

def siteScan() = {
	var toCheck = Array[String]("http://ki.pwr.edu.pl/gebala/");
	var links = Map[String, Array[String]]();
	var sites = Array[String]();
	val regexp = "href=\".*\"".r.unanchored;
	while (!toCheck.isEmpty) {
		var buf = toCheck.toBuffer;
		var address = buf.remove(0);
		toCheck = buf.toArray;
		
		if (address.startsWith("http://ki.pwr.edu.pl/gebala") && (address.endsWith(".html") || address.endsWith("/")) && links.get(address) == None) {
			println(address);
			if (!sites.contains(address)) sites = sites :+ address;
			var prefix1 = "";
			var prefix2 = "http://ki.pwr.edu.pl";
			if (address.endsWith("/")) {
				prefix1 = address;
			} else if (address.endsWith(".html")) {
				prefix1 = address.substring(0, address.lastIndexOf("/")+1);
			} else {
				prefix1 = address + "/";
			}
			try {
				val html = Source.fromURL(address).mkString;
				var list = Array[String]();
				for (i <- regexp.findAllIn(html)) {
					var newAddress = i.split("\"")(1);
					if (newAddress.startsWith("/") && newAddress.endsWith(".html")) newAddress = prefix2 + newAddress;
					else if (newAddress.startsWith("/")) newAddress = prefix1 + newAddress.substring(1);
					else if (!newAddress.startsWith("http://")) newAddress = prefix1 + newAddress;
					toCheck = toCheck :+ newAddress;
					list = list :+ newAddress;
				}
				links += address -> list;
			} catch {
				case fileNotFound : FileNotFoundException => println("[File not found]");
				case charset : UnmappableCharacterException => println("[Charset error]");
				case ioException : IOException => println("[IOException]");
			}
		}
	}
	
	(links, sites);
}

def testMap() = {
	var links = Map[String, Array[String]]("1" -> Array[String]("1", "2", "3", "a"), "2" -> Array[String]("1", "3", "b"), "3" -> Array[String]());
	var sites = Array[String]("1", "2", "3");
	(links, sites);
}

def runFor(links : Map[String, Array[String]], sites : Array[String]) : Unit = {
	var A = siteMapToAdjMatrix(links, sites);
	var G = prepareGoogleMatrix(A, 0.85);
	var resultAsArray = matrixPower(G, 32).getArray;
	var row = resultAsArray(0);
	for (i <- Range(0, sites.size)) println(row(i) + ": " +sites(i));
}

def runSimple() : Unit = {
	var (links, sites) = testMap;
	runFor(links, sites);
}

def runStandard() : Unit = {
	var (links, sites) = siteScan;
	runFor(links, sites);
}

def printLinks(links : Map[String, Array[String]], sites : Array[String]) : Unit = {
	for (l <- sites) {
		print(l + " -> ");
		val listOption = links.get(l)
		if (listOption != None) {
			print("[");
			val list = listOption.get;
			print(list(0));
			for (i <- Range(1, list.size)) print("," + list(i));
			println("]\n");
		} else println("[]")
	}
}
