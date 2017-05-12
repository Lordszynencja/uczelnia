#Szymon Lewandowski

using JuMP 
using GLPKMathProgInterface
using Clp

function preparePrecedence(n, precedence)
	m = Array{Int64}(n, n)
	for i=1:n, j=1:n
		m[i, j] = 0
	end
	for i=1:size(precedence)[1]
		m[precedence[i][1], precedence[i][2]] = 1
		m[precedence[i][2], precedence[i][1]] = -1
	end
	return m
end

function solveFor(n, m, times, precedence)
	mod = Model(solver = GLPKSolverMIP())
	
	bigNum = sum(times)*10+100
	
	@variable(mod, endTimes[1:n], Int)
	@variable(mod, y[1:n, 1:n], Bin)
	@variable(mod, machines[1:n, 1:m], Bin)
	@variable(mod, sameMachine[1:n, 1:n], Bin)
	@variable(mod, ms, Int)
	
	@objective(mod, Min, ms)
	
	precedences = preparePrecedence(n, precedence)
	
	for i=1:size(precedence)[1]
		@constraint(mod, y[precedence[i][1], precedence[i][2]] == 1)
		@constraint(mod, y[precedence[i][2], precedence[i][1]] == 0)
	end
	
	for i=1:n
		@constraint(mod, sum(machines[i, 1:m]) == 1)
	end
	
	for i=1:n, j=1:n, k=1:m
		@constraint(mod, machines[j, k]+machines[i,k] <= sameMachine[i, j]+1)
	end
	
	for i=1:n
		@constraint(mod, endTimes[i]-ms <= 0)
	end
	
	for i=1:n
		@constraint(mod, endTimes[i] >= times[i])
	end
	
	for k=1:m, i=1:n, j=1:n
		if i != j
			if precedences[i, j] == 1
				@constraint(mod, endTimes[i]-times[i]-endTimes[j]+bigNum*(y[i, j]) >= 0)
				@constraint(mod, endTimes[j]-times[j]-endTimes[i]+bigNum*(1-y[i, j]) >= 0)
			else
				@constraint(mod, endTimes[i]-times[i]-endTimes[j]+bigNum*(y[i, j])+bigNum*(1-sameMachine[i, j]) >= 0)
				@constraint(mod, endTimes[j]-times[j]-endTimes[i]+bigNum*(1-y[i, j])+bigNum*(1-sameMachine[i, j]) >= 0)
			end
		end
	end
	
	#print(mod)
	
	res = solve(mod)
	
	println("\nObjective value: ", getobjectivevalue(mod))
	println("endTimes:")
	resx = getvalue(endTimes)
	res_machines = getvalue(machines)
	for i=1:n
		for j=1:m
			if (res_machines[i, j] == 1)
				println(i, ": ", "m", j, " ", resx[i])
			end
		end
	end
	
	#res_y = getvalue(y)
	#for i=1:n
	#	for j=1:n
	#		println(i, ",", j, ": ", res_y[i, j])
	#	end
	#end
	
	#res_same = getvalue(sameMachine)
	#for i=1:n, j=1:n
	#	println(i, j, res_same[i, j])
	#end
	
	return res
end

function solveSimple1()
	n = 4
	m = 1
	times = [1, 1, 1, 1]
	precedence = []
	solveFor(n, m, times, precedence)
end

function solveSimple()
	n = 4
	m = 2
	times = [1, 1, 1, 1]
	precedence = [[1, 3], [2, 4]]
	solveFor(n, m, times, precedence)
end

function solveStandard()
	m = 3
	n = 9
	times = [1, 2, 1, 2, 1, 1, 3, 6, 2]
	precedence = [[1, 4], [2, 4], [2, 5], [3, 4], [3, 5], [4, 6], [4,7], [5, 7], [5, 8], [6, 9],[7, 9]]
	solveFor(n, m, times, precedence)
end  
