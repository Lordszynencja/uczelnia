import scala.io.Source;
import java.io.PrintWriter;
import java.io.File;

val regex = "\u005b\u005e\u0041\u002d\u005a\u0061\u002d\u007a\u0105\u0104\u0119\u0118\u015b\u015a\u0107\u0106\u017a\u0179\u017c\u017b\u00f3\u00d3\u0142\u0141\u0144\u0143\u0027\u005d\u002b";

def countWords(filename : String) : Seq[(String, Int)] = {
	Source.fromFile(filename, "UTF-8")
		.mkString
		.toLowerCase()
		.split(regex)
		.filterNot(Source
			.fromFile("stopwords.txt", "UTF-8")
			.mkString
			.toLowerCase()
			.split(regex)
			.contains(_))
		.groupBy(x=>x)
		.mapValues(x=>x.length)
		.toSeq
		.sortWith((x,y)=>x._2>y._2);
}

def printResults(filename : String, results : Seq[(String, Int)]) : Unit = {
	val pw = new PrintWriter(new File(filename))
	results.foreach(p => {
		pw.println(p._2 + " " + p._1);
	})
	pw.close();
}

printResults("scala_results.txt", countWords("Pratchett Terry Kolor magii.txt"));