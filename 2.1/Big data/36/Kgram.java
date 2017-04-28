import java.util.*;

public class Kgram {
	private String[] words;
	
	public Kgram(String[] words) {
		this.words = words;
	}
	
	public boolean equals(Object o) {
		if (!(o instanceof Kgram)) {
			return false;
		}
		
		Kgram kgram = (Kgram)o;
		if (this.words.length != kgram.words.length) {
			return false;
		}
		
		for (int i=0;i<this.words.length;i++) {
			if ((words[i] == null && kgram.words[i] != null) || //
				(words[i] != null && kgram.words[i] == null) || //
				(!words[i].equals(kgram.words[i]))) return false;
		}
		return true;
	}
	
	public int hashCode() {
		int hash = 0;
		for (int i=0;i<words.length;i++) hash = hash*2+words[i].hashCode();
		return hash;
	}
	
	public String toString() {
		StringBuilder b = new StringBuilder("[");
		for (int i=0;i<this.words.length;i++) b.append(this.words[i]).append(i<this.words.length-1 ? "," : "]");
		return b.toString();
	}
	
	public static Set<Kgram> toKgrams(String[] words, int k) {
		Set<Kgram> set = new HashSet<Kgram>();
		if (words.length < k) return set;
		String[] kwords = new String[k];
		for (int i=0;i<k;i++) kwords[i] = words[i];
		set.add(new Kgram(kwords));
		
		for (int i=k;i<words.length;i++) {
			String[] newKwords = new String[k];
			for (int j=0;j<k-1;j++) newKwords[j] = kwords[j+1];
			newKwords[k-1] = words[i];
			kwords = newKwords;
			set.add(new Kgram(kwords));
		}
		return set;
	}
}