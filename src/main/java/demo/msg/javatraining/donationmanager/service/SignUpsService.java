package demo.msg.javatraining.donationmanager.service;

import demo.msg.javatraining.donationmanager.persistence.model.volManager.EventJobs;
import demo.msg.javatraining.donationmanager.persistence.model.volManager.SignUps;
import demo.msg.javatraining.donationmanager.persistence.repository.EventJobsRepository;
import demo.msg.javatraining.donationmanager.persistence.repository.SignUpsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SignUpsService {

    @Autowired
    private SignUpsRepository signUpsRepository;
    @Autowired
    private EventJobsRepository eventJobsRepository;

    public List<SignUps>  getAllSignUps(){
        return signUpsRepository.findAll();
    }

    public List<SignUps> getAllSignUpsFromEventJob(Long eventJobId){
        return signUpsRepository.findSignUpsByEventJobsId(eventJobId);
    }
    public SignUps saveSignUp(SignUps signUps,Long eventJobId){
        EventJobs evj = eventJobsRepository.findEventJobsById(eventJobId);
        signUps.setEventJobs(evj);
        signUpsRepository.save(signUps);
        return signUps;
    }
    public SignUps updateSignUp(Long id,SignUps newSignUps){
        SignUps signUpsToBeModified = signUpsRepository.getReferenceById(id);   //nush daca e bine

        signUpsToBeModified.setCheckedIn(newSignUps.isCheckedIn());
        signUpsToBeModified.setCheckedInDate(newSignUps.getCheckedInDate());
        signUpsToBeModified.setSubDate(newSignUps.getSubDate());
        signUpsToBeModified.setStartDateJob(newSignUps.getStartDateJob());
        signUpsToBeModified.setEdnDateJob(newSignUps.getEdnDateJob());
        signUpsToBeModified.setVolunteer(newSignUps.getVolunteer());
        signUpsToBeModified.setEventJobs(newSignUps.getEventJobs());

        signUpsRepository.save(signUpsToBeModified);
        return signUpsToBeModified;

    }

    public SignUps findById(Long id){
        return signUpsRepository.getReferenceById(id);
    }

    public SignUps deleteSignUp(Long id){
        signUpsRepository.deleteById(id);
        return signUpsRepository.getReferenceById(id);
    }

}
