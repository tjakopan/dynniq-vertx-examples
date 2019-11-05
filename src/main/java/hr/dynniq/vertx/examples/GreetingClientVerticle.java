package hr.dynniq.vertx.examples;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;

import java.util.concurrent.atomic.AtomicInteger;

public class GreetingClientVerticle extends AbstractVerticle {
	private static final int N = 100_000;

	private volatile AtomicInteger completed = new AtomicInteger(0);
	private long start;

	@Override
	public void start() throws Exception {
		final WebClient webClient = WebClient.create(vertx);
		for (int i = 1; i <= N; i++) {
			if (i == 1) {
				start = System.currentTimeMillis();
			}
			webClient.get(8080, "localhost", "/greeting")
				.send(asyncResult -> {
					if (asyncResult.succeeded()) {
						final HttpResponse<Buffer> response = asyncResult.result();
						System.out.println("Got HTTP response with status " + response.statusCode() + " with data " +
							response.bodyAsString());
						if (completed.incrementAndGet() == N) {
							final long duration = System.currentTimeMillis() - start;
							System.out.println(N + " requests took " + duration + " ms.");
						}
					} else {
						asyncResult.cause().printStackTrace();
					}
				});
		}
	}
}
