import scala.io.Source;
import java.io.PrintWriter;
import java.io.File;

val regex = "[^a-zA-Z\u0105\u0104\u0119\u0118\u015b\u015a\u0107\u0106\u017a\u0179\u017c\u017b\u00f3\u00d3\u0142\u0141\u0144\u0143\u0027]+"

class CounterK(k : Int) {
	private var A = Map[String, Int]()
	private val maxSize = k

	def add(x : String) : Unit = {
		if (A.contains(x)) {
			val el = A.get(x).get+1
			A += (x -> el)
		} else {
			A += (x -> 1)
		}
		if (A.size > maxSize) {
			val B = A.mapValues(a => a-1)
			A = B.filterKeys(a => B.get(a).get>0)
		}
	}
	
	def get() : Map[String, Int] = {
		return A
	}
	
}

def majorityK(list : List[String], k : Int) : Map[String, Int] = {
	var c = new CounterK(k)
	for (x <- list) {
		c.add(x)
	}
	return c.get()
}

val list : List[String] =
	(Source.fromFile("a.txt", "UTF-8")
		.mkString
		.toLowerCase()
		.split(regex)
		.toList)
print(majorityK(list, 2))