package hr.dynniq.vertx.examples;

import io.reactivex.Completable;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;

// http GET localhost:3000/offer 'Client-Request-Id:1234' --verbose
// http GET localhost:8080 'Client-Request-Id:1234' --verbose
public class MainVerticle extends AbstractVerticle {
	@Override
	public Completable rxStart() {
		final DeploymentOptions options1 = new DeploymentOptions().setConfig(new JsonObject().put("port", 3001));
		final DeploymentOptions options2 = new DeploymentOptions().setConfig(new JsonObject().put("port", 3002));
		final DeploymentOptions options3 = new DeploymentOptions().setInstances(2);
		return vertx.rxDeployVerticle(BiddingServiceVerticle.class.getName())
			.flatMap(deploymentId -> vertx.rxDeployVerticle(BiddingServiceVerticle.class.getName(), options1))
			.flatMap(deploymentId -> vertx.rxDeployVerticle(BiddingServiceVerticle.class.getName(), options2))
			.flatMap(deploymentId -> vertx.rxDeployVerticle(BestOfferServiceVerticle.class.getName(), options3))
			.ignoreElement();
	}
}
