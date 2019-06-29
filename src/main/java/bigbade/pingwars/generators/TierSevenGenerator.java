package bigbade.pingwars.generators;

import bigbade.pingwars.api.Generator;
import bigbade.pingwars.util.TimeUnit;

public class TierSevenGenerator extends Generator {
    public TierSevenGenerator(byte id) {
        super("Outsourcing", "With the power of third world countries, you get 1.339 billion pings a week", TimeUnit.WEEK, 1339000000, 20000000, 0, id);
    }
}
