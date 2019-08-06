package pj.com.cn.vertx_cluser_02;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.ServiceDiscoveryOptions;
import io.vertx.servicediscovery.types.HttpEndpoint;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

public class MainVerticle extends AbstractVerticle {

	public void start() {
		ClusterManager mgr = new HazelcastClusterManager();
		VertxOptions options = new VertxOptions().setClusterManager(mgr);
		Vertx.clusteredVertx(options, res -> {
			if (res.succeeded()) {
				vertx = res.result();
				vertx.deployVerticle(new MyThirdVerticle());
				// ×¢²á·þÎñ
				ServiceDiscovery.create(vertx, sd -> {
					sd.publish(HttpEndpoint.createRecord("third-service", "localhost", 8083, "/"), ar -> {
						System.out.println("cluser02 - third service publish success!");
					});
				});
			} else {
				res.cause().printStackTrace();
			}
		});

	}
}
