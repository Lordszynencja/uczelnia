import java.util.*;

public class Transformer {
	public static void testLists() {
		List<byte[]> b0 = new ArrayList<byte[]>();
		b0.add(new byte[]{(byte) 0, (byte) 1});
		b0.add(new byte[]{(byte) 1, (byte) 2});
		b0.add(new byte[255*256+255]);
		b0.add(new byte[255*256+256]);
		
		PrettyPrint.byteArraysList(b0, 5);
		
		byte[] b1 = Transformer.join(b0);
		
		PrettyPrint.byteArray(b1, 15);
		
		List<byte[]> b2 = Transformer.split(b1);
		
		PrettyPrint.byteArraysList(b2, 5);
	}
	
	public static void testBlocks() {
		int blockSize = 1;
		int logSize = (blockSize<8 ? 15 : blockSize*2);
		
		byte[] b0 = new byte[] {(byte) 1, (byte) 35, (byte) 99, (byte) 3, (byte) 123, (byte) 1, (byte) 35, (byte) 99, (byte) 3, (byte) 123};
		PrettyPrint.byteArray(b0, logSize);
		
		byte[][] b1 = Transformer.splitToBlocks(b0, blockSize);
		PrettyPrint.byteArraysArray(b1, logSize);
		
		byte[] b2 = Transformer.simpleMergeBlocks(b1, blockSize);
		PrettyPrint.byteArray(b2, logSize);
		
		byte[][] b3 = Transformer.simpleSplitToBlocks(b2, blockSize);
		PrettyPrint.byteArraysArray(b3, logSize);
		
		byte[] b4 = Transformer.mergeBlocks(b3, blockSize);
		PrettyPrint.byteArray(b4, logSize);
	}
	
	public static List<byte[]> split(byte[] b) {
		List<byte[]> b1 = new ArrayList<byte[]>(b[0]<0 ? (int)b[0]+256 : (int)b[0]);
		int counter = 1;
		while (counter<b.length) {
			int size = (b[counter] < 0 ? (int)b[counter]+256 : b[counter])*256 + (b[counter+1] < 0 ? (int)b[counter+1]+256 : b[counter+1]);
			b1.add(Arrays.copyOfRange(b, counter+2, counter+2+size));
			counter += size+2;
		}
		return b1;
	}
	
	public static byte[] join(List<byte[]> bytesList) {
		int size = 1;
		for (byte[] bytes : bytesList) {
			size += 2+(bytes.length>65535 ? 65535 : bytes.length);
		}
		byte[] b = new byte[size];
		b[0] = (byte)bytesList.size();
		
		int counter = 1;
		for (byte[] bytes : bytesList) {
			int copySize = (bytes.length>65535 ? 65535 : bytes.length);
			b[counter] = (byte)(copySize/256);
			b[counter+1] = (byte)(copySize%256);
			for (int j=0;j<copySize;j++) b[counter+2+j] = bytes[j];
			counter += copySize+2;
		}
		return b;
	}
	
	public static byte[][] splitToArray(byte[] b) {
		byte[][] b1 = new byte[b[0]<0 ? (int)b[0]+256 : (int)b[0]][];
		int counter = 1;
		int i = 0;
		while (counter<b.length) {
			int size = (b[counter] < 0 ? (int)b[counter]+256 : b[counter])*256 + (b[counter+1] < 0 ? (int)b[counter+1]+256 : b[counter+1]);
			b1[i] = Arrays.copyOfRange(b, counter+2, counter+2+size);
			counter += size+2;
			i++;
		}
		return b1;
	}
	
	public static byte[] joinArray(byte[][] bytesArray) {
		int size = 1;
		for (byte[] bytes : bytesArray) {
			size += 2+(bytes.length>65535 ? 65535 : bytes.length);
		}
		byte[] b = new byte[size];
		b[0] = (byte)bytesArray.length;
		
		int counter = 1;
		for (byte[] bytes : bytesArray) {
			int copySize = (bytes.length>65535 ? 65535 : bytes.length);
			b[counter] = (byte)(copySize/256);
			b[counter+1] = (byte)(copySize%256);
			for (int j=0;j<copySize;j++) b[counter+2+j] = bytes[j];
			counter += copySize+2;
		}
		return b;
	}
	
	public static byte[][] splitToBlocks(byte[] b, int blockSize) {
		int length = b.length + 1;
		int blocksNo = (length%blockSize == 0 ? length/blockSize : length/blockSize+1);
		byte[][] blocks = new byte[blocksNo][];
		for (int i=0;i<blocksNo;i++) blocks[i] = Arrays.copyOfRange(b, blockSize*i, blockSize*i+blockSize);
		blocks[blocksNo-1][blockSize-1] = (byte)(blockSize>1 ? blockSize-(length%blockSize)+1 : 1);
		return blocks;
	}
	
	public static byte[] mergeBlocks(byte[][] blocks, int blockSize) {
		int length = blockSize*blocks.length-(int)blocks[blocks.length-1][blockSize-1];
		byte[] b = new byte[length];
		for (int i=0;i<length;i++) {
			b[i] = blocks[i/blockSize][i%blockSize];
		}
		return b;
	}
	
	public static byte[][] simpleSplitToBlocks(byte[] b, int blockSize) {
		byte[][] blocks = new byte[b.length/blockSize][];
		for (int i=0;i<blocks.length;i++) blocks[i] = Arrays.copyOfRange(b, blockSize*i, blockSize*i+blockSize);
		return blocks;
	}
	
	public static byte[] simpleMergeBlocks(byte[][] blocks, int blockSize) {
		byte[] b = new byte[blockSize*blocks.length];
		for (int i=0;i<blockSize*blocks.length;i++) {
			b[i] = blocks[i/blockSize][i%blockSize];
		}
		return b;
	}
}