import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

public class VehicleRentalTest {

    private RentalSystem system;

    // runs before every test method to get the system ready
    @BeforeEach
    public void setUp() {
        system = RentalSystem.getInstance();
    }

    // TEST 1 - license plate validation
    // the assignment asks us to use assertTrue, assertFalse, assertThrows
    // isValidPlate is private so I test it by checking if setLicensePlate
    // throws an exception or not - if no exception the plate was valid
    @Test
    public void testLicensePlate() {

        // create vehicles to test the plates on
        // Car needs make, model, year, seats - matching the starter code constructor
        Vehicle v1 = new Car("Toyota", "Corolla", 2019, 5);
        Vehicle v2 = new Car("Honda", "Civic", 2020, 5);
        Vehicle v3 = new Car("Ford", "Focus", 2021, 5);

        // set valid plates - these should not throw so we catch any exception
        // and use assertTrue to confirm no exception was thrown
        boolean valid1 = false;
        try {
            v1.setLicensePlate("AAA100");
            valid1 = true; // only reaches here if no exception was thrown
        } catch (IllegalArgumentException e) {
            valid1 = false;
        }
        assertTrue(valid1); // AAA100 should be valid

        boolean valid2 = false;
        try {
            v2.setLicensePlate("ABC567");
            valid2 = true;
        } catch (IllegalArgumentException e) {
            valid2 = false;
        }
        assertTrue(valid2); // ABC567 should be valid

        boolean valid3 = false;
        try {
            v3.setLicensePlate("ZZZ999");
            valid3 = true;
        } catch (IllegalArgumentException e) {
            valid3 = false;
        }
        assertTrue(valid3); // ZZZ999 should be valid

        // now test invalid plates - these should throw an exception
        // we use assertFalse to confirm they are not valid
        // and assertThrows to confirm the exception type is IllegalArgumentException

        // empty string
        boolean invalid1 = false;
        try {
            Vehicle bad = new Car("Toyota", "Corolla", 2019, 5);
            bad.setLicensePlate("");
            invalid1 = true; // should NOT reach here
        } catch (IllegalArgumentException e) {
            invalid1 = false;
        }
        assertFalse(invalid1); // empty string should be invalid

        assertThrows(IllegalArgumentException.class, () -> {
            Vehicle bad = new Car("Toyota", "Corolla", 2019, 5);
            bad.setLicensePlate("");
        });

        // null
        boolean invalid2 = false;
        try {
            Vehicle bad = new Car("Toyota", "Corolla", 2019, 5);
            bad.setLicensePlate(null);
            invalid2 = true;
        } catch (IllegalArgumentException e) {
            invalid2 = false;
        }
        assertFalse(invalid2); // null should be invalid

        assertThrows(IllegalArgumentException.class, () -> {
            Vehicle bad = new Car("Toyota", "Corolla", 2019, 5);
            bad.setLicensePlate(null);
        });

        // AAA1000 has 4 digits instead of 3
        boolean invalid3 = false;
        try {
            Vehicle bad = new Car("Toyota", "Corolla", 2019, 5);
            bad.setLicensePlate("AAA1000");
            invalid3 = true;
        } catch (IllegalArgumentException e) {
            invalid3 = false;
        }
        assertFalse(invalid3); // AAA1000 should be invalid

        assertThrows(IllegalArgumentException.class, () -> {
            Vehicle bad = new Car("Toyota", "Corolla", 2019, 5);
            bad.setLicensePlate("AAA1000");
        });

        // ZZZ99 only has 2 digits
        boolean invalid4 = false;
        try {
            Vehicle bad = new Car("Toyota", "Corolla", 2019, 5);
            bad.setLicensePlate("ZZZ99");
            invalid4 = true;
        } catch (IllegalArgumentException e) {
            invalid4 = false;
        }
        assertFalse(invalid4); // ZZZ99 should be invalid

        assertThrows(IllegalArgumentException.class, () -> {
            Vehicle bad = new Car("Toyota", "Corolla", 2019, 5);
            bad.setLicensePlate("ZZZ99");
        });
    }

    // ---------------------------------------------------------------
    // TEST 2 - rent and return a vehicle
    // ---------------------------------------------------------------
    @Test
    public void testRentAndReturnVehicle() {

        // create a vehicle and customer to test with
        Vehicle testVehicle = new Car("Toyota", "Corolla", 2020, 5);
        testVehicle.setLicensePlate("TST100");

        // customer ID is an int in the starter code
        Customer testCustomer = new Customer(9999, "TestPerson");

        // add them to the system
        system.addVehicle(testVehicle);
        system.addCustomer(testCustomer);

        // vehicle should start as Available
        assertEquals(Vehicle.VehicleStatus.Available, testVehicle.getStatus());

        // rent the vehicle - should return true
        boolean rentResult = system.rentVehicle(testVehicle, testCustomer,
                java.time.LocalDate.now(), 50.0);
        assertTrue(rentResult);

        // vehicle should now be Rented
        assertEquals(Vehicle.VehicleStatus.Rented, testVehicle.getStatus());

        // trying to rent it again should fail - returns false
        boolean rentAgain = system.rentVehicle(testVehicle, testCustomer,
                java.time.LocalDate.now(), 50.0);
        assertFalse(rentAgain);

        // return the vehicle - should return true
        boolean returnResult = system.returnVehicle(testVehicle, testCustomer,
                java.time.LocalDate.now(), 0.0);
        assertTrue(returnResult);

        // vehicle should be Available again
        assertEquals(Vehicle.VehicleStatus.Available, testVehicle.getStatus());

        // trying to return it again should fail - returns false
        boolean returnAgain = system.returnVehicle(testVehicle, testCustomer,
                java.time.LocalDate.now(), 0.0);
        assertFalse(returnAgain);
    }

    // TEST 3 - singleton check
    @Test
    public void testSingletonRentalSystem() throws Exception {

        // getDeclaredConstructor gets the constructor even if it is private
        Constructor<RentalSystem> constructor = RentalSystem.class.getDeclaredConstructor();

        // getModifiers returns a number representing public/private/protected
        int modifiers = constructor.getModifiers();

        // check that the constructor is private - required for singleton to work
        assertEquals(Modifier.PRIVATE, modifiers);

        // getInstance should return an actual object not null
        RentalSystem instance1 = RentalSystem.getInstance();
        assertNotNull(instance1);

        // calling getInstance twice should give back the exact same object
        RentalSystem instance2 = RentalSystem.getInstance();
        assertSame(instance1, instance2);
    }
}