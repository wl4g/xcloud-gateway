package com.wl4g.gateway.server.authc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.wl4g.components.common.log.SmartLogger;
import com.wl4g.components.common.log.SmartLoggerFactory;
import com.wl4g.iam.client.handler.WebFluxFilterDispatcherHandler;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * {@link IamOpenApiAuthenticaingFilter}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @author vjay
 * @version v1.0 2020-07-04
 * @since
 */
@Component
public class IamOpenApiAuthenticaingFilter implements GlobalFilter, Ordered {
	private static final SmartLogger log = SmartLoggerFactory.getLogger(IamOpenApiAuthenticaingFilter.class);

	@Autowired
	protected WebFluxFilterDispatcherHandler handler;

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		// handler.filter(exchange, chain);

		// Map<String, Object> attributes = exchange.getAttributes();
		// System.out.println(attributes);

		exchange.getAttributes().put(REQUEST_TIME_BEGIN, System.currentTimeMillis());
		log.info("token is " + exchange.getRequest().getHeaders().get("token"));

		if (exchange.getRequest().getHeaders().containsKey("token")) {
			return chain.filter(exchange).then(Mono.fromRunnable(() -> {
				Long startTime = exchange.getAttribute(REQUEST_TIME_BEGIN);
				if (startTime != null) {
					log.info(
							exchange.getRequest().getURI().getRawPath() + ": " + (System.currentTimeMillis() - startTime) + "ms");
				}
			}));
		} else {
			byte[] bytes = "{\"status\":429,\"msg\":\"Too Many Requests\",\"data\":{}}".getBytes(StandardCharsets.UTF_8);
			DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
			ServerHttpResponse serverHttpResponse = exchange.getResponse();
			serverHttpResponse.setStatusCode(HttpStatus.OK);
			return exchange.getResponse().writeWith(Flux.just(buffer));
		}

	}

	@Override
	public int getOrder() {
		return 0;
	}

	private static final String REQUEST_TIME_BEGIN = "requestTimeBegin";

}
