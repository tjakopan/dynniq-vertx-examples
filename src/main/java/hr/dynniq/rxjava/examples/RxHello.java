package hr.dynniq.rxjava.examples;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;
import io.reactivex.functions.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RxHello {
	private static final Logger LOGGER = LoggerFactory.getLogger(RxHello.class);

	public static void main(String[] args) {
		Single.just(1)
			.map(i -> i * 10)
			.map(Object::toString)
			.subscribe((Consumer<String>) LOGGER::info);

		Maybe.just("something")
			.subscribe(LOGGER::info);

		Maybe.never()
			.subscribe(o -> LOGGER.info("Something is here..."));

		Completable.complete()
			.subscribe(() -> LOGGER.info("Completed"));

		Flowable.just("foo", "bar", "baz")
			.filter(string -> string.startsWith("b"))
			.map(String::toUpperCase)
			.subscribe(LOGGER::info);
	}
}
