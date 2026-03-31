// RentalSystem is the "brain" of the program - it manages vehicles, customers, and rentals
// It uses the Singleton design pattern so only ONE instance can ever exist at a time

import java.util.List;
import java.util.ArrayList;
import java.time.LocalDate;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;

public class RentalSystem {

    // This holds the single instance - "static" means it belongs to the class, not one object
    private static RentalSystem instance;

    private List<Vehicle> vehicles = new ArrayList<>();
    private List<Customer> customers = new ArrayList<>();
    private RentalHistory rentalHistory = new RentalHistory();

    // The constructor is PRIVATE so nobody outside this class can do "new RentalSystem()"
    // That is the whole point of Singleton - only one instance allowed
    private RentalSystem() {
        loadData(); // load any saved data from files when the program starts
    }

    // This is how you get the instance - if it doesn't exist yet, create it
    // If it already exists, just return it (same object every time)
    public static RentalSystem getInstance() {
        if (instance == null) {
            instance = new RentalSystem(); // only ever created once
        }
        return instance;
    }

    // Adds a vehicle - first checks if a vehicle with the same plate already exists
    // Returns true if the vehicle was added, false if it was a duplicate
    public boolean addVehicle(Vehicle vehicle) {
        if (findVehicleByPlate(vehicle.getLicensePlate()) != null) {
            // Already exists - don't add it
            System.out.println("Error: Vehicle with plate " + vehicle.getLicensePlate() + " already exists.");
            return false;
        }
        vehicles.add(vehicle);
        saveVehicle(vehicle); // write to file right away
        return true;
    }

    // Adds a customer - first checks if someone with the same ID already exists
    // Returns true if added, false if duplicate
    public boolean addCustomer(Customer customer) {
        if (findCustomerById(customer.getCustomerId()) != null) {
            System.out.println("Error: Customer with ID " + customer.getCustomerId() + " already exists.");
            return false;
        }
        customers.add(customer);
        saveCustomer(customer); // write to file right away
        return true;
    }

    // Rents a vehicle to a customer - returns true if it worked, false if not
    public boolean rentVehicle(Vehicle vehicle, Customer customer, LocalDate date, double amount) {
        if (vehicle.getStatus() == Vehicle.VehicleStatus.Available) {
            vehicle.setStatus(Vehicle.VehicleStatus.Rented);
            RentalRecord record = new RentalRecord(vehicle, customer, date, amount, "RENT");
            rentalHistory.addRecord(record);
            saveRecord(record);    // save this rental event to the records file
            saveAllVehicles();     // rewrite vehicles file so the new status is saved
            System.out.println("Vehicle rented to " + customer.getCustomerName());
            return true;
        } else {
            System.out.println("Vehicle is not available for renting.");
            return false;
        }
    }

    // Returns a vehicle - returns true if it worked, false if not
    public boolean returnVehicle(Vehicle vehicle, Customer customer, LocalDate date, double extraFees) {
        if (vehicle.getStatus() == Vehicle.VehicleStatus.Rented) {
            vehicle.setStatus(Vehicle.VehicleStatus.Available);
            RentalRecord record = new RentalRecord(vehicle, customer, date, extraFees, "RETURN");
            rentalHistory.addRecord(record);
            saveRecord(record);    // save this return event to the records file
            saveAllVehicles();     // rewrite vehicles file so the status shows Available again
            System.out.println("Vehicle returned by " + customer.getCustomerName());
            return true;
        } else {
            System.out.println("Vehicle is not rented.");
            return false;
        }
    }

    // --- File Saving Methods ---

    // Saves one vehicle to vehicles.txt in append mode (adds to end of file)
    private void saveVehicle(Vehicle vehicle) {
        try {
            // "true" = append mode, don't erase the file
            BufferedWriter writer = new BufferedWriter(new FileWriter("vehicles.txt", true));
            // getSimpleName() gives us "Car", "Minibus", or "PickupTruck"
            String type = vehicle.getClass().getSimpleName();
            writer.write(type + "," + vehicle.getLicensePlate() + "," + vehicle.getMake()
                    + "," + vehicle.getModel() + "," + vehicle.getYear() + "," + vehicle.getStatus());
            writer.newLine(); // move to the next line in the file
            writer.close();
        } catch (IOException e) {
            System.out.println("Error saving vehicle: " + e.getMessage());
        }
    }

