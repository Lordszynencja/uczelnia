# Szymon Lewandowski

using JuMP
using GLPKMathProgInterface
using Clp

function preparePossibleSizes(sizes, possibleSizes, actualSizes, sizeLeft)#prepares possible cuttings
	for i=1:length(sizes)
		if sizes[i] <= sizeLeft
			possibleSizes = union(possibleSizes, preparePossibleSizes(sizes, [], vcat(actualSizes, [sizes[i]]), sizeLeft-sizes[i]))
		elseif sizeLeft<minimum(sizes)
			s = [0 for i=1:length(sizes)]
			for i=1:length(actualSizes)
				s[findfirst(sizes, actualSizes[i])] += 1
			end
			possibleSizes = union(possibleSizes, [s])
		end
	end
	return possibleSizes
end

function calculateTrash(deskSize, sizes, possibleSizes)#calculates trash left after each possible cutting
	trash = []
	for i=1:size(possibleSizes)[1]
		append!(trash, deskSize - sum(possibleSizes[i][j]*sizes[j] for j=1:size(sizes)[1]))
	end
	return trash
end

function solveFor(deskSize, wantedSizes, wantedAmounts)
	possibleSizes = preparePossibleSizes(wantedSizes, [], [], deskSize)
	println("\npossibleSizes:")
	for i=1:size(possibleSizes)[1]
		print(i)
		print(":")
		println(possibleSizes[i])
	end
	
	trash = calculateTrash(deskSize, wantedSizes, possibleSizes)
	println("\ntrash:")
	for i=1:size(trash)[1]
		print(i)
		print(":")
		println(trash[i])
	end
	
	n = size(possibleSizes)[1]
	
	m = Model(solver = GLPKSolverMIP())
	
	@variable(m, x[1:n] >= 0, Int)#number of made cuttings
	
	#minimal trash
	@objective(m, Min, sum(trash[i]*x[i] for i=1:n) + sum((sum(x[j] * possibleSizes[j][i] for j=1:n) - wantedAmounts[i])*wantedSizes[i] for i=1:length(wantedSizes)))
	
	for i=1:length(wantedSizes)
		@constraint(m, sum(x[j] * possibleSizes[j][i] for j=1:n) >= wantedAmounts[i])#sum of desk with size wantedSizes[j] should meet needs
	end
	
	#print(m)
	
	res = solve(m)
	
	println("\nObjective value: ", getobjectivevalue(m))
	println("x:")
	resx = getvalue(x)
	for i=1:n
		println(i, ":", Int64(resx[i]-resx[i]%1.0))
	end
	
	return res
end

function solveStandard()
	standardDeskSize = 22
	standardWantedSizes = [3, 5, 7]
	standardWantedAmounts = [80, 120, 110]
	solveFor(standardDeskSize, standardWantedSizes, standardWantedAmounts)
end
