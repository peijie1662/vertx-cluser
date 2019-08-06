package pj.com.cn.vertx_client;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.ext.web.client.WebClient;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.ServiceReference;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

public class MyRestAPIClient {

	public void sayHi() {
		ClusterManager mgr = new HazelcastClusterManager();
		VertxOptions options = new VertxOptions().setClusterManager(mgr);
		Vertx.clusteredVertx(options, res -> {
			if (res.succeeded()) {
				Vertx vertx = res.result();
				ServiceDiscovery discovery = ServiceDiscovery.create(vertx);
				discovery.getRecord(new JsonObject().put("name", "first-service"), rr -> {
					if (rr.succeeded()) {
						Record rd = rr.result();
						// reference
						ServiceReference ref = discovery.getReference(rd);
						// µ÷ÓÃ
						WebClient wc = ref.getAs(WebClient.class);
						wc.get("/").send(wr -> {
							if (wr.succeeded()) {
								System.out.println(wr.result().body().toString());
							}
						});
						//
						ref.release();
					}
				});
			}
		});

	}
}
