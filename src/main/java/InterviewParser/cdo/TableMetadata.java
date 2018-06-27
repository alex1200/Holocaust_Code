package InterviewParser.cdo;

public class TableMetadata
{
    private String name = "";
    private String rg_number = "";
    private String gender = "";
    private String date_of_birth = "";
    private String place_of_birth = "";
    private String country = "";
    private String source = "";
    private String classification = "";
    private String ghetto_association = "";
    private String searchable = "";
    private String comments = "";

    public TableMetadata(String[] metadata)
    {
        name = get(0,metadata);
        rg_number = get(1,metadata);
        gender = get(2,metadata);
        date_of_birth = get(3,metadata);
        place_of_birth = get(4,metadata);
        country = get(5,metadata);
        source = get(6,metadata);
        classification = get(7,metadata);
        ghetto_association = get(8,metadata);
        searchable = get(9,metadata);
        comments = get(10,metadata);
    }

    private String get(int index, String[] metadata){
        if(metadata.length > index){
            return metadata[index];
        }
        return "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRg_number() {
        return rg_number;
    }

    public void setRg_number(String rg_number) {
        this.rg_number = rg_number;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDate_of_birth() {
        return date_of_birth;
    }

    public void setDate_of_birth(String date_of_birth) {
        this.date_of_birth = date_of_birth;
    }

    public String getPlace_of_birth() {
        return place_of_birth;
    }

    public void setPlace_of_birth(String place_of_birth) {
        this.place_of_birth = place_of_birth;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public String getGhetto_association() {
        return ghetto_association;
    }

    public void setGhetto_association(String ghetto_association) {
        this.ghetto_association = ghetto_association;
    }

    public String getSearchable() {
        return searchable;
    }

    public void setSearchable(String searchable) {
        this.searchable = searchable;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
