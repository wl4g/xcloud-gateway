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
package com.wl4g.gateway.server.route.repository;

import com.google.common.collect.Maps;
import com.wl4g.components.common.log.SmartLogger;
import com.wl4g.gateway.server.route.IRouteAlterSubscriber;
import com.wl4g.gateway.server.route.IRouteCacheRefresh;
import com.wl4g.gateway.server.route.NotifyType;
import com.wl4g.gateway.server.route.RefreshRoutesApplicationListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.InMemoryRouteDefinitionRepository;
import org.springframework.cloud.gateway.route.RouteDefinition;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;

import static com.wl4g.components.common.log.SmartLoggerFactory.getLogger;

import java.util.Map;

/**
 * Persistent routing information abstract class. < br > abstract class If you
 * need to extend the persistence method, please inherit this class and override
 * the following methods of this class {@link #getRouteDefinitionsByPermanent()}
 * and {@link #save(Mono)} and {@link #delete(Mono)} and
 * {@link #notifyAllRefresh(NotifyType)} } and {@link #initSubscriber()} and
 * {@link #flushRoutesPermanentToMemery()}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-07-04
 * @since
 */
public abstract class AbstractRouteRepository extends InMemoryRouteDefinitionRepository
		implements IRouteCacheRefresh, IRouteAlterSubscriber {

	protected final SmartLogger log = getLogger(getClass());

	@Autowired
	private RefreshRoutesApplicationListener routeAlterHandler;

	protected Mono<Void> notifyAllClientRefresh(NotifyType notifyType) {
		return routeAlterHandler.refresh(notifyType);
	}

	@Override
	public final Flux<RouteDefinition> getRouteDefinitions() {
		return getRouteDefinitionsByMemery();
	}

	/**
	 * 从内存组件中获取路由信息
	 * 
	 * @return
	 */
	public final Flux<RouteDefinition> getRouteDefinitionsByMemery() {
		return super.getRouteDefinitions();
	}

	/**
	 * 从持久化组件中获取路由信息
	 * 
	 * @return
	 */
	protected abstract Flux<RouteDefinition> getRouteDefinitionsByPermanent();

	/**
	 * 刷新内存中的路由信息
	 * </p>
	 * 通过对比内存和持久化存储中的路由差异信息，进行更新
	 * 
	 * @return
	 */
	@Override
	public synchronized Mono<Void> flushRoutesPermanentToMemery() {
		Flux<RouteDefinition> memeryRoutes = getRouteDefinitionsByMemery();
		Flux<RouteDefinition> permanentRoutes = getRouteDefinitionsByPermanent();

		Map<RouteDefinition, Boolean> memRouteMap = Maps.newHashMap();
		memeryRoutes.subscribe(route -> memRouteMap.put(route, false));

		Map<RouteDefinition, Boolean> perRouteMap = Maps.newHashMap();
		permanentRoutes.subscribe(route -> perRouteMap.put(route, false));

		// 给内存中新增路由信息
		Flux.fromIterable(perRouteMap.keySet()).filter(perRoute -> !memRouteMap.containsKey(perRoute))
				.flatMap(route -> super.save(Mono.just(route))).subscribe();
		// 给内存中删除无效路由
		Flux.fromIterable(memRouteMap.keySet()).filter(perRoute -> !perRouteMap.containsKey(perRoute))
				.flatMap(route -> super.delete(Mono.just(route.getId()))).subscribe();
		// 提醒刷新状态
		return notifyAllClientRefresh(NotifyType.state);
	}

	@PostConstruct
	public synchronized Mono<Void> initMemeryByPermanent() {
		return flushRoutesPermanentToMemery();
	}

}