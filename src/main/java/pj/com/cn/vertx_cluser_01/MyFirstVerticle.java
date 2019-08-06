package pj.com.cn.vertx_cluser_01;

import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.Router;

public class MyFirstVerticle extends AbstractVerticle {

	public void start() {
		Router router = Router.router(vertx);
		router.get("/").handler(rc -> {
			rc.response().end("Welcome use first-service!");
		});
		vertx.createHttpServer().requestHandler(router).listen(8081);
	}
}