package com.tracksecure.iotgatewayservice.config;

import com.tracksecure.iotgatewayservice.mqtt.MqttMessageHandler;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

@Configuration
public class MqttConfig {
    @Value("${mqtt.broker.url}")
    private String brokerUrl;
    @Value("${mqtt.client.id}")
    private String clientId;
    @Value("${mqtt.username}")
    private String username;
    @Value("${mqtt.password}")
    private String password;
    @Value("${mqtt.topics}")
    private String[] topics;
    @Value("${mqtt.qos}")
    private int qos;
    @Value("${mqtt.connection.timeout}")
    private int connectionTimeout;
    @Value("${mqtt.keepalive.interval}")
    private int keepAliveInterval;
    @Value("${mqtt.clean.session}")
    private boolean cleanSession;
    @Value("${mqtt.automatic.reconnect}")
    private boolean automaticReconnect;

    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options =  new MqttConnectOptions();
        options.setServerURIs(new String[]{brokerUrl});
        options.setConnectionTimeout(connectionTimeout);
        options.setKeepAliveInterval(keepAliveInterval);
        options.setCleanSession(cleanSession);
        options.setAutomaticReconnect(automaticReconnect);

        if(username != null && username.isEmpty()) {
            options.setUserName(username);
            options.setPassword(password.toCharArray());
        }

        factory.setConnectionOptions(options);
        return factory;
    }

    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageProducer inbound() {
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(clientId + "-inbound",
                        mqttClientFactory(),
                        topics);

        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(qos);
        adapter.setOutputChannel(mqttInputChannel());

        return adapter;
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public MessageHandler handler(MqttMessageHandler mqttMessageHandler) {
        return mqttMessageHandler;
    }
}
