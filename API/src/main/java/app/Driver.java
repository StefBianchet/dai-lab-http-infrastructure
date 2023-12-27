package app;

public class Driver {
    public String firstName;
    public String lastName;
    public String country;
    public int age;
    public String birthDate;
    public String team;
    public int rank;
    public int points;

    public Driver() { }

    public Driver(String firstName, String lastName,String country, String birthDate, int age, String team, int rank, int points) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.country = country;
        this.age = age;
        this.birthDate = birthDate;
        this.team = team;
        this.rank = rank;
        this.points = points;
    }
}
