package cyclops.data.basetests;

import cyclops.collectionx.AbstractIterableXTest;
import cyclops.collectionx.mutable.ListX;
import cyclops.companion.Monoids;
import cyclops.data.*;
import cyclops.reactive.ReactiveSeq;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public abstract class BaseImmutableSortedSetTest extends BaseImmutableSetTest {

    protected abstract <T> ImmutableSortedSet<T> fromStream(Stream<T> s);

    @Override
    public abstract <T> ImmutableSortedSet<T> of(T... values);

    @Test
    public void testMapA(){
        assertThat(of(1,2,3).map(i->i*2),equalTo(HashSet.of(2,4,6)));
        assertThat(this.<Integer>empty().map(i->i*2),equalTo(HashSet.empty()));
    }
    @Test
    public void testFlatMapA(){
        assertThat(of(1,2,3).flatMap(i-> of(i*2)),equalTo(HashSet.of(2,4,6)));
        assertThat(this.<Integer>empty().concatMap(i-> of(i*2)),equalTo(HashSet.empty()));
    }

    @Test
    public void testFoldRightA(){
        assertThat(fromStream(ReactiveSeq.range(0,100_000)).foldRight(Monoids.intSum),equalTo(704982704));
    }


    @Test
    public void forEach2Filter() {

        assertThat(of(1, 2, 3).forEach2(a -> Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10), (a , b) -> a > 2 && b < 8,
                (a ,b) -> a + b).toList().size(), equalTo(Arrays.asList(3, 4, 5, 6, 7, 8, 9, 10).size()));
    }
    @Test
    public void testCycleTimesNoOrder() {
        assertEquals(2,of(1, 2).cycle(3).toListX().size());

    }

    @Test
    public void permuations3() {
        System.out.println(of(1, 2, 3).permutations().map(s->s.toList()).toList());
        Seq<List<Integer>> x = of(1, 2, 3).permutations().map(s -> s.toList()).seq();


        assertTrue(x.containsValue(ListX.of(1,2,3)));
        assertTrue(x.containsValue(ListX.of(3,2,1)));
        assertTrue(x.containsValue(ListX.of(2,1,3)));
        assertTrue(x.containsValue(ListX.of(2,3,1)));
        assertTrue(x.containsValue(ListX.of(3,1,2)));
        assertTrue(x.containsValue(ListX.of(1,3,2)));
    }
    @Test
    public void batchWhileCollection(){
        assertThat(of(1,2,3,4,5,6)
                .groupedWhile(i->i%3!=0,()->new ArrayList<>())
                .toList().size(),equalTo(2));
        ImmutableSet<List<Integer>> x = of(1, 2, 3, 4, 5, 6)
                .groupedWhile(i -> i % 3 != 0, () -> new ArrayList<>());

        ImmutableList<List<Integer>> l = x.seq();

        assertTrue(l.containsValue(ListX.of(1,2,3)));
        assertTrue(l.containsValue(ListX.of(4,5,6)));

    }
}
