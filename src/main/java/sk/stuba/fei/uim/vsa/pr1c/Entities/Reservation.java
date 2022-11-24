package sk.stuba.fei.uim.vsa.pr1c.Entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NamedNativeQuery(name = Reservation.FIND_ALL, query = "select * from RESERVATION", resultClass = Reservation.class)
public class Reservation implements Serializable {

    public Reservation() {

        this.beginTime = LocalDateTime.now();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private LocalDateTime beginTime;
    private LocalDateTime endTime;
    private int price;

    public static final String FIND_ALL = "Reservation.findAll";

}
