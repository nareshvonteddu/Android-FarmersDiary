package vnr.farmersdiary;

public enum SPFStrings
{
    SPFNAME ("FARMERSDIARY"),
    PHONENUMBER("PHONENUMBER"),
    USERID("USERID"),
    LANGUAGE("LANG"),
    SYNCONSTART("SYNCONSTART");

    private String value;

    SPFStrings(String value)
    {
        this.value = value;
    }

    public String getValue() {return value;}
}
