package com.kthcorp.cmts.util;

import java.io.InputStream;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLContext;

import groovy.transform.Synchronized;
import org.apache.commons.io.IOUtils;
import org.apache.http.*;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.MessageConstraints;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.DnsResolver;
import org.apache.http.conn.HttpConnectionFactory;
import org.apache.http.conn.ManagedHttpClientConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.DefaultHttpResponseParser;
import org.apache.http.impl.conn.DefaultHttpResponseParserFactory;
import org.apache.http.impl.conn.ManagedHttpClientConnectionFactory;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.conn.SystemDefaultDnsResolver;
import org.apache.http.impl.io.DefaultHttpRequestWriterFactory;
import org.apache.http.io.HttpMessageParser;
import org.apache.http.io.HttpMessageParserFactory;
import org.apache.http.io.HttpMessageWriterFactory;
import org.apache.http.io.SessionInputBuffer;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicLineParser;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.message.LineParser;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.CharArrayBuffer;
import org.apache.http.util.EntityUtils;

import static org.apache.http.ssl.SSLContexts.createSystemDefault;

public class HttpClientUtil {

    public static String reqGet(String reqUrl, String reqAddPath, Charset targetCharset, Map<String, Object> reqParamMap, String type) throws Exception {
        String resultStr = "";
        Map<String, Object> resultMap = reqGetHtml(reqUrl, reqAddPath, targetCharset, reqParamMap, type);
        if (resultMap != null && resultMap.get("resultStr") != null) {
            resultStr = resultMap.get("resultStr").toString();
        }
        return resultStr;
    }

    public static Map<String, Object> reqGetHtml(String reqUrl, String reqAddPath, Charset targetCharset, Map<String, Object> reqParamMap, String type) throws Exception {

        Map<String, Object> resultMap = new HashMap<String, Object>();

        String resultStr = "";
        String resultUri = "";
        String resultCharset = "";

        //if (!"".equals(reqUrl)) {

        /* set params */
            String params = "";
            if ("bypass".equals(type)) {
                params = getParamsByMap2(reqUrl, reqParamMap);
            } else {
                params = getParamsByMap(reqUrl, reqParamMap);
            }
            String subUrl = (reqAddPath != null ? reqAddPath : "" ) + params;
            subUrl = subUrl.replaceAll("//*", "/");
            if (subUrl.length() > 3 && subUrl.startsWith("/")) {
                subUrl = subUrl.substring(1, subUrl.length());
            }

            String url = reqUrl + subUrl;
            System.out.println("#HttpClient reqGet params:: reqUrl:" + url);

            // Use custom message parser / writer to customize the way HTTP
            // messages are parsed from and written out to the data stream.
            HttpMessageParserFactory<HttpResponse> responseParserFactory = new DefaultHttpResponseParserFactory() {

                @Override
                public HttpMessageParser<HttpResponse> create(
                        SessionInputBuffer buffer, MessageConstraints constraints) {
                    LineParser lineParser = new BasicLineParser() {

                        @Override
                        public Header parseHeader(final CharArrayBuffer buffer) {
                            try {
                                return super.parseHeader(buffer);
                            } catch (ParseException ex) {
                                return new BasicHeader(buffer.toString(), null);
                            }
                        }

                    };
                    return new DefaultHttpResponseParser(
                            buffer, lineParser, DefaultHttpResponseFactory.INSTANCE, constraints) {

                        @Override
                        protected boolean reject(final CharArrayBuffer line, int count) {
                            // try to ignore all garbage preceding a status line infinitely
                            return false;
                        }

                    };
                }

            };
            HttpMessageWriterFactory<HttpRequest> requestWriterFactory = new DefaultHttpRequestWriterFactory();

            // Use a custom connection factory to customize the process of
            // initialization of outgoing HTTP connections. Beside standard connection
            // configuration parameters HTTP connection factory can define message
            // parser / writer routines to be employed by individual connections.
            HttpConnectionFactory<HttpRoute, ManagedHttpClientConnection> connFactory = new ManagedHttpClientConnectionFactory(
                    requestWriterFactory, responseParserFactory);

            // Client HTTP connection objects when fully initialized can be bound to
            // an arbitrary network socket. The process of network socket initialization,
            // its connection to a remote address and binding to a local one is controlled
            // by a connection socket factory.

            // SSL context for secure connections can be created either based on
            // system or application specific properties.
            SSLContext sslcontext = SSLContexts.createSystemDefault();

            // Create a registry of custom connection socket factories for supported
            // protocol schemes.
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.INSTANCE)
                    .register("https", new SSLConnectionSocketFactory(sslcontext))
                    .build();

