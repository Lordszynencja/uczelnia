def toS2IMap(sets : Array[Set[String]]) : Map[String, Int] = {
	return sets.reduce((s1, s2) => s1.union(s2)).toArray.zipWithIndex.map(t => t._1 -> t._2).toMap;
}

def reverseMap(m : Map[String, Int]) : Map[Int, String] = {
	return m.map(t => t._2 -> t._1);
}

val a = Array[Set[String]](Set[String]("123", "222"), Set[String]("000", "222"));
val S2I = toS2IMap(a);
val I2S = reverseMap(S2I);