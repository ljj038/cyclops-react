package cyclops;

import cyclops.collections.*;
import cyclops.collections.immutable.*;
import org.junit.Test;
import org.pcollections.*;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

/**
 * Created by johnmcclean on 17/05/2017.
 */
public class ConvertersTest {
    @Test
    public void convert(){
        LinkedList<Integer> list1 = ListX.of(1,2,3).to(Converters::LinkedList);
        ArrayList<Integer> list2 = ListX.of(1,2,3).to(Converters::ArrayList);


        assertThat(list1,equalTo(list2));
        assertThat(list1,equalTo(ListX.of(1,2,3)));

        PStack<Integer> pstack = PStackX.of(1,2,3).to(Converters::PStack);
        PVector<Integer> pvector = PVectorX.of(1,2,3).to(Converters::PVector);
        PSet<Integer> pset = PSetX.of(1,2,3).to(Converters::PSet);
        POrderedSet<Integer> pOrderedSet = POrderedSetX.of(1,2,3).to(Converters::POrderedSet);
        PBag<Integer> pBag = PBagX.of(1,2,3).to(Converters::PBag);
        PQueue<Integer> pQueue = PQueueX.of(1,2,3).to(Converters::PQueue);
        PMap<Integer,Integer> pMap = PMapXs.of(1,2).to(Converters::PMap);

        HashSet<Integer> set = SetX.of(1,2,3).to(Converters::HashSet);
        ArrayDeque<Integer> deque = DequeX.of(1,2,3).to(Converters::ArrayDeque);
        ArrayBlockingQueue<Integer> queue = QueueX.of(1,2,3).to(Converters::ArrayBlockingQueue);
        TreeSet<Integer> tset = SortedSetX.of(1,2,3).to(Converters::TreeSet);
        HashMap<Integer,Integer> map = MapXs.of(1,2).to(Converters::HashMap);

        assertThat(pstack,equalTo(pvector));
        assertThat(pset,equalTo(pOrderedSet));
        assertThat(pset,equalTo(pBag.stream().collect(Collectors.toSet())));
        assertThat(pQueue.stream().collect(Collectors.toList()), equalTo(list1));

        assertThat(pMap,equalTo(map));
        assertThat(set,equalTo(tset));
        assertThat(deque.stream().collect(Collectors.toList()),equalTo(list1));
        assertThat(queue.stream().collect(Collectors.toList()),equalTo(list1));

    }
}