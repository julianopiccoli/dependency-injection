package org.jpiccoli.injector.tests;

import org.jpiccoli.injector.Injector;

public class Test {

	public static void main(String[] args) throws ReflectiveOperationException {
		testInjection();
		testProviderInjection();
		testImplementationMapping();
		testImplementationProviderMapping();
		testStaticInjection();
		testNamedInjection();
		testSingletonInjection();
		testCustomProviderInjection();
	}
	
	private static void testInjection() throws ReflectiveOperationException {
		Injector injector = new Injector();
		A a = injector.inject(A.class);
		assert a.getB1() != null;
		assert a.getB2() != null;
		assert a.getB3() != null;
		assert a.getB1().getC() != null;
		assert a.getB2().getC() != null;
		assert a.getB3().getC() != null;
		System.out.println("Test 1 OK");
	}
	
	private static void testProviderInjection() throws ReflectiveOperationException {
		Injector injector = new Injector();
		D d = injector.inject(D.class);
		assert d.getProviderA1().get() != null;
		assert d.getProviderA2().get() != null;
		assert d.getProviderA3().get() != null;
		assert d.getProviderA1().get().getB1() != null;
		assert d.getProviderA2().get().getB1() != null;
		assert d.getProviderA3().get().getB1() != null;
		System.out.println("Test 2 OK");
	}
	
	private static void testImplementationMapping() throws ReflectiveOperationException {
		Injector injector = new Injector();
		injector.addImplementationMapping(AInterface.class, AImpl.class);
		AImplContainer testImplContainer = injector.inject(AImplContainer.class);
		assert testImplContainer.getTest1() != null;
		assert testImplContainer.getTest2() != null;
		assert testImplContainer.getTest3() != null;
		testImplContainer.getTest1().test();
		testImplContainer.getTest2().test();
		testImplContainer.getTest3().test();
		System.out.println("Test 3 OK");
	}
	
	private static void testImplementationProviderMapping() throws ReflectiveOperationException {
		Injector injector = new Injector();
		injector.addImplementationMapping(AInterface.class, AImpl.class);
		AImplProviderContainer testImplContainer = injector.inject(AImplProviderContainer.class);
		assert testImplContainer.getTest1() != null;
		assert testImplContainer.getTest2() != null;
		assert testImplContainer.getTest3() != null;
		testImplContainer.getTest1().get().test();
		testImplContainer.getTest2().get().test();
		testImplContainer.getTest3().get().test();
		System.out.println("Test 4 OK");
	}
	
	public static void testStaticInjection() throws ReflectiveOperationException {
		Injector injector = new Injector();
		E e1 = injector.inject(E.class);
		E e2 = injector.inject(E.class);
		assert e1 != null;
		assert e2 != null;
		assert E.a != null;
		assert E.b != null;
		assert E.providerA != null;
		assert E.providerB != null;
		System.out.println("Test 5 OK");
	}
	
	public static void testNamedInjection() throws ReflectiveOperationException {
		Injector injector = new Injector();
		injector.addNamedBoundToInstance(A.class, "asingleton", new A(new B()));
		injector.addNamedBoundToImplementation(AInterface.class, "aimpl", AImpl.class);
		F f = injector.inject(F.class);
		assert f.getA1() == f.getA2();
		assert f.getA2() == f.getA3();
		assert f.getA1() != f.getA4();
		assert f.getaInterface1() != null;
		assert f.getaInterface2() != null;
		assert f.getaInterface3() != null;
		f.getaInterface1().test();
		f.getaInterface2().test();
		f.getaInterface3().test();
		System.out.println("Test 6 OK");
	}
	
	public static void testSingletonInjection() throws ReflectiveOperationException {
		
		Injector injector = new Injector();
		H h = injector.inject(H.class);
		assert h.getG1() == h.getG2();
		assert h.getG2() == h.getG3();
		assert h.getG1() == injector.inject(G.class);
		System.out.println("Test 7 OK");
		
	}
	
	public static void testCustomProviderInjection() throws ReflectiveOperationException {
		
		Injector injector = new Injector();
		CustomProvider customProvider = new CustomProvider();
		injector.addProviderBinding(C.class, customProvider);
		C instance = injector.inject(C.class);
		assert instance != null;
		assert customProvider.isProviderCalled();
		System.out.println("Test 8 OK");
		
	}
	
}
