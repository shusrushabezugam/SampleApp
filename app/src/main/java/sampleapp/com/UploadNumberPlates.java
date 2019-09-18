package sampleapp.com;

public class UploadNumberPlates
{
    private String custName;
    private String imageUrl;

    public UploadNumberPlates() {
        //empty constructor needed
    }

    public UploadNumberPlates(String name, String imageUrl) {
        if (name.trim().equals("")) {
            name = "No Name";
        }

        custName = name;
        imageUrl = imageUrl;
    }

    public String getName() {
        return custName;
    }

    public void setName(String name) {
        custName = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        imageUrl = imageUrl;
    }
}
