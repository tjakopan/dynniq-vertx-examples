package hr.dynniq.vertx.examples;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

import java.util.concurrent.atomic.AtomicLong;

public class GreetingServerVerticle extends AbstractVerticle {
	private static final String TEMPLATE = "Hello, %s!";
	private final AtomicLong counter = new AtomicLong();

	@Override
	public void start(final Future<Void> startFuture) throws Exception {
		vertx.createHttpServer()
			.requestHandler(req -> {
				final JsonObject greeting = new JsonObject()
					.put("id", counter.incrementAndGet())
					.put("content", String.format(TEMPLATE, "World"))
					.put("thread", Thread.currentThread().getName());
				req.response().end(greeting.encode());
			})
			.listen(8080, asyncResult -> {
				if (asyncResult.failed()) {
					System.out.println("Could not start HTTP server.");
					startFuture.fail(asyncResult.cause());
				} else {
					System.out.println("Server started.");
					startFuture.complete();
				}
			});
	}
}
