package bigbade.pingwars.generators;

import bigbade.pingwars.api.Generator;
import bigbade.pingwars.util.TimeUnit;

public class TierOneGenerator extends Generator {
    public TierOneGenerator(byte id) {
        super("Old Computer", "An old computer found on the side of the road. Can ping your enemies 5 times a minute.", TimeUnit.MINUTE, 5, 10, 0, id);
    }
}
