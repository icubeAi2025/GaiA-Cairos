package kr.co.ideait.platform.gaiacairos.core.config;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;

import io.netty.channel.ChannelOption;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

@Configuration
public class WebClientConfig {    
	
	DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory();

	HttpClient httpClient = HttpClient.create()
	            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000 * 60);

	//[SS]	Unread field: kr.co.ideait.platform.gaiacairos.core.config.WebClientConfig.CON_TIMEOUT; should this field be static?
	private final static int CON_TIMEOUT = 1000 * 60; // 연결 대기시간
	private final static int RW_TIMEOUT = 1000 * 30; // 읽기/쓰기 대기시간
	private final static int RES_TIMEOUT = 1000 * 30; // 응답 대기시간

	@Bean
	public WebClient webClient() {
		factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY);

		// ConnectionProvider를 설정하여 커넥션 풀을 제어합니다.
//		ConnectionProvider provider = ConnectionProvider.builder("secondary-provider")
//				// 유지할 최대 커넥션 수를 설정합니다.
//				.maxConnections(200)
//				// 사용하지 않는 상태의 커넥션을 유지하는 시간을 설정합니다.
//				.maxIdleTime(Duration.ofSeconds(5))
//				// 커넥션 풀에서 커넥션의 최대 수명을 설정합니다.
//				.maxLifeTime(Duration.ofSeconds(5))
//				// 모든 커넥션이 사용 중일 때 커넥션을 얻기 위해 대기하는 최대 시간을 설정합니다.
//				.pendingAcquireTimeout(Duration.ofSeconds(5))
//				// 커넥션을 얻기 위해 대기하는 최대 수를 설정합니다.
//				.pendingAcquireMaxCount(1000)
//				// 만료된 커넥션을 백그라운드에서 제거하는 주기를 설정합니다.
//				.evictInBackground(Duration.ofSeconds(5))
//				// 커넥션을 마지막에 사용된 순서대로 재사용하는 LIFO(Last In, First Out) 방식을 설정합니다.
//				.lifo()
//				// ConnectionProvider를 빌드합니다.
//				.build();

//		return WebClient.builder()
//			// 인코딩 및 디코딩 설정을 구성합니다.
//			.codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(2 * 1024 * 1024))
//			// 기본적으로 JSON 형식의 Accept 및 Content-Type 헤더를 설정합니다.
//			.defaultHeader("Accept", MediaType.APPLICATION_JSON_VALUE)
//			.defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
//			// Reactor Netty의 HttpClient를 사용하여 커스텀 클라이언트 커넥터를 설정합니다.
//			.clientConnector(
//				new ReactorClientHttpConnector(
//					// ConnectionProvider를 사용하여 HttpClient를 생성하고 설정합니다.
//					HttpClient.create()
//					// 연결 시간 제한을 설정합니다.
//					.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, CON_TIMEOUT)
//					// 응답 시간 제한을 설정합니다.
//					.responseTimeout(Duration.ofMillis(RES_TIMEOUT))
//					// 연결이 설정된 후에 수행할 작업을 설정합니다.
//					.doOnConnected(conn -> conn
//						// 읽기 작업 시간 제한을 설정합니다.
//						.addHandlerLast(new ReadTimeoutHandler(RW_TIMEOUT, TimeUnit.MILLISECONDS))
//						// 쓰기 작업 시간 제한을 설정합니다.
//						.addHandlerLast(new WriteTimeoutHandler(RW_TIMEOUT, TimeUnit.MILLISECONDS))
//					)
//				)
//			)
//			.build();
		return WebClient.builder()
				.uriBuilderFactory(factory)
				.codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(-1))
				.clientConnector(new ReactorClientHttpConnector(
					httpClient
//					// 연결 시간 제한을 설정합니다.
					.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, CON_TIMEOUT)
//					// 응답 시간 제한을 설정합니다.
					.responseTimeout(Duration.ofMillis(RES_TIMEOUT))
//					// 연결이 설정된 후에 수행할 작업을 설정합니다.
					.doOnConnected(conn -> conn
//						// 읽기 작업 시간 제한을 설정합니다.
						.addHandlerLast(new ReadTimeoutHandler(RW_TIMEOUT, TimeUnit.MILLISECONDS))
//						// 쓰기 작업 시간 제한을 설정합니다.
						.addHandlerLast(new WriteTimeoutHandler(RW_TIMEOUT, TimeUnit.MILLISECONDS))
					)
				))
				.build();
	}
	
	@Bean
	public ConnectionProvider connectionProvider() {
	    return ConnectionProvider.builder("http-pool")
	            .maxConnections(100)                    		// connection pool의 갯수
	            .pendingAcquireTimeout(Duration.ofMillis(0)) 	//커넥션 풀에서 커넥션을 얻기 위해 기다리는 최대 시간
	            .pendingAcquireMaxCount(-1)             		//커넥션 풀에서 커넥션을 가져오는 시도 횟수 (-1: no limit)
	            .maxIdleTime(Duration.ofMillis(1000L))        	//커넥션 풀에서 idle 상태의 커넥션을 유지하는 시간
	            .build();
	}
}
