package com.oath.cyclops.react.lazy;

import java.util.function.Supplier;

import com.oath.cyclops.react.ThreadPools;
import com.oath.cyclops.react.base.BaseNumberOperationsTest;
import cyclops.async.LazyReact;
import cyclops.reactive.FutureStream;

public class LazyFutureNumberOperationsTest extends BaseNumberOperationsTest {
	@Override
	protected <U> FutureStream<U> of(U... array) {
		return new LazyReact().of(array);
	}
	@Override
	protected <U> FutureStream<U> ofThread(U... array) {
		return new LazyReact(ThreadPools.getCommonFreeThread()).of(array);
	}

	@Override
	protected <U> FutureStream<U> react(Supplier<U>... array) {
		return LazyReact.parallelBuilder().ofAsync(array);

	}


}
