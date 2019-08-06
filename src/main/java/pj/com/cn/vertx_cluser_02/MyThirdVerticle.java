package pj.com.cn.vertx_cluser_02;

import io.vertx.circuitbreaker.CircuitBreaker;
import io.vertx.circuitbreaker.CircuitBreakerOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.client.WebClient;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.ServiceReference;

public class MyThirdVerticle extends AbstractVerticle {

	private CircuitBreaker breaker;

	public void start() {
		breaker = CircuitBreaker
				.create("third-circuit-breaker", vertx, new CircuitBreakerOptions().setMaxFailures(5).setTimeout(2000))
				.openHandler(v -> {
					System.out.println("Circuit opened");
				}).closeHandler(v -> {
					System.out.println("Circuit closed");
				});
		// 重试策略，线性增长
		// .retryPolicy(retryCount -> retryCount * 100L)

		Router router = Router.router(vertx);
		router.get("/").handler(rc -> {
			rc.response().end("Welcome use third-service!");
		});
		router.get("/cluser01/first").handler(rc -> {
			ServiceDiscovery sd = ServiceDiscovery.create(vertx);
			sd.getRecord(new JsonObject().put("name", "first-service"), r -> {
				if (r.succeeded()) {
					// System.out.println("record:"+r.result().toJson());
					breaker.execute(f -> {
						Record rd = r.result();
						ServiceReference ref = sd.getReference(rd);
						WebClient wc = ref.getAs(WebClient.class);
						wc.get("/").send(res -> {
							if (res.succeeded()) {
								f.complete("call first-service:" + res.result().body().toString());
							} else {
								f.fail("first-service unvalid! " + breaker.failureCount());
							}
						});
						ref.release();
					}).setHandler(ar -> {
						if (ar.succeeded()) {
							rc.response().end(
									"breaker status:" + breaker.state().toString() + "   " + ar.result().toString());
						} else {
							rc.response().end(
									"breaker status:" + breaker.state().toString() + "   " + ar.cause().getMessage());
						}
					});
				} else {
					rc.response().end("could not found first-service!");
				}
			});
			sd.close();
		});
		vertx.createHttpServer().requestHandler(router).listen(8083);
	}
}