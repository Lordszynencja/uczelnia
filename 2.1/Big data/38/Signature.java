public class Signature {
	byte[] signature;
	
	public Signature(int size) {
		if (size%8 != 0) {
			System.err.println("[ERROR] Size is not multiple of 8!!");
		}
		this.signature = new byte[size/8];
	}
	
	public void setBit(int bit, int val) {
		this.signature[bit/8] |= val<<(bit%8);
	}
	
	public int hammingDistance(Signature s) {
		int different = 0;
		if (s.signature.length != this.signature.length) {
			System.err.println("[ERROR] Sizes are different!!");
		} else {
			for (int i=0;i<this.signature.length;i++) {
				for (int j=0;j<8;j++) {
					if ((this.signature[i]&(1<<j)) != (s.signature[i]&(1<<j))) different++;
				}
			}
		}
		return different;
	}
}