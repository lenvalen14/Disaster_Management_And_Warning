package disasterwarning.com.vn.configuration;

import disasterwarning.com.vn.models.entities.Location;
import disasterwarning.com.vn.models.entities.User;
import disasterwarning.com.vn.repositories.LocationRepo;
import disasterwarning.com.vn.repositories.UserRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

@Configuration
public class DataInitializerConfig {

    @Bean
    CommandLineRunner initDatabase(UserRepo userRepo, PasswordEncoder passwordEncoder, LocationRepo locationRepo) {
        return args -> {

            Location location = locationRepo.findByName("Quảng Trị");
            if (location == null) {
                location = new Location();
                location.setLocationName("Quảng Trị");
                location.setLatitude(BigDecimal.valueOf(16.6904));
                location.setLongitude(BigDecimal.valueOf(107.1897));
                location.setStatus("active");
                locationRepo.save(location);
            }

            if (userRepo.findByEmail("nlhieunhi1402@gmail.com") == null) {
                User newUser = new User();
                newUser.setEmail("nlhieunhi1402@gmail.com");
                newUser.setPassword(passwordEncoder.encode("admin"));
                newUser.setRole("admin");
                newUser.setUserName("Nguyễn Lê Hiếu Nhi");
                newUser.setLocation(location);
                newUser.setStatus("active");
                userRepo.save(newUser);
            }
        };
    }

}
