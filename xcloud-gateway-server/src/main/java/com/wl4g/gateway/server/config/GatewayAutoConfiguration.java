/*
 * Copyright 2017 ~ 2025 the original author or authors. <wanglsir@gmail.com, 983708408@qq.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wl4g.gateway.server.config;

import com.wl4g.components.common.task.RunnerProperties;
import com.wl4g.gateway.server.console.GatewayConsole;
import com.wl4g.gateway.server.route.RefreshRoutesApplicationListener;
import com.wl4g.gateway.server.route.TimingTaskRefresher;
import com.wl4g.gateway.server.route.repository.RedisRouteDefinitionRepository;
import com.wl4g.iam.client.core.RequestMappingDispatcher;
import com.wl4g.iam.client.core.WebFluxFilterMappingDispatcher;
import com.wl4g.iam.client.handler.StandardSignApiWebHandler;
import com.wl4g.iam.client.handler.WebIamHandler;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Gateway properties configuration.
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2018年9月16日
 * @since
 */
@Configuration
public class GatewayAutoConfiguration {

	@Bean
	public RefreshRoutesApplicationListener routeAlterHandler() {
		return new RefreshRoutesApplicationListener();
	}

	@Bean
	public RouteDefinitionRepository redisRouteDefinitionRepository() {
		return new RedisRouteDefinitionRepository();
	}

	@Bean
	public TimingTaskRefresher refreshableConfigurationCoordinator() {
		return new TimingTaskRefresher(new RunnerProperties().withConcurrency(1));
	}

	@Bean
	public GatewayConsole gatewayConsole() {
		return new GatewayConsole();
	}

	@Bean
	@ConfigurationProperties(prefix = "gateway")
	public RefreshProperties gatewayRefreshProperties() {
		return new RefreshProperties();
	}

	// --- Authenticating. ---

	@Bean
	public StandardSignApiWebHandler standardSignApiWebHandler() {
		return new StandardSignApiWebHandler();
	}

	@Bean
	public RequestMappingDispatcher webFluxFilterMappingDispatcher(List<WebIamHandler> interceptors) {
		return new WebFluxFilterMappingDispatcher(interceptors);
	}

}