            // Use custom DNS resolver to override the system DNS resolution.
            DnsResolver dnsResolver = new SystemDefaultDnsResolver() {

                @Override
                public InetAddress[] resolve(final String host) throws UnknownHostException {
                    if (host.equalsIgnoreCase("myhost")) {
                        return new InetAddress[]{InetAddress.getByAddress(new byte[]{127, 0, 0, 1})};
                    } else {
                        return super.resolve(host);
                    }
                }

            };

            // Create a connection manager with custom configuration.
            PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(
                    socketFactoryRegistry, connFactory, dnsResolver);

            // Create socket configuration
            SocketConfig socketConfig = SocketConfig.custom()
                    .setTcpNoDelay(true)
                    .build();
            // Configure the connection manager to use socket configuration either
            // by default or for a specific host.
            connManager.setDefaultSocketConfig(socketConfig);
            connManager.setSocketConfig(new HttpHost("somehost", 80), socketConfig);
            // Validate connections after 1 sec of inactivity
            connManager.setValidateAfterInactivity(1000);

            // Create message constraints
            MessageConstraints messageConstraints = MessageConstraints.custom()
                    .setMaxHeaderCount(200)
                    .setMaxLineLength(2000)
                    .build();
            // Create connection configuration

            if (targetCharset != null) System.out.println("#HttpClient targetCharset:"+targetCharset.toString());

            ConnectionConfig connectionConfig = ConnectionConfig.custom()
                    .setMalformedInputAction(CodingErrorAction.IGNORE)
                    .setUnmappableInputAction(CodingErrorAction.IGNORE)
                    //.setCharset(Consts.UTF_8)
                    .setCharset(targetCharset)
                    .setMessageConstraints(messageConstraints)
                    .build();
            // Configure the connection manager to use connection configuration either
            // by default or for a specific host.
            connManager.setDefaultConnectionConfig(connectionConfig);
            connManager.setConnectionConfig(new HttpHost("somehost", 80), ConnectionConfig.DEFAULT);

            // Configure total max or per route limits for persistent connections
            // that can be kept in the pool or leased by the connection manager.
            connManager.setMaxTotal(100);
            connManager.setDefaultMaxPerRoute(10);
            connManager.setMaxPerRoute(new HttpRoute(new HttpHost("somehost", 80)), 20);

            // Use custom cookie store if necessary.
            CookieStore cookieStore = new BasicCookieStore();
            // Use custom credentials provider if necessary.
            CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            // Create global request configuration
            RequestConfig defaultRequestConfig = RequestConfig.custom()
                    .setCookieSpec(CookieSpecs.DEFAULT)
                    .setExpectContinueEnabled(true)
                    .setTargetPreferredAuthSchemes(Arrays.asList(AuthSchemes.NTLM, AuthSchemes.DIGEST))
                    .setProxyPreferredAuthSchemes(Arrays.asList(AuthSchemes.BASIC))
                    .build();

            // Create an HttpClient with the given custom dependencies and configuration.
            CloseableHttpClient httpclient = HttpClients.custom()
                    .setConnectionManager(connManager)
                    .setDefaultCookieStore(cookieStore)
                    .setDefaultCredentialsProvider(credentialsProvider)
                    //.setProxy(new HttpHost("myproxy", 8080))
                    .setDefaultRequestConfig(defaultRequestConfig)
                    //.setRedirectStrategy(new LaxRedirectStrategy())
                    .setRedirectStrategy(new DefaultRedirectStrategy())
                    .build();

