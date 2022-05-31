package ru.itis.glabplugin.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.Messages;
import lombok.NonNull;
import org.apache.commons.collections.MapUtils;
import org.apache.http.conn.ConnectTimeoutException;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.function.BiConsumer;

/**
 * @author Dmitry Sadchikov
 */
public class RestClient {

    private static final Logger logger = Logger.getInstance(RestClient.class);

    private static final int DEFAULT_TIMEOUT = 30 * 1000;
    private static final MediaType DEFAULT_CONTENT_TYPE = MediaType.APPLICATION_JSON;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    public <T> RequestBuilder<T> post(String url, Class<T> responseType) {
        return new RequestBuilder<>(HttpMethod.POST, url, responseType);
    }

    public <T> RequestBuilder<T> get(String url, Class<T> responseType) {
        return new RequestBuilder<>(HttpMethod.GET, url, responseType);
    }

    public <T> RequestBuilder<T> put(String url, Class<T> responseType) {
        return new RequestBuilder<>(HttpMethod.PUT, url, responseType);
    }

    public <T> RequestBuilder<T> patch(String url, Class<T> responseType) {
        return new RequestBuilder<>(HttpMethod.PATCH, url, responseType);
    }

    public <T> RequestBuilder<T> delete(String url, Class<T> responseType) {
        return new RequestBuilder<>(HttpMethod.DELETE, url, responseType);
    }

    public class RequestBuilder<T> {

        private final HttpMethod method;
        private String url;
        private MultiValueMap<String, String> parameters;
        private HttpHeaders headers;
        private Object body;
        private MediaType contentType;
        private Class<T> responseType;
        private TypeReference<T> typeReference;
        private Integer timeout;
        private Integer retries;
        private BiConsumer<? super T, HttpStatus> errorHandler;

        private RequestBuilder(@NonNull HttpMethod method, @NonNull String url,
                               @NonNull Class<T> responseType) {
            this.method = method;
            this.url = url;
            this.responseType = responseType;
        }

        private RequestBuilder(@NonNull HttpMethod method, @NonNull String url,
                               @NonNull TypeReference<T> typeReference) {
            this.method = method;
            this.url = url;
            this.typeReference = typeReference;
        }

        public RequestBuilder<T> parameter(String parameterName, Object parameterValue) {
            if (parameters == null) {
                parameters = new LinkedMultiValueMap<>();
            }
            parameters.add(parameterName, parameterValue != null ? parameterValue.toString() : null);
            return this;
        }

        public RequestBuilder<T> parameters(MultiValueMap<String, String> parameters) {
            if (parameters == null) {
                return this;
            }
            if (this.parameters == null) {
                this.parameters = new LinkedMultiValueMap<>();
            }
            this.parameters.putAll(parameters);
            return this;
        }

        public RequestBuilder<T> header(String headerName, String headerValue) {
            if (headers == null) {
                headers = new HttpHeaders();
            }
            headers.add(headerName, headerValue);
            return this;
        }

        public RequestBuilder<T> headers(HttpHeaders headers) {
            if (headers == null) {
                return this;
            }
            if (this.headers == null) {
                this.headers = new HttpHeaders();
            }
            this.headers.putAll(headers);
            return this;
        }

        public RequestBuilder<T> body(Object body) {
            if (Arrays.asList(HttpMethod.GET, HttpMethod.HEAD, HttpMethod.DELETE).contains(this.method)) {
                throw new UnsupportedOperationException(
                        String.format("Not available for method %s", this.method.name())
                );
            }
            this.body = body;
            return this;
        }

        public RequestBuilder<T> contentType(MediaType contentType) {
            if (contentType == MediaType.APPLICATION_JSON) {
                this.contentType = MediaType.APPLICATION_JSON_UTF8;
            } else {
                this.contentType = contentType;
            }
            return this;
        }

        public RequestBuilder<T> timeout(Integer timeout) {
            this.timeout = timeout;
            return this;
        }

        public RequestBuilder<T> handleErrors(BiConsumer<? super T, HttpStatus> errorHandler) {
            this.errorHandler = errorHandler;
            return this;
        }

        public RequestBuilder<T> withRetries(Integer retries) {
            this.retries = retries;
            return this;
        }

