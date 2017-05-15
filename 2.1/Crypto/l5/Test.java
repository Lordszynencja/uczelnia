public class Test {
	public static void main(String[] args) {
		ICoder rsa = new RSA();
		rsa.createKey();
		System.out.println("key created");
		ICoder rsa2 = new RSA();
		rsa2.setPublicKey(rsa.getPublicKey());
		
		byte[] msg = "random msg".getBytes();
		byte[] c = rsa2.encrypt(msg, EncryptionModeEnum.STANDARD);
		byte[] m = rsa.decrypt(c, EncryptionModeEnum.STANDARD);
		System.out.println(new String(m));
	}
}