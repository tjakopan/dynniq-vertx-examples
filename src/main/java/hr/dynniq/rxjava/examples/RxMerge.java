package hr.dynniq.rxjava.examples;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class RxMerge {
	private static final Logger LOGGER = LoggerFactory.getLogger(RxMerge.class);

	public static void main(String[] args) throws InterruptedException {
		final Flowable<String> intervals = Flowable.interval(100, TimeUnit.MILLISECONDS, Schedulers.computation())
			.limit(10)
			.map(tick -> "Tick #" + tick)
			.subscribeOn(Schedulers.computation());

		final Flowable<String> strings = Flowable.just("abc", "def", "ghi", "jkl")
			.subscribeOn(Schedulers.computation());

		final Flowable<Object> uuids = Flowable.generate(emitter -> emitter.onNext(UUID.randomUUID()))
			.limit(10)
			.subscribeOn(Schedulers.computation());

		Flowable.merge(strings, intervals, uuids)
			.subscribe(obj -> LOGGER.info("Received: {}", obj));

		Thread.sleep(3000);

		LOGGER.info("==================");

		Flowable.zip(intervals, uuids, strings,
			(i, u, s) -> String.format("%s {%s} -> %s", i, u, s))
			.subscribe(obj -> LOGGER.info("Received: {}", obj));

		Thread.sleep(3000);
	}
}
