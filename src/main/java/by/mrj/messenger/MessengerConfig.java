package by.mrj.messenger;


import by.mrj.messaging.network.discovery.DiscoveryService;
import by.mrj.messaging.network.discovery.DnsDiscovery;
import by.mrj.messaging.network.transport.SimpleSocketTransport;
import by.mrj.messaging.network.transport.Transport;
import lombok.extern.log4j.Log4j2;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

@PropertySource("classpath:default.properties")
@ComponentScan(value = "by.mrj.messenger")
@EnableAutoConfiguration
@Log4j2
public class MessengerConfig {

    @Value("${dns.address}")
    private String dnsAddress;
    @Value("${app.net.address}")
    private String appNetAddress;
    @Value("${app.root.node.name}")
    private String appName;
    @Autowired(required = false)
    private Boolean torProxyEnabled;

    @PostConstruct
    public void post() {
        log.info("Tor proxy set to [{}]", torProxyEnabled == null ? false : torProxyEnabled);
    }

    @Bean
    public DiscoveryService discoveryService(Transport transport) {
        return new DnsDiscovery(transport, dnsAddress, appNetAddress);
    }

    @Bean
    public Transport transport() {
        return new SimpleSocketTransport(appNetAddress);
    }

    // fixme: should be removed. Client should do it by it's own.
    @Bean
    @ConditionalOnProperty(value = "tor.proxy.enabled", havingValue = "true")
    public Boolean torProxyEnabled() {
        System.setProperty("socksProxyHost", "127.0.0.1");
        System.setProperty("socksProxyPort", "9050");
        return true;
    }

    public static void main(String[] args) throws Exception {
        new SpringApplicationBuilder()
                .sources(MessengerConfig.class)
                .bannerMode(Banner.Mode.OFF)
                .run(args);
    }
}
