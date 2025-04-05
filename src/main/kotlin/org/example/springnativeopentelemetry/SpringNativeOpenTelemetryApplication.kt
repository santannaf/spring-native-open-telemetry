package org.example.springnativeopentelemetry

import io.micrometer.observation.Observation
import io.micrometer.observation.ObservationRegistry
import io.opentelemetry.api.common.AttributeKey
import io.opentelemetry.api.trace.SpanKind
import io.opentelemetry.contrib.sampler.RuleBasedRoutingSampler
import io.opentelemetry.sdk.autoconfigure.spi.AutoConfigurationCustomizer
import io.opentelemetry.sdk.autoconfigure.spi.AutoConfigurationCustomizerProvider
import io.opentelemetry.sdk.autoconfigure.spi.ConfigProperties
import io.opentelemetry.sdk.trace.samplers.Sampler
import org.slf4j.LoggerFactory
import org.springframework.boot.actuate.autoconfigure.observation.ObservationRegistryCustomizer
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.http.server.observation.ServerRequestObservationContext
import org.springframework.util.AntPathMatcher
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
class SpringNativeOpenTelemetryApplication

fun main(args: Array<String>) {
    runApplication<SpringNativeOpenTelemetryApplication>(args = args)
}

@RestController
class Controller {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    @GetMapping("/ping")
    fun ping(): String {
        logger.info("m=ping, msg=Receive Request")
        return "pong"
    }
}

@Configuration
class OpenTelemetryConfig {
    @Bean
    fun otelCustomizer(): AutoConfigurationCustomizerProvider {
        return AutoConfigurationCustomizerProvider { p: AutoConfigurationCustomizer ->
            p.addSamplerCustomizer { fallback: Sampler, _: ConfigProperties ->
                RuleBasedRoutingSampler.builder(SpanKind.SERVER, fallback)
                    .drop(AttributeKey.stringKey("url.path"), "^.actuator.*")
                    .build()
            }
        }
    }

    @Bean
    @Primary
    fun skipActuatorEndpointsFromObservation(): ObservationRegistryCustomizer<ObservationRegistry> {
        val pathMatcher = AntPathMatcher("/")
        return ObservationRegistryCustomizer { registry: ObservationRegistry ->
            registry.observationConfig().observationPredicate { _: String, context: Observation.Context ->
                if (context is ServerRequestObservationContext) {
                    return@observationPredicate !pathMatcher.match("/actuator/**", context.carrier.requestURI)
                } else {
                    return@observationPredicate true
                }
            }
        }
    }
}
