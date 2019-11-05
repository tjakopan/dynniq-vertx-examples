package hr.dynniq.vertx.examples;

import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;

public class TwitterFeedApplication {
	public static void main(String[] args) {
		final Vertx vertx = Vertx.vertx();
		final WebClient webClient = WebClient.create(vertx);
		vertx.createHttpServer()
			.requestHandler(req -> {
				webClient.getAbs("https://twitter.com/Dynniq")
					.send(asyncResult -> {
						if (asyncResult.failed()) {
							req.response()
								.end("Cannot access the twitter feed: " + asyncResult.cause().getMessage());
						} else {
							req.response()
								.end(asyncResult.result().bodyAsString());
						}
					});
			})
			.listen(8080);
	}
}
