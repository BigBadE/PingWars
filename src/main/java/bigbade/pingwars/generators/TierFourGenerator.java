package bigbade.pingwars.generators;

import bigbade.pingwars.api.Generator;
import bigbade.pingwars.util.TimeUnit;

public class TierFourGenerator extends Generator {
    public TierFourGenerator(byte id) {
        super("BestPurchase Computer", "You got a good computer at BestPurchase, and you get 6000 pings every hour.", TimeUnit.HOUR, 6000, 12000, 10, id);
    }
}
