package org.jpiccoli.injector;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Qualifier;
import javax.inject.Singleton;

/**
 * Main class of the dependency injection framework implementation.
 * Provides the methods inject and injectStatic for creating the
 * injected instances.
 * 
 * @author Piccoli
 *
 */
public class Injector {
	
	private static Set<Class<?>> staticInjectedClasses;
	
	private Map<Class<?>, Class<?>> implementationBindings;
	
	private Map<QualifierBindingKey<?, ?>, QualifierBindingValue<?>> qualifiersBindings;
	
	private Map<Class<?>, ScopeHandler> scopeHandlers;
	
	static {
		staticInjectedClasses = new HashSet<Class<?>>();
	}
	
	/**
	 * Constructor.
	 */
	public Injector() {
		implementationBindings = new ConcurrentHashMap<Class<?>, Class<?>>();
		qualifiersBindings = new ConcurrentHashMap<QualifierBindingKey<?, ?>, QualifierBindingValue<?>>();
		scopeHandlers = new ConcurrentHashMap<Class<?>, ScopeHandler>();
		scopeHandlers.put(Singleton.class, new SingletonScopeHandler());
	}
	
	/**
	 * Defines which class to instantiate when injecting an object of type "type".
	 * The class represented by "implementation" must extend or implement the specified
	 * type and cannot be abstract.
	 * @param type Type to be injected.
	 * @param implementation Class of objects to instantiate for the given type.
	 * 
	 * @throws IllegalArgumentException In case the "implementation" class is either not concrete
	 * or does not extend or implement the specified "type".
	 */
	public <T, S extends T> void addImplementationMapping(Class<T> type, Class<S> implementation) {
		if (!implementation.isInterface() && !Modifier.isAbstract(implementation.getModifiers()) && type.isAssignableFrom(implementation)) {
			implementationBindings.put(type, implementation);
		} else {
			throw new IllegalArgumentException("The \"implementation\" parameter must be a concrete class which implements or subclasses parameter \"type\"");
		}
	}
	
	/**
	 * Defines which class to instantiate when injecting an object of type "type" for a field
	 * of parameter annotated with the given "qualifier" annotation.
	 * @param qualifier Annotation annotated with the {@literal @}Qualifier annotation.
	 * @param type Type to be injected.
	 * @param implementation Class of objects to instantiate for the given type.
	 */
	public <Q, T, S extends T> void addQualifierBoundToImplementation(Class<Q> qualifier, Class<T> type, Class<S> implementation) {
		if (qualifier.isAnnotation() && qualifier.isAnnotationPresent(Qualifier.class)) {
			if (!implementation.isInterface() && !Modifier.isAbstract(implementation.getModifiers()) && type.isAssignableFrom(implementation)) {
				qualifiersBindings.put(new QualifierBindingKey<Q, T>(qualifier, type), new QualifierBoundToImplementation<T, S>(this, implementation));
			} else {
				throw new IllegalArgumentException("The \"implementation\" parameter must be a concrete class which implements or subclasses parameter \"type\"");
			}
		} else {
			throw new IllegalArgumentException("The \"qualifier\" parameter must be an annotation annotated with @Qualifier");
		}
	}
	
	/**
	 * Defines which class to instantiate when injecting an object of type "type" for a field
	 * or parameter annotated with the Named annotation.
	 * The class represented by "implementation" must extend or implement the specified
	 * type and cannot be abstract.
	 * @param type Type to be injected.
	 * @param name The "name" parameter of the Named annotation.
	 * @param implementation Class of objects to instantiate for the given type.
	 * 
	 * @throws IllegalArgumentException In case the "implementation" class is either not concrete
	 * or does not extend or implement the specified "type".
	 */
	public <T, S extends T> void addNamedBoundToImplementation(Class<T> type, String name, Class<S> implementation) {
		if (!implementation.isInterface() && !Modifier.isAbstract(implementation.getModifiers()) && type.isAssignableFrom(implementation)) {
			qualifiersBindings.put(new NamedBindingKey<T>(type, name), new QualifierBoundToImplementation<T, S>(this, implementation));
		} else {
			throw new IllegalArgumentException("The \"implementation\" parameter must be a concrete class which implements or subclasses parameter \"type\"");
		}
	}
	
