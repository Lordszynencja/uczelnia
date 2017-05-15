#Szymon Lewandowski

using JuMP 
using GLPKMathProgInterface
using Clp

function solveFor(times, precedence, needs, maxNeeds)
	n = size(times)[1]
	m = size(maxNeeds)[1]

	mod = Model(solver = GLPKSolverMIP())
	
	bigNum = 2^10
	
	### VARIABLES ###
	@variable(mod, ms, Int)
	@variable(mod, endTimes[1:n], Int)#end time of job i
	@variable(mod, startTimes[1:n], Int)#start time of job i (= end time - job time)
	@variable(mod, workingAtStart[1:n, 1:n], Bin)#if (i,j) == 1 then j is working during i start
	
	@variable(mod, startAfterI[1:n, 1:n], Bin)#if (i,j) == 1 then j starts >= i starts
	@variable(mod, startAfterIEnd[1:n, 1:n], Bin)#if (i,j) == 1 then j starts >= i ends
	@variable(mod, endAfterI[1:n, 1:n], Bin)#if (i,j) == 1 then j ends >= i ends
	@variable(mod, endAfterIStart[1:n, 1:n], Bin)#if (i,j) == 1 then j ends >= i starts
	
	@variable(mod, costs[1:n, 1:n, 1:m], Int)#costs for (i, j, k) = at startTime[i] costs[i, j-1, k] + (j works at startTime[i] ? needs[j][k] : 0)
	
	### OBJECTIVE ###
	@objective(mod, Min, ms)
		
	### CONSTRAINTS ###
	for i=1:n
		@constraint(mod, endTimes[i]-startTimes[i]-times[i] == 0)#start time = end time - job time
		@constraint(mod, endTimes[i]-ms <= 0)#ms is max from endTimes
		@constraint(mod, startTimes[i] >= 0)#time starts at 0
		@constraint(mod, startAfterI[i, i] == 1)#
		@constraint(mod, startAfterIEnd[i, i] == 0)#
		@constraint(mod, endAfterI[i, i] == 1)#
		@constraint(mod, endAfterIStart[i, i] == 1)#
		@constraint(mod, workingAtStart[i, i] == 1)#
	end
	
	for i=1:size(precedence)[1]
		@constraint(mod, startTimes[precedence[i][2]]-endTimes[precedence[i][1]] >= 0)#set precedence
		
		@constraint(mod, startAfterI[precedence[i][1], precedence[i][2]] == 1)
		@constraint(mod, startAfterI[precedence[i][2], precedence[i][1]] == 0)
		
		@constraint(mod, startAfterIEnd[precedence[i][1], precedence[i][2]] == 1)
		@constraint(mod, startAfterIEnd[precedence[i][2], precedence[i][1]] == 0)
		
		@constraint(mod, endAfterI[precedence[i][1], precedence[i][2]] == 1)
		@constraint(mod, endAfterI[precedence[i][2], precedence[i][1]] == 0)
		
		@constraint(mod, endAfterIStart[precedence[i][1], precedence[i][2]] == 1)
		
		@constraint(mod, workingAtStart[precedence[i][1], precedence[i][2]] == 0)
		@constraint(mod, workingAtStart[precedence[i][2], precedence[i][1]] == 0)
	end
	
	for i=1:n, j=1:n
		if i != j
			@constraint(mod, startTimes[i]-startTimes[j]+bigNum*(startAfterI[i, j]) >= 1)
			@constraint(mod, startTimes[j]-startTimes[i]+bigNum*(1-startAfterI[i, j]) >= 0)
			
			@constraint(mod, endTimes[i]-startTimes[j]+bigNum*(startAfterIEnd[i, j]) >= 1)
			@constraint(mod, startTimes[j]-endTimes[i]+bigNum*(1-startAfterIEnd[i, j]) >= 0)
			
			@constraint(mod, endTimes[i]-endTimes[j]+bigNum*(endAfterI[i, j]) >= 1)
			@constraint(mod, endTimes[j]-endTimes[i]+bigNum*(1-endAfterI[i, j]) >= 0)
			
			@constraint(mod, startTimes[i]-endTimes[j]+bigNum*(endAfterIStart[i, j]) >= 1)
			@constraint(mod, endTimes[j]-startTimes[i]+bigNum*(1-endAfterIStart[i, j]) >= 0)
			
			@constraint(mod, startAfterI[j, i]-startAfterIEnd[j, i] <= workingAtStart[i, j])
		end
		if j == 1
			for k=1:m
				@constraint(mod, 0 <= costs[i, 1, k] <= maxNeeds[k])
				@constraint(mod, costs[i, 1, k] == needs[1][k]*workingAtStart[i, 1])
			end
		else
			for k=1:m
				@constraint(mod, 0 <= costs[i, j, k] <= maxNeeds[k])
				@constraint(mod, costs[i, j, k] == costs[i, j-1, k]+needs[j][k]*workingAtStart[i, j])
			end
		end
	end
	
	
	
	#print(mod)
	
	res = solve(mod)
	
	println("\nObjective value: ", getobjectivevalue(mod))
	println("startTimes:")
	res_startTimes = getvalue(startTimes)
	for i=1:n
		println(i, ": ", res_startTimes[i])
	end
	
	println()
	println("endTimes:")
	res_endTimes = getvalue(endTimes)
	for i=1:n
		println(i, ": ", res_endTimes[i])
	end
	
	println()
	println("costs:")
	res_costs = getvalue(costs)
	for i=1:n, k=1:m
		println(i, ",", n, ",", m, ": ", res_costs[i, n, k])
	end
	
	#println()
	#println("startAfterI: OK")
	res_startAfterI = getvalue(startAfterI)
	for i=1:n, j=1:n
		#println(i, ",", j, ": ", res_startAfterI[i, j])
	end
	
	#println()
	#println("startAfterIEnd: OK")
	res_startAfterIEnd = getvalue(startAfterIEnd)
	for i=1:n, j=1:n
		#println(i, ",", j, ": ", res_startAfterIEnd[i, j])
	end
	
	#println()
	#println("endAfterI: OK")
	res_endAfterI= getvalue(endAfterI)
	for i=1:n, j=1:n
		#println(i, ",", j, ": ", res_endAfterI[i, j])
	end
	
	#println()
	#println("endAfterIStart: OK")
	res_endAfterIStart= getvalue(endAfterIStart)
	for i=1:n, j=1:n
		#println(i, ",", j, ": ", res_endAfterIStart[i, j])
	end
	
	#println()
	#println("workingAtStart: OK")
	res_workingAtStart = getvalue(workingAtStart)
	for i=1:n, j=1:n
		#println(i, ",", j, ": ", res_workingAtStart[i, j])
	end
	
	return res
end

function solveSimple1()
	times = [1, 1, 1]
	precedence = []
	needs = [[1], [1], [1]]
	maxNeeds = [3]
	solveFor(times, precedence, needs, maxNeeds)
end

function solveSimple2()
	times = [1, 1, 1]
	precedence = [[1, 3]]
	needs = [[1], [1], [1]]
	maxNeeds = [4]
	solveFor(times, precedence, needs, maxNeeds)
end

function solveStandard()
	times = [50, 47, 55, 46, 32, 57, 15, 62]
	precedence = [[1, 2], [1, 3], [1, 4], [2, 5], [3, 6], [4, 6], [4, 7], [5, 8], [6, 8], [7, 8]]
	needs = [[9], [17], [11], [4], [13], [7], [7], [17]]
	maxNeeds = [30]
	solveFor(times, precedence, needs, maxNeeds)
end  
