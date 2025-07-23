package co.za.kasi.enums;


public enum ProvinceEnum {

	GAUTENG("Gauteng"),
    LIMPOPO("Limpopo"),
    KWA_ZULU_NATAL("Kwa-Zulu Natal"),
    NORTHERN_PROVINCE("Northern Cape"),
    NORTH_WEST("North West"),
    FREE_STATE("Free State"),
    WESTERN_CAPE("Western Cape"),
    MPUMALANGA("Mpumalanga"),
	EASTERN_CAPE("Eastern Cape"),
	UNKNOWN("Unknown Province");

    private String description;

    ProvinceEnum(String description) {
        this.description = description;
    }

    public String description() { return description; }
    
    
    public static ProvinceEnum findByName(String name) {
        final String province = name.replace("-", "_").replace(" ", "_").toUpperCase();
        for (ProvinceEnum v : values()) {
            if (v.name().equals(province)) {
                return v;
            }
        }
        return null;
    }
}
