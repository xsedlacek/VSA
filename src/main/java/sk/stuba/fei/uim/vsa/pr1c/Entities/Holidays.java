package sk.stuba.fei.uim.vsa.pr1c.Entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "HOLIDAYS")
@NamedQuery(name = Holidays.FIND_BY_NAME, query = "select h from Holidays h where h.name = :name ")
@NamedQuery(name = Holidays.FIND_BY_DATE, query = "select h from Holidays h where h.date = :date ")
@NamedNativeQuery(name = Holidays.FIND_ALL, query = "select * from HOLIDAYS", resultClass = Holidays.class)
@NoArgsConstructor
public class Holidays implements Serializable {

    public Holidays(String name, String date) {
        this.name = name;
        this.date = date;
    }

    public static final String FIND_BY_NAME = "Holidays.findByName";
    public static final String FIND_BY_DATE = "Holidays.findByDate";
    public static final String FIND_ALL = "Holidays.findAll";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @Column(unique = true)
    private String date;


}
