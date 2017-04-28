import java.io.*;
import java.util.*;

public class Counter {
	List<String> stopwords = new ArrayList<String>();
	public static String notLettersRegex = "\u005b\u005e\u0041\u002d\u005a\u0061\u002d\u007a\u0105\u0104\u0119\u0118\u015b\u015a\u0107\u0106\u017a\u0179\u017c\u017b\u00f3\u00d3\u0142\u0141\u0144\u0143\u0027\u005d\u002b";
	public Counter() {
		stopwords = Arrays.asList(this.readFile("stopwords.txt").split(","));
	}
	
	public List<WordCount> countWords(String filename) {
		Map<String, WordCount> wordsCount = new HashMap<String, WordCount>();
		String fileValue = this.readFile(filename);
		if (fileValue == null) return new ArrayList<WordCount>();
		String[] words = fileValue.split(Counter.notLettersRegex);
		for (String word : words) {
			String lowercaseWord = word.toLowerCase();
			WordCount wordCount = wordsCount.get(lowercaseWord);
			if (wordCount == null) wordCount = new WordCount(lowercaseWord);
			wordCount.count++;
			wordsCount.put(lowercaseWord, wordCount);
		}
		Set<Map.Entry<String, WordCount>> wordSet = wordsCount.entrySet();
		List<WordCount> wordsList = new ArrayList<WordCount>(wordSet.size());
		for (Map.Entry<String, WordCount> entry : wordSet) {
			WordCount wc = entry.getValue();
			if (!stopwords.contains(wc.word)) {
				wordsList.add(entry.getValue());
			}
		}
		Collections.sort(wordsList);
		Collections.reverse(wordsList);
		return wordsList;
	}
	
	public Map<String, Map<String, Double>> calculateTfIdf(String[] filenames) {
		List<String> words = new ArrayList<String>();
		Map<String, Map<String, Integer>> documentWordCounts = new HashMap<String, Map<String, Integer>>();
		Map<String, Integer> documentWordNumbers = new HashMap<String, Integer>();
		
		Map<String, Map<String, Double>> tf = new HashMap<String, Map<String, Double>>();
		Map<String, Double> idf = new HashMap<String, Double>();
		Map<String, Map<String, Double>> tfIdf = new HashMap<String, Map<String, Double>>();
		
		for (String filename : filenames) {
			List<WordCount> wordCounts = this.countWords(filename);
			Map<String, Integer> wordCountsMap = new HashMap<String, Integer>();
			Integer documentWordCount = 0;
			for (WordCount wc : wordCounts) {
				if (!words.contains(wc.word)) {
					words.add(wc.word);
				}
				documentWordCount += wc.count;
				wordCountsMap.put(wc.word, wc.count);
			}
			documentWordCounts.put(filename, wordCountsMap);
			documentWordNumbers.put(filename, documentWordCount);
		}
		
		for (String filename : filenames) {
			Map<String, Double> tfj = new HashMap<String, Double>();
			Map<String, Integer> documentWordCount = documentWordCounts.get(filename);
			for (String word : words) {
				Integer wordCountInDocument = documentWordCount.get(word);
				Double value = 0.0;
				if (wordCountInDocument != null) {
					value = Double.valueOf(wordCountInDocument)/Double.valueOf(documentWordNumbers.get(filename));
				}
				tfj.put(word, value);
			}
			tf.put(filename, tfj);
		}
		for (String word : words) {
			int count = 0;
			for (String filename : filenames) {
				if (documentWordCounts.get(filename).get(word) != null) {
					count++;
				}
			}
			Double value = Math.log(filenames.length*1.0/count)/Math.log(2);
			idf.put(word, value);
		}
		for (String filename : filenames) {
			Map<String, Double> tfIdfj = new HashMap<String, Double>();
			Map<String, Double> tfj = tf.get(filename);
			for (String word : words) {
				tfIdfj.put(word, tfj.get(word) * idf.get(word));
			}
			tfIdf.put(filename, tfIdfj);
		}
		return tfIdf;
	}
	
	private String readFile(String filename) {
		String value = null;
		try {
			File file = new File(filename);
			long fileLength = file.length();
			FileInputStream fileInputStream = new FileInputStream(file);
			byte[] bytes = new byte[(int)fileLength];
			fileInputStream.read(bytes);
			value = new String(bytes, "UTF-8");
			fileInputStream.close();
		} catch (IOException e) {
			System.out.println("[ERROR]:reading file error for file "+filename);
		}
		return value;
	}
	
	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println("No input files");
		}
		Counter counter = new Counter();
		if (args[0].equals("c")) {
			String filename = args[1];
			String outputFilename = "output"+filename;
			try {
				FileOutputStream output = new FileOutputStream(outputFilename);
				System.out.println("Counting words for file " + filename);
				List<WordCount> wordsCount = counter.countWords(filename);
				for (WordCount s : wordsCount) {
					String line = s.count+" "+s.word+"\n";
					output.write(line.getBytes("UTF-8"));
				}
			} catch (IOException e) {
				System.out.println("[ERROR]:file "+outputFilename+" couldn't be created");
			}
		} else if (args[0].equals("tf")) {
			int files = Integer.valueOf(args[2]);
			String[] filenames = new String[files];
			for (int i=0;i<files;i++) {
				filenames[i] = args[1] + i + args[3];
			}
			
			Map<String, Map<String, Double>> tfIdf = counter.calculateTfIdf(filenames);
			try {
				FileOutputStream output = new FileOutputStream(args[4]);
				for (String filename : filenames) {
					String msg = "####################\n" + filename + "\n";
					output.write(msg.getBytes("UTF-8"));
					Map<String, Double> tfIdfj = tfIdf.get(filename);
					for (String s : tfIdfj.keySet()) {
						msg = s + "-" + tfIdfj.get(s) + "\n";
						output.write(msg.getBytes("UTF-8"));
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}