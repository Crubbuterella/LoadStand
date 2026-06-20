package kafkaload

import io.gatling.javaapi.core.*
import io.gatling.javaapi.core.CoreDsl.*
import org.apache.kafka.clients.producer.ProducerConfig
import org.galaxio.gatling.kafka.javaapi.KafkaDsl.*
import java.time.Duration
import kotlin.random.Random
import java.util.UUID

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

    private val dataFeeder = generateSequence {
        val isMale = Random.nextBoolean()

        val surnamesMale = listOf("Иванов", "Смирнов", "Кузнецов", "Попов", "Васильев", "Петров", "Соколов", "Закроев")
        val namesMale = listOf("Василий", "Александр", "Максим", "Иван", "Артем", "Дмитрий", "Никита", "Михаил")
        val patronymicsMale = listOf("Геннадьевич", "Иванович", "Александрович", "Сергеевич", "Дмитриевич", "Алексеевич")

        val surnamesFemale = listOf("Иванова", "Смирнова", "Кузнецова", "Попова", "Васильева", "Петрова", "Соколова", "Закроева")
        val namesFemale = listOf("Анна", "Мария", "Елена", "Дарья", "Алина", "Ирина", "Екатерина", "Анастасия", "Ольга")
        val patronymicsFemale = listOf("Геннадьевна", "Ивановна", "Александровна", "Сергеевна", "Дмитриевна", "Алексеевна")

        val surname = if (isMale) surnamesMale.random() else surnamesFemale.random()
        val name = if (isMale) namesMale.random() else namesFemale.random()
        val patronymic = if (isMale) patronymicsMale.random() else patronymicsFemale.random()

        mapOf(
            "msg_id" to UUID.randomUUID().toString(),
            "full_name" to "$surname $name $patronymic",
            "inn" to generateValidInn()
        )
    }.iterator()

    private val scn = scenario("Kafka Load Scenario")
        .feed(dataFeeder)
        .exec(
            kafka("Send Json Message")
                .topic("topic1")
                .send( """
                        {
                          "msg_id": "#{msg_id}",
                          "full_name": "#{full_name}",
                          "inn": "#{inn}"
                        }
                        """.trimIndent())
        )

    init {
        setUp(
            scn.injectOpen(
                constantUsersPerSec(10.0).during(Duration.ofMinutes(5)),
                constantUsersPerSec(15.0).during(Duration.ofMinutes(5)),
                constantUsersPerSec(25.0).during(Duration.ofMinutes(5)),
                constantUsersPerSec(50.0).during(Duration.ofMinutes(5))
            )
        ).protocols(kafkaConf);
    }

    companion object {

        fun generateValidInn(): String {
            val digits = IntArray(12)
            for (i in 1..10) {
                digits[i] = Random.nextInt(0, 10)
            }

            // Вычисляем 11-ю цифру
            val weights11 = intArrayOf(7, 2, 4, 10, 3, 5, 9, 4, 6, 8)
            val sum11 = digits.take(10).mapIndexed { index, v -> v * weights11[index] }.sum()
            digits[10] = (sum11 % 11) % 10

            // Вычисляем 12-ю цифру
            val weights12 = intArrayOf(3, 7, 2, 4, 10, 3, 5, 9, 4, 6, 8)
            val sum12 = digits.take(11).mapIndexed { index, v -> v * weights12[index] }.sum()
            digits[11] = (sum12 % 11) % 10

            return digits.joinToString("")
        }
    }
}
