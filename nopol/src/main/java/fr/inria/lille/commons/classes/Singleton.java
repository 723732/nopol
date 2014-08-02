package fr.inria.lille.commons.classes;

import static java.lang.String.format;

import java.util.Map;

import fr.inria.lille.commons.collections.MapLibrary;

public class Singleton {

	public static <T> T of(Class<T> theClass) {
		T instance = (T) singletons().get(theClass);
		if (instance == null) {
			return createSingleton(theClass);
		}
		return instance;
	}
	
	private static <T> T createSingleton(Class<T> theClass) {
		try {
			T firstInstance = (T) theClass.getMethod("firstInstance").invoke(theClass);
			singletons().put(theClass, firstInstance);
			return firstInstance;
		} catch (Exception exception) {
			exception.printStackTrace();
			throw new RuntimeException(format("Unable to create singleton for %s. The class must implement #firstInstance() method", theClass.toString()));
		}
	}
	
	private static Map<Class<?>, Object> singletons() {
		if (singletons == null) {
			singletons = MapLibrary.newHashMap();
		}
		return singletons;
	}
	
	private static Map<Class<?>, Object> singletons;
}
