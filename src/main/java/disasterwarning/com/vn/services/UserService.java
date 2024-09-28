package disasterwarning.com.vn.services;

import disasterwarning.com.vn.models.dtos.UserDTO;
import disasterwarning.com.vn.models.entities.Location;
import disasterwarning.com.vn.models.entities.User;
import disasterwarning.com.vn.repositories.LocationRepo;
import disasterwarning.com.vn.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements IUserService{

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private LocationRepo locationRepo;

    @Autowired
    private Mapper mapper;

    public UserDTO createUser(UserDTO userDTO) {
        User newUser = mapper.convertToEntity(userDTO, User.class);
        User existingUser = userRepo.findByEmail(newUser.getEmail());
        if (existingUser != null) {
            throw new RuntimeException("User already exists");
        }
        Location location = locationRepo.findById(newUser.getLocation().getLocationId())
                .orElseThrow(()->new RuntimeException("Location not found"));
        newUser.setLocation(location);
        userRepo.save(newUser);
        return mapper.convertToDto(newUser, UserDTO.class);
    }


    public UserDTO updateUser(UserDTO userDTO) {
        User user = mapper.convertToEntity(userDTO, User.class);
        User existingUser = userRepo.findByEmail(user.getEmail());
        if (existingUser == null) {
            throw new RuntimeException("User does not exist");
        }
        existingUser.setUserName(user.getUserName());
        existingUser.setEmail(user.getEmail());
        existingUser.setRole(user.getRole());
        existingUser.setLocation(user.getLocation());
        userRepo.save(user);
        return mapper.convertToDto(user, UserDTO.class);
    }

    public UserDTO findUserById(int id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User does not exist"));
        return mapper.convertToDto(user, UserDTO.class);
    }

    public UserDTO findUserByEmail(String email) {
        User user = userRepo.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User does not exist");
        }
        return mapper.convertToDto(user, UserDTO.class);
    }

    public List<UserDTO> findAllUsers() {
        List<User> users = userRepo.findAll();
        if (users.isEmpty()) {
            throw new RuntimeException("User does not exist");
        }
        return mapper.convertToDtoList(users, UserDTO.class);
    }

    public boolean deleteUser(int id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User does not exist"));
        userRepo.delete(user);
        return true;
    }
}
