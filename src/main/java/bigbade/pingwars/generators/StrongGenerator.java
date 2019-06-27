package bigbade.pingwars.generators;

import bigbade.pingwars.api.Generator;
import bigbade.pingwars.util.TimeUnit;

public class StrongGenerator extends Generator {
    public StrongGenerator(byte id) {
        super("Newer Computer", "You found a newer computer. Congrats! You get 20 pings every minute!", TimeUnit.MINUTE, 20, 50, 0, id);
    }
}