	/**
	 * Defines which object to inject for a field or parameter of the given type which is annotated
	 * with the Named annotation.
	 * @param type Type to be injected.
	 * @param name The "name" parameter of the Named annotation.
	 * @param instance Instance to be injected.
	 */
	public <T> void addNamedBoundToInstance(Class<T> type, String name, T instance) {
		qualifiersBindings.put(new NamedBindingKey<T>(type, name), new QualifierBoundToInstance<T>(instance));
	}
	
	/**
	 * Retrieve the instance to be injected for the given class.
	 * @param c Class of the instance to be injected.
	 * @return The injected instance.
	 * 
	 * @throws ReflectiveOperationException
	 */
	public <T> T inject(Class<T> c) throws ReflectiveOperationException {
		return inject(c, null);
	}
	
	/**
	 * Retrieve the instance to be injected for the given class and
	 * parameterized type. The second parameter is necessary when
	 * creating instances of class Provider.
	 * @param c Class of the instance to be injected.
	 * @param t The parameterized type of the Provider class (if any).
	 * @return The injected instance.
	 * 
	 * @throws ReflectiveOperationException
	 */
	public <T> T inject(Class<T> c, Type t) throws ReflectiveOperationException {
		
		if (c.equals(Provider.class)) {
			// Providers are a special case. They must create injected instances of the
			// specified parameterized type, so they are treated differently from any
			// other type.
			return (T) new ProviderImplementation(this, getClassGenericTypeArgument(t));
		}
		
		ScopeHandler scopeHandler = getScopeHandler(c);
		T injected = null;
		
		try {
		
			if (scopeHandler != null) {
				// If the field or parameter is annotated with a scope
				// annotation, we try to get the instance from the scope
				// handler.
				injected = (T) scopeHandler.getValue(c);
				if (injected != null) {
					return injected;
				}
			}
			
			injected = createInstance(c);
		
		} finally {
			if (scopeHandler != null) {
				// Setting the created instance in the scope handler.
				scopeHandler.putValue(injected);
			}
		}
		
		return injected;
			
	}
	
	/**
	 * Inject the static members of the given class. The static members of parent classes are
	 * automatically injected.
	 * @param c Class whose static members will be injected.
	 * @throws ReflectiveOperationException
	 */
	public <T> void injectStatic(Class<T> c) throws ReflectiveOperationException {
		
		// Static members should be set only once. The synchronized block below
		// prevents two concurrent threads from initializing the same static fields
		// at the same time.
		synchronized(staticInjectedClasses) {
			if (!staticInjectedClasses.contains(c)) {
				List<Class<?>> classHierarchy = listClassHierarchy(c);
				Collections.reverse(classHierarchy);
				
				for (Class<?> clazz : classHierarchy) {
					Field[] fields = clazz.getDeclaredFields();
					for (Field field : fields) {
						if (field.isAnnotationPresent(Inject.class) && Modifier.isStatic(field.getModifiers())) {
							field.setAccessible(true);
							handleField(field, null);
						}
					}
					
					Method[] methods = clazz.getDeclaredMethods();
					for (Method method : methods) {
						if (method.isAnnotationPresent(Inject.class) && Modifier.isStatic(method.getModifiers())) {
							method.setAccessible(true);
							List<Object> methodParametersValues = handleExecutableParameters(method);
							method.invoke(null, methodParametersValues.toArray());
						}
					}
					staticInjectedClasses.add(clazz);
				}
			}
		}
	}
	
