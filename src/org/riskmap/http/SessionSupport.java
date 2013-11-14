package org.riskmap.http;

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
public class SessionSupport {

    protected HttpHost targetHost;

    public SessionSupport(HttpHost targetHost) {
        this.targetHost = targetHost;
    }

    public DefaultHttpClient buildProxiedClient() throws IOException {
        DefaultHttpClient httpclient = new DefaultHttpClient();
        List<String> authPreferences = new ArrayList<>();
        //authPreferences.add(AuthPolicy.NTLM);
//        authPreferences.add(AuthPolicy.SPNEGO);
        httpclient.getParams().setParameter(AuthPNames.PROXY_AUTH_PREF, authPreferences);

        HttpHost proxy = new HttpHost("localhost", 4545);
        httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);

        return httpclient;
    }

}
