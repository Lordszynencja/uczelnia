import java.io.*;
import java.util.*;

public class WordCount implements Comparable<WordCount> {
	public int count;
	public String word;
	
	public WordCount(String word) {
		this.count = 0;
		this.word = word;
	}
	
	@Override
	public int compareTo(WordCount s) {
		return (this.count>s.count ? 1 : (s.count>this.count ? -1 : 0));
	}
	
	@Override
	public String toString() {
		return this.word + " - " + this.count;
	}
}