	/**
	 * Creates an instance of the given class.
	 * @param c Class of the object to be created.
	 * @return The created instance.
	 * @throws ReflectiveOperationException
	 */
	private <T> T createInstance(Class<T> c) throws ReflectiveOperationException {
		Constructor<T> constructor = selectConstructor(c);
		if (constructor == null) {
			throw new InstantiationException("No constructor found for type " + c.getName());
		}
		
		// Inject the static members of the class before the non-static ones.
		// I'm not sure if this is JSR-330 compliant, but it seems more reasonable
		// than doing the inverse.
		injectStatic(constructor.getDeclaringClass());
		
		List<Object> constructorParametersValues = handleExecutableParameters(constructor);
		constructor.setAccessible(true);
		T injected = constructor.newInstance(constructorParametersValues.toArray());
		
		List<Class<?>> classHierarchy = listClassHierarchy(constructor.getDeclaringClass());
		
		// Building the list of methods to be executed. Starting searching methods from the most
		// specialized class because we need to skip superclasses methods which are overridden
		// by subclasses.
		Set<String> skipMethodsSignatures = new HashSet<String>();
		Map<Class<?>, List<Method>> methodsToExecuteMap = new HashMap<Class<?>, List<Method>>();
		
		for (Class<?> clazz : classHierarchy) {
			Method[] methods = clazz.getDeclaredMethods();
			List<Method> methodsToExecuteList = new ArrayList<Method>();
			methodsToExecuteMap.put(clazz, methodsToExecuteList);
			for (Method method : methods) {
				String signature = buildMethodSignatureString(method);
				if (!skipMethodsSignatures.contains(signature) && method.isAnnotationPresent(Inject.class) && !Modifier.isStatic(method.getModifiers())) {
					methodsToExecuteList.add(method);
				}
				skipMethodsSignatures.add(signature);
			}
		}
		
		// Reverse the class hierarchy. Fields initialization and methods execution must be done
		// in top-down hierarchy order.
		Collections.reverse(classHierarchy);
		
		for (Class<?> clazz : classHierarchy) {
			// Initializing fields of the class.
			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {
				if (field.isAnnotationPresent(Inject.class) && !Modifier.isStatic(field.getModifiers())) {
					field.setAccessible(true);
					handleField(field, injected);
				}
			}
			// Executing methods of the class.
			List<Method> methodsToExecuteList = methodsToExecuteMap.get(clazz);
			for (Method method : methodsToExecuteList) {
				method.setAccessible(true);
				List<Object> methodParametersValues = handleExecutableParameters(method);
				method.invoke(injected, methodParametersValues.toArray());
			}
		}
		
		return injected;
	}
	
	/**
	 * List all the class hierarchy starting from class "c".
	 * @param c Class for which to list the parent classes.
	 * @return The list of classes in the class hierarchy starting
	 * from the class "c".
	 */
	private List<Class<?>> listClassHierarchy(Class<?> c) {
		List<Class<?>> classHierarchy = new ArrayList<Class<?>>();
		classHierarchy.add(c);
		Class<?> superClass = c.getSuperclass();
		while(superClass != null && !superClass.equals(Object.class)) {
			classHierarchy.add(superClass);
			superClass = superClass.getSuperclass();
		}
		return classHierarchy;
	}
	
	/**
	 * Gets the actual Class parameter from the specified type.
	 * @param t Type from which to obtain the Class parameter.
	 * @return The Class parameter from the specified type.
	 */
	private Class<?> getClassGenericTypeArgument(Type t) {
		ParameterizedType parameterizedType = (ParameterizedType) t;
		Class<?> classParam = (Class<?>) parameterizedType.getActualTypeArguments()[0];
		return classParam;
	}
	
	/**
	 * Handle the injection of the specified field of the given instance.
	 * @param field Field to be processed.
	 * @param instance Instance on which to inject the object.
	 * @throws ReflectiveOperationException
	 */
	private void handleField(Field field, Object instance) throws ReflectiveOperationException {
		Class<?> parameterClass = field.getType();
		Type parameterType = field.getGenericType();
		Object value = handleQualifiedAnnotations(field, parameterClass, parameterType);
		if (value != null) {
			field.set(instance, value);
		} else {
			field.set(instance, inject(parameterClass, parameterType));
		}
	}
	
	/**
	 * Handle any annotation which is a itself annotated with {@literal @}Qualifier on
	 * the given annotatedElement. If any such annotation is found, this method returns
	 * the injected object. Otherwise, it returns null.
	 * @param annotatedElement The element which may contain annotations.
	 * @param annotatedElementClass The class of the annotated element.
	 * @param annotatedElementGenericType The generic type of the annotated element (may be null)
	 * @return The injected object in case any {@literal @}Qualifier annotation is found on the annotatedElement
	 * or null otherwise.
	 * @throws ReflectiveOperationException
	 */
	private Object handleQualifiedAnnotations(AnnotatedElement annotatedElement, Class<?> annotatedElementClass, Type annotatedElementGenericType) throws ReflectiveOperationException {
		Annotation[] annotations = annotatedElement.getAnnotations();
		for (Annotation annotation : annotations) {
			if (annotation.annotationType().isAnnotationPresent(Qualifier.class)) {
				QualifierBindingKey<?, ?> key;
				Class<?> classParam = annotatedElementClass;
				boolean isProvider = annotatedElementClass.equals(Provider.class);
				if (isProvider) {
					classParam = getClassGenericTypeArgument(annotatedElementGenericType);
				}
				if (annotation.annotationType().equals(Named.class)) {
					Named named = (Named) annotation;
					key = new NamedBindingKey(classParam, named.value());
				} else {
					key = new QualifierBindingKey(annotation.annotationType(), classParam);
				}
				QualifierBindingValue<?> value = qualifiersBindings.get(key);
				if (value != null) {
					if (isProvider) {
						return new QualifiedProviderImplementation(value);
					} else {
						return value.getValue();
					}
				} else {
					throw new IllegalArgumentException("Qualifier not bound for annotation " + annotation.toString() + " on type " + annotatedElementClass.getName());
				}
			}
		}
		return null;
	}
	
