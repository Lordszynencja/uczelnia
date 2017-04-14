import scala.io.Source
import java.io._

val STOPWORDS = Array("a", "aby", "ach", "acz", "aczkolwiek", "aj", "albo", "ale", "ale�", "ani", "a�", "bardziej", "bardzo", "bo", "bowiem", "by", "byli", "bynajmniej", "by�", "by�", "by�a", "by�o", "by�y", "b�dzie", "b�d�", "cali", "ca�a", "ca�y", "ci", "ci�", "ciebie", "co", "cokolwiek", "co�", "czasami", "czasem", "czemu", "czy", "czyli", "daleko", "dla", "dlaczego", "dlatego", "do", "dobrze", "dok�d", "do��", "du�o", "dwa", "dwaj", "dwie", "dwoje", "dzi�", "dzisiaj", "gdy", "gdyby", "gdy�", "gdzie", "gdziekolwiek", "gdzie�", "i", "ich", "ile", "im", "inna", "inne", "inny", "innych", "i�", "ja", "j�", "jak", "jaka�", "jakby", "jaki", "jakich�", "jakie", "jaki�", "jaki�", "jakkolwiek", "jako", "jako�", "je", "jeden", "jedna", "jedno", "jednak", "jednak�e", "jego", "jej", "jemu", "jest", "jestem", "jeszcze", "je�li", "je�eli", "ju�", "j�", "ka�dy", "kiedy", "kilka", "kim�", "kto", "ktokolwiek", "kto�", "kt�ra", "kt�re", "kt�rego", "kt�rej", "kt�ry", "kt�rych", "kt�rym", "kt�rzy", "ku", "lat", "lecz", "lub", "ma", "maj�", "ma�o", "mam", "mi", "mimo", "mi�dzy", "mn�", "mnie", "mog�", "moi", "moim", "moja", "moje", "mo�e", "mo�liwe", "mo�na", "m�j", "mu", "musi", "my", "na", "nad", "nam", "nami", "nas", "nasi", "nasz", "nasza", "nasze", "naszego", "naszych", "natomiast", "natychmiast", "nawet", "ni�", "nic", "nich", "nie", "niech", "niego", "niej", "niemu", "nigdy", "nim", "nimi", "ni�", "no", "o", "obok", "od", "oko�o", "on", "ona", "one", "oni", "ono", "oraz", "oto", "owszem", "pan", "pana", "pani", "po", "pod", "podczas", "pomimo", "ponad", "poniewa�", "powinien", "powinna", "powinni", "powinno", "poza", "prawie", "przecie�", "przed", "przede", "przedtem", "przez", "przy", "roku", "r�wnie�", "sama", "s�", "si�", "sk�d", "sobie", "sob�", "spos�b", "swoje", "ta", "tak", "taka", "taki", "takie", "tak�e", "tam", "te", "tego", "tej", "temu", "ten", "teraz", "te�", "to", "tob�", "tobie", "tote�", "trzeba", "tu", "tutaj", "twoi", "twoim", "twoja", "twoje", "twym", "tw�j", "ty", "tych", "tylko", "tym", "u", "w", "wam", "wami", "was", "wasz", "wasza", "wasze", "we", "wed�ug", "wiele", "wielu", "wi�c", "wi�cej", "wszyscy", "wszystkich", "wszystkie", "wszystkim", "wszystko", "wtedy", "wy", "w�a�nie", "z", "za", "zapewne", "zawsze", "ze", "z�", "znowu", "zn�w", "zosta�", "�aden", "�adna", "�adne", "�adnych", "�e", "�eby")

def notInStopWords(w:String):Boolean = {
	!STOPWORDS.contains(w)
}

def map(in:String):Seq[(String, String)] = {
	val bufor  = Source.fromFile(in, "UTF-8")
	var keyval = collection.mutable.ListBuffer.empty[(String,String)]
	var words : Array[String] = Array()
	
	// Wczytujemy kolejne linie, aby to bardziej upodobni� prawdziwego Mappera
	for (line <- bufor.getLines()) {
		words = line.mkString.split("\\s+")
		for (word <- words if notInStopWords(word)){
			var tmp = word.toLowerCase().replaceAll("[,.!:?*;��()�]", "")
			if (tmp.length>1) keyval += Tuple2(tmp, "1")
		} 
	}
	bufor.close
	keyval
}

def saveKeyval(keyval:Seq[(String, String)], outName: String):Unit = {
	val printer = new PrintWriter(new File(outName), "UTF-8")
	for (word <- keyval) {
		printer.write(word._1.toString + "," + word._2.toString + "\n")
	}
	printer.close
}

def loadKeyval(in: String):Seq[(String, String)] = {
	val bufor  = Source.fromFile(in, "UTF-8")
	var keyval = collection.mutable.ListBuffer.empty[(String,String)]
	
	for (line <- bufor.getLines()) {
		var tmp = line.split(",") match { case Array(word, count) => Tuple2(word, count)}
		if (!tmp._1.isEmpty) keyval += tmp
	}
	bufor.close
	keyval
}

def reduce(keyval:Seq[(String, String)], out:String):Unit = {
	val printer = new PrintWriter(new File(out), "UTF-8")
	val countedWords = keyval.groupBy(_._1).mapValues(_.size).toSeq.sortWith(_._2>_._2)
	
	printer.write("output:\n")
	
	for (x<-countedWords){
		printer.write(x._1.toString + "," + x._2.toString + "\n")
	}
	printer.close
}

var keyVal = map("a.txt");
saveKeyval(keyVal, "map.txt");
var keyVal2 = loadKeyval("map.txt");
reduce(keyVal2, "result.txt");


