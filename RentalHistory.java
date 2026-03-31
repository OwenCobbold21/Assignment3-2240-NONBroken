// RentalHistory keeps a list of all RentalRecord objects (the full log of rentals and returns)

import java.util.List;
import java.util.ArrayList;

public class RentalHistory {
    // This list holds every rental and return event that has happened
    private List<RentalRecord> rentalRecords = new ArrayList<>();

    // Adds a new record to the list
    public void addRecord(RentalRecord record) {
        rentalRecords.add(record);
    }

    // Returns the whole list so other classes can read it
    public List<RentalRecord> getRentalHistory() {
        return rentalRecords;
    }

    // Finds all records for a customer by searching their name
    public List<RentalRecord> getRentalRecordsByCustomer(String customerName) {
        List<RentalRecord> result = new ArrayList<>();
        for (RentalRecord record : rentalRecords) {
            // toLowerCase() makes the search case-insensitive
            if (record.getCustomer().toString().toLowerCase().contains(customerName.toLowerCase())) {
                result.add(record);
            }
        }
        return result;
    }

    // Finds all records for a specific vehicle plate number
    public List<RentalRecord> getRentalRecordsByVehicle(String licensePlate) {
        List<RentalRecord> result = new ArrayList<>();
        for (RentalRecord record : rentalRecords) {
            if (record.getVehicle().getLicensePlate().equalsIgnoreCase(licensePlate)) {
                result.add(record);
            }
        }
        return result;
    }
}