package com.example.manish.flash;

/**
 * Created by Manish on 02-Nov-17.
 */

public class ModelClass {

    private String Name, Age, Gender, Eye, Image, Details;
    private Long Date;

    public ModelClass(){}

    public ModelClass(Long date, String name, String age, String gender, String eye, String image,String details) {
        Date = date;
        Name = name;
        Age = age;
        Gender = gender;
        Eye = eye;
        Image = image;
        Details = details;
    }

    public Long getDate() {
        return Date;
    }

    public void setDate(Long date) {
        Date = date;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getAge() {
        return Age;
    }

    public void setAge(String age) {
        Age = age;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public String getEye() {
        return Eye;
    }

    public void setEye(String eye) {
        Eye = eye;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getDetails() {
        return Details;
    }

    public void setDetails(String details) {
        Details = details;
    }
}
