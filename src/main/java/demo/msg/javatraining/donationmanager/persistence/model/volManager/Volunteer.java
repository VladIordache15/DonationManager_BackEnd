package demo.msg.javatraining.donationmanager.persistence.model.volManager;

import demo.msg.javatraining.donationmanager.persistence.campaignModel.Campaign;
import demo.msg.javatraining.donationmanager.persistence.model.Role;
import demo.msg.javatraining.donationmanager.persistence.model.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

import java.util.Set;


@Entity
//@PrimaryKeyJoinColumn(name = "user_id")
@Table(name="Volunteer")
public class Volunteer extends User {

    private String adress;
    private int JobsCount;

    public Volunteer(String firstName, String lastName, String mobileNumber, String username, String email, String password,
                     boolean active, boolean firstLogin, int retryCount, Set<Role> roles, Set<Campaign> campaigns,String adress,int jobsCount ) {
        super( firstName, lastName, mobileNumber, username, email, password, active, firstLogin, retryCount, roles, campaigns);

        this.adress=adress;
        this.JobsCount=jobsCount;
    }
    public Volunteer(){

    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public int getJobsCount() {
        return JobsCount;
    }

    public void setJobsCount(int jobsCount) {
        JobsCount = jobsCount;
    }
}
