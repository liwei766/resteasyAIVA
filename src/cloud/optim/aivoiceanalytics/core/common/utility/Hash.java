/*
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 */

package cloud.optim.aivoiceanalytics.core.common.utility;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.ArrayUtils;

/**
 * ハッシュ.
 */
public final class Hash {

  private static final String DEFAULT_ALGORITHM_NAME = "SHA-256";
  private static final int SECURE_RANDOM_SIZE = 16;

  /**
   * メッセージダイジェストを取得する.
   *
   * @param text 対象文字列
   * @return バイト列
   */
  public static byte[] getMessageDigest(final String text) {
    MessageDigest md = null;
    try {
      md = MessageDigest.getInstance(DEFAULT_ALGORITHM_NAME);
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
    //byte[] b = ArrayUtils.addAll(generateSecureRandom(), text.getBytes());
    md.update(text.getBytes());
    return md.digest();
  }

  /**
   * メッセージダイジェストを取得する.
   *
   * @param text 対象文字列
   * @return 文字列
   */
  public static String getMessageDigestString(final String text) {
    return Hex.encodeHexString(getMessageDigest(text)).toLowerCase();
  }

  /**
   * ランダムなバイト列を生成する.
   * <p>個人的に昔AESのIV(Initalization Vector)生成に実装したものを移植した.<br>
   * Salt等にも利用可能.</p>
   *
   * @return バイト列
   */
  public static byte[] generateSecureRandom() {
    SecureRandom random;
    try {
      random = SecureRandom.getInstance("SHA1PRNG");
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
    byte[] iv = new byte[SECURE_RANDOM_SIZE];
    random.nextBytes(iv);
    return iv;
  }

}
