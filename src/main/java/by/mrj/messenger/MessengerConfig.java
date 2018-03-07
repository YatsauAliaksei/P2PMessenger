package by.mrj.messenger;


import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

@PropertySource("classpath:default.properties")
@ComponentScan("by.mrj.messenger")
@EnableAutoConfiguration
public class MessengerConfig {

    public static void main(String[] args) throws Exception {
        new SpringApplicationBuilder()
                .sources(MessengerConfig.class)
                .bannerMode(Banner.Mode.OFF)
                .run(args);
    }
}
