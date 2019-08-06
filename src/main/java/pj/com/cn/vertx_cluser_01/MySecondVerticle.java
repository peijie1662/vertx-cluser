package pj.com.cn.vertx_cluser_01;

import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.Router;

public class MySecondVerticle extends AbstractVerticle {

	public void start() {
		Router router = Router.router(vertx);
		router.get("/").handler(rc -> {
			rc.response().end("Welcome use second-service!");
		});
		vertx.createHttpServer().requestHandler(router).listen(8082);
	}
}