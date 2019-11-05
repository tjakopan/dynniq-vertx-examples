package hr.dynniq.vertx.examples;

import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.http.HttpServer;
import io.vertx.reactivex.ext.web.client.HttpResponse;
import io.vertx.reactivex.ext.web.client.WebClient;

public class RxTwitterFeedApplication {
	public static void main(String[] args) {
		final Vertx vertx = Vertx.vertx();
		final WebClient webClient = WebClient.create(vertx);
		final HttpServer httpServer = vertx.createHttpServer();
		httpServer.requestStream()
			.toFlowable()
			.flatMapCompletable(req ->
				webClient.getAbs("https://twitter.com/Dynniq")
					.rxSend()
					.map(HttpResponse::bodyAsString)
					.onErrorReturn(throwable -> "Cannot access the twitter feed: " + throwable.getMessage())
					.doOnSuccess(res -> req.response().end(res))
					.ignoreElement())
			.subscribe();
		httpServer.listen(8080);
	}
}
