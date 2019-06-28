package bigbade.pingwars.api;

public class Generator {
    //Name and description
    private final String name, description;
    //Time for each ping, ping per time, price in bp, and price
    private final long time, pings, bpPrice, price;
    private final byte id;

    public Generator(String name, String description, long time, long pings, long price, long bpPrice, byte id) {
        this.name = name;
        this.description = description;
        this.time = time;
        this.pings = pings;
        this.price = price;
        this.bpPrice = bpPrice;
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

    public long getBpPrice() {
        return bpPrice;
    }
}
