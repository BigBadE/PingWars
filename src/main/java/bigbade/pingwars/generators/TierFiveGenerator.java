package bigbade.pingwars.generators;

import bigbade.pingwars.api.Generator;
import bigbade.pingwars.util.TimeUnit;

public class TierFiveGenerator extends Generator {
    public TierFiveGenerator(byte id) {
        super("Boss Computer", "A computer made of pure boss essence, gives 5 pings per second", TimeUnit.SECOND, 5, 0, 200, id);
    }
}
