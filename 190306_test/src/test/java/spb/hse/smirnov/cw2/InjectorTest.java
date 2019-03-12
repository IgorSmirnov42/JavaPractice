package spb.hse.smirnov.cw2;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import spb.hse.smirnov.cw2.testClasses.*;

import java.util.Collections;
import java.util.List;


public class InjectorTest {

    @Test
    public void injectorShouldInitializeClassWithoutDependencies()
            throws Exception {
        Object object = Injector.initialize("spb.hse.smirnov.cw2.testClasses.ClassWithoutDependencies", Collections.emptyList());
        assertTrue(object instanceof ClassWithoutDependencies);
    }

    @Test
    public void injectorShouldInitializeClassWithOneClassDependency()
            throws Exception {
        Object object = Injector.initialize(
                "spb.hse.smirnov.cw2.testClasses.ClassWithOneClassDependency",
                Collections.singletonList("spb.hse.smirnov.cw2.testClasses.ClassWithoutDependencies")
        );
        assertTrue(object instanceof ClassWithOneClassDependency);
        ClassWithOneClassDependency instance = (ClassWithOneClassDependency) object;
        assertTrue(instance.dependency != null);
    }

    @Test
    public void injectorShouldInitializeClassWithOneInterfaceDependency()
            throws Exception {
        Object object = Injector.initialize(
                "spb.hse.smirnov.cw2.testClasses.ClassWithOneInterfaceDependency",
                Collections.singletonList("spb.hse.smirnov.cw2.testClasses.InterfaceImpl")
        );
        assertTrue(object instanceof ClassWithOneInterfaceDependency);
        ClassWithOneInterfaceDependency instance = (ClassWithOneInterfaceDependency) object;
        assertTrue(instance.dependency instanceof InterfaceImpl);
    }

    @Test
    public void injectorShouldThrowIfCycleDependency() throws Exception {
        assertThrows(InjectionCycleException.class, () -> Injector.initialize(
                "spb.hse.smirnov.cw2.testClasses.ClassWithObjectDependency",
                Collections.singletonList("spb.hse.smirnov.cw2.testClasses.ClassWithObjectDependency")
        ));
    }

    @Test
    public void injectorShouldThrowIfTwoOrMoreImplementations() {
        assertThrows(AmbiguousImplementationException.class, () -> Injector.initialize(
                "spb.hse.smirnov.cw2.testClasses.ClassWithObjectDependency",
                List.of("spb.hse.smirnov.cw2.testClasses.ClassWithObjectDependency",
                        "spb.hse.smirnov.cw2.testClasses.ClassWithObjectDependency")
        ));
    }

    @Test
    public void injectorShouldThrowIfImplementationWasNotFound() {
        assertThrows(ImplementationNotFoundException.class, () -> Injector.initialize(
                "spb.hse.smirnov.cw2.testClasses.ClassWithObjectDependency",
                Collections.emptyList()
        ));
    }

    @Test
    public void shouldCreateEachOnlyOneTime() throws Exception {
        CounterClass.clear();
        Object object = Injector.initialize(
                "spb.hse.smirnov.cw2.testClasses.ClassWithDependencyWithObject",
                Collections.singletonList("spb.hse.smirnov.cw2.testClasses.CounterClass")
        );
        assertTrue(object instanceof ClassWithDependencyWithObject);
        assertEquals(1, CounterClass.counter);
        CounterClass.clear();
    }

}