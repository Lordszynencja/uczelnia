class SlidingWindow(N : Int, k : Int) {
	var B = new scala.collection.mutable.ArrayBuffer[Array[Long]]();
	//* B(i)(0) = timestamp
	//* B(i)(1) = wartosc
	var last = 0L; //zawartosc najstarszego licznika
	var total = 0L; //suma zawartosci licznikow
	var id = 0L; //liczba wczytanych liczb; czyli numer najnowszego elementu
	
	def add(x : Int) : Unit = {
		id = id + 1;
		
		while ((B.length>0) && (B(0)(0) < id - N)) {
			total = total - B(0)(1);
			B.remove(0);
			last = if (B.length>0) B(0)(1) else 0;
		}
		if (x == 1) {
			B += Array(id, 1);
			total = total + 1;
		}
		
		var check = true;
		var size = 1;
		while (check) {
			check = false;
			if (B.length >= 0) {
				var oldest = -1;
				var oldestId = -1L;
				var oldest2 = -1;
				var oldest2Id = -1L;
				var count = 0;
				for (i <- Range(0, B.length)) {
					if (B(i)(1) == size) {
						count += 1;
						if (oldest == -1 || oldestId > B(i)(0)) {
							oldest2 = oldest;
							oldest2Id = oldestId;
							oldest = i;
							oldestId = B(i)(0);
						} else if (oldest2 == -1 || oldest2Id > B(i)(0)) {
							oldest2 = i;
							oldest2Id = B(i)(0);
						}
					}
				}
				if (count >= k/2+2) {
					check = true;
					size *= 2;
					if (oldest > oldest2) {
						B.remove(oldest);
						B.remove(oldest2);
						B += Array(oldest2Id, size);
					} else {
						B.remove(oldest2);
						B.remove(oldest);
						B += Array(oldestId, size);
					}
					last = size;
				}
			}
		}
	}
	
	def get() : Long = {
		total - last/2;
	}
}


