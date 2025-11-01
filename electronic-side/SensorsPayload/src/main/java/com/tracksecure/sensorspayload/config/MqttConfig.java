package com.tracksecure.sensorspayload.config;

import lombok.RequiredArgsConstructor;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

@Configuration
@RequiredArgsConstructor
public class MqttConfig {

    private final MqttProperties mqttProps;

    @Bean
    public MqttClient mqttClient() throws Exception {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(mqttProps.getUsername());
        options.setPassword(mqttProps.getPassword().toCharArray());

        // Load CA certificate from classpath
        KeyStore caKs = KeyStore.getInstance(KeyStore.getDefaultType());
        caKs.load(null, null);

        try (InputStream is = new ClassPathResource(mqttProps.getCaCertPath().replace("classpath:", "")).getInputStream()) {
            Certificate caCert = CertificateFactory.getInstance("X.509").generateCertificate(is);
            caKs.setCertificateEntry("mosquittoCA", caCert);
        }

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(caKs);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tmf.getTrustManagers(), null);
        options.setSocketFactory(sslContext.getSocketFactory());

        MqttClient client = new MqttClient(mqttProps.getBroker(), mqttProps.getClientId(), new MemoryPersistence());
        client.connect(options);

        for (String topic : mqttProps.getTopics()) {
            client.subscribe(topic);
        }

        return client;
    }
}
