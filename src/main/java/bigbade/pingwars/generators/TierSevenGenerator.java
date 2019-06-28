package bigbade.pingwars.generators;

import bigbade.pingwars.api.Generator;
import bigbade.pingwars.util.TimeUnit;

public class TierSevenGenerator extends Generator {
    public TierSevenGenerator(byte id) {
        super("Outsourcing", "With the power of third world countries, you get 10 pings a second", TimeUnit.SECOND, 10, 200000, 0, id);
    }
}
