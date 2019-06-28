package bigbade.pingwars.generators;

import bigbade.pingwars.api.Generator;
import bigbade.pingwars.util.TimeUnit;

public class TierSixGenerator extends Generator {
    public TierSixGenerator(byte id) {
        super("Supercomputer", "A strong Supercomputer, capable of pinging 25000 times an hour", TimeUnit.HOUR, 25000, 100000, 0, id);
    }
}
