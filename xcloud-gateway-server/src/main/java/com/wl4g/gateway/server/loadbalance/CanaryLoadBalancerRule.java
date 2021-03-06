package com.wl4g.gateway.server.loadbalance;

import com.fasterxml.jackson.core.type.TypeReference;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.Server;
import com.wl4g.components.common.lang.Assert2;
import com.wl4g.components.common.log.SmartLogger;
import com.wl4g.components.common.serialize.JacksonUtils;
import com.wl4g.gateway.server.loadbalance.model.HostWeight;

import org.apache.commons.lang3.RandomUtils;

import static com.wl4g.components.common.log.SmartLoggerFactory.getLogger;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * {@link CanaryLoadBalancerRule}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @author vjay
 * @date 2020-07-22
 * @since
 */
public class CanaryLoadBalancerRule extends AbstractLoadBalancerRule {

	final protected SmartLogger log = getLogger(getClass());

	@Override
	public void initWithNiwsConfig(IClientConfig config) {
		System.out.println(config);
	}

	@Override
	public Server choose(Object key) {
		// List<Server> servers = this.getLoadBalancer().getReachableServers();
		if (key instanceof Map) {
			try {
				Object hosts = ((Map) key).get("hosts");
				if (hosts instanceof List) {
					List list = (List) hosts;
					List<HostWeight> hostWeights = JacksonUtils.parseJSON(JacksonUtils.toJSONString(list),
							new TypeReference<List<HostWeight>>() {
							});
					HostWeight hostWeight = weightChoose(hostWeights);
					if (Objects.nonNull(hostWeight)) {
						return new Server(hostWeight.getUri());
					}
				}
			} catch (Exception e) {
				log.error("", e);
			}
		}
		// Server server = new Server("localhost",14086);
		return null;
	}

	private HostWeight randomChoose(List<HostWeight> hostWeights) {
		if (hostWeights.isEmpty()) {
			return null;
		}
		if (hostWeights.size() == 1) {
			return hostWeights.get(0);
		}
		int randomIndex = RandomUtils.nextInt(0, hostWeights.size());
		return hostWeights.get(randomIndex);
	}

	private HostWeight weightChoose(List<HostWeight> hostWeights) {
		Assert2.notNullOf(hostWeights, "hostWeights");
		int weightSum = 0;
		for (HostWeight hw : hostWeights) {
			weightSum += hw.getWeight();
		}

		if (weightSum <= 0) {
			return randomChoose(hostWeights);
		}
		int n = RandomUtils.nextInt(0, weightSum); // n in [0, weightSum)
		int m = 0;
		for (HostWeight hostWeight : hostWeights) {
			if (m <= n && n < m + hostWeight.getWeight()) {
				log.debug("This Random Host is " + hostWeight.getUri());
				return hostWeight;
			}
			m += hostWeight.getWeight();
		}
		return null;
	}

}
