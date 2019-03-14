import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Kripto {

	public static void main(String[] args) throws Throwable, InvalidAlgorithmParameterException {

		String fileName = "";
		String cipherType = "";
		String thread = "";
		String input = "";
		String inputFile = "";
		String output = "";
		String outputFile = "";
		String algorithim = "";
		String mode = "";
		String keyFile = "";

		Scanner scanner1 = new Scanner(System.in);

		String command = scanner1.nextLine();
		String[] word = command.split(" ");

		String condition = word[2];

		if (condition.equals("-p")) {
			fileName = word[0];
			cipherType = word[1];
			thread = word[2];
			input = word[3];
			inputFile = word[4];
			output = word[5];
			outputFile = word[6];
			algorithim = word[7];
			mode = word[8];
			keyFile = word[9];
		} else if (condition.equals("-i")) {
			fileName = word[0];
			cipherType = word[1];
			input = word[2];
			inputFile = word[3];
			output = word[4];
			outputFile = word[5];
			algorithim = word[6];
			mode = word[7];
			keyFile = word[8];
		}

		ArrayList<String> inputPath = new ArrayList<>();
		File f = new File(inputFile);
		do {
			inputPath.add(f.getPath());
			f = f.getParentFile();
		} while (f.getParentFile() != null);

		// System.out.println(inputPath.get(1));

		if ((cipherType.equals("=e") || cipherType.equals("-d"))
				|| (algorithim.equals("AES") || algorithim.equals("DES"))
				|| (mode.equals("CBC") || mode.equals("OFB") || mode.equals("CTR"))) {
		} else {
			System.out.println("Wrong Parameter Entered by User!");
		}

		String newKeyFile = inputPath.get(1) + "/" + keyFile;
		Scanner scanner2 = new Scanner(new File(newKeyFile));

		String line = scanner2.nextLine();
		String[] keys = line.split(" - ");
		String iv = keys[0];
		String key = keys[1];

		if (cipherType.equals("-e")) {
			FileInputStream fis = new FileInputStream(inputFile);
			FileOutputStream fos = new FileOutputStream(inputPath.get(1) + "/" + outputFile);
			encrypt(key, iv, mode, algorithim, fis, fos);

		} else if (cipherType.equals("-d")) {
			FileInputStream fis2 = new FileInputStream(inputFile);
			FileOutputStream fos2 = new FileOutputStream(inputPath.get(1) + "/" + outputFile);
			decrypt(key, iv, mode, algorithim, fis2, fos2);

		}

	}

	public static void encrypt(String key, String myiv, String mode, String alg, InputStream is, OutputStream os)
			throws Throwable, InvalidAlgorithmParameterException {
		encryptOrDecrypt(key, myiv, Cipher.ENCRYPT_MODE, mode, alg, is, os);
	}

	public static void decrypt(String key, String myiv, String mode, String alg, InputStream is, OutputStream os)
			throws Throwable, InvalidAlgorithmParameterException {
		encryptOrDecrypt(key, myiv, Cipher.DECRYPT_MODE, mode, alg, is, os);
	}

	public static void encryptOrDecrypt(String key, String myiv, int encordec, String mode, String alg, InputStream is,
			OutputStream os) throws Throwable, InvalidAlgorithmParameterException {
		Long ivSize = Long.parseLong(myiv);
		int length = (int) (Math.log10(ivSize) + 1);

		byte[] keyBytes = key.getBytes();
		byte[] iv = myiv.getBytes();

		if (alg.equals("AES")) {
			if (iv.length != 16 && keyBytes.length != 16) {
				System.out.println("AES algorithm must be 16 bits key and iv!");
			}
		} else if (alg.equals("DES")) {
			if (iv.length != 8 && keyBytes.length != 8) {
				System.out.println("DES algorithm must be 8 bits key and iv!");
			}
		}

		IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		digest.update(key.getBytes("UTF-8"));

		SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, alg);

		Cipher cipher = Cipher.getInstance(alg + "/" + mode + "/PKCS5Padding");

		if (encordec == Cipher.ENCRYPT_MODE) {
			cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
			CipherInputStream cis = new CipherInputStream(is, cipher);
			doCopy(cis, os);
		} else if (encordec == Cipher.DECRYPT_MODE) {
			cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
			CipherOutputStream cos = new CipherOutputStream(os, cipher);
			doCopy(is, cos);
		}

	}

	public static void doCopy(InputStream is, OutputStream os) throws IOException, InvalidAlgorithmParameterException {
		byte[] bytes = new byte[128];
		int numBytes;
		while ((numBytes = is.read(bytes)) != -1) {
			os.write(bytes, 0, numBytes);
		}
		os.flush();
		os.close();
		is.close();

	}

}