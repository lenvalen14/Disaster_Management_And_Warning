package disasterwarning.com.vn.models.dtos;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;

@Data
public class UserDTO {

    private int userId;
    private String userName;
    private String email;
    private String password;
    private String role;

    @JsonManagedReference
    private LocationDTO locationDTO;
}