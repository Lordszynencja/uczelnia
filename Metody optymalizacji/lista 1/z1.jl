# Szymon Lewandowski

using JuMP
using GLPKMathProgInterface
using Clp

function createA(n) # creates Hilbert matrix
	A = Array{Float64}(n, n)
	for i=1:n
		for j=1:n
			A[i, j] = 1.0/(i+j-1.0)
		end
	end
	return A
end

function createB(n) #creates right sides for Hilbert matrix so that x = {1}^n
	B = Array{Float64}(n)
	for i=1:n
		B[i] = 0
		for j=1:n
			B[i] += 1.0/(i+j-1.0)
		end
	end
	return B
end

function createC(n)
	return createB(n)
end

function calculate_error(x, n)
	s = 0
	for i=1:n
		s += (1-x[i])*(1-x[i])
	end
	return sqrt(s)
end

function solveFor(n, s)
	A = createA(n) # Hilbert matrix
	b = createB(n) # right sides vector
	c = createC(n) # equals right sides vector
	m = Model(solver = s)
	
	@variable(m, x[1:n] >= 0)
	
	@objective(m, Min, sum(c[i]*x[i] for i=1:n))
	
	@constraint(m, A*x .== b)
	
	res = solve(m)
	
	println("x = ", getvalue(x))
	println("error = ", calculate_error(getvalue(x), n))
	
	return res
end

function solveGLPK(n)
	return solveFor(n, GLPKSolverLP())
end

function solveCLP(n)
	return solveFor(n, ClpSolver())
end
