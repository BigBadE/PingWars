package bigbade.pingwars.generators;

import bigbade.pingwars.api.Generator;
import bigbade.pingwars.util.TimeUnit;

public class TierTenGenerator extends Generator {
    public TierTenGenerator(byte id) {
        super("Enslave humanity", "Enslave humanity using monsters to ping for you, generates 8000000000 pings every week", TimeUnit.WEEK, 8000000000L, 20000000000000L, 2500, id);
    }
}
