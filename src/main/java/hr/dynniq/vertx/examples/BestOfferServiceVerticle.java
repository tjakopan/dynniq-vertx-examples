package hr.dynniq.vertx.examples;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.RxHelper;
import io.vertx.reactivex.core.http.HttpServerRequest;
import io.vertx.reactivex.ext.web.client.HttpResponse;
import io.vertx.reactivex.ext.web.client.WebClient;
import io.vertx.reactivex.ext.web.codec.BodyCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class BestOfferServiceVerticle extends AbstractVerticle {
	private static final Logger LOGGER = LoggerFactory.getLogger(BestOfferServiceVerticle.class);

	private final AtomicLong requestIds = new AtomicLong();
	private List<JsonObject> targets;
	private WebClient webClient;

	@Override
	public Completable rxStart() {
		webClient = WebClient.create(vertx);

		targets = config().getJsonArray("targets", DEFAULT_TARGETS)
			.stream()
			.map(JsonObject.class::cast)
			.collect(Collectors.toList());

		return vertx.createHttpServer()
			.requestHandler(this::findBestOffer)
			.rxListen(8080)
			.doAfterSuccess(httpServer -> LOGGER.info("Best offer service listening on HTTP port {}", 8080))
			.doOnError(e -> LOGGER.error("Best offer service failed to start", e))
			.ignoreElement();
	}

	private void findBestOffer(final HttpServerRequest request) {
		final String requestId = String.valueOf(requestIds.getAndIncrement());

		final List<Single<JsonObject>> responses = targets.stream()
			.map(target ->
				webClient.get(target.getInteger("port"), target.getString("host"), target.getString("path"))
					.putHeader("Client-Request-Id", requestId)
					.as(BodyCodec.jsonObject())
					.rxSend()
					.retry(1)
					.timeout(500, TimeUnit.MILLISECONDS, RxHelper.scheduler(vertx))
					.map(HttpResponse::body)
					.map(body -> {
						LOGGER.info("#{} received offer {}", requestId, body.encodePrettily());
						return body;
					})
					.onErrorReturnItem(EMPTY_RESPONSE))
			.collect(Collectors.toList());

		Single.merge(responses)
			.reduce((acc, next) -> {
				if (next.containsKey("bid") && isHigher(acc, next)) {
					return next;
				}
				return acc;
			})
			.flatMapSingle(best -> {
				if (!best.containsKey("empty")) {
					return Single.just(best);
				} else {
					return Single.error(new Exception("No offer could be found for requestId " + requestId));
				}
			})
			.subscribe(best -> {
				LOGGER.info("#{} best offer: {}", requestId, best.encodePrettily());
				request.response()
					.putHeader("Content-Type", "application/json")
					.end(best.encode());
			}, e -> {
				LOGGER.error(String.format("#%s ends in error", requestId), e);
				request.response()
					.setStatusCode(502)
					.end();
			});
	}

	private boolean isHigher(final JsonObject acc, final JsonObject next) {
		return acc.getInteger("bid") > next.getInteger("bid");
	}

	private static final JsonObject EMPTY_RESPONSE = new JsonObject()
		.put("empty", true)
		.put("bid", Integer.MAX_VALUE);

	private static final JsonArray DEFAULT_TARGETS = new JsonArray()
		.add(new JsonObject()
			.put("host", "localhost")
			.put("port", 3000)
			.put("path", "/offer"))
		.add(new JsonObject()
			.put("host", "localhost")
			.put("port", 3001)
			.put("path", "/offer"))
		.add(new JsonObject()
			.put("host", "localhost")
			.put("port", 3002)
			.put("path", "/offer"));
}
