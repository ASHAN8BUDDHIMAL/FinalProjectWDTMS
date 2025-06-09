package com.example.demo.DTO;

import com.example.demo.model.Location;
import com.example.demo.model.UserRegistration;
import com.example.demo.model.Worker;

import java.util.Base64;

public class ViewProfileDTO {
    private String fullName;
    private String phone;
    private String userType;
    private String skills;
    private Double chargePerHour;
    private String city;
    private Double latitude;
    private Double longitude;
    private String profilePictureBase64;

    public ViewProfileDTO(UserRegistration user, Worker worker, Location location) {
        this.fullName = user.getFirstName() + " " + user.getLastName();
        this.phone = user.getPhone();
        this.userType = user.getUserType();

        if (worker != null) {
            this.skills = worker.getSkills();
            this.chargePerHour = worker.getChargePerHour();
        }

        if (location != null) {
            this.city = location.getCity();
            this.latitude = location.getLatitude();
            this.longitude = location.getLongitude();
        }

        if (user.getProfilePicture() != null) {
            this.profilePictureBase64 = Base64.getEncoder().encodeToString(user.getProfilePicture());

        }
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public Double getChargePerHour() {
        return chargePerHour;
    }

    public void setChargePerHour(Double chargePerHour) {
        this.chargePerHour = chargePerHour;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getProfilePictureBase64() {
        return profilePictureBase64;
    }

    public void setProfilePictureBase64(String profilePictureBase64) {
        this.profilePictureBase64 = profilePictureBase64;
    }
}
