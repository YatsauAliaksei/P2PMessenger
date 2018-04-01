package by.mrj;

import by.mrj.crypto.util.CryptoUtils;
import by.mrj.crypto.util.EncodingUtils;
import by.mrj.messaging.network.DiscoveryService;
import by.mrj.messaging.network.domain.Registration;
import by.mrj.messenger.MessengerConfig;
import lombok.extern.log4j.Log4j2;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import com.google.common.collect.Lists;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

// for local run tests
@Log4j2
@SpringBootTest(classes = {
//        MessengerConfig.class,
        LocalMessengerConfig.TestConfig.class,
})
@RunWith(SpringRunner.class)
@TestPropertySource("classpath:local.default.properties")
public class LocalMessengerConfig {

    @Configuration
    public static class TestConfig extends MessengerConfig {

        @Bean
        public DiscoveryService discoveryService() {
            DiscoveryService mock = mock(DiscoveryService.class);
            String netAddress = transport().netAddress();
            when(mock.discoverNodes()).thenReturn(Lists.newArrayList(netAddress));

            String address = CryptoUtils.sha256ripemd160(EncodingUtils.HEX.encode(CryptoUtils.pubKey));
            Registration registration = Registration.builder()
                    .address(address)
                    .ip(netAddress)
                    .build();
            when(mock.getNodeData(anyString(), any())).thenReturn(registration);
            return mock;
        }
    }

    @Test
    public void mainTest() throws Exception {

    }

}
