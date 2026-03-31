// A RentalRecord stores one event - either a RENT or a RETURN
// The starter code uses a "RENT"/"RETURN" string and a dollar amount

import java.time.LocalDate;

public class RentalRecord {
    private Vehicle vehicle;
    private Customer customer;
    private LocalDate recordDate;
    private double totalAmount;
    private String recordType; // either "RENT" or "RETURN"

    public RentalRecord(Vehicle vehicle, Customer customer, LocalDate recordDate,
                        double totalAmount, String recordType) {
        this.vehicle = vehicle;
        this.customer = customer;
        this.recordDate = recordDate;
        this.totalAmount = totalAmount;
        this.recordType = recordType;
    }

    // Getter methods
    public Customer getCustomer()     { return customer; }
    public Vehicle getVehicle()       { return vehicle; }
    public LocalDate getRecordDate()  { return recordDate; }
    public double getTotalAmount()    { return totalAmount; }
    public String getRecordType()     { return recordType; }

    @Override
    public String toString() {
        return recordType + " | Plate: " + vehicle.getLicensePlate() +
               " | Customer: " + customer.getCustomerName() +
               " | Date: " + recordDate +
               " | Amount: $" + totalAmount;
    }
}