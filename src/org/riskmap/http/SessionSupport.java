package org.riskmap.http;

/**
 * <p></p>
 *
 * @author Maxim Galushka
 * @since 11/13/13
 */

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.params.AuthPNames;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.AuthPolicy;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;

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
    protected String sessionId;

    public SessionSupport(HttpHost targetHost) {
        this.targetHost = targetHost;
    }

    public void initCookie(DefaultHttpClient httpClient){
        if(sessionId != null){
            BasicClientCookie sessionCookie = new BasicClientCookie("ASP.NET_SessionId", sessionId);
            sessionCookie.setPath("/");

            httpClient.getCookieStore().addCookie(sessionCookie);
        }
    }

    public DefaultHttpClient buildProxiedClient() throws IOException {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        List<String> authPreferences = new ArrayList<>();
        httpClient.getParams().setParameter(AuthPNames.PROXY_AUTH_PREF, authPreferences);

        HttpHost proxy = new HttpHost("localhost", 4545);
        httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);

        // building session
        HttpGet get = new HttpGet("/");
        HttpResponse response = httpClient.execute(targetHost, get);
        HttpEntity entity = response.getEntity();

        // hard-coded
        String content = IOUtils.convertStreamToString(entity.getContent(), "UTF-8");

        List<Cookie> lc = httpClient.getCookieStore().getCookies();
        for (Cookie cookie : lc) {
            if("ASP.NET_SessionId".equals(cookie.getName())){
                sessionId = cookie.getValue();
            }
        }

        return httpClient;
    }

}
