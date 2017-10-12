package com.aol.cyclops2.control.anym;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import cyclops.control.anym.Witness;
import org.junit.Before;
import org.junit.Test;

import cyclops.control.anym.AnyM;
import cyclops.control.Try;
import com.aol.cyclops2.util.box.Mutable;

import java.util.NoSuchElementException;

public class TryAnyMValueTest extends BaseAnyMValueTest<Witness.tryType> {
    @Before
    public void setUp() throws Exception {
        just = AnyM.fromTry(Try.success(10));
        none = AnyM.fromTry(Try.failure(new NoSuchElementException()));
    }
    @Test
    public void testPeek() {
        Mutable<Integer> capture = Mutable.of(null);
        just = just.peek(c->capture.set(c));
        
        
        just.get();
        assertThat(capture.get(),equalTo(10));
    }
}
