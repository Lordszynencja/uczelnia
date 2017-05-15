# Szymon Lewandowski

using JuMP
using GLPKMathProgInterface
using Clp

function solveFor(times, weights, readyTimes)
	n = size(times)[1]
	
	m = Model(solver = GLPKSolverMIP())
	
	bigNum = (sum(times) + sum(readyTimes))*10+100 # big number
	
	@variable(m, startTimes[1:n], Int)#start times
	@variable(m, endTimes[1:n], Int)#end times
	@variable(m, after[1:n, 1:n], Bin)#after[i, j] == TRUE <=> i is later than j
	
	@objective(m, Min, sum(endTimes[i]*weights[i] for i=1:n))
	
	for i=1:n
		@constraint(m, startTimes[i] >= readyTimes[i])#minimal start times
		@constraint(m, endTimes[i] == times[i]+startTimes[i])#end times are start times + computation times
		
		for j=1:n
			if i != j
				@constraint(m, startTimes[i]-endTimes[j]+bigNum*after[i, j] >= 0)#if start[i] is before end[j] then after[i, j] = true
				@constraint(m, startTimes[j]-endTimes[i]+bigNum*(1-after[i, j]) >= 0)#if end[j] is before start[i] then after[i, j] = false
			end
		end
	end
	
	#println(m)
	
	res = solve(m)
	
	println("Objective value: ", getobjectivevalue(m))
	println("endTimes:")
	resx = getvalue(endTimes)
	for i=1:n
		println(i, ":", Int64(resx[i]-resx[i]%1.0))
	end
	
	res_after = getvalue(after)
	for i=1:n, j=1:n
		println(i, ",", j, ":", res_after[i, j])
	end
	
	return res
end

function solveSimple()
	times = [1, 1]
	weights = [1, 2]
	readyTimes = [0, 0]
	solveFor(times, weights, readyTimes)
end

function solveStandard()
	times = [1, 3, 2, 5]
	weights = [1, 7, 3, 2]
	readyTimes = [0, 2, 1, 1]
	solveFor(times, weights, readyTimes)
end