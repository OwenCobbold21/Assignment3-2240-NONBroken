// Owen Cobbold
// 0895910
// March 25th, 2026

// This is the main program - it shows the menu and lets the user interact with the system
// Uses RentalSystem.getInstance() instead of "new RentalSystem()" because of the Singleton pattern

import java.util.Scanner;
import java.time.LocalDate;

public class VehicleRentalApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Get the single shared instance of RentalSystem - NOT "new RentalSystem()"
        RentalSystem rentalSystem = RentalSystem.getInstance();

        while (true) {
            System.out.println("\n1: Add Vehicle\n" +
                               "2: Add Customer\n" +
                               "3: Rent Vehicle\n" +
                               "4: Return Vehicle\n" +
                               "5: Display Available Vehicles\n" +
                               "6: Show Rental History\n" +
                               "0: Exit\n");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    System.out.println("  1: Car\n" +
                                       "  2: Minibus\n" +
                                       "  3: Pickup Truck");
                    int type = scanner.nextInt();
                    scanner.nextLine();

                    // Keep asking for a plate until they enter a valid one
                    // The try/catch here catches the IllegalArgumentException from setLicensePlate()
                    String plate = "";
                    boolean plateValid = false;
                    while (!plateValid) {
                        System.out.print("Enter license plate (format: AAA111): ");
                        plate = scanner.nextLine().toUpperCase(); // uppercase so "aaa111" still works
                        try {
                            // We test the plate by calling setLicensePlate on a temporary object
                            // If it throws an exception, the plate was bad
                            Vehicle temp = new Car("Test", "Test", 2000, 4);
                            temp.setLicensePlate(plate);
                            plateValid = true; // no exception means it's valid
                        } catch (IllegalArgumentException e) {
                            // setLicensePlate threw an error - tell the user and try again
                            System.out.println("Error: " + e.getMessage());
                        }
                    }

                    System.out.print("Enter make: ");
                    String make = scanner.nextLine();
                    System.out.print("Enter model: ");
                    String model = scanner.nextLine();
                    System.out.print("Enter year: ");
                    int year = scanner.nextInt();
                    scanner.nextLine();

                    Vehicle vehicle = null;
                    if (type == 1) {
                        System.out.print("Enter number of seats: ");
                        int seats = scanner.nextInt();
                        scanner.nextLine();
                        vehicle = new Car(make, model, year, seats);
                        System.out.println("Car added successfully.");
                    } else if (type == 2) {
                        System.out.print("Is accessible? (true/false): ");
                        boolean isAccessible = scanner.nextBoolean();
                        scanner.nextLine();
                        vehicle = new Minibus(make, model, year, isAccessible);
                        System.out.println("Minibus added successfully.");
                    } else if (type == 3) {
                        System.out.print("Enter the cargo size: ");
                        double cargoSize = scanner.nextDouble();
                        scanner.nextLine();
                        System.out.print("Has trailer? (true/false): ");
                        boolean hasTrailer = scanner.nextBoolean();
                        scanner.nextLine();
                        vehicle = new PickupTruck(make, model, year, cargoSize, hasTrailer);
                        System.out.println("Pickup Truck added successfully.");
                    }

                    if (vehicle != null) {
                        vehicle.setLicensePlate(plate); // safe - plate was already validated above
                        // addVehicle checks for duplicate plate and returns false if found
                        if (!rentalSystem.addVehicle(vehicle)) {
                            System.out.println("Vehicle was not added due to a duplicate plate.");
                        }
                    } else {
                        System.out.println("Vehicle not added successfully.");
                    }
                    break;

                case 2:
                    System.out.print("Enter customer ID: ");
                    int cid = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Enter name: ");
                    String cname = scanner.nextLine();

                    // addCustomer checks for duplicate ID and returns false if found
                    if (!rentalSystem.addCustomer(new Customer(cid, cname))) {
                        System.out.println("Customer was not added due to a duplicate ID.");
                    } else {
                        System.out.println("Customer added successfully.");
                    }
                    break;

                case 3:
                    rentalSystem.displayVehicles(Vehicle.VehicleStatus.Available);

                    System.out.print("Enter license plate: ");
                    String rentPlate = scanner.nextLine().toUpperCase();

                    System.out.println("Registered Customers:");
                    rentalSystem.displayAllCustomers();

                    System.out.print("Enter customer ID: ");
                    int cidRent = scanner.nextInt();

                    System.out.print("Enter rental amount: ");
                    double rentAmount = scanner.nextDouble();
                    scanner.nextLine();

                    Vehicle vehicleToRent = rentalSystem.findVehicleByPlate(rentPlate);
                    Customer customerToRent = rentalSystem.findCustomerById(cidRent);

                    if (vehicleToRent == null || customerToRent == null) {
                        System.out.println("Vehicle or customer not found.");
                        break;
                    }

                    rentalSystem.rentVehicle(vehicleToRent, customerToRent, LocalDate.now(), rentAmount);
                    break;

                case 4:
                    rentalSystem.displayVehicles(Vehicle.VehicleStatus.Rented);

                    System.out.print("Enter license plate: ");
                    String returnPlate = scanner.nextLine().toUpperCase();

                    System.out.println("Registered Customers:");
                    rentalSystem.displayAllCustomers();

                    System.out.print("Enter customer ID: ");
                    int cidReturn = scanner.nextInt();

                    System.out.print("Enter any additional return fees: ");
                    double returnFees = scanner.nextDouble();
                    scanner.nextLine();

                    Vehicle vehicleToReturn = rentalSystem.findVehicleByPlate(returnPlate);
                    Customer customerToReturn = rentalSystem.findCustomerById(cidReturn);

                    if (vehicleToReturn == null || customerToReturn == null) {
                        System.out.println("Vehicle or customer not found.");
                        break;
                    }

                    rentalSystem.returnVehicle(vehicleToReturn, customerToReturn, LocalDate.now(), returnFees);
                    break;

                case 5:
                    rentalSystem.displayVehicles(Vehicle.VehicleStatus.Available);
                    break;

                case 6:
                    rentalSystem.displayRentalHistory();
                    break;

                case 0:
                    scanner.close();
                    System.exit(0);
            }
        }
    }
}