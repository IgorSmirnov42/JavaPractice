package spb.hse.smirnov.cw2;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.*;


public class Injector {

    private static Map<Class<?>, Boolean> checkedDependency = new HashMap<>();
    private static Map<Class<?>, Object>  madeInstances = new HashMap<>();

    /**
     * Create and initialize object of `rootClassName` class using classes from
     * `implementationClassNames` for concrete dependencies.
     */
    public static Object initialize(@NotNull String rootClassName, @NotNull List<String> implementationClassNames) throws Exception {
        Class<?> classToInject = Class.forName(rootClassName);
        var classes = new ArrayList<Class<?>>();
        for (var className : implementationClassNames) {
            classes.add(Class.forName(className));
        }
        try {
            checkDependencies(classToInject, classes);
        } finally {
            checkedDependency.clear();
            madeInstances.clear();
        }

        return makeInstance(classToInject, classes);
    }

    /** Recursively creates instance of clazz using only implementationClasses */
    private static Object makeInstance(@NotNull Class<?> clazz, @NotNull List<Class<?>> implementationClasses) throws IllegalAccessException, InvocationTargetException, InstantiationException, AmbiguousImplementationException {
        if (madeInstances.containsKey(clazz)) {
            return madeInstances.get(clazz);
        }

        var listOfArgs = new ArrayList<>();
        for (var constructorParameter : getConstructorParameters(clazz)) {
            for (var classToConstruct : implementationClasses) {
                if (constructorParameter.isAssignableFrom(classToConstruct)) {
                    listOfArgs.add(makeInstance(classToConstruct, implementationClasses));
                }
            }
        }

        Object object = clazz.getConstructors()[0].newInstance(listOfArgs.toArray());
        madeInstances.put(clazz, object);
        return object;
    }

    /**
     * Checks if class object can be constructed only in one way
     * Otherwise throws exceptions that I have no time to document
     */
    private static void checkDependencies(@NotNull Class<?> clazz, @NotNull List<Class<?>> implementationClasses) throws InjectionCycleException, AmbiguousImplementationException, ImplementationNotFoundException {
        if (checkedDependency.containsKey(clazz)) {
            if (!checkedDependency.get(clazz)) {
                throw new InjectionCycleException();
            } else {
                return;
            }
        }

        checkedDependency.put(clazz, false);

        for (var constructorParameter : getConstructorParameters(clazz)) {
            int suitableClassesCounter = 0;
            for (var classToConstruct : implementationClasses) {
                if (constructorParameter.isAssignableFrom(classToConstruct)) {
                    ++suitableClassesCounter;
                }
            }
            if (suitableClassesCounter == 1) {
                for (var classToConstruct : implementationClasses) {
                    if (constructorParameter.isAssignableFrom(classToConstruct)) {
                        checkDependencies(classToConstruct, implementationClasses);
                    }
                }
            } else if (suitableClassesCounter > 1) {
                throw new AmbiguousImplementationException();
            } else {
                throw new ImplementationNotFoundException();
            }
        }

        checkedDependency.put(clazz, true);
    }

    private static Class<?>[] getConstructorParameters(@NotNull Class<?> clazz) {
        var constructors = clazz.getConstructors();
        if (constructors.length != 1) {
            throw new IllegalArgumentException("Class doesn't have exactly one constructor");
        }
        var constructor = constructors[0];
        return constructor.getParameterTypes();
    }

}

