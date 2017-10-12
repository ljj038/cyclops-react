package com.aol.cyclops2.control.anym;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import cyclops.control.anym.Witness;
import org.junit.Before;
import org.junit.Test;

import cyclops.control.anym.AnyM;
import cyclops.control.Ior;
import com.aol.cyclops2.util.box.Mutable;

public class IorAnyMValueTest extends BaseAnyMValueTest<Witness.ior> {
    @Before
    public void setUp() throws Exception {
        just = AnyM.fromIor(Ior.primary(10));
        none = AnyM.fromIor(Ior.secondary(null));
    }
    @Test
    public void testPeek() {
        Mutable<Integer> capture = Mutable.of(null);

        just = just.peek(c->capture.set(c));
        
        
        just.get();
        assertThat(capture.get(),equalTo(10));
    }
}
