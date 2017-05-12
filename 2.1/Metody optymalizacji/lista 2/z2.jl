# Szymon Lewandowski

using JuMP
using GLPKMathProgInterface
using Clp

function solveFor(n, times, weights, readyTimes)
	m = Model(solver = GLPKSolverMIP())
	
	bigNum = sum(times) + sum(readyTimes)
	
	@variable(m, endTimes[1:n], Int)
	@variable(m, y[1:n, 1:n], Bin)
	
	@objective(m, Min, sum(endTimes[i]*weights[i] for i=1:n))
	
	for i=1:n
		@constraint(m, endTimes[i] >= times[i]+readyTimes[i])
		
		for j=1:n
			if i != j
				@constraint(m, endTimes[i]-times[i]-endTimes[j]+bigNum*y[i,j] >= 0)
				@constraint(m, endTimes[j]-times[j]-endTimes[i]+bigNum*(1-y[i,j]) >= 0)
			end
		end
	end
	
	print(m)
	
	res = solve(m)
	
	println("\nObjective value: ", getobjectivevalue(m))
	println("endTimes:")
	resx = getvalue(endTimes)
	for i=1:n
		println(i, ":", Int64(resx[i]-resx[i]%1.0))
	end
	
	return res
end

function solveSimple()
	n = 2
	times = [1, 1]
	weights = [1, 2]
	readyTimes = [0, 0]
	solveFor(n, times, weights, readyTimes)
end

function solveStandard()
	n = 4
	times = [1, 3, 2, 5]
	weights = [1, 7, 3, 2]
	readyTimes = [0, 2, 1, 1]
	solveFor(n, times, weights, readyTimes)
end