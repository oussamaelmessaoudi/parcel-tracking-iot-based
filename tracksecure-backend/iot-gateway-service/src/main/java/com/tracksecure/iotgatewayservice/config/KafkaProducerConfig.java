package com.tracksecure.iotgatewayservice.config;

import com.fasterxml.jackson.databind.ser.std.StringSerializer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {
    @Value("${kafka.bootstrap-servers}") // Reads the kafka cluster address from the src/app.yaml and inject it into bootstrapServers
    private String bootstrapServers;

    @Bean // This class is more like you're telling the spring that whenever u run the app take whatever it return this method and -
    // store it in the spring container and like this it will be reusable whenenver you want
    public ProducerFactory<String, String> producerFactory(){
        // Now i'll try to create a helper methode / factory to reusable creation of producers
        Map<String,Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class); //Serialization mode (String serializer)
        config.put(ProducerConfig.ACKS_CONFIG,"all"); // broker waits for all replicas to ack before confirming
        config.put(ProducerConfig.RETRIES_CONFIG,3); // retries up to 3 in case failures
        config.put(ProducerConfig.LINGER_MS_CONFIG,10); // wait up to 10 ms before sending messages
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    //KafkaTemplate will play the role of the data sender
    public KafkaTemplate<String,String> kafkaTemplate(){
        return new KafkaTemplate<>(producerFactory());
    }
}
