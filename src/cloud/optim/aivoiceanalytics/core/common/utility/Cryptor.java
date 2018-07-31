/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：Cryptor.java
 * 概要：
 *
 * 修正履歴：
 *   編集者        日付                  概要
 *
 */

package cloud.optim.aivoiceanalytics.core.common.utility;

import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.security.crypto.keygen.KeyGenerators;

/**
 * AES暗号ユーティリティ.
 * <p>エンジンは Spring-Security の実装を利用する.</p>
 * <p>利用するためにはJCEのポリシーを変更する必要があります.詳細は別途.</p>
 */
public final class Cryptor {

  /**
   * デフォルトコンストラクタ.
   */
  private Cryptor() {
  }

  /**
   * 暗号化する.
   *
   * @param key キー
   * @param text 対象文字列
   * @return 暗号化された文字列
   */
  public static String encrypt(String key, String text) {
    //System.out.println("encrypt:key=" + key);
    String salt = generateSalt();
    //System.out.println("encrypt:salt=" + salt);
    TextEncryptor textEncryptor = Encryptors.text(key, salt);
    //System.out.println("encrypt:end init");
    String encText = textEncryptor.encrypt(text);
    //System.out.println("encrypt:encText=" + encText);
    return salt + encText;
  }

  /**
   * 復号化する.
   *
   * @param key キー
   * @param text 暗号化された文字列
   * @return 復号化された文字列
   */
  public static String decrypt(String key, String text) {
    //System.out.println("decrypt:key=" + key);
    String salt = splitSalt(text);
    //System.out.println("decrypt:salt=" + salt);
    String body = splitBody(text);
    //System.out.println("decrypt:body=" + body);
    TextEncryptor textEncryptor = Encryptors.text(key, salt);
    return textEncryptor.decrypt(body);
  }


  /**
   * ソルトを生成する.
   *
   * @return ソルト(8byte -> HEX 16byte)
   */
  private static String generateSalt() {
    return KeyGenerators.string().generateKey();
  }

  /**
   * ソルトを切り出す.
   *
   * @return ソルト(8byte -> HEX 16byte)
   */
  private static String splitSalt(final String text) {
    return text.substring(0, 16); // 8byte => HEX 16byte
  }

  /**
   * 暗号処理部分を切り出す.
   *
   * @return 暗号処理部分
   */
  private static String splitBody(final String text) {
    return text.substring(16); // 8byte => HEX 16byte
  }

}
