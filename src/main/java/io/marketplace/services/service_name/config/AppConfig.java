package io.marketplace.services.service_name.config;

import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.prometheus.client.hotspot.GarbageCollectorExports;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        io.marketplace.commons.config.GsonConfiguration.class,
        io.marketplace.commons.config.RestTemplateConfig.class,
        io.marketplace.commons.config.HttpClientConfig.class,
        io.marketplace.commons.config.ConversionServiceConfiguration.class
})
public class AppConfig {

    @Bean
    public GarbageCollectorExports garbageCollectorExports(PrometheusMeterRegistry registry) {
        GarbageCollectorExports gcMetrics = new GarbageCollectorExports();
        registry.getPrometheusRegistry().register(gcMetrics);
        return gcMetrics;
    }
}
