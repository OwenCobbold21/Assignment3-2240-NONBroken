// Minibus is a type of Vehicle that tracks whether it is wheelchair accessible

public class Minibus extends Vehicle implements Rentable {
    private boolean isAccessible;

    public Minibus(String make, String model, int year, boolean isAccessible) {
        super(make, model, year);
        this.isAccessible = isAccessible;
    }

    @Override
    public String getInfo() {
        return super.getInfo() + " | Accessible: " + (isAccessible ? "Yes" : "No");
    }

    @Override
    public void rentVehicle() {
        setStatus(VehicleStatus.Rented);
        System.out.println("Minibus " + getLicensePlate() + " has been rented.");
    }

    @Override
    public void returnVehicle() {
        setStatus(VehicleStatus.Available);
        System.out.println("Minibus " + getLicensePlate() + " has been returned.");
    }
}