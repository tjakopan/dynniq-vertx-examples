package hr.dynniq.vertx.examples;

import io.reactivex.Completable;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.UUID;

public class BiddingServiceVerticle extends AbstractVerticle {
	private static final Logger LOGGER = LoggerFactory.getLogger(BiddingServiceVerticle.class);

	@Override
	public Completable rxStart() {
		final int port = config().getInteger("port", 3000);

		final Router router = Router.router(vertx);
		router.get("/offer").handler(this::handleOffer);

		return vertx.createHttpServer()
			.requestHandler(router)
			.rxListen(port)
			.doAfterSuccess(httpServer -> LOGGER.info("Bidding service listening on HTTP port {}", port))
			.doOnError(e -> LOGGER.error("Bidding service failed to start", e))
			.ignoreElement();
	}

	private void handleOffer(final RoutingContext context) {
		final Random random = new Random();
		final String myId = UUID.randomUUID().toString();

		final String clientIdHeader = context.request()
			.getHeader("Client-Request-Id");
		final String clientId = clientIdHeader != null ? clientIdHeader : "N/A";
		final int myBid = 10 + random.nextInt(20);

		final JsonObject payload = new JsonObject()
			.put("origin", myId)
			.put("bid", myBid);
		if (clientIdHeader != null) {
			payload.put("clientRequestId", clientId);
		}

		final long artificialDelay = random.nextInt(1000);
		vertx.setTimer(artificialDelay, timerId -> {
			if (random.nextInt(20) == 1) {
				context.response()
					.setStatusCode(500)
					.end();
				LOGGER.error("{} injects an error (client-id={}, artificialDelay={})", myId, clientId, artificialDelay);
			} else {
				context.response()
					.putHeader("Content-Type", "application/json")
					.end(payload.encode());
				LOGGER.info("{} offers {} (client-id={}, artificialDelay={})", myId, myBid, clientId, artificialDelay);
			}
		});
	}
}
