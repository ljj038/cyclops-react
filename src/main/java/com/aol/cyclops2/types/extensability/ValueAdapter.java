package com.aol.cyclops2.types.extensability;

import com.aol.cyclops2.types.anyM.AnyMValue;
import cyclops.control.Option;
import cyclops.control.anym.WitnessType;

import java.util.function.Function;

public interface ValueAdapter<W extends WitnessType<W>> extends FunctionalAdapter<W> {

    default <R> R visit(Function<? super FunctionalAdapter<W>,? extends R> fn1, Function<? super  ValueAdapter<W>, ? extends R> fn2){
        return fn2.apply(this);
    }
    <T> Option<T> get(AnyMValue<W,T> t);
}
