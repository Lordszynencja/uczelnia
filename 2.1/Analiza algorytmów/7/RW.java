import java.io.*;

public class RW {
	public static String read(String filename) {
		return new String(RW.readB(filename));
	}
	
	public static byte[] readB(String filename) {
		try {
			File f = new File(filename);
			FileInputStream input = new FileInputStream(f);
			byte[] bytes = new byte[(int)f.length()];
			if (bytes != null) {
				input.read(bytes);
				return bytes;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new byte[0];
	}
	
	public static void write(String filename, String content) {
		RW.writeB(filename, content.getBytes());
	}
	
	public static void writeB(String filename, byte[] content) {
		try {
			File f = new File(filename);
			FileOutputStream output = new FileOutputStream(f);
			output.write(content);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}