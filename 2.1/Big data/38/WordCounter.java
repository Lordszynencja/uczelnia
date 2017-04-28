import java.util.*;

public class WordCounter {
	public static Map<String, Integer> countWords(String[] words) {
		Map<String, Integer> count = new HashMap<String, Integer>();
		for (String w : words) {
			Integer wordCount = count.get(w);
			if (wordCount == null) {
				wordCount = 0;
			}
			wordCount++;
			count.put(w, wordCount);
		}
		return count;
	}
}