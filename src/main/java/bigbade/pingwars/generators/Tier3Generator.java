package bigbade.pingwars.generators;

import bigbade.pingwars.api.Generator;
import bigbade.pingwars.util.TimeUnit;

public class Tier3Generator extends Generator {
    public Tier3Generator(byte id) {
        super("Dehl Computer", "You got a Dehl Computer from an office that went out of business. You can ping 1 time a second.", TimeUnit.SECOND, 35, 1000, 20, id);
    }
}
