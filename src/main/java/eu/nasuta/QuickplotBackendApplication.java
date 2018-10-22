package eu.nasuta;

import eu.nasuta.model.User;
import eu.nasuta.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class QuickplotBackendApplication implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    public static void main(String... args){
        SpringApplication.run(QuickplotBackendApplication.class);
    }

    @Override
    public void run(String... args) throws Exception {
        userRepository.save(new User("test","test"));
    }

}
