package ru.itis.pipelineerrorsclassifier.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.boot.task.TaskSchedulerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.function.Supplier;

/**
 * 27.05.2022
 *
 * @author Azat Yamanaev
 */
@Configuration
public class HttpConfig {

    @Bean("apiObjectMapper")
    public ObjectMapper objectMapper(){
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

    @Bean("apiHttpRequestFactory")
    public ClientHttpRequestFactory httpRequestFactory() {
        return new HttpComponentsClientHttpRequestFactory(httpClient());
    }

    @Bean("apiRestTemplate")
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate(httpRequestFactory());
        List<HttpMessageConverter<?>> converters = restTemplate
                .getMessageConverters();

        for (HttpMessageConverter<?> converter : converters) {
            if (converter instanceof MappingJackson2HttpMessageConverter) {
                MappingJackson2HttpMessageConverter jsonConverter = (MappingJackson2HttpMessageConverter) converter;
                jsonConverter.setObjectMapper(objectMapper());
            }
        }

        return restTemplate;
    }

    @Bean("apiHttpClient")
    public HttpClient httpClient() {
        return HttpClientBuilder.create()
                .setConnectionManager(new PoolingHttpClientConnectionManager())
                .setDefaultRequestConfig(RequestConfig.DEFAULT)
                .build();
    }

    @Bean
    @Primary
    public AsyncTaskExecutor asyncTaskExecutor() {
        return new TaskSchedulerBuilder()
                .poolSize(2)
                .build();
    }

    @Bean(name = "ymlMapper")
    @Primary
    public ObjectMapper ymlMapper() {
        return new ObjectMapper(new YAMLFactory());
    }
}
