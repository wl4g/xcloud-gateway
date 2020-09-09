package com.wl4g.gateway.server.authc;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
//import org.springframework.core.io.buffer.DataBuffer;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.server.reactive.ServerHttpResponse;
//
//import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import com.google.common.hash.Hashing;
import com.wl4g.components.common.log.SmartLogger;
import com.wl4g.components.common.web.rest.RespBase;

import static com.google.common.base.Charsets.UTF_8;
import static com.wl4g.components.common.lang.Assert2.hasTextOf;
import static com.wl4g.components.common.log.SmartLoggerFactory.getLogger;
import static com.wl4g.components.common.web.WebUtils2.getMultiMapFirstValue;
import static java.lang.System.getenv;
import static java.security.MessageDigest.isEqual;
import static org.apache.commons.lang3.StringUtils.isAnyEmpty;
import static org.springframework.http.HttpStatus.OK;
import static reactor.core.publisher.Flux.just;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * {@link TempStandardApiAuthenticaingFilter}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @author vjay
 * @version v1.0 2020-07-04
 * @since
 */
public class TempStandardApiAuthenticaingFilter implements GlobalFilter, Ordered {
	protected final SmartLogger log = getLogger(getClass());

	@Override
	public int getOrder() {
		return 0;
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

		Map<String, List<String>> params = exchange.getRequest().getQueryParams();
		String appId = getMultiMapFirstValue(params, "appId");
		String nonce = getMultiMapFirstValue(params, "nonce");
		String timestamp = getMultiMapFirstValue(params, "timestamp");
		String signature = getMultiMapFirstValue(params, "signature");
		if (isAnyEmpty(appId, nonce, timestamp, signature)) {
			log.warn("appId/nonce/timestamp/signature is requires");
			return writeResponse(4000, "Invalid parameters", exchange);
		}

		// Gets stored appSecret token.
		String storedAppSecret = getenv("IAM_AUTHC_SIGN_APPSECRET_".concat(appId));
		hasTextOf(storedAppSecret, "storedAppSecret");
		byte[] storedAppSecretBuf = storedAppSecret.getBytes(UTF_8);

		// Join token parts
		StringBuffer signtext = new StringBuffer();
		signtext.append(appId);
		signtext.append(storedAppSecretBuf);
		signtext.append(timestamp);
		signtext.append(nonce);

		// Ascii sort
		byte[] signInput = signtext.toString().getBytes(UTF_8);
		Arrays.sort(signInput);
		// Calc signature
		byte[] sign = Hashing.sha256().hashBytes(signInput).asBytes();

		// Signature assertion
		if (!isEqual(sign, signature.getBytes(UTF_8))) {
			log.warn("Invalid signature. sign: {}, request sign: {}", new String(sign), signature);
			return writeResponse(4001, "Invalid signature", exchange);
		}

		return chain.filter(exchange);

		// exchange.getAttributes().put(REQUEST_TIME_BEGIN,
		// System.currentTimeMillis());
		// log.info("token is " +
		// exchange.getRequest().getHeaders().get("token"));
		//
		// if (exchange.getRequest().getHeaders().containsKey("token")) {
		// return chain.filter(exchange).then(Mono.fromRunnable(() -> {
		// Long startTime = exchange.getAttribute(REQUEST_TIME_BEGIN);
		// if (startTime != null) {
		// log.info(
		// exchange.getRequest().getURI().getRawPath() + ": " +
		// (System.currentTimeMillis() - startTime) + "ms");
		// }
		// }));
		// } else {
		// byte[] bytes = "{\"status\":429,\"msg\":\"Too Many
		// Requests\",\"data\":{}}".getBytes(StandardCharsets.UTF_8);
		// DataBuffer buffer =
		// exchange.getResponse().bufferFactory().wrap(bytes);
		// ServerHttpResponse serverHttpResponse = exchange.getResponse();
		// serverHttpResponse.setStatusCode(HttpStatus.OK);
		// return exchange.getResponse().writeWith(Flux.just(buffer));
		// }
	}

	private Mono<Void> writeResponse(int errcode, String errmsg, ServerWebExchange exchange) {
		RespBase<?> resp = RespBase.create().withCode(errcode).withMessage(errmsg);

		ServerHttpResponse response = exchange.getResponse();
		DataBuffer buffer = response.bufferFactory().wrap(resp.asJson().getBytes(UTF_8));
		response.setStatusCode(OK);
		return response.writeWith(just(buffer));
	}

	// private static final String REQUEST_TIME_BEGIN = "requestTimeBegin";

}
