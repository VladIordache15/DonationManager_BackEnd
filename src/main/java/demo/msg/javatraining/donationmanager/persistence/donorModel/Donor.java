package demo.msg.javatraining.donationmanager.persistence.donorModel;

import jakarta.persistence.*;

@Entity
@Table(	name = "donor")
public class Donor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private String additionalName;
    private String maidenName;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAdditionalName() {
        return additionalName;
    }

    public void setAdditionalName(String additionalName) {
        this.additionalName = additionalName;
    }

    public String getMaidenName() {
        return maidenName;
    }

    public void setMaidenName(String maidenName) {
        this.maidenName = maidenName;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Donor(String firstName, String lastName, String additionalName, String maidenName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.additionalName = additionalName;
        this.maidenName = maidenName;
    }

    public Donor(){}
}
