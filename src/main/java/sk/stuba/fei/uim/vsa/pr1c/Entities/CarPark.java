package sk.stuba.fei.uim.vsa.pr1c.Entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@NamedNativeQuery(name = CarPark.FIND_ALL, query = "select * from CAR_PARK", resultClass = CarPark.class)
@NamedQuery(name = CarPark.FIND_BY_NAME, query = "select cp from CarPark cp where cp.name = :name")
//@NamedNativeQuery(name  = CarPark.DELETE_BY_ID, query = "DELETE  FROM CAR_PARK WHERE id = ?")
@Table(name = "CAR_PARK")
public class CarPark implements Serializable, DomainEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique=true)
    private String name;
    private String address;
    private int pricePerHour;

    @OneToMany(cascade = CascadeType.REMOVE,orphanRemoval = true)
    @JoinColumn(name = "car_park_id", referencedColumnName = "id")
    private List<CarParkFloor> floors =  new ArrayList<>();

    public static final String FIND_ALL = "CarPark.findAll";
    public static final String FIND_BY_NAME = "CarPark.findByName";
    public static final String DELETE_BY_ID = "CarPark.deleteById";


    @Override
    public String toString() {
        return "Parkovisko: " +
                " Meno " + name +
                " Adresa: " + address +
                " Cena za hodinu: " + pricePerHour +
                " ID: "+id+"\n";
    }
}
