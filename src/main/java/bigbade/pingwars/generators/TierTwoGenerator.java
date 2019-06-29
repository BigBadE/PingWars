package bigbade.pingwars.generators;

import bigbade.pingwars.api.Generator;
import bigbade.pingwars.util.TimeUnit;

public class TierTwoGenerator extends Generator {
    public TierTwoGenerator(byte id) {
        super("New Computer", "You found a newer computer. Congrats! You get 10 pings every minute!", TimeUnit.MINUTE, 10, 50, 0, id);
    }
}
