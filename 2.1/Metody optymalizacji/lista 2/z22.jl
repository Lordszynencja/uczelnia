# Szymon Lewandowski

using JuMP
using GLPKMathProgInterface
using Clp

function prettyPrintResult(n, result, names)
	for i=1:n
		for j=1:n
			if result[i, j] != 0
				println("[", i, "]", names[i], "->", "[", j, "]", names[j], ": ", result[i, j])
			end
		end
	end
end

function solveFor(n, needI, needII, availableI, availableII, priceI, priceII, distances, IIasI, IasII, s, names)
	m = Model(solver = s)
	
	@variable(m, xI[1:n, 1:n] >= 0) # movements of type I machines
	@variable(m, xII[1:n, 1:n] >= 0) # movements of type II machines
	
	# cost of movement must be minimal
	@objective(m, Min, sum(sum(xI[i, j]*distances[i][j]*priceI+xII[i, j]*distances[i][j]*priceII for j=1:n) for i=1:n))
	
	for j=1:n # sum of both types of machines coming to city should be bigger or equal to sum of needs
		@constraint(m, sum(xI[i, j]+xII[i, j] for i=1:n) >= needI[j]+needII[j])
	end
	
	if IIasI
		if !IasII
			for j=1:n
				@constraint(m, sum(xII[i, j] for i=1:n) >= needII[j]) # type I can't be used as type II
			end
		end
	else
		if IasII
			for j=1:n
				@constraint(m, sum(xI[i, j] for i=1:n) >= needI[j]) # type II can't be used as type I
			end
		else
			for j=1:n
				@constraint(m, sum(xI[i, j] for i=1:n) >= needI[j]) # type II can't be used as type I
				@constraint(m, sum(xII[i, j] for i=1:n) >= needII[j]) # type I can't be used as type II
			end
		end
	end
	
	for i=1:n # check availability
		@constraint(m, sum(xI[i, j] for j=1:n) <= availableI[i])
		@constraint(m, sum(xII[i, j] for j=1:n) <= availableII[i])
	end
	
	res = solve(m)
	
	#println("########################")
	#print(m)
	#println("########################")
	
	println(res)
	println("xI:")
	prettyPrintResult(n, getvalue(xI), names)
	println("xII:")
	prettyPrintResult(n, getvalue(xII), names)
end

n = 7
needI = [0, 10, 0, 4, 0, 8, 0]
needII = [2, 0, 0, 0, 4, 2, 1]
availableI = [7, 0, 6, 0, 5, 0, 0]
availableII = [0, 1, 2, 10, 0, 0, 0]
priceI = 1
priceII = 1.2
distances = [
#Opole
	[0, 43, 55, 50, 34, 49, 78],
#Brzeg
    [43, 0, 53, 81, 96, 95, 122],
#Nysa
    [55, 53, 0, 29, 86, 75, 89],
#Prudnik
    [50, 81, 29, 0, 69, 48, 62],
#Strzelce Opolskie
    [34, 96, 86, 69, 0, 24, 59],
#Koźle
    [49, 95, 75, 48, 24, 0, 37],
#Racibórz
    [78, 122, 89, 62, 59, 37, 0]
]
names = ["Opole", "Brzeg", "Nysa", "Prudnik", "Strzelce Opolskie", "Koźle", "Racibórz"]

solveFor(n, needI, needII, availableI, availableII, priceI, priceII, distances, true, false, ClpSolver(), names)
