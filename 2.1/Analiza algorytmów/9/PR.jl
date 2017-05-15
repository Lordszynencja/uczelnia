using JuMP
using GLPKMathProgInterface

function getA1()
	n = 6
	A = Array{Float64}(n, n)
	for i=1:n, j=1:n
		A[i, j] = 0.0
	end
	
	A[1, 1] = 1.0
	A[2, 3] = 0.5
	A[2, 5] = 0.5
	A[3, 1] = 1.0
	A[4, 2] = 0.5
	A[4, 5] = 0.5
	A[5, 4] = 1.0
	A[6, 3] = 1.0
	
	return A
end

function getA2()
	n = 6
	A = Array{Float64}(n, n)
	for i=1:n, j=1:n
		A[i, j] = 0.0
	end
	
	A[1, 1] = 1.0
	
	A[2, 5] = 0.5
	A[3, 1] = 1.0
	A[4, 2] = 0.5
	A[4, 5] = 0.5
	A[5, 4] = 1.0
	A[6, 3] = 1.0
	
	return A
end

function toP(A, alpha)
	n = size(A)[1]
	
	return (1-alpha)*A+alpha/n*ones(n, n)
end

function solveFor(P)
	println("P:\n", P)
	n = size(P)[1]
	model = Model(solver = GLPKSolverMIP())
	
	@variable(model, 0 <= x[1:n] <= 1)
	
	@objective(model, Min, 1)
	
	@constraint(model, sum(x) == 1)
	for i=1:n
		@constraint(model, sum(P[j, i]*x[j] for j=1:n) == x[i])
	end
	
	
	#print(model)
	
	res = solve(model)
	
	if res == :Optimal
		res_x = getvalue(x)
		println(res_x)
		
		return res_x
	end
end

alphas = [0, 0.15, 0.5, 1]

for i=1:size(alphas)[1]
	alpha = alphas[i]
	println("\nalpha:", alpha)
	solveFor(toP(getA1(), alpha))
	solveFor(toP(getA2(), alpha))
end

