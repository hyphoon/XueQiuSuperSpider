package org.decaywood.utils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * @author: decaywood
 * @date: 2015/11/23 14:27
 */
public class HttpRequestHelper {

    private Map<String, String> config;
    private boolean post;
    private boolean gzip;

    public HttpRequestHelper(String webSite) {
        this.config = new HashMap<>();
        this.gzipDecode()
                .addToHeader("Referer", webSite)
                .addToHeader("Cookie", FileLoader.loadCookie(webSite))
                .addToHeader("Host", "xueqiu.com")
                .addToHeader("Accept-Encoding", "gzip,deflate,sdch");
    }

    public HttpRequestHelper post() {
        this.post = true;
        return this;
    }

    public HttpRequestHelper gzipDecode() {
        this.gzip = true;
        return this;
    }

    public HttpRequestHelper addToHeader(String key, String val) {
        this.config.put(key, val);
        return this;
    }

    public HttpRequestHelper addToHeader(String key, int val) {
        this.config.put(key, String.valueOf(val));
        return this;
    }

    public String request(URL url) throws IOException {
        return request(url, this.config);
    }


    public String request(URL url, Map<String, String> config) throws IOException {
        HttpURLConnection httpURLConn = null;
        try {
            httpURLConn = (HttpURLConnection) url.openConnection();
            if (post) httpURLConn.setRequestMethod("POST");
            httpURLConn.setDoOutput(true);
            for (Map.Entry<String, String> entry : config.entrySet())
                httpURLConn.setRequestProperty(entry.getKey(), entry.getValue());
            httpURLConn.connect();
            InputStream in = httpURLConn.getInputStream();
            if (gzip) {
                // 判断是否压缩流
                BufferedInputStream bis = new BufferedInputStream(in);
                bis.mark(2);
                byte[] header = new byte[2];
                int length = bis.read(header);
                bis.reset();
                int ss = (header[0] & 0xff) | ((header[1] & 0xff) << 8);
                if (length != -1 && ss == GZIPInputStream.GZIP_MAGIC) {
                    in = new GZIPInputStream(bis);
                } else {
                    in = bis;
                }
            }
            BufferedReader bd = new BufferedReader(new InputStreamReader(in));
            StringBuilder builder = new StringBuilder();
            String text;
            while ((text = bd.readLine()) != null) builder.append(text);
            return builder.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        } finally {
            if (httpURLConn != null) httpURLConn.disconnect();
        }
    }


}
