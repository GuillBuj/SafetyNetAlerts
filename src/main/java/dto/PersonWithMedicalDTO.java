package dto;

import java.util.List;

public record PersonWithMedicalDTO(
    String firstName,
    String lastName,
    String phone,
    int age,
    List<String> medications,
    List<String> allergies
) {

}
