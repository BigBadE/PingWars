package bigbade.pingwars.generators;

import bigbade.pingwars.api.Generator;

public class WeakGenerator extends Generator {
    public WeakGenerator(byte id) {
        super("Old Computer", "An old computer found on the side of the road. Can ping your enemies once a second.", 1000, 1, 10, id);
    }
}
