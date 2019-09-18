package sampleapp.com;

public class User {
    public String name, email, type,imagepath;

    public User(){

    }

    public User(String name, String email, String type) {
        this.name = name;
        this.email = email;
        this.type = type;
    }
    public String getName()
    {
return name;
    }
    public String getType()
    {
        return type;
    }
    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImagepath() {
        return imagepath;
    }

    public void setImagepath(String imagepath) {
        this.imagepath = imagepath;
    }
}
