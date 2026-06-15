package kafkaload

import io.gatling.javaapi.core.*
import io.gatling.javaapi.core.CoreDsl.*
import org.apache.kafka.clients.producer.ProducerConfig
import org.galaxio.gatling.kafka.javaapi.KafkaDsl.*

class KafkaSimulation : Simulation() {
    private val kafkaConf = kafka()
        .properties(
            mapOf(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to "161.104.32.243:9092",
                ProducerConfig.ACKS_CONFIG to "1",
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to "org.apache.kafka.common.serialization.StringSerializer",
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to "org.apache.kafka.common.serialization.StringSerializer"
            )
        )

    private val scn = scenario("Kafka Load Scenario")
        .exec(
            kafka("Send Test Massage")
                .topic("topic1")
                .send("key", "value")
        )

    // Define injection profile and execute the test
    init {
        setUp(
            scn.injectOpen(incrementUsersPerSec(1000.0)
                .times(4).eachLevelLasting(60)
                .separatedByRampsLasting(10)
                .startingFrom(100.0))
        ).protocols(kafkaConf);
    }

}
