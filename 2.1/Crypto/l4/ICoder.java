public interface ICoder {
	public void createKey();
	
	public byte[] getPublicKey();
	public void setPublicKey(byte[] k);
	
	public byte[] getPrivateKey();
	public void setPrivateKey(byte[] k);
	
	public byte[] encrypt(byte[] m);
	public byte[] decrypt(byte[] c);
}