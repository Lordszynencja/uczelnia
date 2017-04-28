import java.util.*;
import java.io.*;
import java.lang.Math.*;

public class InvertedDocumentFrequency {
	public static List<String> getWordsFromFile(String filename) {
		List<String> words = new ArrayList<String>();
		
		String file = "";
		try {
			File f = new File(filename);
			InputStream input = new FileInputStream(f);
			byte[] bytes = new byte[(int)f.length()];
			input.read(bytes);
			file = new String(bytes, "UTF-8");
			input.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String[] wordsArray = file.split("\\s+");
		words = Arrays.asList(wordsArray);
		return words;
	}
	
	public static Map<String, Set<String>> map(Map<String, List<String>> documentWords) {
		Map<String, Set<String>> map = new HashMap<String, Set<String>>();
		for (String key : documentWords.keySet()) {
			for (String word : documentWords.get(key)) {
				Set<String> documents = map.get(word);
				if (documents == null) documents = new HashSet<String>();
				documents.add(key);
				map.put(word, documents);
			}
		}
		return map;
	}
	
	public static Map<String, Double> reduce(Map<String, Set<String>> map, int documents) {
		Map<String, Double> reduced = new HashMap<String, Double>();
		for (String word : map.keySet()) {
			Double IDF = Math.log(((double)documents)/((double)map.get(word).size()))/Math.log(2.0);
			reduced.put(word, IDF);
		}
		return reduced;
	}
	
	public static void saveToFile(Map<String, Double> reduced) {
		try {
			OutputStream out = new FileOutputStream("output.txt");
			for (String word : reduced.keySet()) {
				out.write((word + " -> " + reduced.get(word) + "\n").getBytes());
			}
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		Map<String, List<String>> documentWords = new HashMap<String, List<String>>();
		for (String filename : args) {
			List<String> words = InvertedDocumentFrequency.getWordsFromFile(filename);
			documentWords.put(filename, words);
		}
		Map<String, Set<String>> map = InvertedDocumentFrequency.map(documentWords);
		Map<String, Double> reduced = InvertedDocumentFrequency.reduce(map, args.length);
		InvertedDocumentFrequency.saveToFile(reduced);
	}
}