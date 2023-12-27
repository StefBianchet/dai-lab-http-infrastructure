package app;

import io.javalin.http.Context;
import java.util.concurrent.ConcurrentHashMap;

// DAO = Data Access Object
public class DriverDao {
    private ConcurrentHashMap<Integer, Driver> drivers = new ConcurrentHashMap<Integer, Driver>();
    private int lastId = 0;

    public DriverDao(){
        // Add some drivers to our list
        drivers.put(++lastId, new Driver("Max", "Verstappen", "Netherlands", "30-09-1997", 26, "Red Bull", 1, 0));
        drivers.put(++lastId, new Driver("Sergio", "Perez", "Mexico", "26-01-1990", 33, "Red Bull",2,0));
        drivers.put(++lastId, new Driver("Lewis", "Hamilton", "United Kingdom", "07-01-1985", 38,"Mercedes",3,0));
        drivers.put(++lastId, new Driver("Fernando", "Alonso", "Spain", "31-07-1981", 42,"Aston Martin",4,0));
        drivers.put(++lastId, new Driver("Charles", "Leclerc", "Monaco", "16-10-1997", 26,"Aston Martin",5,0));
    }

    // Create
    public void createDriver(Context context){
        Driver driver = context.bodyAsClass(Driver.class);
        drivers.put(++lastId, driver);
        context.status(201);
    }

    // Read all
    public void getAllDrivers(Context context){
        context.json(drivers);
    }

    // Read one
    public void getOneDriver(Context context){
        int id = Integer.parseInt(context.pathParam("id"));
        context.json(drivers.get(id));
    }

    // Update
    public void updateDriver(Context context) {
        int id = Integer.parseInt(context.pathParam("id"));
        Driver driver = context.bodyAsClass(Driver.class);
        drivers.put(id, driver);
        context.status(200);
    }

    // Delete
    public void deleteDriver(Context context){
        int id = Integer.parseInt(context.pathParam("id"));
        drivers.remove(id);
        context.status(204);
    }
}
