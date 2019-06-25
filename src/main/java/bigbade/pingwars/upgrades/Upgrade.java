package bigbade.pingwars.upgrades;

public class Upgrade {

    private byte id;
    private String name;
    private long price;

    public Upgrade(byte id, String name, long price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public byte getId() {
        return id;
    }

    public String getName() { return name; }

    public long getPrice() { return price; }
}
