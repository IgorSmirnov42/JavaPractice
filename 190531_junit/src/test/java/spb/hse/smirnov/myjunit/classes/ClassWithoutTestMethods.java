package spb.hse.smirnov.myjunit.classes;

import spb.hse.smirnov.myjunit.After;
import spb.hse.smirnov.myjunit.AfterClass;
import spb.hse.smirnov.myjunit.Before;
import spb.hse.smirnov.myjunit.BeforeClass;

public class ClassWithoutTestMethods {
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
}
