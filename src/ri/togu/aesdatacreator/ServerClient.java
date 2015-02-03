package ri.togu.aesdatacreator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;

/**
 * サーバ専用クライアント.
 */
class ServerClient {

    enum AesMenu {
        ENCRYPTED, DECRYPTED;
    }

    /* HTTP Method */
    private static final String HTTP_POST = "POST";
    private static final String HTTPS = "HTTPS";

    // APIクライアント用のインスタンスをシングルトンで取得する.
    static ServerClient getInstance() {
        return InstanceHolder.sInstance;
    }

    // 遅延初期化
    private static class InstanceHolder {
        private static final ServerClient sInstance = new ServerClient();
    }

    /**
     * HTTP POST (Async, AES).
     */
    JSONObject sendPostRequestAsync(Context context, String endpointBaseUrl,
            HashMap<String, Object> params, AesMenu aesMenu) {
        return post(context, endpointBaseUrl, params, isSSL(endpointBaseUrl),
                aesMenu);
    }

    // HTTP POST
    private JSONObject post(Context context, String endpoint,
            HashMap<String, Object> params, boolean ssl, AesMenu aesMenu) {

        // POST用パラメータ生成
        HashMap<String, Object> reqParams = createCommonReqParams(context);
        if (params != null && !params.isEmpty()) {
            reqParams.putAll(params);
        }

        try {
            URL url = new URL(endpoint);
            return new HttpPostTask(url, new JSONObject(reqParams).toString(),
                    ssl, aesMenu).execute().get();
        } catch (Exception e) {
        }
        return null;
    }

    // SSLチェック
    private static boolean isSSL(String endpointBaseUrl) {
        return endpointBaseUrl.startsWith(HTTPS);
    }

    // 共通パラメータ
    private static HashMap<String, Object> createCommonReqParams(Context context) {
        HashMap<String, Object> params = new HashMap<String, Object>();

        // 端末型番
        params.put("model_number", Build.MODEL);

        return params;
    }

    // HTTPS POST（非同期）.
    private class HttpPostTask extends AsyncTask<Void, Void, JSONObject> {
        URL mUrl;
        String mParams;
        boolean mSsl;
        AesMenu mAesMenu;

        HttpPostTask(URL url, String params, boolean ssl, AesMenu aesMenu) {
            mUrl = url;
            mParams = params;
            mSsl = ssl;
            mAesMenu = aesMenu;
        }

        @Override
        protected JSONObject doInBackground(Void... params) {

            HttpURLConnection conn;
            try {

                // SSL
                if (mSsl) {
                    conn = (HttpsURLConnection) mUrl.openConnection();
                } else {
                    conn = (HttpURLConnection) mUrl.openConnection();
                }

                conn.setRequestMethod(HTTP_POST);
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setChunkedStreamingMode(0);
                conn.setConnectTimeout(10000);
                conn.setRequestProperty("Content-Type",
                        "application/json; charset=utf-8");

                String paramsString = mParams;
                StringBuilder res = ServerClient.connection(conn, paramsString);
                return didPost(res, mAesMenu);
            } catch (IOException e) {
                System.err.println(e.getLocalizedMessage());
            }
            return null;
        }
    }

    // 共通コネクション処理.
    private static StringBuilder connection(HttpURLConnection conn,
            String postParams) {
        InputStream is = null;
        StringBuilder resSb = null;
        try {
            conn.connect();

            // POST パラメータ処理
            if (postParams != null) {
                OutputStreamWriter osw = new OutputStreamWriter(
                        conn.getOutputStream());
                osw.write(postParams);
                osw.flush();
                osw.close();

                int networkStatus = conn.getResponseCode();
                if (networkStatus != HttpsURLConnection.HTTP_OK) {
                    return null;
                }
            }

            is = conn.getInputStream();
            resSb = convertStringBuilder(is);
        } catch (Exception e) {
            System.err.println(e.getLocalizedMessage());
        } finally {
            finishInputStream(is);
        }
        return resSb;
    }

    // resをStringBuilderで書き出し
    private static StringBuilder convertStringBuilder(InputStream is)
            throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(is,
                "UTF-8"));

        StringBuilder resSb = new StringBuilder();
        String line = null;
        while ((line = br.readLine()) != null) {
            resSb.append(line);
        }
        return resSb;
    }

    // connectionを閉じるときはInputStreamをcloseすればok
    private static void finishInputStream(InputStream is) {

        // Makes sure that the InputStream is closed after the app is
        // finished using it.
        // cf.http://developer.android.com/training/basics/network-ops/connecting.html#download
        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {
            }
        }
    }

    // post完了後の処理
    private static JSONObject didPost(StringBuilder res, AesMenu aesMenu) {
        // resがnullの場合は諦めてもらう
        if (res == null) {
            return null;
        }

        JSONObject object = null;
        try {
            System.out.println(res);
            System.out.println("↓↓↓結果↓↓↓");
            String s;
            switch (aesMenu) {
            case ENCRYPTED:
                s = CipherUtil.encryptByAes(res.toString());
                if (s != null) {
                    System.out.println(s);
                } else {
                    System.out.println("null");
                }
                break;
            case DECRYPTED:
                s = CipherUtil.decryptByAes(res.toString());
                if (s == null) {
                    object = new JSONObject(res.toString());
                } else {
                    object = new JSONObject(s);
                }
                System.out.println(object);
                break;
            default:
                s = res.toString();
                break;
            }

        } catch (JSONException e) {
        } finally {
        }

        return object;
    }
}
