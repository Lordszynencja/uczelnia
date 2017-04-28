import scala.io.Source;
import java.io.PrintWriter;
import java.io.File;

val regex = "[^a-zA-Z\u0105\u0104\u0119\u0118\u015b\u015a\u0107\u0106\u017a\u0179\u017c\u017b\u00f3\u00d3\u0142\u0141\u0144\u0143\u0027]+"

class Counter() {
	private var champion = ""
	private var count = 0

	def add(x : String) : Unit = {
		if (count == 0) champion = x
		if (champion == x) count = count+1
		else count = count-1
	}
	
	def get() : String = {
		return champion
	}
	
}

def majority(list : List[String]) : String = {
	var c = new Counter()
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
print(majority(list))