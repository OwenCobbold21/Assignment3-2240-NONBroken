// This is an interface - it forces any class that uses it to have these two methods
// Car, Minibus, and PickupTruck all "implement" this interface

public interface Rentable {
    void rentVehicle();   // must be able to rent out the vehicle
    void returnVehicle(); // must be able to return the vehicle
}