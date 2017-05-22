# Szymon Lewandowski

using JuMP
using GLPKMathProgInterface
using Clp

function solveFor(costs, processingTime, maxMachineProcessingTimes)
	c = costs
	p = processingTime
	T = maxMachineProcessingTimes
	
	(n, m) = size(c)
	println(n, ":", m)
	
	model = Model(solver = GLPKSolverMIP())
	
	@variable(model, x[1:n, 1:m], Bin)
	
	#minimal trash
	@objective(model, Min, sum(c[j, i]*x[j, i] for i=1:m, j=1:n))
	
	for j=1:n
		@constraint(model, sum(x[j, i] for i=1:m) == 1)
	end
	
	for i=1:m
		@constraint(model, sum(x[j, i]*p[j, i] for j=1:n) <= T[i])
	end
	
	#print(model)
	
	res = solve(model)
	
	println("\nObjective value: ", getobjectivevalue(model))
	println("x:")
	res_x = getvalue(x)
	for i=1:n, j=1:m
		if res_x[i, j] == 1
			println("zadanie #", i, " zostało zrobione na maszynie #", j)
		end
	end
	
	return res_x
end

function solveSimple()
	costs = [1 2 3; 2 3 2]
	processingTime = [2 2 2; 1 1 1]
	maxMachineProcessingTimes = [2, 1, 1]
	solveFor(costs, processingTime, maxMachineProcessingTimes)
end

function solveStandard()
	costs = 			[1 2 3; 2 3 2; 3 3 3; 1 4 5; 1 2 1; 5 6 2]
	processingTime = 	[2 2 2; 2 1 2; 1 1 1; 3 2 1; 1 1 1; 1 1 2]
	maxMachineProcessingTimes = [5, 9, 4]
	solveFor(costs, processingTime, maxMachineProcessingTimes)
end
