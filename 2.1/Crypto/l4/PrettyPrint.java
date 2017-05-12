import java.util.*;

public class PrettyPrint {
	public static void byteArraysArray(byte[][] bytesArrays, int max) {
		int i = 0;
		for (byte[] bytes : bytesArrays) {
			System.out.print(i + ". ");
			PrettyPrint.byteArray(bytes, max);
			i++;
		}
	}
	
	public static void byteArraysList(List<byte[]> list, int max) {
		int i = 0;
		for (byte[] bytes : list) {
			System.out.print(i + ". ");
			PrettyPrint.byteArray(bytes, max);
			i++;
		}
	}
	
	public static void byteArray(byte[] bytes, int max) {
		System.out.print("size:"+bytes.length + " contents: [");
		for (int i=0;i<max && i<bytes.length;i++) {
			System.out.print(bytes[i]);
			if (i == max-1 && i<bytes.length-1) System.out.print("...");
			else if (i<max-1 && i<bytes.length-1) System.out.print(",");
		}
		System.out.println("]");
	}
}