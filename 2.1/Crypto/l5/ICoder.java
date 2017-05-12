public interface ICoder {
	public void createKey();
	public void createKey(int keysize);
	public void createKey(int k, int keysize);
	
	public byte[] getPublicKey();
	public void setPublicKey(byte[] k);
	
	public byte[] getPrivateKey();
	public void setPrivateKey(byte[] k);
	
	public byte[] encrypt(byte[] m, EncryptionModeEnum mode);
	public byte[] decrypt(byte[] c, EncryptionModeEnum mode);
}