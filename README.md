# dependency-injection
Simple and partial implementation on JSR-330. The annotations @Inject, @Named and @Singleton and the Provider interface, which are defined in the specification, are implemented. Sample usage:

```java
  Injector injector = new Injector();
  MyImplementation myImplementation = injector.inject(MyImplementation.class);
```