	/**
	 * Handle the injection of the parameter list of a constructor or method.
	 * @param executable Method or constructor on which to inject parameters.
	 * @return The list of injected instances which can be used as parameters to invoke
	 * the specified method or constructor.
	 * @throws ReflectiveOperationException
	 */
	private List<Object> handleExecutableParameters(Executable executable) throws ReflectiveOperationException {
		List<Object> parametersValues = new LinkedList<>();
		if (executable.getParameterCount() > 0) {
			Parameter[] parameters = executable.getParameters();
			for (Parameter parameter : parameters) {
				Class<?> parameterClass = parameter.getType();
				Type parameterType = parameter.getParameterizedType();
				Object value = handleQualifiedAnnotations(parameter, parameterClass, parameterType);
				if (value != null) {
					parametersValues.add(value);
				} else {
					parametersValues.add(inject(parameterClass, parameterType));
				}
			}
		}
		return parametersValues;
	}
	
	/**
	 * Selects an appropriate constructor to create an injected instance
	 * of the given type. It may return null for interfaces which don't
	 * have a corresponding implementation defined with the "addImplementationMapping"
	 * method or classes which lacks both a null-args constructor and a constructor
	 * annotated with the Inject annotation.
	 * @param c Class of object to be created.
	 * @return The injected object.
	 */
	private <T, S extends T> Constructor<S> selectConstructor(Class<T> c) {

		Class<S> boundImplementation = (Class<S>) implementationBindings.get(c);
		if (boundImplementation != null) {
			return selectConstructor(boundImplementation);
		} else {
			Constructor<S> nullArgsConstructor = null;
			Constructor<?>[] constructors = c.getDeclaredConstructors();
			for (Constructor<?> constructor : constructors) {
				if (constructor.getParameterCount() == 0) {
					nullArgsConstructor = (Constructor<S>) constructor;
				}
				if (constructor.isAnnotationPresent(Inject.class)) {
					return (Constructor<S>) constructor;
				}
			}
			return nullArgsConstructor;
		}
		
	}
	
	public <T> ScopeHandler getScopeHandler(Class<T> c) {
		Annotation[] annotations = c.getAnnotations();
		for (Annotation annotation : annotations) {
			ScopeHandler handler = scopeHandlers.get(annotation.annotationType());
			if (handler != null) {
				return handler;
			}
		}
		return null;
	}
	
	/**
	 * Builds a signature string for the specified method.
	 * The returned string will be formatted according to the
	 * following rules:
	 * <ul>
	 * 	<li>public and protected methods: *.[method name]([param 1 class name], [param 2 class name], ...)</li>
	 * 	<li>package private methods: [package name].[method name]([param 1 class name], [param 2 class name], ...)</li>
	 * 	<li>private methods: [class name].[method name]([param 1 class name], [param 2 class name], ...)</li>
	 * </ul>
	 * 
	 * @param method Method for which to build the signature
	 * @return The signature of the given method
	 */
	private String buildMethodSignatureString(Method method) {
		StringBuilder signature = new StringBuilder();
		if (Modifier.isPrivate(method.getModifiers()) ) {
			signature.append(method.getDeclaringClass().getName());
			signature.append(".");
		} else if (!Modifier.isProtected(method.getModifiers()) && !Modifier.isPublic(method.getModifiers())) {
			signature.append(method.getDeclaringClass().getPackage().getName());
			signature.append(".");
		} else {
			signature.append("*.");
		}
		signature.append(method.getName() + "(");
		Parameter[] parameters = method.getParameters();
		for (int i = 0; i < parameters.length; i++) {
			if (i > 0) {
				signature.append(",");
			}
			signature.append(parameters[i].getType().getName());
		}
		signature.append(")");
		return signature.toString();
		
	}
	
}
