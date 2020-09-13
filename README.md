# dependency-injection
Lightweight implementation of the JSR-330 specification. It is fully compliant and passes the TCK.
Sample usage:

```java
  Injector injector = new Injector();
  injector.addImplementationMapping(Car.class, Convertible.class);                          // Binding an interface to its implementation 
  injector.addQualifierBoundToImplementation(Drivers.class, Seat.class, DriversSeat.class); // Binding a qualified type to its implementation
  injector.addNamedBoundToImplementation(Tire.class, "spare", SpareTire.class);             // Binding a named type to its implementation
  SpareTire spareInstance = new SpareTire(new FuelTank(), new FuelTank());
  injector.addNamedBoundToInstance(Tire.class, "spare-instance", spareInstance);            // Binding a named type to a single instance
  Car car = injector.inject(Car.class);
```
