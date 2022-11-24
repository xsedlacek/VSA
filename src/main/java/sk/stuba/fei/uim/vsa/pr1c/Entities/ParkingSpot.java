package sk.stuba.fei.uim.vsa.pr1c.Entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "PARKING_SPOT")
@NamedNativeQuery(name = ParkingSpot.FIND_ALL, query = "select * from PARKING_SPOT WHERE floor_id", resultClass = CarPark.class)
@NamedQuery(name = ParkingSpot.FIND_BY_CAR,query = "select spot from ParkingSpot spot where spot.car.id = :id")
public class ParkingSpot implements Serializable {

    public static final String FIND_ALL = "ParkingSpot.findAll";
    public static final String FIND_BY_CAR = "ParkingSpot.findByCar";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String parkingSpotIdentifier;

    @OneToOne(cascade = CascadeType.REMOVE,orphanRemoval = true)
    private Car car;


}
