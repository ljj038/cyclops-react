package com.aol.cyclops.types;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import com.aol.cyclops.Monoid;
import com.aol.cyclops.Reducer;
import com.aol.cyclops.control.Eval;
import com.aol.cyclops.control.FutureW;
import com.aol.cyclops.control.Ior;
import com.aol.cyclops.control.LazyReact;
import com.aol.cyclops.control.Matchable;
import com.aol.cyclops.control.Maybe;
import com.aol.cyclops.control.ReactiveSeq;
import com.aol.cyclops.control.SimpleReact;
import com.aol.cyclops.control.Try;
import com.aol.cyclops.control.Xor;
import com.aol.cyclops.control.Matchable.CheckValue1;
import com.aol.cyclops.data.LazyImmutable;
import com.aol.cyclops.data.Mutable;
import com.aol.cyclops.data.collections.extensions.persistent.PBagX;
import com.aol.cyclops.data.collections.extensions.persistent.POrderedSetX;
import com.aol.cyclops.data.collections.extensions.persistent.PQueueX;
import com.aol.cyclops.data.collections.extensions.persistent.PSetX;
import com.aol.cyclops.data.collections.extensions.persistent.PStackX;
import com.aol.cyclops.data.collections.extensions.persistent.PVectorX;
import com.aol.cyclops.data.collections.extensions.standard.DequeX;
import com.aol.cyclops.data.collections.extensions.standard.ListX;
import com.aol.cyclops.data.collections.extensions.standard.QueueX;
import com.aol.cyclops.data.collections.extensions.standard.SetX;
import com.aol.cyclops.data.collections.extensions.standard.SortedSetX;

import com.aol.cyclops.types.futurestream.LazyFutureStream;
import com.aol.cyclops.types.futurestream.SimpleReactStream;
import com.aol.cyclops.types.stream.reactive.ValueSubscriber;
import com.aol.cyclops.util.function.Predicates;

import lombok.AllArgsConstructor;

