package dto;

import java.util.List;
import java.util.Set;

public record FirePersonDTO(
    String firstName,
    String lastName,
    String phone,
    int age,
    List<String> medications,
    List<String> allergies
) {

}
