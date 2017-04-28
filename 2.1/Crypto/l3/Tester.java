public class Tester {
	private static String shorten(String s) {
		if (s.length()<20) return s;
		return s.substring(0, 17)+"...";
	}
	
	private static void testMode(String[] messages, EncryptionModeEnum mode) {
		System.out.println("Testing "+mode.toString()+" mode:");
		for (String msg : messages) {
			System.out.print("Message: '"+Tester.shorten(msg)+"'("+msg.length()+") -> ");
			byte[] key = Z1.createKey();
			byte[] encrypted = Encryptor.encrypt(key, msg.getBytes(), mode);
			String decrypted = new String(Decryptor.decrypt(key, encrypted, mode));
			if (decrypted.equals(msg)) {
				System.out.println("SUCCESS");
			} else {
				System.out.println("FAILURE");
			}
		}
	}
	
	public static void test() {
		StringBuilder longString = new StringBuilder();
		for (int i=0;i<1024;i++) for (int j=0;j<1024;j++) for (int k=0;k<4;k++) longString.append("0123456789ABCDEF");
		
		String[] messages = new String[] {
			"",
			"1",
			"test",
			"1234567899012345",
			"12345678990123456",
			"123456789901234567",
			"LONG STRIIIIING 123456789901234567refuiwqyhtog8iqu3ywe4opguivb ah[u[aqi[wpioa[0eru9hpty76jkg,5rfytg	 8ut	]IA8GH[0AUH9PYPYp9y -r8eg[ai",
			longString.toString()};

		for (EncryptionModeEnum mode : EncryptionModeEnum.values()) {
			testMode(messages, mode);
		}
	}
}