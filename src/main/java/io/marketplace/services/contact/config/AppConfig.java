package io.marketplace.services.contact.config;

import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.prometheus.client.hotspot.GarbageCollectorExports;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan({"io.marketplace.commons.gson",
        "io.marketplace.services.pxchange.client"})
@Import({
        io.marketplace.commons.config.BaseSerializationConfig.class,
        io.marketplace.commons.config.GsonConfiguration.class,
        io.marketplace.commons.config.RestTemplateConfig.class,
        io.marketplace.commons.config.HttpClientConfig.class,
        io.marketplace.commons.config.ConversionServiceConfiguration.class,
        io.marketplace.commons.crypto.jws.JWSUtils.class,
        io.marketplace.commons.config.JWTFactoryConfig.class,
        io.marketplace.commons.config.JWTConfigurations.class,
        io.marketplace.commons.filter.CustomWebSecurity.class,
        io.marketplace.commons.security.CustomSecurityAspect.class,
        io.marketplace.commons.config.RequestLogConfig.class
})
public class AppConfig {

    @Bean
    public GarbageCollectorExports garbageCollectorExports(PrometheusMeterRegistry registry) {
        GarbageCollectorExports gcMetrics = new GarbageCollectorExports();
        registry.getPrometheusRegistry().register(gcMetrics);
        return gcMetrics;
    }
}
