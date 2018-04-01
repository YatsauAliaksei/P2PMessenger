package by.mrj.messenger;


import by.mrj.messaging.network.DiscoveryService;
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
@ComponentScan(value = "by.mrj.messenger")//, useDefaultFilters = false)
@EnableAutoConfiguration
@Log4j2
public class MessengerConfig {

    @Value("${app.listener.port}")
    private int port;
    @Value("${discovery.service.connection.address}")
    private String connection;
    @Value("${app.root.node.name}")
    private String appName;
    @Value("${app.net.address}")
    private String appNetAddress;
    @Autowired(required = false)
    private Boolean torProxyEnabled;

    @PostConstruct
    public void post() {
        log.info("Tor proxy set to [{}]", torProxyEnabled == null ? false : torProxyEnabled);
    }

    @Bean
    public DiscoveryService discoveryService() {
        Transport transport = transport();
        return new DiscoveryService(connection, appName, transport.netAddress());
    }

    @Bean
    public Transport transport() {
        return new SimpleSocketTransport(appNetAddress, port);
//        TorTransport torTransport = new TorTransport();
//        torTransport.init();
//        return torTransport;
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

    public static void main(String[] args) throws Exception {
        new SpringApplicationBuilder()
                .sources(MessengerConfig.class)
                .bannerMode(Banner.Mode.OFF)
                .run(args);
    }
}
