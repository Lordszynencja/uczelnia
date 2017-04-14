import java.io.*;
import java.util.*;

public class FilesLoader {
	public static String regex1 = "\\s+";
	public static String notWordsRegex = "[^A-Za-z\u0105\u0104\u0119\u0118\u015b\u015a\u0107\u0106\u017a\u0179\u017c\u017b\u00f3\u00d3\u0142\u0141\u0144\u0143']+";
	
	public static String[] readFile(final String filename) {
		final File f = new File(filename);
		final List<int[]> list = new ArrayList<int[]>();
		String file = "";
		try {
			final InputStream input = new FileInputStream(f);
			final byte[] fileBytes = new byte[(int) f.length()];
			input.read(fileBytes, 0, (int) f.length());
			input.close();
			file = new String(fileBytes, "UTF-8");
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		}
		
		String[] words = file.split(FilesLoader.notWordsRegex);
		return words;
	}
}
