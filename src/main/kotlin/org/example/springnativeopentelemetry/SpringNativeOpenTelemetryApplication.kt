package org.example.springnativeopentelemetry

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.ParameterizedTypeReference
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestClient

@SpringBootApplication
class SpringNativeOpenTelemetryApplication

fun main(args: Array<String>) {
    runApplication<SpringNativeOpenTelemetryApplication>(args = args)
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class PostResponse(val id: Int, val title: String, val body: String)

@RestController
class Controller(
    private val restClient: RestClient
) {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    @GetMapping("/ping")
    fun ping(): List<PostResponse> {
        logger.info("m=ping, msg=Receive Request")
        return restClient.get()
            .uri("/posts")
            .retrieve()
            .body(object : ParameterizedTypeReference<List<PostResponse>>() {}) ?: emptyList()
    }
}

@Configuration
class RestConfiguration {
    @Bean
    fun restClient(builder: RestClient.Builder): RestClient {
        return builder
            .baseUrl("http://localhost:30001")
            .build()
    }

}

//@Configuration
//class OpenTelemetryConfig {
//    @Bean
//    fun otelCustomizer(): AutoConfigurationCustomizerProvider {
//        return AutoConfigurationCustomizerProvider { p: AutoConfigurationCustomizer ->
//            p.addSamplerCustomizer { fallback: Sampler, _: ConfigProperties ->
//                RuleBasedRoutingSampler.builder(SpanKind.SERVER, fallback)
//                    .drop(AttributeKey.stringKey("url.path"), "^.actuator.*")
//                    .build()
//            }
//        }
//    }
//
//    @Bean
//    @Primary
//    fun skipActuatorEndpointsFromObservation(): ObservationRegistryCustomizer<ObservationRegistry> {
//        val pathMatcher = AntPathMatcher("/")
//        return ObservationRegistryCustomizer { registry: ObservationRegistry ->
//            registry.observationConfig().observationPredicate { _: String, context: Observation.Context ->
//                if (context is ServerRequestObservationContext) {
//                    return@observationPredicate !pathMatcher.match("/actuator/**", context.carrier.requestURI)
//                } else {
//                    return@observationPredicate true
//                }
//            }
//        }
//    }
//}
