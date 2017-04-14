import java.io.*;
import java.util.*;

public class FileReader {
	public static int[][] readFile(final String filename) {
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
		
		int i = 0;
		int[] values = new int[3];
		String value = "";
		int line = 0;
		for (final char c : file.toCharArray()) {
			if (c == ',') {
				values[i] = Integer.valueOf(value);
				value = "";
				i++;
			} else if (c != '\n' && c != '\r' && c != ' ') {
				value += c;
			} else {
				line++;
			}
			if (i == 2 && (c == '\n' || c == '\r')) {
				try {
					values[i] = Integer.valueOf(value);
				} catch (NumberFormatException e) {
					System.out.println(values[0] + " "+values[1]+" "+values[2]);
					System.out.println("wrong : \""+value+"\" -"+i+"-"+line);
				}
				list.add(values);
				values = new int[3];
				value = "";
				i = 0;
			}
		}
		int[][] result = new int[list.size()][3];
		result = list.toArray(result);
		return result;
	}
}
