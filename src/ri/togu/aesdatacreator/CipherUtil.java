package ri.togu.aesdatacreator;

import static ri.togu.aesdatacreator.Constants.AES_KEY;
import static ri.togu.aesdatacreator.Constants.AES_IV;

class CipherUtil {

    // 指定KeyとIVでAES暗号化
    static String encryptByAes(String text) {

        return Aes.encryptBase64(text, AES_KEY, AES_IV);
    }

    // 指定KeyとIVでAES復号
    static String decryptByAes(String base64EncodedCryptedText) {

        return Aes.decryptBase64(base64EncodedCryptedText, AES_KEY, AES_IV);
    }
}
