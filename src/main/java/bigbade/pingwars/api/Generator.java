package bigbade.pingwars.api;

public class Generator {
    private final String name, description;
    private final long time, pings, price;
    private final byte id;

    public Generator(String name, String description, long time, long pings, long price, byte id) {
        this.name = name;
        this.description = description;
        this.time = time;
        this.pings = pings;
        this.price = price;
        this.id = id;
    }

    public long getPings() {
        return pings;
    }

    public String getName() {
        return name;
    }

    public long getTime() {
        return time;
    }

    public long getPrice() {
        return price;
    }

    public byte getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }
}
