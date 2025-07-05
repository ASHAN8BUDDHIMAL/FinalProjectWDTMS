package com.example.demo.model;


import jakarta.persistence.*;
import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
@Table(name = "sam3_users")
public class UserRegistration {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String firstName;
        private String lastName;
        private String email;
        private String password;
        private String phone;
        private String address;
        private String district;
        private String city;
        private String postalCode;
        private String userType;
        private boolean active = true;
        @Lob
        @Column(columnDefinition = "MEDIUMBLOB")
        private byte[] profilePicture;


        public Long getId() {
                return id;
        }

        public void setId(Long id) {
                this.id = id;
        }

        public String getFirstName() {
                return firstName;
        }

        public void setFirstName(String firstName) {
                this.firstName = firstName;
        }

        public String getLastName() {
                return lastName;
        }

        public void setLastName(String lastName) {
                this.lastName = lastName;
        }

        public String getEmail() {
                return email;
        }

        public void setEmail(String email) {
                this.email = email;
        }

        public String getPassword() {
                return password;
        }

        public void setPassword(String password) {
                this.password = password;
        }

        public String getPhone() {
                return phone;
        }

        public void setPhone(String phone) {
                this.phone = phone;
        }

        public String getAddress() {
                return address;
        }

        public void setAddress(String address) {
                this.address = address;
        }

        public String getDistrict() {
                return district;
        }

        public void setDistrict(String district) {
                this.district = district;
        }

        public String getCity() {
                return city;
        }

        public void setCity(String city) {
                this.city = city;
        }

        public String getPostalCode() {
                return postalCode;
        }

        public void setPostalCode(String postalCode) {
                this.postalCode = postalCode;
        }

        public String getUserType() {
                return userType;
        }

        public void setUserType(String userType) {
                this.userType = userType;
        }

        public boolean isActive() {
                return active;
        }

        public void setActive(boolean active) {
                this.active = active;
        }

        public byte[] getProfilePicture() {
                return profilePicture;
        }

        public void setProfilePicture(byte[] profilePicture) {
                this.profilePicture = profilePicture;
        }
}
