package eu.nasuta;

import eu.nasuta.model.User;
import eu.nasuta.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class QuickplotBackendApplication implements CommandLineRunner {

    @Autowired
    private IUserRepository userRepository;

    public static void main(String... args){
        SpringApplication.run(QuickplotBackendApplication.class);
    }

    @Override
    public void run(String... args) throws Exception {
        userRepository.save(new User("wer","wer"));
    }

}
