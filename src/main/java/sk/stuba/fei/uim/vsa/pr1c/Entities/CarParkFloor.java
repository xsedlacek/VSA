package sk.stuba.fei.uim.vsa.pr1c.Entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "CAR_PARK_FLOOR")
@NamedQuery(name = CarParkFloor.FIND_BY_FLOOR_IDENTIFIER, query = "select cpf from CarParkFloor cpf where cpf.floorIdentifier = :floor_id ")
@NamedQuery(name = CarParkFloor.GET_CAR_PARK, query = "SELECT cp FROM CarPark cp  ")
public class CarParkFloor implements Serializable, DomainEntity {

    public static final String FIND_BY_FLOOR_IDENTIFIER = "CarParkFloor.findByFloorIdentifier";
    public static final String GET_CAR_PARK = "CarParkFloor.getCarPark";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String floorIdentifier;

    @OneToMany(cascade = CascadeType.REMOVE,orphanRemoval = true)
    @JoinColumn(name = "floor_id" , referencedColumnName = "id")
    private List<ParkingSpot> spots;

}
