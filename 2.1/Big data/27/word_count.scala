import scala.io.Source
import java.io._

val STOPWORDS = Array("a", "aby", "ach", "acz", "aczkolwiek", "aj", "albo", "ale", "ale¿", "ani", "a¿", "bardziej", "bardzo", "bo", "bowiem", "by", "byli", "bynajmniej", "byæ", "by³", "by³a", "by³o", "by³y", "bêdzie", "bêd¹", "cali", "ca³a", "ca³y", "ci", "ciê", "ciebie", "co", "cokolwiek", "coœ", "czasami", "czasem", "czemu", "czy", "czyli", "daleko", "dla", "dlaczego", "dlatego", "do", "dobrze", "dok¹d", "doœæ", "du¿o", "dwa", "dwaj", "dwie", "dwoje", "dziœ", "dzisiaj", "gdy", "gdyby", "gdy¿", "gdzie", "gdziekolwiek", "gdzieœ", "i", "ich", "ile", "im", "inna", "inne", "inny", "innych", "i¿", "ja", "j¹", "jak", "jakaœ", "jakby", "jaki", "jakichœ", "jakie", "jakiœ", "jaki¿", "jakkolwiek", "jako", "jakoœ", "je", "jeden", "jedna", "jedno", "jednak", "jednak¿e", "jego", "jej", "jemu", "jest", "jestem", "jeszcze", "jeœli", "je¿eli", "ju¿", "j¹", "ka¿dy", "kiedy", "kilka", "kimœ", "kto", "ktokolwiek", "ktoœ", "która", "które", "którego", "której", "który", "których", "którym", "którzy", "ku", "lat", "lecz", "lub", "ma", "maj¹", "ma³o", "mam", "mi", "mimo", "miêdzy", "mn¹", "mnie", "mog¹", "moi", "moim", "moja", "moje", "mo¿e", "mo¿liwe", "mo¿na", "mój", "mu", "musi", "my", "na", "nad", "nam", "nami", "nas", "nasi", "nasz", "nasza", "nasze", "naszego", "naszych", "natomiast", "natychmiast", "nawet", "ni¹", "nic", "nich", "nie", "niech", "niego", "niej", "niemu", "nigdy", "nim", "nimi", "ni¿", "no", "o", "obok", "od", "oko³o", "on", "ona", "one", "oni", "ono", "oraz", "oto", "owszem", "pan", "pana", "pani", "po", "pod", "podczas", "pomimo", "ponad", "poniewa¿", "powinien", "powinna", "powinni", "powinno", "poza", "prawie", "przecie¿", "przed", "przede", "przedtem", "przez", "przy", "roku", "równie¿", "sama", "s¹", "siê", "sk¹d", "sobie", "sob¹", "sposób", "swoje", "ta", "tak", "taka", "taki", "takie", "tak¿e", "tam", "te", "tego", "tej", "temu", "ten", "teraz", "te¿", "to", "tob¹", "tobie", "tote¿", "trzeba", "tu", "tutaj", "twoi", "twoim", "twoja", "twoje", "twym", "twój", "ty", "tych", "tylko", "tym", "u", "w", "wam", "wami", "was", "wasz", "wasza", "wasze", "we", "wed³ug", "wiele", "wielu", "wiêc", "wiêcej", "wszyscy", "wszystkich", "wszystkie", "wszystkim", "wszystko", "wtedy", "wy", "w³aœnie", "z", "za", "zapewne", "zawsze", "ze", "z³", "znowu", "znów", "zosta³", "¿aden", "¿adna", "¿adne", "¿adnych", "¿e", "¿eby")

def notInStopWords(w:String):Boolean = {
	!STOPWORDS.contains(w)
}

def map(in:String):Seq[(String, String)] = {
	val bufor  = Source.fromFile(in, "UTF-8")
	var keyval = collection.mutable.ListBuffer.empty[(String,String)]
	var words : Array[String] = Array()
	
	// Wczytujemy kolejne linie, aby to bardziej upodobniæ prawdziwego Mappera
	for (line <- bufor.getLines()) {
		words = line.mkString.split("\\s+")
		for (word <- words if notInStopWords(word)){
			var tmp = word.toLowerCase().replaceAll("[,.!:?*;»…()«]", "")
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


