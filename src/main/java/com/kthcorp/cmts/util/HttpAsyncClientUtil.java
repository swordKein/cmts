package com.kthcorp.cmts.util;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.Future;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.xml.bind.MarshalException;

import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.io.IOUtils;
import org.apache.http.*;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.MessageConstraints;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.DnsResolver;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.conn.SystemDefaultDnsResolver;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.codecs.DefaultHttpRequestWriterFactory;
import org.apache.http.impl.nio.codecs.DefaultHttpResponseParser;
import org.apache.http.impl.nio.codecs.DefaultHttpResponseParserFactory;
import org.apache.http.impl.nio.conn.ManagedNHttpClientConnectionFactory;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicLineParser;
import org.apache.http.message.LineParser;
import org.apache.http.nio.IOControl;
import org.apache.http.nio.NHttpMessageParser;
import org.apache.http.nio.NHttpMessageParserFactory;
import org.apache.http.nio.NHttpMessageWriterFactory;
import org.apache.http.nio.client.methods.AsyncCharConsumer;
import org.apache.http.nio.conn.ManagedNHttpClientConnection;
import org.apache.http.nio.conn.NHttpConnectionFactory;
import org.apache.http.nio.conn.NoopIOSessionStrategy;
import org.apache.http.nio.conn.SchemeIOSessionStrategy;
import org.apache.http.nio.conn.ssl.SSLIOSessionStrategy;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.nio.reactor.SessionInputBuffer;
import org.apache.http.nio.util.HeapByteBufferAllocator;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.CharArrayBuffer;
import org.apache.http.util.EntityUtils;

import static org.apache.http.ssl.SSLContexts.createSystemDefault;

public class HttpAsyncClientUtil {

    public final static void reqGet(String reqUrl, String reqAddPath, Map<String, Object> reqParamMap) throws Exception {

        /* set params */
        String url = reqUrl + reqAddPath + getParamsByMap(reqUrl, reqParamMap);


        // Use custom message parser / writer to customize the way HTTP
        // messages are parsed from and written out to the data stream.
        NHttpMessageParserFactory<HttpResponse> responseParserFactory = new DefaultHttpResponseParserFactory() {

            @Override
            public NHttpMessageParser<HttpResponse> create(
                    final SessionInputBuffer buffer,
                    final MessageConstraints constraints) {
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
                        buffer, lineParser, DefaultHttpResponseFactory.INSTANCE, constraints);
            }

        };
        NHttpMessageWriterFactory<HttpRequest> requestWriterFactory = new DefaultHttpRequestWriterFactory();

        // Use a custom connection factory to customize the process of
        // initialization of outgoing HTTP connections. Beside standard connection
        // configuration parameters HTTP connection factory can define message
        // parser / writer routines to be employed by individual connections.
        NHttpConnectionFactory<ManagedNHttpClientConnection> connFactory = new ManagedNHttpClientConnectionFactory(
                requestWriterFactory, responseParserFactory, HeapByteBufferAllocator.INSTANCE);

        // Client HTTP connection objects when fully initialized can be bound to
        // an arbitrary network socket. The process of network socket initialization,
        // its connection to a remote address and binding to a local one is controlled
        // by a connection socket factory.

        // SSL context for secure connections can be created either based on
        // system or application specific properties.
        SSLContext sslcontext = createSystemDefault();
        // Use custom hostname verifier to customize SSL hostname verification.
        HostnameVerifier hostnameVerifier = new DefaultHostnameVerifier();

        // Create a registry of custom connection session strategies for supported
        // protocol schemes.
        Registry<SchemeIOSessionStrategy> sessionStrategyRegistry = RegistryBuilder.<SchemeIOSessionStrategy>create()
                .register("http", NoopIOSessionStrategy.INSTANCE)
                .register("https", new SSLIOSessionStrategy(sslcontext, hostnameVerifier))
                .build();

        // Use custom DNS resolver to override the system DNS resolution.

        DnsResolver dnsResolver = new SystemDefaultDnsResolver() {

            @Override
            public InetAddress[] resolve(final String host) throws UnknownHostException {
                if (host.equalsIgnoreCase("myhost")) {
                    return new InetAddress[] { InetAddress.getByAddress(new byte[] {127, 0, 0, 1}) };
                } else {
                    return super.resolve(host);
                }
            }

        };

        // Create I/O reactor configuration
        IOReactorConfig ioReactorConfig = IOReactorConfig.custom()
                .setIoThreadCount(Runtime.getRuntime().availableProcessors())
                .setConnectTimeout(30000)
                .setSoTimeout(30000)
                .build();

        // Create a custom I/O reactort
        ConnectingIOReactor ioReactor = new DefaultConnectingIOReactor(ioReactorConfig);

        // Create a connection manager with custom configuration.
        PoolingNHttpClientConnectionManager connManager = new PoolingNHttpClientConnectionManager(
                ioReactor, connFactory, sessionStrategyRegistry, dnsResolver);

