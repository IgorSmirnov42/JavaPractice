package spb.hse.smirnov.myjunit.classes;

import spb.hse.smirnov.myjunit.*;

public class FullyAnnotatedClass {
    @Before
    void a() {
    }

    @After
    void foo() {
    }

    @BeforeClass
    void bar() {
    }

    @AfterClass
    void baz() {
    }

    @Test
    void test() {

    }
}
