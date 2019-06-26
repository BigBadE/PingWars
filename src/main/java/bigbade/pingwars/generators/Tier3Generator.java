package bigbade.pingwars.generators;

import bigbade.pingwars.api.Generator;
import bigbade.pingwars.util.TimeUnit;

public class Tier3Generator extends Generator {
    public Tier3Generator(byte id) {
        super("Deli Computer", "You got a Deli Computer from an office that was out of business. You can ping 35 times a second.", TimeUnit.SECOND, 35, 1000, id);
    }
}
