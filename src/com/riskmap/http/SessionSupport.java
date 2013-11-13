package com.riskmap.http;

/**
 * <p></p>
 *
 * @author Maxim Galushka
 * @since 11/13/13
 */

import org.apache.http.HttpHost;
import org.apache.http.auth.params.AuthPNames;
import org.apache.http.client.params.AuthPolicy;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Abstract session support for all sites</p>
 *
 * @author Maxim Galushka
 * @since 07/09/2011
 */
public abstract class SessionSupport {

    // Abstract session ID (PHP or else)
    private String sessionId;

    protected HttpHost targetHost;

    protected SessionSupport() {
    }

    protected SessionSupport(HttpHost targetHost) {
        this.targetHost = targetHost;
    }

    protected String getSessionId() {
        return sessionId;
    }

    protected void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    protected DefaultHttpClient buildProxiedClient() throws IOException {
        DefaultHttpClient httpclient = new DefaultHttpClient();
        List<String> authPreferences = new ArrayList<String>();
        authPreferences.add(AuthPolicy.NTLM);
        authPreferences.add(AuthPolicy.SPNEGO);
        httpclient.getParams().setParameter(AuthPNames.PROXY_AUTH_PREF, authPreferences);

        HttpHost proxy = new HttpHost("localhost", 4545);
        httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);

        return httpclient;
    }

    protected abstract void buildBasicCookies(DefaultHttpClient httpclient);

    protected abstract DefaultHttpClient buildSessionClient(DefaultHttpClient client) throws IOException;

    /**
     * @return each call will return new session client linked to new HTTP session
     * @throws IOException is any
     */
    public DefaultHttpClient getNewSessionClient() throws IOException {
        DefaultHttpClient httpclient = buildProxiedClient();
        return buildSessionClient(httpclient);
    }

    public DefaultHttpClient relogin() throws IOException {
        setSessionId(null);
        return getNewSessionClient();
    }

    public HttpHost getTargetHost() {
        return targetHost;
    }
}
