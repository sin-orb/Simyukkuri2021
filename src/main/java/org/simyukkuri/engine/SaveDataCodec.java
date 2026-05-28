package org.simyukkuri.engine;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * セーブデータの JSON / GZIP / 暗号化を担当する.
 */
public final class SaveDataCodec {
	private static final byte[] SAVE_MAGIC = new byte[] { 'S', 'Y', 'S', 'V' };
	private static final byte SAVE_VERSION = 1;
	private static final int SALT_LEN = 16;
	private static final int NONCE_LEN = 16;
	private static final int HMAC_LEN = 32;
	private static final byte[] PEPPER = new byte[] {
			0x41, 0x33, 0x6d, 0x52, 0x27, 0x7a, 0x11, 0x5c,
			0x09, 0x2f, 0x6a, 0x1b, 0x5e, 0x3c, 0x7d, 0x22,
			0x19, 0x4a, 0x6e, 0x0b, 0x2d, 0x71, 0x12, 0x53,
			0x3a, 0x68, 0x25, 0x7c, 0x04, 0x1f, 0x55, 0x2a
	};

	private SaveDataCodec() {
	}

	/**
	 * World を保存ファイルへ書き出す.
	 *
	 * @param world 保存対象
	 * @param file  保存先
	 * @throws IOException 入出力失敗時
	 */
	public static void save(World world, File file) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(world);
		byte[] gzBytes = compressStringToGzipBytes(json);
		byte[] encrypted = encryptSaveBytes(gzBytes);
		Files.write(file.toPath(), encrypted);
	}

	/**
	 * 保存ファイルから World を読み出す.
	 *
	 * @param file 読み込み元
	 * @return 復元した World
	 * @throws IOException 入出力失敗時
	 */
	public static World load(File file) throws IOException {
		byte[] payload = Files.readAllBytes(file.toPath());
		byte[] gzBytes = decryptSaveBytes(payload);
		String json = decompressGzipToString(gzBytes);
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return mapper.readValue(json, World.class);
	}

	/**
	 * GZIP バイト列を UTF-8 文字列へ戻す.
	 *
	 * @param gzipBytes GZIP 圧縮済みデータ
	 * @return 復元した文字列
	 * @throws IOException 展開失敗時
	 */
	public static String decompressGzipToString(byte[] gzipBytes) throws IOException {
		try (ByteArrayInputStream bais = new ByteArrayInputStream(gzipBytes);
				GZIPInputStream gis = new GZIPInputStream(bais);
				ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			byte[] buffer = new byte[1024];
			int bytesRead;
			while ((bytesRead = gis.read(buffer)) != -1) {
				baos.write(buffer, 0, bytesRead);
			}
			return baos.toString("UTF-8");
		}
	}

	private static byte[] compressStringToGzipBytes(String json) throws IOException {
		byte[] jsonBytes = json.getBytes(StandardCharsets.UTF_8);
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
				GZIPOutputStream gos = new GZIPOutputStream(baos)) {
			gos.write(jsonBytes);
			gos.finish();
			return baos.toByteArray();
		}
	}

	private static byte[] encryptSaveBytes(byte[] plain) throws IOException {
		byte[] salt = new byte[SALT_LEN];
		byte[] nonce = new byte[NONCE_LEN];
		SecureRandom rnd = new SecureRandom();
		rnd.nextBytes(salt);
		rnd.nextBytes(nonce);

		try {
			byte[] encKey = hkdf("enc", salt, 16);
			byte[] macKey = hkdf("mac", salt, HMAC_LEN);
			byte[] cipherText = aesCtr(plain, encKey, nonce);

			ByteArrayOutputStream header = new ByteArrayOutputStream();
			try (DataOutputStream dos = new DataOutputStream(header)) {
				dos.write(SAVE_MAGIC);
				dos.writeByte(SAVE_VERSION);
				dos.writeByte(0);
				dos.write(salt);
				dos.write(nonce);
				dos.writeInt(cipherText.length);
			}

			byte[] headerBytes = header.toByteArray();
			byte[] hmac = hmacSha256(macKey, headerBytes, cipherText);

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			out.write(headerBytes);
			out.write(cipherText);
			out.write(hmac);
			return out.toByteArray();
		} catch (GeneralSecurityException e) {
			throw new IOException("save encrypt failed", e);
		}
	}

	private static byte[] decryptSaveBytes(byte[] payload) throws IOException {
		try (DataInputStream dis = new DataInputStream(new ByteArrayInputStream(payload))) {
			byte[] magic = new byte[SAVE_MAGIC.length];
			dis.readFully(magic);
			if (!Arrays.equals(magic, SAVE_MAGIC)) {
				throw new IOException("save magic mismatch");
			}
			byte ver = dis.readByte();
			if (ver != SAVE_VERSION) {
				throw new IOException("save version mismatch");
			}
			dis.readByte();
			byte[] salt = new byte[SALT_LEN];
			byte[] nonce = new byte[NONCE_LEN];
			dis.readFully(salt);
			dis.readFully(nonce);
			int len = dis.readInt();
			if (len < 0 || len > payload.length) {
				throw new IOException("save length invalid");
			}

			byte[] cipherText = new byte[len];
			dis.readFully(cipherText);
			byte[] storedHmac = new byte[HMAC_LEN];
			dis.readFully(storedHmac);

			byte[] encKey = hkdf("enc", salt, 16);
			byte[] macKey = hkdf("mac", salt, HMAC_LEN);

			byte[] headerBytes = Arrays.copyOfRange(payload, 0, SAVE_MAGIC.length + 1 + 1 + SALT_LEN + NONCE_LEN + 4);
			byte[] calcHmac = hmacSha256(macKey, headerBytes, cipherText);
			if (!Arrays.equals(storedHmac, calcHmac)) {
				throw new IOException("save tampered");
			}

			return aesCtr(cipherText, encKey, nonce);
		} catch (GeneralSecurityException e) {
			throw new IOException("save decrypt failed", e);
		}
	}

	private static byte[] aesCtr(byte[] input, byte[] key, byte[] nonce) throws GeneralSecurityException {
		Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
		SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
		IvParameterSpec iv = new IvParameterSpec(nonce);
		cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv);
		return cipher.doFinal(input);
	}

	private static byte[] hkdf(String info, byte[] salt, int len) throws GeneralSecurityException {
		Mac mac = Mac.getInstance("HmacSHA256");
		mac.init(new SecretKeySpec(salt, "HmacSHA256"));
		byte[] prk = mac.doFinal(PEPPER);

		mac.init(new SecretKeySpec(prk, "HmacSHA256"));
		byte[] infoBytes = info.getBytes(StandardCharsets.UTF_8);
		byte[] t = new byte[0];
		ByteArrayOutputStream okm = new ByteArrayOutputStream();
		int counter = 1;
		while (okm.size() < len) {
			mac.update(t);
			mac.update(infoBytes);
			mac.update((byte) counter);
			t = mac.doFinal();
			okm.write(t, 0, t.length);
			mac.reset();
			mac.init(new SecretKeySpec(prk, "HmacSHA256"));
			counter++;
		}
		byte[] out = okm.toByteArray();
		return Arrays.copyOf(out, len);
	}

	private static byte[] hmacSha256(byte[] key, byte[]... chunks) throws GeneralSecurityException {
		Mac mac = Mac.getInstance("HmacSHA256");
		mac.init(new SecretKeySpec(key, "HmacSHA256"));
		for (byte[] chunk : chunks) {
			mac.update(chunk);
		}
		return mac.doFinal();
	}
}