            try {
                HttpGet httpget = new HttpGet(new URI(url));
                // Request configuration can be overridden at the request level.
                // They will take precedence over the one set at the client level.
                RequestConfig requestConfig = RequestConfig.copy(defaultRequestConfig)
                        .setSocketTimeout(15000)
                        .setConnectTimeout(15000)
                        .setConnectionRequestTimeout(15000)
                        //.setProxy(new HttpHost("myotherproxy", 8080))
                        .build();
                httpget.setConfig(requestConfig);
                httpget.setHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36");

                // Execution context can be customized locally.
                HttpClientContext context = HttpClientContext.create();
                // Contextual attributes set the local context level will take
                // precedence over those set at the client level.
                context.setCookieStore(cookieStore);
                context.setCredentialsProvider(credentialsProvider);

                //System.out.println("#HttpClientUtil:: executing request " + httpget.getURI());
                CloseableHttpResponse response = httpclient.execute(httpget, context);
                try {
                    //System.out.println(EntityUtils.toString(response.getEntity()));

                    System.out.println("#HttpClientUtil:: Response code: " + response.getStatusLine());
                    HttpEntity entity = response.getEntity();
                    ContentType contentType = ContentType.getOrDefault(entity);
                    Charset detCharset = null;
                    if (targetCharset == null) {
                        detCharset = contentType.getCharset();
                        if (detCharset != null && detCharset == Charset.forName("UTF-8")){
                            resultCharset = "UTF-8";
                        } else {
                            detCharset = Charset.forName("EUC-KR");
                            resultCharset = "EUC-KR";
                        }
                    } else {
                        detCharset = targetCharset;
                    }

                    System.out.println("#HttpClientUtil:: Response charset: " + detCharset);
                    if (entity != null) {
                        InputStream istream = entity.getContent();
                        //byte[] bytesForCharset2 = new byte[4096];
                        //IOUtils.read(istream, bytesForCharset2);

                        try {
                            //String detCharset2 = DetectEncoding.guessEncoding(bytesForCharset2);
                            //System.out.println("Detect encoding:" +detCharset);

                            resultStr = IOUtils.toString(istream, detCharset);
                            HttpUriRequest currentReq = (HttpUriRequest) context.getAttribute(
                                    ExecutionContext.HTTP_REQUEST);
                            HttpHost currentHost = (HttpHost)  context.getAttribute(
                                    ExecutionContext.HTTP_TARGET_HOST);
                            resultUri = (currentReq.getURI().isAbsolute()) ? currentReq.getURI().toString() : (currentHost.toURI() + currentReq.getURI());

                            System.out.println("#HttpClientUtil:: Response URI: " + resultUri);
                            // 오류로 예외로직 사용 못함 java.io.IOException: Attempted read from closed stream.
                            /*
                            if (result.contains("�")) {
                                if (detCharset == Charset.forName("EUC-KR")) {
                                    InputStream istream2 = entity.getContent();
                                    result = IOUtils.toString(istream2, Charset.forName("UTF-8"));
                                } else if (detCharset == Charset.forName("UTF-8")) {
                                    InputStream istream2 = entity.getContent();
                                    result = IOUtils.toString(istream2, Charset.forName("EUC-KR"));
                                }
                            }
                            */
                            //System.out.println("Response: content::" + result);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            istream.close();
                        }
                    }
                    // Once the request has been executed the local context can
                    // be used to examine updated state and various objects affected
                    // by the request execution.

                    // Last executed request
                    context.getRequest();
                    // Execution route
                    context.getHttpRoute();
                    // Target auth state
                    context.getTargetAuthState();
                    // Proxy auth state
                    context.getTargetAuthState();
                    // Cookie origin
                    context.getCookieOrigin();
                    // Cookie spec used
                    context.getCookieSpec();
                    // User security token
                    context.getUserToken();

                } finally {
                    response.close();
                }
            } finally {
                httpclient.close();
            }

        //}

        resultMap.put("resultStr", resultStr);
        resultMap.put("resultUri", resultUri);
        resultMap.put("resultCharset", resultCharset);