        // Create message constraints
        MessageConstraints messageConstraints = MessageConstraints.custom()
                .setMaxHeaderCount(200)
                .setMaxLineLength(2000)
                .build();
        // Create connection configuration
        ConnectionConfig connectionConfig = ConnectionConfig.custom()
                .setMalformedInputAction(CodingErrorAction.IGNORE)
                .setUnmappableInputAction(CodingErrorAction.IGNORE)
                .setCharset(Consts.UTF_8)
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
        credentialsProvider.setCredentials(new AuthScope("localhost", 8889), new UsernamePasswordCredentials("squid", "nopassword"));
        // Create global request configuration
        RequestConfig defaultRequestConfig = RequestConfig.custom()
                .setCookieSpec(CookieSpecs.DEFAULT)
                .setExpectContinueEnabled(true)
                .setTargetPreferredAuthSchemes(Arrays.asList(AuthSchemes.NTLM, AuthSchemes.DIGEST))
                .setProxyPreferredAuthSchemes(Arrays.asList(AuthSchemes.BASIC))
                .build();


        HttpRequestRetryHandler retryHandler = new HttpRequestRetryHandler() {
            public boolean retryRequest(
                    IOException exception,
                    int executionCount,
                    HttpContext context) {
                if (executionCount > 3) {
                    // Do not retry if over max retry count
                    return false;
                }
                if (exception instanceof InterruptedIOException) {
                    // Timeout
                    return false;
                }
                if (exception instanceof UnknownHostException) {
                    // Unknown host
                    return false;
                }
                if (exception instanceof ConnectTimeoutException) {
                    // Connection refused
                    return false;
                }
                if (exception instanceof SSLException) {
                    // SSL handshake exception
                    return false;
                }
                HttpClientContext clientContext = HttpClientContext.adapt(context);
                HttpRequest request = clientContext.getRequest();
                boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);
                if (idempotent) {
                    // Retry if the request is considered idempotent
                    return true;
                }
                return false;
            }
        };

        // Create an HttpClient with the given custom dependencies and configuration.
        CloseableHttpAsyncClient httpclient = HttpAsyncClients.custom()
                //.setRetryHandler(retryHandler)
                .setConnectionManager(connManager)
                .setDefaultCookieStore(cookieStore)
                .setDefaultCredentialsProvider(credentialsProvider)
                //.setProxy(new HttpHost("localhost", 8889))
                .setDefaultRequestConfig(defaultRequestConfig)
                //.setRetryHandler(retryHandler)
                .build();

        try {
            HttpGet httpget = new HttpGet(url);
            // Request configuration can be overridden at the request level.
            // They will take precedence over the one set at the client level.
            RequestConfig requestConfig = RequestConfig.copy(defaultRequestConfig)
                    .setSocketTimeout(10000)
                    .setConnectTimeout(10000)
                    .setConnectionRequestTimeout(10000)
                    //.setProxy(new HttpHost("localhost", 8888))
                    .build();
            httpget.setConfig(requestConfig);

            // Execution context can be customized locally.
            HttpClientContext localContext = HttpClientContext.create();
            // Contextual attributes set the local context level will take
            // precedence over those set at the client level.
            localContext.setCookieStore(cookieStore);
            localContext.setCredentialsProvider(credentialsProvider);


            System.out.println("Executing request " + httpget.getRequestLine());

            httpclient.start();

            // Pass local context as a parameter
            Future<HttpResponse> future = httpclient.execute(httpget, localContext, null);

            // Please note that it may be unsafe to access HttpContext instance
            // while the request is still being executed

            HttpResponse response = future.get();
            System.out.println("Response: " + response.getStatusLine());


            HttpEntity entity = response.getEntity();
            ContentType contentType = ContentType.getOrDefault(entity);
            Charset detCharset = contentType.getCharset();

            System.out.println("Response: " + detCharset);

            if (entity != null) {
                InputStream istream  = entity.getContent();
                //byte[] bytesForCharset = new byte[4096];
                //IOUtils.read(istream, bytesForCharset);

                try {
                    //String detCharset = DetectEncoding.guessEncoding(bytesForCharset);
                    //System.out.println("Detect encoding:" +detCharset);

                    String resultContent = IOUtils.toString(istream, detCharset);
                    System.out.println("Response: content::" + resultContent);
                } finally {
                    istream.close();
                }
            }


            // Once the request has been executed the local context can
            // be used to examine updated state and various objects affected
            // by the request execution.

            // Last executed request
            localContext.getRequest();
            // Execution route
            localContext.getHttpRoute();
            // Target auth state
            localContext.getTargetAuthState();
            // Proxy auth state
            localContext.getTargetAuthState();
            // Cookie origin
            localContext.getCookieOrigin();
            // Cookie spec used
            localContext.getCookieSpec();
            // User security token
            localContext.getUserToken();
        } finally {
            httpclient.close();
        }
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

    /*
    static class MyResponseConsumer extends AsyncCharConsumer<Boolean> {

        @Override
        protected void onResponseReceived(final HttpResponse response) {
        }

        @Override
        protected void onCharReceived(final CharBuffer buf, final IOControl ioctrl) throws IOException {
            while (buf.hasRemaining()) {
                System.out.print(buf.get());
            }
        }

        @Override
        protected void releaseResources() {
        }

        @Override
        protected Boolean buildResult(final HttpContext context) {
            return Boolean.TRUE;
        }

    }
    */
}
