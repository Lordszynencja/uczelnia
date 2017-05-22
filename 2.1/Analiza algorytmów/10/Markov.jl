using JuMP
using GLPKMathProgInterface

function getP()
	A = [0 3 1 6;
		1 1 7 1;
		1 7 1 1;
		9 1 0 0]
	
	return A/10
end

function solveFor(P)
	n = size(P)[1]
	model = Model(solver = GLPKSolverMIP())
	
	@variable(model, 0 <= x[1:n] <= 1)
	
	@objective(model, Min, 1)
	
	@constraint(model, sum(x) == 1)
	for i=1:n
		@constraint(model, sum(P[j, i]*x[j] for j=1:n) == x[i])
	end
	
	res = solve(model)
	
	if res == :Optimal
		res_x = getvalue(x)
		println(res_x)
		return res_x
	end
	return res
end

function pointA()
	solveFor(getP())
end

function pointB()
	println("szansa na przejście 0->3 w 32 krokach: ", (getP()^32)[1, 4])
end

function pointC()
	P1 = getP()^128
	println("szansa na przejście do stanu 3 w 128 krokach z losowo wybranego stanu: ", sum(P1[i, 4] for i=1:4)/4)
end

function pointD()
	P = getP()
	x = solveFor(P)
	for i=1:3
		eps = 1/10^i
		println("\neps: ", eps)
		
		P1 = P
		count = 1
		max = maximum(P1[1, j]-x[j] for j=1:4)
		previousMax = max+1
		while (previousMax != max && max > eps)
			P1 = P1*P
			previousMax = max
			max = maximum(P1[1, j]-x[j] for j=1:4)
			count = count+1
		end
		println("count: ", count)
		println("max: ", max)
	end
end

println()
println()
println()

pointA()
pointB()
pointC()
pointD()
