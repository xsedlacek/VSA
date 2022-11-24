package sk.stuba.fei.uim.vsa.pr1c.Entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "CAR")
@NoArgsConstructor
@NamedQuery(name = Car.FIND_BY_PLATE, query = "select car from Car car where car.licensePlate = :plate")
@NamedQuery(name = Car.FIND_BY_RESERVATION,query = "select car from Car car where car.reservation.id = :id")
public class Car implements Serializable, DomainEntity {

    public Car(String brand, String model, String licensePlate, String color) {
        this.brand = brand;
        this.model = model;
        this.licensePlate = licensePlate;
        this.color = color;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String brand;

    private String model;

    @Column(unique=true)
    private String licensePlate;

    private String color;


    public static final String FIND_BY_PLATE = "Car.findByPlate";
    public static final String FIND_BY_RESERVATION = "Car.findByReservation";

    @OneToOne(cascade = CascadeType.PERSIST)
    Reservation reservation;

    @Override
    public String toString() {
        return "Auto:" +
                " ID:" + id +
                " Znacka: " + brand +
                " Model: " + model +
                " SPZ: " + licensePlate +
                " Farba" + color + "\n";
    }
}
