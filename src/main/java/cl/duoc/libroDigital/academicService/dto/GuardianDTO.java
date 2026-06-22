package cl.duoc.libroDigital.academicService.dto;

import java.time.LocalDateTime;

public class GuardianDTO {
    private Long id;
    private String rut;
    private String firstName;
    private String lastName;
    private String secondLastName;
    private String email;
    private String phone;
    private String emergencyPhone;
    private String address;
    private String commune;
    private String city;
    private String relationship;
    private String occupation;
    private String workplace;
    private String workPhone;
    private Boolean isPrimary;
    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getRut() { return rut; }
    public void setRut(String rut) { this.rut = rut; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getSecondLastName() { return secondLastName; }
    public void setSecondLastName(String secondLastName) { this.secondLastName = secondLastName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmergencyPhone() { return emergencyPhone; }
    public void setEmergencyPhone(String emergencyPhone) { this.emergencyPhone = emergencyPhone; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getCommune() { return commune; }
    public void setCommune(String commune) { this.commune = commune; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getRelationship() { return relationship; }
    public void setRelationship(String relationship) { this.relationship = relationship; }
    public String getOccupation() { return occupation; }
    public void setOccupation(String occupation) { this.occupation = occupation; }
    public String getWorkplace() { return workplace; }
    public void setWorkplace(String workplace) { this.workplace = workplace; }
    public String getWorkPhone() { return workPhone; }
    public void setWorkPhone(String workPhone) { this.workPhone = workPhone; }
    public Boolean getIsPrimary() { return isPrimary; }
    public void setIsPrimary(Boolean isPrimary) { this.isPrimary = isPrimary; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
