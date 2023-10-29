package demo.msg.javatraining.donationmanager.persistence.campaignModel;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(	name = "campaign",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "name")
        })
@Data
@NoArgsConstructor

@Builder
@AllArgsConstructor
public class Campaign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String purpose;

//    @ManyToMany(mappedBy = "campaigns")
//    private Set<User> users = new HashSet<>();


    public Campaign(String name, String purpose) {
        this.name = name;
        this.purpose = purpose;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

}
