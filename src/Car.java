// Car extends Vehicle and implements Rentable

public class Car extends Vehicle implements Rentable {
    private int numSeats;

    // Constructor - passes make/model/year up to Vehicle, and saves numSeats here
    public Car(String make, String model, int year, int numSeats) {
        super(make, model, year); // "super" calls the Vehicle constructor
        this.numSeats = numSeats;
    }

    public int getNumSeats() {
        return numSeats;
    }

    // Adds seat count to the info string from Vehicle
    @Override
    public String getInfo() {
        return super.getInfo() + " | Seats: " + numSeats;
    }

    // These two methods are required because we implement Rentable
    @Override
    public void rentVehicle() {
        setStatus(VehicleStatus.Rented);
        System.out.println("Car " + getLicensePlate() + " has been rented.");
    }

    @Override
    public void returnVehicle() {
        setStatus(VehicleStatus.Available);
        System.out.println("Car " + getLicensePlate() + " has been returned.");
    }
}