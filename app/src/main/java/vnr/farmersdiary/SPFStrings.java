package vnr.farmersdiary;

public enum SPFStrings
{
    SPFNAME ("FARMERSDIARY"),
    PHONENUMBER("PHONENUMBER"),
    LANGUAGE("LANG");


    private String value;

    SPFStrings(String value)
    {
        this.value = value;
    }

    public String getValue() {return value;}
}
