package ri.togu.aesdatacreator;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import android.text.TextUtils;
import android.util.Base64;

class Aes {

    private static final String UTF8 = "UTF-8";

    static String encryptBase64(String text, String encryptKey, String encryptIv) {
        byte[] crypted = Aes.encrypt(text, encryptKey, encryptIv);
        String resultStr = Base64.encodeToString(crypted, Base64.DEFAULT);
        return resultStr;
    }

    static byte[] encrypt(String text, String encryptKey, String encryptIv) {

        if (TextUtils.isEmpty(text)) {
            return null;
        }
        byte[] byteResult = null;
        try {

            // 文字列をバイト配列へ変換
            byte[] byteText = text.getBytes(UTF8);

            // 暗号化キーと初期化ベクトルをバイト配列へ変換
            byte[] byteKey = encryptKey.getBytes(UTF8);
            byte[] byteIv = encryptIv.getBytes(UTF8);

            // 暗号化キーと初期化ベクトルのオブジェクト生成
            SecretKeySpec key = new SecretKeySpec(byteKey, "AES");
            IvParameterSpec iv = new IvParameterSpec(byteIv);

            // Cipherオブジェクト生成
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

            // Cipherオブジェクトの初期化
            cipher.init(Cipher.ENCRYPT_MODE, key, iv);

            // 暗号化の結果格納
            byteResult = cipher.doFinal(byteText);

            return byteResult;

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }

        // 暗号化文字列を返却
        return byteResult;
    }

    static String decryptBase64(String base64EncodedCryptedText,
            String encryptKey, String encryptIv) {
        byte[] byteArray = Base64.decode(base64EncodedCryptedText,
                Base64.DEFAULT);
        String ret = Aes.decrypt(byteArray, encryptKey, encryptIv);
        return ret;
    }

    static String decrypt(byte[] byteArray, String encryptKey, String encryptIv) {

        if (byteArray == null) {
            return null;
        }

        // 変数初期化
        String strResult = null;

        try {

            // 暗号化キーと初期化ベクトルをバイト配列へ変換
            byte[] byteKey = encryptKey.getBytes(UTF8);
            byte[] byteIv = encryptIv.getBytes(UTF8);

            // 復号化キーと初期化ベクトルのオブジェクト生成
            SecretKeySpec key = new SecretKeySpec(byteKey, "AES");
            IvParameterSpec iv = new IvParameterSpec(byteIv);

            // Cipherオブジェクト生成
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

            // Cipherオブジェクトの初期化
            cipher.init(Cipher.DECRYPT_MODE, key, iv);

            // 復号化の結果格納
            byte[] byteResult = cipher.doFinal(byteArray);

            // バイト配列を文字列へ変換
            strResult = new String(byteResult, UTF8);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }

        // 復号化文字列を返却
        return strResult;
    }
}
