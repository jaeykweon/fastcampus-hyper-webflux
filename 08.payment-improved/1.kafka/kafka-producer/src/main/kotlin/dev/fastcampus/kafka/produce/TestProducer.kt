package dev.fastcampus.kafka.produce

import kotlinx.coroutines.reactor.awaitSingle
import mu.KotlinLogging
import org.apache.kafka.clients.producer.ProducerConfig
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate
import org.springframework.stereotype.Service
import reactor.kafka.sender.SenderOptions

private val logger = KotlinLogging.logger {}

@Service
class TestProducer(
//    private val template: KafkaTemplate<String,String>
    private val template: ReactiveKafkaProducerTemplate<String,String>,
) {
    suspend fun send(topic: String, message: String) {
        logger.debug { ">> send to [$topic]: $message" }
        template.send(topic,message).awaitSingle()
    }
}

@Configuration
class ReactiveKafkaInitializer{
    @Bean
    fun reactiveProducer(properties: KafkaProperties): ReactiveKafkaProducerTemplate<String,String> {
        return properties.buildProducerProperties()
            .let { prop ->
                prop[ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG] = true
                SenderOptions.create<String,String>(prop) }
            .let { option -> ReactiveKafkaProducerTemplate(option) }
    }
}