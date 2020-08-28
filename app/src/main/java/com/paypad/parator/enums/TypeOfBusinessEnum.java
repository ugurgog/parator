package com.paypad.parator.enums;

public enum TypeOfBusinessEnum {

    ADULT("Adult", "Yetişkın",1),
    BEAUTY_AND_COSMETICS("Beauty & Cosmetics", "Güzellik & Kozmetik",2),
    BIKE("Bike", "Bisiklet",3),
    CAFE_FOOD_TRUCK("Cafe & Food Truck", "Cafe & Yemek Kamyonu",4),
    CBD_AND_VAPE("CBD & Vape", "CBD & Elektronik Sigara",5),
    ELECTRONICS("Electronics", "Elektronik",6),
    FASHION_AND_CLOTHING("Fashion & Clothing", "Mode & Giyim",7),
    FLORIST("Florist", "Çiçekçi",8),
    FOOTWEAR("Footwear", "Ayakkabı",9),
    FURNITURE("Furniture", "Mobilya",10),
    GIFT_STORE("Gift Store", "Hediye Dükkanı",11),
    GROCERIES_AND_FOOD_RETAIL("Groceries & Food Retail", "Bakkaliye ve Gıda Perakendeciliği",12),
    HAIR_AND_BEAUTY_SALON("Hair & Beauty Salon", "Saç & Güzellik Salonu",13),
    HEALTH("Health", "Sağlık",14),
    HOMEWARE("Homeware", "Ev eşyaları",15),
    JEWELLERY("Jewellery", "Mücevher",16),
    LIQUOR_STORES_AND_BOTTLE_SHOP("Liquor Stores & Bottle Shop", "İçki Mağazaları & Şişe Dükkanı",17),
    PET("Pet", "Evcil Hayvan",18),
    RESTAURANT("Restaurant", "Restorant",19),
    SECOND_HAND_SHOP("Second Hand Shop & Op Shop", "İkinci El Mağaza",20),
    SERVICES("Services", "Servisler",21),
    SPORTING_AND_OUTDOOR("Sporting & Outdoor", "Spor & Outdoor",22),
    SPORTS_TEAM("Sports Team", "Spor Takımı",23),
    STADIUM_AND_EVENTS("Stadium & Events", "Stadyum & Etkinlikler",24),
    TOOLS_AND_HARDWARE("Tools & Hardware", "Araçlar & Donanım",25),
    TOURISM("Tourism", "Turizm",26),
    TOYS_HOBBIES_CRAFTS("Toys, Hobbies & Crafts", "Oyuncaklar, Hobiler & El Sanatları",27),
    OTHER_RETAIL("Other Retail", "Diğer Perakende",28),
    OTHER_NON_RETAIL("Other Non-Retail", "Diğer Perakende Olmayan",29);

    private final String labelTr;
    private final String labelEn;
    private final int id;

    TypeOfBusinessEnum(String labelEn, String labelTr, int id) {
        this.labelTr = labelTr;
        this.labelEn = labelEn;
        this.id = id;
    }

    public static TypeOfBusinessEnum getById(int id) {
        for (TypeOfBusinessEnum e : values()) {
            if (e.id == id)
                return e;
        }
        return null;
    }

    public String getLabelTr() {
        return labelTr;
    }

    public String getLabelEn() {
        return labelEn;
    }

    public int getId() {
        return id;
    }
}
