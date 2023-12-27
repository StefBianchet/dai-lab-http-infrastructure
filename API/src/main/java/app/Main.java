package app;

import io.javalin.Javalin;

public class Main {
    public static void main(String[] args) {
        Javalin app = Javalin.create().start(7070);
        DriverDao driverDao = new DriverDao();
        app.get("/api/drivers", driverDao::getAllDrivers);
        app.get("/api/drivers/{id}", driverDao::getOneDriver);
        app.post("/api/drivers/", driverDao::createDriver);
        app.put("/api/drivers/{id}", driverDao::updateDriver);
        app.delete("/api/drivers/{id}", driverDao::deleteDriver);
    }
}