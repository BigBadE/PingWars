package bigbade.pingwars.generators;

import bigbade.pingwars.api.Generator;
import bigbade.pingwars.util.TimeUnit;

public class TierNineGenerator extends Generator {
    public TierNineGenerator(byte id) {
        super("Quantum Computer", "A quantum computer, the government deems it unsafe so it can only be used once a day for 400000 pings", TimeUnit.DAY, 400000, 100000, 0, id);
    }
}
