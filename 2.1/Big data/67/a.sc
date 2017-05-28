val alpha = 0.3;

def toS2IMap(sets : Array[Set[String]]) : Map[String, Int] = {
	return sets.reduce((s1, s2) => s1.union(s2)).toArray.zipWithIndex.map(t => t._1 -> t._2).toMap;
}

def reverseMap(m : Map[String, Int]) : Map[Int, String] = {
	return m.map(t => t._2 -> t._1);
}

def toNumbers(sets : Array[Set[String]], S2I : Map[String, Int]) : Array[Set[Int]] = {
	sets.map(set => set.map(str => S2I.get(str).get));
}

def toCount(S2I : Map[String, Int], TI : Array[Set[Int]]) : Map[Int, Int] = {
	return S2I.map(t => t._2 -> TI.map(ti => ti.count(_ == t._2)).reduce((ti1, ti2) => ti1+ti2)).toMap;
}

val T = Array[Set[String]](Set[String]("123", "222"), Set[String]("000", "222"), Set[String]("123", "222"), Set[String]("122", "222"), Set[String]("11", "222"));
val S2I = toS2IMap(T);
val n = S2I.size;
val I2S = reverseMap(S2I);
val TI = toNumbers(T, S2I);
val C = toCount(S2I, TI).filter(c0 => c0._2 > alpha*n).map(t => t._1).toSet;