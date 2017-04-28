#####################################################			
# Pawel Zielinski
#####################################################	

using JuMP
using GLPKMathProgInterface # pakiet GLPK

function LP(A::Matrix{Float64},
						b::Vector{Float64},
						c::Vector{Float64})
(m,n)=size(A)
# m - liczba ograniczen
# n - liczba zmiennych
# A - macierz ograniczen
# b - wektor prawych stron
# c - wektor wspolczynnikow
						
										
	model = Model(solver = GLPKSolverLP()) # wybor solvera 
	
	@variable(model, x[1:n]>=0) # zmienne decyzyjne
	
	@objective(model,Max, vecdot(c,x))  # funkcja celu 

  @constraint(model,A*x .<=b) # ogranizenia
	
	print(model) # drukuj skonkretyzowany model

	status = solve(model, suppress_warnings=true) # rozwiaz model
	
	if status==:Optimal
		 return status, getobjectivevalue(model), getvalue(x)
	else
		return status, nothing,nothing
	end
		
end #LP

 # Przyklad 1 z materialow do wykladu 
 # Przyklad ilustruje sytuacje, ze istnieje dokladnie jedno rozwiazanie 
 # Funkcja celu 
 # max: 4 x1 +5 x2;
 #
 # Ograniczenia 
 #   x1  + 2 x2 <= 40;
 # 4 x1  + 3 x2 <= 120;
 # x1 i x2 sa rzeczywiste nieujemne 
 


b = [ 40.0; 120.0]
A = [1.0 2.0;
     4.0 3.0]
c = [ 4.0; 5.0]

(status, fval, x)=LP(A,b,c)
if status==:Optimal
	 println("fval: ", fval)
   println("x: ", x)
else
   println("Status: ", status)
end
   println("")



 # Przyklad 2 z materialow do wykladu 
 # Przyklad ilustruje sytuacje, ze istnieje nieskonczenie wiele rozwiazan 
 # Funkcja celu 
 # max: 4 x1 +3 x2;
 # Ograniczenia 
 #    x1  + 2 x2 <= 40;
 #  4 x1  + 3 x2 <= 120;
 #  x1 i x2 sa rzeczywiste nieujemne 

b = [ 40.0; 120.0]
A = [1.0 2.0;
     4.0 3.0]
c = [ 4.0; 3.0]

(status, fval, x)=LP(A,b,c)
if status==:Optimal
	 println("fval: ", fval)
   println("x: ", x)
else
   println("Status: ", status)
end
   println("")


 # Przyklad ilustruje sytuacje, w ktorej funkja celu nie jest ograniczona z gory
 # Funkcja celu 
 # max:  x1 +0.3333 x2; 
 # Ograniczenia 
 # -2 x1  + 5 x2 <= 150;          -2 x1  + 5 x2     <= 150;
 # x1  +   x2 >= 20;               - x1    -   x2   <= -20;
 # x1         >= 5;                - x1             <=  -5;
 # domyslnie zmienne x1 i x2 sa rzeczywiste nieujemne
b = [ 150.0; -20.0; -5.0]
A = [-2.0 5.0;
     -1.0 -1.0; 
		 -1.0 0.0]
c = [ 1.0; 0.3333]

(status, fval, x)=LP(A,b,c)
if status==:Optimal
	 println("fval: ", fval)
   println("x: ", x)
else
   println("Status: ", status)
end
   println("")

 # Przyklad ilustruje sytuacje, w ktorej nie ma rozwiazan dopuszczalnych
 # Funkcja celu 
 # min:  x1 +0.3333 x2; <-> max: -x1 - 0.3333 x2; 
 # Ograniczenia 
 # -2 x1  + 5 x2 <= 150;          -2 x1  + 5 x2     <= 150;
 # x1  +   x2 >= 20;               - x1    -   x2   <= -20;
 # x1         >= 5;                - x1             <=  -5;
 # x1  +   x2 <= 10;                 x1  +   x2     <= 10;
 # domyslnie zmienne x1 i x2 sa rzeczywiste nieujemne
b = [ 150.0; -20.0; -5.0; 10.0]
A = [-2.0 5.0;
     -1.0 -1.0;
		 -1.0  0.0;
		  1.0  1.0] 
c = [ -1.0; -0.3333]

(status, fval, x)=LP(A,b,c)
if status==:Optimal
	 println("fval: ", fval)
   println("x: ", x)
else
   println("Status: ", status)
end
   