    // Rewrites ALL vehicles to vehicles.txt (used after rent/return to update statuses)
    private void saveAllVehicles() {
        try {
            // "false" = overwrite mode - erase the file and rewrite everything fresh
            BufferedWriter writer = new BufferedWriter(new FileWriter("vehicles.txt", false));
            for (Vehicle v : vehicles) {
                String type = v.getClass().getSimpleName();
                // We need to save extra fields for Car, Minibus, PickupTruck
                // so we can recreate them exactly when loading
                String extra = "";
                if (v instanceof Car) {
                    extra = "," + ((Car) v).getNumSeats();
                } else if (v instanceof Minibus) {
                    // Minibus doesn't have a getter for isAccessible in the starter
                    // so we just save a placeholder - we'll handle this on load
                    extra = ",false";
                } else if (v instanceof PickupTruck) {
                    extra = "," + ((PickupTruck) v).getCargoSize() + "," + ((PickupTruck) v).hasTrailer();
                }
                writer.write(type + "," + v.getLicensePlate() + "," + v.getMake()
                        + "," + v.getModel() + "," + v.getYear() + "," + v.getStatus() + extra);
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("Error saving all vehicles: " + e.getMessage());
        }
    }

    // Saves one customer to customers.txt in append mode
    private void saveCustomer(Customer customer) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("customers.txt", true));
            writer.write(customer.getCustomerId() + "," + customer.getCustomerName());
            writer.newLine();
            writer.close();
        } catch (IOException e) {
            System.out.println("Error saving customer: " + e.getMessage());
        }
    }

    // Saves one rental record to rental_records.txt in append mode
    private void saveRecord(RentalRecord record) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("rental_records.txt", true));
            // Save: plate, customer ID, date, amount, type (RENT or RETURN)
            writer.write(record.getVehicle().getLicensePlate() + ","
                    + record.getCustomer().getCustomerId() + ","
                    + record.getRecordDate() + ","
                    + record.getTotalAmount() + ","
                    + record.getRecordType());
            writer.newLine();
            writer.close();
        } catch (IOException e) {
            System.out.println("Error saving record: " + e.getMessage());
        }
    }

    // --- File Loading Methods ---

    // Called once from the constructor to load everything from files
    private void loadData() {
        loadVehicles();
        loadCustomers();
        loadRentalRecords();
    }

    // Reads vehicles.txt and rebuilds the vehicles list
    private void loadVehicles() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("vehicles.txt"));
            String line;
            // Read one line at a time - readLine() returns null at the end of the file
            while ((line = reader.readLine()) != null) {
                String[] p = line.split(","); // split by comma to get each field
                if (p.length < 6) continue;  // skip any broken lines

                String type   = p[0];
                String plate  = p[1];
                String make   = p[2];
                String model  = p[3];
                int year      = Integer.parseInt(p[4]); // convert text to a number
                String status = p[5];

                // Rebuild the correct vehicle type
                Vehicle v;
                if (type.equals("Car")) {
                    int seats = (p.length > 6) ? Integer.parseInt(p[6]) : 4;
                    v = new Car(make, model, year, seats);
                } else if (type.equals("Minibus")) {
                    boolean accessible = (p.length > 6) && Boolean.parseBoolean(p[6]);
                    v = new Minibus(make, model, year, accessible);
                } else if (type.equals("PickupTruck")) {
                    double cargo = (p.length > 6) ? Double.parseDouble(p[6]) : 1.0;
                    boolean trailer = (p.length > 7) && Boolean.parseBoolean(p[7]);
                    v = new PickupTruck(make, model, year, cargo, trailer);
                } else {
                    continue; // unknown type - skip it
                }

                // Set the plate back (calling setLicensePlate which validates format)
                try {
                    v.setLicensePlate(plate);
                } catch (IllegalArgumentException e) {
                    System.out.println("Skipping vehicle with bad plate: " + plate);
                    continue;
                }

                // Restore the saved status (Rented, Available, etc.)
                switch (status) {
                    case "Rented":           v.setStatus(Vehicle.VehicleStatus.Rented); break;
                    case "Held":             v.setStatus(Vehicle.VehicleStatus.Held); break;
                    case "UnderMaintenance": v.setStatus(Vehicle.VehicleStatus.UnderMaintenance); break;
                    case "OutOfService":     v.setStatus(Vehicle.VehicleStatus.OutOfService); break;
                    default:                 v.setStatus(Vehicle.VehicleStatus.Available); break;
                }

                vehicles.add(v);
            }
            reader.close();
        } catch (IOException e) {
            // File won't exist on the very first run - that's fine
            System.out.println("No vehicle data found (starting fresh).");
        }
    }

    // Reads customers.txt and rebuilds the customers list
    private void loadCustomers() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("customers.txt"));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] p = line.split(",");
                if (p.length < 2) continue;
                // p[0] is ID (int), p[1] is name
                int id = Integer.parseInt(p[0]);
                String name = p[1];
                customers.add(new Customer(id, name));
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("No customer data found (starting fresh).");
        }
    }

    // Reads rental_records.txt and rebuilds the rental history
    private void loadRentalRecords() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("rental_records.txt"));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] p = line.split(",");
                if (p.length < 5) continue;

                String plate      = p[0];
                int customerId    = Integer.parseInt(p[1]);
                LocalDate date    = LocalDate.parse(p[2]); // convert text back to a date
                double amount     = Double.parseDouble(p[3]);
                String recordType = p[4]; // "RENT" or "RETURN"

                // Look up the vehicle and customer objects from our lists
                Vehicle v  = findVehicleByPlate(plate);
                Customer c = findCustomerById(customerId);

                if (v == null || c == null) continue; // skip if something is missing

                rentalHistory.addRecord(new RentalRecord(v, c, date, amount, recordType));
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("No rental record data found (starting fresh).");
        }
    }

    // --- Display Methods (same as starter, kept exactly) ---

    public void displayVehicles(Vehicle.VehicleStatus status) {
        if (status == null) {
            System.out.println("\n=== All Vehicles ===");
        } else {
            System.out.println("\n=== " + status + " Vehicles ===");
        }

        System.out.printf("|%-16s | %-12s | %-12s | %-12s | %-6s | %-18s |%n",
            " Type", "Plate", "Make", "Model", "Year", "Status");
        System.out.println("|--------------------------------------------------------------------------------------------|");

        boolean found = false;
        for (Vehicle vehicle : vehicles) {
            if (status == null || vehicle.getStatus() == status) {
                found = true;
                String vehicleType;
                if (vehicle instanceof Car)             vehicleType = "Car";
                else if (vehicle instanceof Minibus)    vehicleType = "Minibus";
                else if (vehicle instanceof PickupTruck) vehicleType = "Pickup Truck";
                else                                    vehicleType = "Unknown";

                System.out.printf("| %-15s | %-12s | %-12s | %-12s | %-6d | %-18s |%n",
                    vehicleType, vehicle.getLicensePlate(), vehicle.getMake(),
                    vehicle.getModel(), vehicle.getYear(), vehicle.getStatus().toString());
            }
        }
        if (!found) {
            System.out.println("  No vehicles with Status: " + status);
        }
        System.out.println();
    }

    public void displayAllCustomers() {
        for (Customer c : customers) {
            System.out.println("  " + c.toString());
        }
    }

    public void displayRentalHistory() {
        if (rentalHistory.getRentalHistory().isEmpty()) {
            System.out.println("  No rental history found.");
        } else {
            System.out.printf("|%-10s | %-12s | %-20s | %-12s | %-12s |%n",
                " Type", "Plate", "Customer", "Date", "Amount");
            System.out.println("|-------------------------------------------------------------------------------|");

            for (RentalRecord record : rentalHistory.getRentalHistory()) {
                System.out.printf("| %-9s | %-12s | %-20s | %-12s | $%-11.2f |%n",
                    record.getRecordType(),
                    record.getVehicle().getLicensePlate(),
                    record.getCustomer().getCustomerName(),
                    record.getRecordDate().toString(),
                    record.getTotalAmount());
            }
            System.out.println();
        }
    }

    // Finds a vehicle in the list by its license plate
    public Vehicle findVehicleByPlate(String plate) {
        for (Vehicle v : vehicles) {
            if (v.getLicensePlate() != null && v.getLicensePlate().equalsIgnoreCase(plate))
                return v;
        }
        return null; // not found
    }

    // Finds a customer in the list by their ID number
    public Customer findCustomerById(int id) {
        for (Customer c : customers)
            if (c.getCustomerId() == id)
                return c;
        return null; // not found
    }
}