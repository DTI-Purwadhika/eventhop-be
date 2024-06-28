package com.riri.eventhop;

//import com.riri.eventhop.config.RsaKeyConfigProperties;
import lombok.extern.java.Log;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import redis.clients.jedis.Jedis;

@SpringBootApplication
@EnableCaching
//@EnableConfigurationProperties(RsaKeyConfigProperties.class)
@Log
public class EventhopApplication {

	public static void main(String[] args) {
		SpringApplication.run(EventhopApplication.class, args);
	}
}
