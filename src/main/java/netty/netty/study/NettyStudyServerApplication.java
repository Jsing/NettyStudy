package netty.netty.study;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.logging.Logger;

@SpringBootApplication
public class NettyStudyServerApplication {

	final static Logger logger = Logger.getLogger(String.valueOf(NettyStudyServerApplication.class));

	public static void main(String[] args) {
		SpringApplication.run(NettyStudyServerApplication.class, args);
	}

}
