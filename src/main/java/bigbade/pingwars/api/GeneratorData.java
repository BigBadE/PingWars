package bigbade.pingwars.api;

public class GeneratorData {
    //Amount of gens
    private long amount;
    //Prestige
    private long prestige;

    public GeneratorData(long amount, long prestige) {
        this.amount = amount;
        this.prestige = prestige;
    }

    public long getPrestigue() {
        return prestige;
    }

    public long getAmount() {
        return amount;
    }

    public void addPrestigue() {
        prestige++;
    }

    public void addAmount(long add) {
        amount += add;
    }

    public void removeAmount(long remove) {
        amount -= remove;
    }
}
