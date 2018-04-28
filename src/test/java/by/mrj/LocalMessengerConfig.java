package by.mrj;

import by.mrj.messaging.network.discovery.DiscoveryService;
import by.mrj.messaging.network.discovery.DnsDiscovery;
import by.mrj.messaging.network.transport.SimpleSocketTransport;
import by.mrj.messaging.network.transport.Transport;
import by.mrj.messenger.MessengerConfig;
import lombok.extern.log4j.Log4j2;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

// for local run tests
@Log4j2
@SpringBootTest(classes = {
        LocalMessengerConfig.TestConfig.class,
})
@RunWith(SpringRunner.class)
@TestPropertySource("classpath:local.default.properties")
public class LocalMessengerConfig {

    @Configuration
    public static class TestConfig extends MessengerConfig {

        @Value("${dns.address}")
        private String dnsAddress;
        @Value("${app.net.address}")
        private String appNetAddress;

        @Bean
        public DiscoveryService discoveryService(Transport transport) {
            return new DnsDiscovery(transport, dnsAddress, appNetAddress);
        }

        @Bean
        public Transport transport() {
            return new SimpleSocketTransport(appNetAddress);
        }

        @Bean
        @ConditionalOnProperty(value = "tor.proxy.enabled", havingValue = "true")
        public Boolean torProxyEnabled() {
            System.setProperty("socksProxyHost", "127.0.0.1");
            System.setProperty("socksProxyPort", "9050");
            System.setProperty("http.proxyHost", "127.0.0.1");
            System.setProperty("http.proxyPort", "9050");
            System.setProperty("https.proxyHost", "127.0.0.1");
            System.setProperty("https.proxyPort", "9050");
            return true;
        }

/*        @Bean
        public ZooKeeperDiscoveryService discoveryService() {
            ZooKeeperDiscoveryService mock = mock(ZooKeeperDiscoveryService.class);
            String netAddress = transport().netAddress();
            when(mock.discoverPeers()).thenReturn(Lists.newArrayList(netAddress));

            String address = CryptoUtils.sha256ripemd160(EncodingUtils.HEX.encode(CryptoUtils.pubKey));
            Registration registration = Registration.builder()
                    .address(address)
                    .networkAddress(netAddress)
                    .build();
            when(mock.getPeerData(anyString(), any())).thenReturn(registration);
            return mock;
        }*/
    }

    @Test
    public void mainTest() throws Exception {

    }

}
