package hr.dynniq.rxjava.examples;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RxThreading {
	private static final Logger LOGGER = LoggerFactory.getLogger(RxThreading.class);

	public static void main(String[] args) throws InterruptedException {
		Flowable.range(1, 5)
			.map(i -> i * 10)
			.map(i -> {
				LOGGER.info("map({})", i);
				return i.toString();
			})
			.subscribe(LOGGER::info);

		Thread.sleep(1000);
		LOGGER.info("=======================================");

		Flowable.range(1, 5)
			.map(i -> i * 10)
			.map(i -> {
				LOGGER.info("map({})", i);
				return i.toString();
			})
			.observeOn(Schedulers.single())
			.subscribe(LOGGER::info);

		Thread.sleep(1000);
		LOGGER.info("=======================================");

		Flowable.range(1, 5)
			.map(i -> i * 10)
			.map(i -> {
				LOGGER.info("map({})", i);
				return i.toString();
			})
			.observeOn(Schedulers.single())
			.subscribeOn(Schedulers.computation())
			.subscribe(LOGGER::info);

		Thread.sleep(1000);
		LOGGER.info("=======================================");
	}
}
