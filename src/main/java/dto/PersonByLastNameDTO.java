package dto;

import java.util.List;

public record PersonByLastNameDTO(
    String firstName,
    String lastName,
    String phone,
    int age,
    String email,
    List<String> medications,
    List<String> allergies
) {
    
}
