package hr.dynniq.rxjava.examples;

import io.reactivex.Observable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class RxCreateObservable {
	private static final Logger LOGGER = LoggerFactory.getLogger(RxCreateObservable.class);

	public static void main(String[] args) {
		final List<String> data = Arrays.asList("foo", "bar", "baz");
		final Random random = new Random();
		final Observable<String> source = Observable.create(subscriber -> {
			for (String s : data) {
				if (random.nextInt(6) == 0) {
					subscriber.onError(new RuntimeException("Bad luck for you...."));
				}
				subscriber.onNext(s);
			}
			subscriber.onComplete();
		});

		for (int i = 0; i < 10; i++) {
			LOGGER.info("=======================================");
			source.subscribe(next -> LOGGER.info("Next: {}", next),
				error -> LOGGER.error("Whoops"),
				() -> LOGGER.info("Done"));
		}

		LOGGER.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		source.retry(5)
			.subscribe(next -> LOGGER.info("Next: {}", next),
				error -> LOGGER.error("Whoops"),
				() -> LOGGER.info("Done"));
	}
}
