package org.acme.quickstart;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.inject.Inject;

import org.graalvm.collections.Pair;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

/**
 * OldFashionPoundServiceTest
 */
@QuarkusTest
public class OldFashionPoundServiceTest {

    @Inject
    OldFashionPoundService service;

    @Test
    public void toStringTest() {
        assertEquals("5p 17s 8d", new OldFashionPound(5, 17, 8).toString());
        assertEquals("3p 4s 10d", new OldFashionPound(3, 4, 10).toString());
    }

    @Test
    public void sumTest() {
        OldFashionPound result = service.sum(new OldFashionPound(5, 17, 8), new OldFashionPound(3, 4, 10));

        assertEquals(9, result.sterline);
        assertEquals(2, result.scellini);
        assertEquals(6, result.pence);
    }

    @Test
    public void differenceTest() {
        OldFashionPound result = service.difference(new OldFashionPound(5, 17, 8), new OldFashionPound(3, 4, 10));

        assertEquals(2, result.sterline);
        assertEquals(12, result.scellini);
        assertEquals(10, result.pence);
    }

    @Test
    public void multiplyTest() {
        OldFashionPound result = service.multiply(new OldFashionPound(5, 17, 8), 2);

        assertEquals(11, result.sterline);
        assertEquals(15, result.scellini);
        assertEquals(4, result.pence);
    }

    @Test
    public void divideTest() {
        Pair<OldFashionPound, OldFashionPound> tuple1 = service.divide(new OldFashionPound(5, 17, 8), 3);

        OldFashionPound result1 = tuple1.getLeft();
        assertEquals(1, result1.sterline);
        assertEquals(19, result1.scellini);
        assertEquals(2, result1.pence);

        OldFashionPound rest1 = tuple1.getRight();
        assertEquals(0, rest1.sterline);
        assertEquals(0, rest1.scellini);
        assertEquals(2, rest1.pence);

        Pair<OldFashionPound, OldFashionPound> tuple2 = service.divide(new OldFashionPound(18, 16, 1), 15);

        OldFashionPound result2 = tuple2.getLeft();
        assertEquals(1, result2.sterline);
        assertEquals(5, result2.scellini);
        assertEquals(0, result2.pence);

        OldFashionPound rest2 = tuple2.getRight();
        assertEquals(0, rest2.sterline);
        assertEquals(1, rest2.scellini);
        assertEquals(1, rest2.pence);
    }
}
