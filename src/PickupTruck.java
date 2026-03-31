// PickupTruck is a type of Vehicle with cargo size and trailer info

public class PickupTruck extends Vehicle implements Rentable {
    private double cargoSize;
    private boolean hasTrailer;

    public PickupTruck(String make, String model, int year, double cargoSize, boolean hasTrailer) {
        super(make, model, year);
        // Cargo size must be a positive number or we throw an error right away
        if (cargoSize <= 0) throw new IllegalArgumentException("Cargo size must be > 0");
        this.cargoSize = cargoSize;
        this.hasTrailer = hasTrailer;
    }

    public double getCargoSize() { return cargoSize; }
    public boolean hasTrailer()  { return hasTrailer; }

    @Override
    public String getInfo() {
        return super.getInfo() + " | Cargo Size: " + cargoSize + " | Has Trailer: " + (hasTrailer ? "Yes" : "No");
    }

    @Override
    public void rentVehicle() {
        setStatus(VehicleStatus.Rented);
        System.out.println("Pickup Truck " + getLicensePlate() + " has been rented.");
    }

    @Override
    public void returnVehicle() {
        setStatus(VehicleStatus.Available);
        System.out.println("Pickup Truck " + getLicensePlate() + " has been returned.");
    }
}