# Szymon Lewandowski

using JuMP
using GLPKMathProgInterface
using Clp

function solveRefinery(needs, destI, destII, crack, destPrice, crackPrice, prices, percents, maxPercent, s)
	m = Model(solver = s)
	
	@variable(m, b1 >= 0)
	@variable(m, b2 >= 0)
	
	#cost must be minimal
	@objective(m, Min, b1*prices[1]+b2*prices[2]+(b1+b2)*destPrice+(destI[3]*b1+destII[3]*b2)*crackPrice)
	
	#paliwa silnikowe
	@constraint(m, destI[1]*b1+destII[1]*b2+(destI[3]*b1+destII[3]*b2)*crack[1] >= needs[1])
	
	#domowe paliwa olejowe
	@constraint(m, destI[2]*b1+destII[2]*b2+(destI[3]*b1+destII[3]*b2)*crack[2] >= needs[2])
	
	#ciężkie paliwa olejowe
	@constraint(m, (destI[2]+destI[4])*b1+(destII[2]+destII[4])*b2+(destI[3]*b1+destII[3]*b2)*(crack[2]+crack[3]) >= needs[3]+needs[2])
	
	#zawartość siarki
	@constraint(m, (destI[2]*b1*percents[1]+destII[2]*b2*percents[2]+destI[3]*b1*crack[2]*percents[3]+destII[3]*b2*crack[2]*percents[4])
		 <= maxPercent*(destI[2]*b1+destII[2]*b2+(destI[3]*b1+destII[3]*b2)*crack[2]))
	
	res = solve(m)
	
	println("########################")
	print(m)
	println("########################")
	
	println(res)
	println("b1: ", getvalue(b1), " t")
	println("b2: ", getvalue(b2), " t")
end

function runTest()
	needs = [200000, 400000, 250000]
	destI = [0.15, 0.40, 0.15, 0.15]
	destII = [0.10, 0.35, 0.20, 0.25]
	crack = [0.50, 0.20, 0.06]
	destPrice = 10
	crackPrice = 20
	prices = [1300, 1500]
	percents = [0.002, 0.012, 0.003, 0.025]
	maxPercent  = 0.005

	solveRefinery(needs, destI, destII, crack, destPrice, crackPrice, prices, percents, maxPercent, GLPKSolverLP())
end