        return resultMap;
    }

    private static String getParamsByMap2(String reqUrl, Map<String, Object> reqMap) {
        String returnUrl = "";

        if (reqMap != null) {
            String tag = (reqUrl.contains("?")) ? "&" : "?";

            int cnt = 0;
            for (String key : reqMap.keySet()) {
                //System.out.println(String.format("키 : %s, 값 : %s", key, reqMap.get(key)));
                if(cnt>0) tag += "&";
                tag += key + "=" + reqMap.get(key);
                cnt++;
            }

            returnUrl += tag;
        }

        return returnUrl;
    }

    private static String getParamsByMap(String reqUrl, Map<String, Object> reqparamMap) {
        String pageString = "";

        if (reqparamMap != null) {
            String tag = (reqUrl.contains("?")) ? "&" : "?";

            reqUrl = reqUrl + pageString;

            if (reqparamMap.get("reqDtDate") != null && !reqparamMap.get("reqDtDate").equals("")) {
                pageString = tag + reqparamMap.get("reqDt") + "=" + reqparamMap.get("reqDtDate");
            }

            if (reqparamMap.get("reqPageno") != null
                    && !"".equals(reqparamMap.get("reqPageno")) && !"0".equals(reqparamMap.get("reqPageno"))) {
                //pageString = tag + reqparamMap.get("reqPageKey")+"=" + reqparamMap.get("reqPageno");
                pageString = pageString + "&" + reqparamMap.get("reqPageKey") + "=" + reqparamMap.get("reqPageno");
            }
            if (reqparamMap.get("reqPageNo") != null
                    && !"".equals(reqparamMap.get("reqPageNo")) && !"0".equals(reqparamMap.get("reqPageNo"))) {
                pageString = pageString + "&" + reqparamMap.get("reqPage") + "=" + reqparamMap.get("reqPageNo");
            }
            if (reqparamMap.get("reqQueryString") != null
                    && !"".equals(reqparamMap.get("reqQueryString")) && !"0".equals(reqparamMap.get("reqQueryString"))) {
                pageString = pageString + "&" + reqparamMap.get("reqQuery") + "=" + reqparamMap.get("reqQueryString");
            }
            if (reqparamMap.get("reqStDate") != null
                    && !"".equals(reqparamMap.get("reqStDate")) && !"0".equals(reqparamMap.get("reqStDate"))) {
                pageString = pageString + "&" + reqparamMap.get("reqSt") + "=" + reqparamMap.get("reqStDate");
            }
            if (reqparamMap.get("reqEdDate") != null
                    && !"".equals(reqparamMap.get("reqEdDate")) && !"0".equals(reqparamMap.get("reqEdDate"))) {
                pageString = pageString + "&" + reqparamMap.get("reqEd") + "=" + reqparamMap.get("reqEdDate");
            }

            if (reqUrl.indexOf("daum") > 0) {
                if (reqUrl.indexOf("finance") < 1) {
                    //System.out.println("reqUrl orig::"+reqUrl);
                    reqUrl = reqUrl + pageString + "&type=tit_cont";
                }
            } else {
                reqUrl = reqUrl + pageString;
            }
        }
        return pageString;
    }


    public static String reqPost(String reqUrl, Map<String, Object> paramsMap) throws Exception {
        String result="";
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            RequestBuilder request = RequestBuilder.post()
                    .setUri(new URI(reqUrl));
            for( String key : paramsMap.keySet() ){
                System.out.println( String.format("#HTTP_POST params:: 키:%s, 값:%s", key, paramsMap.get(key)) );

                if("body".equals(key)) {
                    String tmp = paramsMap.get(key).toString();
                    HttpEntity entity = new ByteArrayEntity(tmp.getBytes("UTF-8"));
                    request.setEntity(entity);
                } else {
                    String tmp2 = paramsMap.get(key).toString();
                    request.addParameter(key, new String(tmp2.getBytes("UTF-8")));
                }
            }
            HttpUriRequest requestPost = request.build();

            /*
            File file = new File(args[0]);

            InputStreamEntity reqEntity = new InputStreamEntity(
                    new FileInputStream(file), -1, ContentType.APPLICATION_OCTET_STREAM);
            reqEntity.setChunked(true);
            */
            // It may be more appropriate to use FileEntity class in this particular
            // instance but we are using a more generic InputStreamEntity to demonstrate
            // the capability to stream out data from any arbitrary source
            //
            // FileEntity entity = new FileEntity(file, "binary/octet-stream");

            //System.out.println("Executing request: " + requestPost.getRequestLine());

            CloseableHttpResponse response = httpclient.execute((HttpUriRequest) requestPost);

            try {
                System.out.println("#HTTP_POST response code:"+response.getStatusLine());
                //System.out.println("#HTTP_POST response result:"+EntityUtils.toString(response.getEntity()));
                result = EntityUtils.toString(response.getEntity());
            } finally {
                response.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpclient.close();
        }
        return result;
    }



    public static String reqPut(String reqUrl, Map<String, Object> paramsMap) throws Exception {
        String result="";
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            RequestBuilder request = RequestBuilder.put()
                    .setUri(new URI(reqUrl));
            for( String key : paramsMap.keySet() ){
                System.out.println( String.format("#HTTP_POST params:: 키:%s, 값:%s", key, paramsMap.get(key)) );

                if("body".equals(key)) {
                    String tmp = paramsMap.get(key).toString();
                    HttpEntity entity = new ByteArrayEntity(tmp.getBytes("UTF-8"));
                    request.setEntity(entity);
                } else {
                    request.addParameter(key, (String) paramsMap.get(key));
                }
            }
            HttpUriRequest requestPost = request.build();

            /*
            File file = new File(args[0]);

            InputStreamEntity reqEntity = new InputStreamEntity(
                    new FileInputStream(file), -1, ContentType.APPLICATION_OCTET_STREAM);
            reqEntity.setChunked(true);
            */
            // It may be more appropriate to use FileEntity class in this particular
            // instance but we are using a more generic InputStreamEntity to demonstrate
            // the capability to stream out data from any arbitrary source
            //
            // FileEntity entity = new FileEntity(file, "binary/octet-stream");

            //System.out.println("Executing request: " + requestPost.getRequestLine());

            CloseableHttpResponse response = httpclient.execute((HttpUriRequest) requestPost);

            try {
                System.out.println("#HTTP_POST response code:"+response.getStatusLine());
                //System.out.println("#HTTP_POST response result:"+EntityUtils.toString(response.getEntity()));
                result = EntityUtils.toString(response.getEntity());
            } finally {
                response.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpclient.close();
        }
        return result;
    }



    public static String reqPut1(String reqUrl, Map<String, Object> paramsMap) throws Exception {
        String result="";
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            RequestBuilder request = RequestBuilder.put()
                    .setUri(new URI(reqUrl));
            for( String key : paramsMap.keySet() ){
                System.out.println( String.format("#HTTP_POST params:: 키:%s, 값:%s", key, paramsMap.get(key)) );

                if("body".equals(key)) {
                    String tmp = paramsMap.get(key).toString();
                    HttpEntity entity = new ByteArrayEntity(tmp.getBytes("UTF-8"));
                    request.setEntity(entity);
                } else {
                    request.addParameter(key, (String) paramsMap.get(key));
                }
            }
            HttpUriRequest requestPost = request.build();

            /*
            File file = new File(args[0]);

            InputStreamEntity reqEntity = new InputStreamEntity(
                    new FileInputStream(file), -1, ContentType.APPLICATION_OCTET_STREAM);
            reqEntity.setChunked(true);
            */
            // It may be more appropriate to use FileEntity class in this particular
            // instance but we are using a more generic InputStreamEntity to demonstrate
            // the capability to stream out data from any arbitrary source
            //
            // FileEntity entity = new FileEntity(file, "binary/octet-stream");

            //System.out.println("Executing request: " + requestPost.getRequestLine());

            CloseableHttpResponse response = httpclient.execute((HttpUriRequest) requestPost);

            try {
                System.out.println("#HTTP_POST response code:"+response.getStatusLine());
                //System.out.println("#HTTP_POST response result:"+EntityUtils.toString(response.getEntity()));
                result = EntityUtils.toString(response.getEntity());
            } finally {
                response.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpclient.close();
        }
        return result;
    }

}

class HttpGetWithEntity extends HttpEntityEnclosingRequestBase {
    public final static String METHOD_NAME = "GET";

    @Override
    public String getMethod() {
        return METHOD_NAME;
    }
}