        public String getString() {
            try {
                if (responseType == null && typeReference == null) {
                    throw new IllegalStateException();
                }

                headers = headers == null ? new HttpHeaders() : headers;
                contentType = contentType == null ? DEFAULT_CONTENT_TYPE : contentType;
                timeout = timeout == null ? DEFAULT_TIMEOUT : timeout;

                headers.setContentType(contentType);

                SimpleClientHttpRequestFactory requestFactory =
                        (SimpleClientHttpRequestFactory) restTemplate.getRequestFactory();
                requestFactory.setReadTimeout(timeout);
                requestFactory.setConnectTimeout(timeout);

                if (contentType.isCompatibleWith(MediaType.APPLICATION_JSON)) {
                    body = mapper.writeValueAsString(body);
                }

                if (!url.contains("?") && MapUtils.isNotEmpty(parameters)) {
                    url = UriComponentsBuilder.fromHttpUrl(url)
                            .queryParams(parameters)
                            .toUriString();
                    parameters.clear();
                }

                final HttpEntity<Object> requestEntity = new HttpEntity<>(body, headers);
                if (logger.isTraceEnabled()) {
                    logger.trace("Request " + url + ", body:\n" + requestEntity + "");
                }

                ResponseEntity<String> resp = restTemplate.exchange(
                        url,
                        method,
                        requestEntity,
                        String.class,
                        parameters == null ? Collections.emptyMap() : parameters
                );

                return resp.getBody();
            } catch (JsonProcessingException e) {
                logger.error("Can't map object to json. Cause: ", e);
            }

            return "";
        }

        public Optional<T> send() {
            try {
                if (responseType == null && typeReference == null) {
                    throw new IllegalStateException();
                }

                headers = headers == null ? new HttpHeaders() : headers;
                contentType = contentType == null ? DEFAULT_CONTENT_TYPE : contentType;
                timeout = timeout == null ? DEFAULT_TIMEOUT : timeout;

                headers.setContentType(contentType);

                SimpleClientHttpRequestFactory requestFactory =
                        (SimpleClientHttpRequestFactory) restTemplate.getRequestFactory();
                requestFactory.setReadTimeout(timeout);
                requestFactory.setConnectTimeout(timeout);

                if (contentType.isCompatibleWith(MediaType.APPLICATION_JSON)) {
                    body = mapper.writeValueAsString(body);
                }

                if (!url.contains("?") && MapUtils.isNotEmpty(parameters)) {
                    url = UriComponentsBuilder.fromHttpUrl(url)
                            .queryParams(parameters)
                            .toUriString();
                    parameters.clear();
                }

                final HttpEntity<Object> requestEntity = new HttpEntity<>(body, headers);
                if (logger.isTraceEnabled()) {
                    logger.trace("Request " + url + ", body:\n" + requestEntity + "");
                }

                ResponseEntity<String> resp = restTemplate.exchange(
                        url,
                        method,
                        requestEntity,
                        String.class,
                        parameters == null ? Collections.emptyMap() : parameters
                );

                final Optional<T> responseBody = Optional.ofNullable(resp.getBody())
                        .map(this::parseBody);

                if (resp.getStatusCode().isError()) {
                    logger.error("Server response code - " + resp.getStatusCodeValue() + ", response body - " + resp.getBody());
                    if (errorHandler != null) {
                        responseBody.ifPresent(body -> errorHandler.accept(body, resp.getStatusCode()));
                    }
                } else if (logger.isTraceEnabled()) {
                    logger.trace("Response code - " + resp.getStatusCodeValue() + ", response body - " + resp.getBody());
                }
                return responseBody;

            } catch (RestClientException rce) {
                Throwable cause = rce.getCause();
                if (cause instanceof ConnectTimeoutException || cause instanceof SocketTimeoutException) {
                    logger.error("API timeout: " + cause + " (" + method + " " + url + ")");
                    throw new IllegalStateException();
                } else if (rce instanceof HttpClientErrorException) {
                    final HttpClientErrorException hcee = (HttpClientErrorException) rce;
                    logger.error("Response code - " + hcee.getRawStatusCode() + ", response body - " + hcee.getRawStatusCode());
                }
                logger.error(method + " " + url + " (" + rce.getMessage() + ")");
            } catch (JsonProcessingException e) {
                logger.error("Can't map object to json. Cause: ", e);
            }
            if (retries != null && retries > 0) {
                retries--;
                return send();
            } else {
                return Optional.empty();
            }
        }

        private T parseBody(final String body) {
            try {
                if (responseType != null) {
                    return mapper.readValue(body, responseType);
                }
                return mapper.readValue(body, typeReference);
            } catch (IOException e) {
                logger.error(
                        "Can't parse body for method " + method + " , url - \"" + url + "\", body - " + body);
                logger.error(e.getMessage(), e);
            }
            return null;
        }
    }
}