public interface Value<T> extends Supplier<T>, 
                                  Foldable<T>, 
                                  Matchable<T>, 
                                  Convertable<T>,
                                  Publisher<T>,
                                  Predicate<T>{
    
   
   
    default <R> R visit(Function<? super T,? extends R> present,Supplier<? extends R> absent){
        return toMaybe().visit(present, absent);
    }

    default boolean test(T t){
        if(!(t instanceof Value))
            return Predicates.eqv(Maybe.ofNullable(t)).test(this);
        else
            return Predicates.eqv((Value)t).test(this);
            
    }
    default ValueSubscriber<T> newSubscriber(){
        return ValueSubscriber.subscriber();
    }
	@Override
    default void subscribe(Subscriber<? super T> sub) {
	     sub.onSubscribe(new Subscription(){
	            
            AtomicBoolean running =  new AtomicBoolean(true);
             
             @Override
             public void request(long n) {
                 
                 if(n<1){
                     sub.onError(new IllegalArgumentException("3.9 While the Subscription is not cancelled, Subscription.request(long n) MUST throw a java.lang.IllegalArgumentException if the argument is <= 0."));
                 }
                
                 if(!running.compareAndSet(true, false)){
                     
                     return;
                 }
                try {
                    
                    sub.onNext(get());

                } catch (Throwable t) {
                    sub.onError(t);

                }
                try {
                    sub.onComplete();

                } finally {
                    

                }
                 
             }
             @Override
             public void cancel() {
                
                 running.set(false);
                 
             }
             
         });
        
    }
    public static <T> Value<T> of(Supplier<T> supplier){
		 return new ValueImpl<T>(supplier);
	 }
	 @AllArgsConstructor
	 public static class ValueImpl<T> implements Value<T>{
		 private final Supplier<T> delegate;
		 
		 public T get(){
			 return delegate.get();
		 }

		

		@Override
		public Iterator<T> iterator() {
			return stream().iterator();
		}
	 }
	 
	 default <R> R convertTo(Function<? super Maybe<? super T>,? extends R> convertTo){
		 return convertTo.apply(this.toMaybe());
	 }
	 default<R> FutureW<R> convertToAsync(Function<? super CompletableFuture<? super T>,? extends CompletableFuture<R>> convertTo){
		 CompletableFuture<T> future =  this.toCompletableFuture();
		 return FutureW.of(future.<R>thenCompose(t->convertTo.apply(future)));
	 }
	 
	default ReactiveSeq<T> stream() {
			return ReactiveSeq.of(Try.withCatch(()->get(),NoSuchElementException.class)).filter(Try::isSuccess).map(Try::get);
	}
	 /**
	  * @return matchable
	  */
	 default T getMatchable(){
		return get();
	 }
	 default ListX<?> unapply(){
		 return toListX();
	 }
	 default ReactiveSeq<T> iterate(UnaryOperator<T> fn){
			return ReactiveSeq.iterate(get(),fn);
	 }
	 default ReactiveSeq<T> generate(){
		return ReactiveSeq.generate(this);
	 }
	 default <E> E mapReduce(Reducer<E> monoid){
		 return monoid.mapReduce(toStream());
	 }
	 default T fold(Monoid<T> monoid){
		 return monoid.reduce(toStream());
	 }
	 default  T fold(T identity,BinaryOperator<T> accumulator){
	     Optional<T> opt = toOptional();
	     if(opt.isPresent())
	         return accumulator.apply(identity, get());
	     return identity;
	 }
	 default LazyImmutable<T> toLazyImmutable(){
		 return LazyImmutable.of(get());
	 }
	 default Mutable<T> toMutable(){
		 return Mutable.of(get());
	 }
	 default Xor<?,T> toXor(){
	     if(this instanceof Xor)
	         return (Xor)this;
	     Optional<T> o = toOptional();
	     return o.isPresent() ? Xor.primary(o.get()) : Xor.secondary(new NoSuchElementException());
		
	 }
	 
	 default Try<T,NoSuchElementException> toTry(){
	    return toXor().visit(secondary->Try.failure(new NoSuchElementException()),
	                         primary->Try.success(primary));
		
	 }
	 default <X extends Throwable> Try<T,X> toTry(Class<X>... classes){
		 return Try.withCatch( ()->get(),classes);
	 }
	 default   Ior<?,T> toIor(){
	     if(this instanceof Ior)
             return (Ior)this;
         Optional<T> o = toOptional();
         return o.isPresent() ? Ior.primary(o.get()) : Ior.secondary(null);
	 }
	 
	 default Eval<T> toEvalNow(){
		 return Eval.now(get());
	 }
	 default Eval<T> toEvalLater(){
		 return Eval.later(this);
	 }
	 default Eval<T> toEvalAlways(){
		 return Eval.always(this);
	 }
	 default Maybe<T> toMaybe(){
		 return Maybe.fromOptional(toOptional());
	 }
	 default ListX<T> toListX(){
		 return ListX.fromIterable(toList());
	 }
	 default SetX<T> toSetX(){
		 return SetX.fromIterable(toList());
	 }
	 default SortedSetX<T> toSortedSetX(){
		 return SortedSetX.fromIterable(toList());
	 }
	 default QueueX<T> toQueueX(){
		 return QueueX.fromIterable(toList());
	 }
	 default DequeX<T> toDequeX(){
		 return DequeX.fromIterable(toList());
	 }
	 default PStackX<T> toPStackX(){
		 return PStackX.fromCollection(toList());
	 }
	 default PVectorX<T> toPVectorX(){
		 return PVectorX.fromCollection(toList());
	 }
	 default PQueueX<T> toPQueueX(){
		 return PQueueX.fromCollection(toList());
	 }
	 default PSetX<T> toPSetX(){
		 return PSetX.fromCollection(toList());
	 }
	 default POrderedSetX<T> toPOrderedSetX(){
		 return POrderedSetX.fromCollection(toList());
	 }
	 default PBagX<T> toPBagX(){
		 return PBagX.fromCollection(toList());
	 }
	 default String mkString(){
		 Optional<T> opt = this.toOptional();
		 if(opt.isPresent())
			 return this.getClass().getSimpleName()+"[" + opt.get() + "]";
		 return this.getClass().getSimpleName()+"[]";
	 }

	default LazyFutureStream<T> toFutureStream(LazyReact reactor) {
		return reactor.ofAsync(this);
	}

	default LazyFutureStream<T> toFutureStream() {
		return new LazyReact().ofAsync(this);
	}

	default SimpleReactStream<T> toSimpleReact(SimpleReact reactor) {
		return reactor.ofAsync(this);
	}

	default SimpleReactStream<T> toSimpleReact() {
		return new SimpleReact().ofAsync(this);
	}
	
}
