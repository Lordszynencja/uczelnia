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

function toDec(a, d)
	s = dec(a)
	if length(s)[1]>d
		return s[end-d+1:end]
	end
	return " "^(d-length(s)[1]) * s
end

function printResults(times, startTimes, machines, max, d)
	(n, m) = size(machines)
	for i=1:m
		print("|")
		for t=0:max-1
			count = 0
			for j=1:n
				if startTimes[j] <= t && startTimes[j]+times[j] > t && machines[j, i] == 1
					print(toDec(j, d))
					count = count+1
				end
			end
			if count == 0
				print(" "^d)
			end
			print("|")
		end
		println()
	end
end

function solveFor(m, times, precedence)
	n = size(times)[1]
	mod = Model(solver = GLPKSolverMIP())
	
	bigNum = sum(times)*10+100
	
	@variable(mod, startTimes[1:n], Int)
	@variable(mod, endTimes[1:n], Int)
	@variable(mod, before[1:n, 1:n], Bin)
	@variable(mod, machines[1:n, 1:m], Bin)
	@variable(mod, sameMachine[1:n, 1:n], Bin)
	@variable(mod, ms, Int)
	
	@objective(mod, Min, ms)
	
	for i=1:size(precedence)[1]
		@constraint(mod, before[precedence[i][1], precedence[i][2]] == 1)
		@constraint(mod, before[precedence[i][2], precedence[i][1]] == 0)
		
		@constraint(mod, startTimes[precedence[i][2]] >= endTimes[precedence[i][1]])
	end
	
	for i=1:n
		@constraint(mod, startTimes[i] >= 0)
		@constraint(mod, endTimes[i] == times[i]+startTimes[i])
		@constraint(mod, sum(machines[i, 1:m]) == 1)
		@constraint(mod, endTimes[i]-ms <= 0)
	end
	
	for i=1:n, j=1:n, k=1:m
		@constraint(mod, machines[j, k]+machines[i,k] <= sameMachine[i, j]+1)
	end
	
	precedences = preparePrecedence(n, precedence)
	
	for k=1:m, i=1:n, j=1:n
		if i != j
			@constraint(mod, startTimes[i]-endTimes[j]+bigNum*(before[i, j])+(1-precedences[i, j])*bigNum*(1-sameMachine[i, j]) >= 0)
			@constraint(mod, startTimes[j]-endTimes[i]+bigNum*(1-before[i, j])+(1-precedences[i, j])*bigNum*(1-sameMachine[i, j]) >= 0)
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
	
	res_before = getvalue(before)
	for i=1:n
		for j=1:n
			#println(i, ",", j, ": ", res_before[i, j])
		end
	end
	
	#res_same = getvalue(sameMachine)
	#for i=1:n, j=1:n
	#	println(i, j, res_same[i, j])
	#end
	d = 1
	if n>=10
		d = 2
	end
	printResults(times, getvalue(startTimes), res_machines, getobjectivevalue(mod), d)
	
	return res
end

function solveSimple1()
	m = 1
	times = [1, 1, 1, 1]
	precedence = []
	solveFor(m, times, precedence)
end

function solveSimple()
	m = 2
	times = [1, 1, 1, 1]
	precedence = [[1, 3], [2, 4]]
	solveFor(m, times, precedence)
end

function solveStandard()
	m = 3
	times = [1, 2, 1, 2, 1, 1, 3, 6, 2]
	precedence = [[1, 4], [2, 4], [2, 5], [3, 4], [3, 5], [4, 6], [4,7], [5, 7], [5, 8], [6, 9], [7, 9]]
	solveFor(m, times, precedence)
end  
