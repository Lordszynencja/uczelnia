function prepareA()
	A = Array{Float64}(101, 101)
	for i=1:101
		for j=1:101
			A[i, j] = 0
			if i+1 == j
				A[i, j] = (100-i+1)/100
			elseif i-1 == j
				A[i, j] = (i-1)/100
			end
		end
	end
	return A
end

