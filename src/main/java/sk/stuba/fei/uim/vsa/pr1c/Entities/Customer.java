package sk.stuba.fei.uim.vsa.pr1c.Entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.eclipse.persistence.annotations.CascadeOnDelete;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "USER")
@NoArgsConstructor
@NamedQuery(name = Customer.FIND_BY_EMAIL, query = "select user from Customer user where user.email = :email")
@NamedNativeQuery(name = Customer.FIND_ALL, query = "select * from USER", resultClass = Customer.class)
public class Customer implements Serializable {

    public Customer(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String firstName;

    private String lastName;
    @Column(unique=true)
    private String email;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user",referencedColumnName = "id")
    private List<Car> cars;

    public static final String FIND_BY_EMAIL = "Customer.findByEmail";
    public static final String FIND_ALL = "Customer.findAll";

    @Override
    public String toString() {
        return "Zakaznik " +
                " ID: " + id +
                " Meno: " + firstName +
                " Priezvisko: " + lastName +
                " Email: " + email+"\n";
    }
}
