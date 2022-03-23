package io.marketplace.services.contact.utils;

import io.marketplace.commons.logging.Logger;
import io.marketplace.commons.logging.LoggerFactory;
import io.marketplace.commons.utils.MembershipUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@Component
public class RestUtils {
    private static final Logger LOG = LoggerFactory.getLogger(RestUtils.class);

    private static final String CONTENT_TYPE_HEADER_NAME = "Content-Type";
    private static final String AUTHORIZATION_HEADER_NAME = "X-JWT-Assertion";

    public <T> HttpEntity<T> getHttpEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION_HEADER_NAME, MembershipUtils.getJwtToken());
        headers.set(CONTENT_TYPE_HEADER_NAME, APPLICATION_JSON_VALUE);

        LOG.info(headers.toString());
        return new HttpEntity<>(headers);
    }


    public static URI appendUri(URI uri, String appendQuery) throws URISyntaxException {

        String newQuery = uri.getQuery();
        if (newQuery == null) {
            newQuery = appendQuery;
        } else {
            newQuery += "&" + appendQuery;
        }

        return new URI(uri.getScheme(), uri.getAuthority(),
                uri.getPath(), newQuery, uri.getFragment());
    }
}
