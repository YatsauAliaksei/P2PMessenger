package by.mrj;

import by.mrj.crypto.util.CryptoUtils;
import by.mrj.crypto.util.EncodingUtils;
import by.mrj.messaging.network.DiscoveryService;
import by.mrj.messaging.network.domain.Registration;
import by.mrj.messaging.network.transport.SimpleSocketTransport;
import by.mrj.messaging.network.transport.Transport;
import by.mrj.messenger.MessengerConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;
import com.google.common.collect.Lists;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

// for local run tests
@SpringBootTest(classes = {LocalMessengerConfig.TestConfig.class, MessengerConfig.class})
@RunWith(SpringRunner.class)
public class LocalMessengerConfig {

    @Configuration
    public static class TestConfig {

        @Value("${app.listener.port}")
        private int port;

        @Bean
        public DiscoveryService discoveryService() {
            DiscoveryService mock = mock(DiscoveryService.class);
            when(mock.discoverNodes()).thenReturn(Lists.newArrayList("127.0.0.1"));

            String address = CryptoUtils.sha256ripemd160(EncodingUtils.HEX.encode(CryptoUtils.pubKey));
            Registration registration = Registration.builder()
                    .address(address)
                    .ip("127.0.0.1")
                    .build();
            when(mock.getNodeData(anyString(), any())).thenReturn(registration);
            return mock;
        }

        @Bean
        public Transport transport() {
            return new SimpleSocketTransport(port);
        }
    }

    @Test
    public void mainTest() throws Exception {

    }

}
