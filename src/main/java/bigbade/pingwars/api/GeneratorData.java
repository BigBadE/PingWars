package bigbade.pingwars.api;

public class GeneratorData {
    private long amount;
    private long prestigue;

    public GeneratorData(long amount, long prestigue) {
        this.amount = amount;
        this.prestigue = prestigue;
    }

    public long getPrestigue() {
        return prestigue;
    }

    public long getAmount() {
        return amount;
    }

    public void addPrestigue() {
        prestigue++;
    }

    public void addAmount(long add) {
        amount += add;
    }

    public void removeAmount(long remove) {
        amount -= remove;
    }
}
