import java.io.*;
import java.util.*;

public class Splitter {
	public static String wordRegex = "\\s+";
	public static String PLWordRegex = "[^A-Za-z\u0105\u0104\u0119\u0118\u015b\u015a\u0107\u0106\u017a\u0179\u017c\u017b\u00f3\u00d3\u0142\u0141\u0144\u0143']+";
	
	public static String[] splitToWords(final String s, final String regex) {
		return s.split(regex);
	}
	
	public static String[] splitToWords(final String s) {
		return Splitter.splitToWords(s, Splitter.PLWordRegex);
	}
	
	public static String[] splitFile(final String filename, final String regex) {
		return Splitter.splitToWords(RW.read(filename));
	}
	
	public static String[] splitFile(final String filename) {
		return Splitter.splitFile(filename, Splitter.PLWordRegex);
	}
}
