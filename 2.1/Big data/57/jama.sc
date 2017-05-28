import Jama._;

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

def testMatrix() : Matrix = {
	return new Matrix(Array(
		Array(1.0, 1.0, 1.0),
		Array(1.0, 0.0, 1.0),
		Array(0.0, 1.0, 0.0)
	));
}

def linearMatrix(n : Int) : Matrix = {
	var A = new Matrix(n, n);
	for (i <- Range(0, n-1)) A.set(i, i+1, 1.0);
	return A;
}

var A = linearMatrix(5);
A.print(0, 0);
var G = prepareGoogleMatrix(A, 0.85);
G.print(0, 3);

var G2 = matrixPower(G, 2);
G2.print(0, 4);
var G4 = matrixPower(G2, 2);
G4.print(0, 4);
var G8 = matrixPower(G4, 2);
G8.print(0, 4);
var G16 = matrixPower(G8, 2);
G16.print(0, 4);
var G32 = matrixPower(G16, 2)
G32.print(0, 4);
var G64 = matrixPower(G32, 2)
G64.print(0, 4);