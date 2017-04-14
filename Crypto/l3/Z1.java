import java.io.*;
import java.util.*;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;

public class Z1 {
	public static byte[] createKey() {
		try {
			KeyGenerator kgen = KeyGenerator.getInstance("AES");
			kgen.init(128);
			return kgen.generateKey().getEncoded();
		} catch (final NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return new byte[0];
	}
	
	public static String help() {
		StringBuilder s = new StringBuilder("usage: <mode> <mode2> <key_id> [filenames...]\n") //
			.append("modes:\n") //
			.append("e - file encryption, needs input and output files\n") //
			.append("d - file decryption, needs input and output files\n") //
			.append("o - oracle, needs at least one input file\n") //
			.append("c - challenge, needs two input and one output files\n") //
			.append("mode2:") //
			.append("OFB\n") //
			.append("CTR\n") //
			.append("CBC");
		return s.toString();
	}
	
	private static Map<String, String> readConfig() {
		String content = RW.read("data/config.ini");
		Map<String, String> config = new HashMap<String, String>();
		for (String s : content.split("[\r\n]+")) {
			String[] line = s.split("=");
			if (line.length == 2) config.put(line[0], line[1]);
		}
		return config;
	}
	
	private static Map<String, String> readKeys(String keyfile, String password) {
		String content = KeyDecryptor.decryptKeyfile(RW.readB(keyfile), password);
		Map<String, String> keys = new HashMap<String, String>();
		for (String s : content.split("[\r\n]+")) {
			String[] line = s.split(" ");
			if (line.length == 2) keys.put(line[0], line[1]);
		}
		return keys;
	}
	
	private static void saveKeys(Map<String, String> keys, String keyfile, String password) {
		StringBuilder s = new StringBuilder();
		for (String id : keys.keySet()) {
			s.append(id).append(" ").append(keys.get(id)).append("\n");
		}
		RW.writeB(keyfile, KeyDecryptor.encryptKeyfile(s.toString(), password));
	}
	
	private static byte[] getKey(String keyId) {
		Map<String, String> config = Z1.readConfig();
		String pass = config.get("pass");
		String keyfile = config.get("keystore");
		
		Map<String, String> keys = Z1.readKeys(keyfile, pass);
		String key = keys.get(keyId);
		byte[] keyBytes;
		if (key == null) {
			keyBytes = Z1.createKey();
			keys.put(keyId, Hexer.toHex(keyBytes));
			Z1.saveKeys(keys, keyfile, pass);
		} else {
			keyBytes = Hexer.fromHex(key);
		}
		return keyBytes;
	}
	
	public static void main(String[] args) {
		if (args.length == 1 && args[0].equals("test")) {
			Tester.test();
		} else if (args.length == 1 && args[0].equals("dist")) {
			int successes = 0;
			int tries = 1000;
			for (int i=0;i<tries;i++) if (Distinguisher.distinguish()) successes++;
			System.out.println(successes+"/"+tries+" were right");
		} else if (args.length>2) {
			EncryptionModeEnum mode = EncryptionModeEnum.valueOf(args[1]);
			if (mode == null) {
				System.out.println("WRONG MODE:" + args[1]);
				System.out.println(Z1.help());
				return;
			}
			byte[] key = Z1.getKey(args[2]);
			if (key.length != 16) {
				System.out.println("WRONG KEY, ID:" + args[2]);
				return;
			}
			if (args[0].equals("e") && args.length == 5) {
				Encryptor.encrypt(key, args[3], args[4], mode);
			} else if (args[0].equals("d") && args.length == 5) {
				Decryptor.decrypt(key, args[3], args[4], mode);
			} else if (args[0].equals("o") && args.length>3) {
				String[] files = new String[args.length-3];
				for (int i=3;i<args.length;i++) files[i-3] = args[i];
				EncryptionOracle.encrypt(key, files, mode);
			} else if (args[0].equals("c") && args.length == 6) {
				Challenger.challenge(key, args[3], args[4], args[5], mode);
			} else System.out.println(Z1.help());
		} else System.out.println(Z1.help());
	}
}