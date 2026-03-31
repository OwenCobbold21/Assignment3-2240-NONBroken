// This is the base Vehicle class - all vehicle types (Car, Minibus, PickupTruck) extend this

public abstract class Vehicle {
    private String licensePlate;
    private String make;
    private String model;
    private int year;
    private VehicleStatus status;

    // These are all the possible statuses a vehicle can have
    public enum VehicleStatus { Available, Held, Rented, UnderMaintenance, OutOfService }

    // Main constructor - runs when you create any vehicle subclass
    public Vehicle(String make, String model, int year) {
        // Use the capitalize helper to fix the make (e.g. "tOYOTA" becomes "Toyota")
        if (make == null || make.isEmpty())
            this.make = null;
        else
            this.make = capitalize(make);

        // Same thing for model
        if (model == null || model.isEmpty())
            this.model = null;
        else
            this.model = capitalize(model);

        this.year = year;
        this.status = VehicleStatus.Available; // new vehicles start as available
        this.licensePlate = null;
    }

    // No-argument constructor - just calls the main one with empty values
    public Vehicle() {
        this(null, null, 0);
    }

    // This helper method makes the first letter uppercase and everything else lowercase
    // Example: "fORD" becomes "Ford"
    // This was pulled out of the constructor to avoid repeating the same formatting logic twice
    private String capitalize(String input) {
        if (input == null || input.isEmpty()) return input;
        // Take the first character, uppercase it, then add the rest in lowercase
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }

    // Checks if a license plate follows the right format: 3 uppercase letters then 3 digits
    // Example: "AAA111" is valid, "AA111" and "AAA1000" and null are not
    private boolean isValidPlate(String plate) {
        if (plate == null || plate.isEmpty()) return false;
        // matches() checks the string against a pattern (called a regex)
        // [A-Z]{3} means exactly 3 uppercase letters, [0-9]{3} means exactly 3 digits
        return plate.matches("[A-Z]{3}[0-9]{3}");
    }

    // Sets the license plate - but only if it passes the format check
    public void setLicensePlate(String plate) {
        // Convert to uppercase first so "aaa111" becomes "AAA111"
        String upperPlate = (plate == null) ? null : plate.toUpperCase();
        if (!isValidPlate(upperPlate)) {
            // Throw an error if the plate is not valid - this stops bad data from getting in
            throw new IllegalArgumentException("Invalid license plate: " + plate
                    + ". Must be 3 letters followed by 3 digits (e.g. AAA111).");
        }
        this.licensePlate = upperPlate;
    }

    // Lets other classes change the status (e.g. from Available to Rented)
    public void setStatus(VehicleStatus status) {
        this.status = status;
    }

    // Getter methods - let other classes read these private fields
    public String getLicensePlate() { return licensePlate; }
    public String getMake()         { return make; }
    public String getModel()        { return model; }
    public int getYear()            { return year; }
    public VehicleStatus getStatus(){ return status; }

    // Returns a formatted string of vehicle details - subclasses add extra info on top
    public String getInfo() {
        return "| " + licensePlate + " | " + make + " | " + model + " | " + year + " | " + status + " |";
    }
}