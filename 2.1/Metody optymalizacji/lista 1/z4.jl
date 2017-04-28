# Szymon Lewandowski

using JuMP
using GLPKMathProgInterface
using Clp

days = ["Pn.", "Wt.", "Śr.", "Czw.", "Pt."]

function asHour(h)
	return "$(Int64(h-h%1.0)):$(Int64(h%1.0*60))"
end

function calcLunchHours(classes)
	hours = Array(Array, length(classes))
	for i=1:length(classes)
		hours[i] = Array(Float64, length(classes[i]))
		for j=1:length(classes[i])
			if classes[i][j][2] <= 14 && classes[i][j][3] >= 12
				if classes[i][j][2] >= 12
					hours[i][j] = 14 - classes[i][j][2]
				elseif classes[i][j][3] <= 14
					hours[i][j] = classes[i][j][3]-12
				else
					hours[i][j] = 2
				end
			else
				hours[i][j] = 0
			end
		end
	end
	return hours
end

function solveSchool(classes, classesValues, names, additionalConstraints)
	m = Model(solver = GLPKSolverMIP())
	
	# chosen classes
	@variable(m, chosenClasses[1:size(classes)[1], 1:size(classes[1])[1]], Bin)
	
	# sum of points for chosen classes should be maximal
	@objective(m, Max, sum(sum(classesValues[(i-1)*size(chosenClasses)[2]+j]*chosenClasses[i, j] for j=1:size(chosenClasses)[2]) for i=1:size(chosenClasses)[1]))
	
	# every class should be chosen once
	for i=1:length(classes)-1
		@constraint(m, sum(chosenClasses[i, j] for j=1:length(classes[i])) == 1)
	end
	
	@constraint(m, chosenClasses[length(classes), 1] == 0)
	@constraint(m, sum(chosenClasses[length(classes), j] for j=2:length(classes[1])) >= 1)
	
	# one class at time
	for i1=1:length(classes)
		for i2=i1:length(classes)
			for j1=1:length(classes[1])
				for j2=j1:length(classes[1])
					if (i1 != i2 || j1 != j2) && classes[i1][j1][1] == classes[i2][j2][1] &&
						classes[i1][j1][2] <= classes[i2][j2][3] &&
						classes[i1][j1][3] >= classes[i2][j2][2]
						@constraint(m, chosenClasses[i1, j1] + chosenClasses[i2, j2] <= 1)
					end
				end
			end
		end
	end
	
	# max 4 hours a day
	for d=1:5
		@constraint(m, sum(sum(chosenClasses[i, j]*(classes[i][j][1]==d)*(classes[i][j][3]-classes[i][j][2]) for j=1:length(classes[1])) for i=1:length(classes)-1) <= 4.0)
	end
	
	lunchHours = calcLunchHours(classes)
	
	# at least one hour free between 12 and 14
	for d=1:5
		@constraint(m, sum(sum(chosenClasses[i, j]*lunchHours[i][j]*(classes[i][j][1]==d) for j=1:length(classes[1])) for i=1:length(classes)) <= 1.0)
	end
	
	# additional constraints
	if additionalConstraints
		# classes not on wednesday or friday
		@constraint(m, sum(sum((classes[i][j][1] == 3 || classes[i][j][1] == 5)*chosenClasses[i, j] for j=1:length(classes[i])) for i=1:length(classes)-1) == 0)
		
		#classes with value < 5 can't be chosen
		for i=1:length(classes)-1
			for j=1:length(classes[i])
				if classesValues[(i-1)*size(chosenClasses)[2]+j] < 5
					@constraint(m, chosenClasses[i, j] == 0)
				end
			end
		end
	end
	
	#println("########################")
	#print(m)
	#println("########################")
	
	res = solve(m)
	println()
	println(res)
	println("Objective value: ", getobjectivevalue(m))
	println("chosen classes: ")
	chosenOnes = getvalue(chosenClasses)
	for i=1:length(names)
		for j=1:length(classes[i])
			if chosenOnes[i, j] == 1
				println(names[i], ": ", j, " ", days[Int64(classes[i][j][1])], " ", asHour(classes[i][j][2]), "-", asHour(classes[i][j][3]))
			end
		end
	end
end

function testNormal()
	classes = [
		[[1, 13, 15], [2, 10, 12], [3, 10, 12], [3, 11, 13]],
		[[1, 13, 15], [2, 10, 12], [3, 11, 13], [4, 8, 10]],
		[[2, 8, 11], [2, 10, 13], [4, 15, 18], [4, 17, 20]],
		[[1, 8, 10], [1, 8, 10], [4, 13, 15], [5, 13, 15]],
		[[1, 9, 10.5], [1, 10.5, 12], [5, 11, 12.5], [5, 13, 14.5]],
		[[1, 13, 15], [1, 13, 15], [3, 11, 13], [3, 13, 15]]]

	classesValues = [
		[5, 4, 10, 5];
		[4, 4, 5, 6];
		[3, 5, 7, 8];
		[10, 10, 7, 5];
		[0, 5, 3, 4];
		[0, 0.1, 0.1, 0.1]]
		
	names = ["Algebra", "Analiza", "Fizyka", "Chemia minerałów", "Chemia organiczna", "Sport"]

	solveSchool(classes, classesValues, names, false)
end

function testSpecial()
	classes = [
		[[1, 13, 15], [2, 10, 12], [3, 10, 12], [3, 11, 13]],
		[[1, 13, 15], [2, 10, 12], [3, 11, 13], [4, 8, 10]],
		[[2, 8, 11], [2, 10, 13], [4, 15, 18], [4, 17, 20]],
		[[1, 8, 10], [1, 8, 10], [4, 13, 15], [5, 13, 15]],
		[[1, 9, 10.5], [1, 10.5, 12], [5, 11, 12.5], [5, 13, 14.5]],
		[[1, 13, 15], [1, 13, 15], [3, 11, 13], [3, 13, 15]]]

	classesValues = [
		[5, 4, 10, 5];
		[4, 4, 5, 6];
		[3, 5, 7, 8];
		[10, 10, 7, 5];
		[0, 5, 3, 4];
		[0, 0.1, 0.1, 0.1]]
		
	names = ["Algebra", "Analiza", "Fizyka", "Chemia minerałów", "Chemia organiczna", "Sport"]

	solveSchool(classes, classesValues, names, true)
end
