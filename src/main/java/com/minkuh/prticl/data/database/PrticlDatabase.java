package com.minkuh.prticl.data.database;

import com.minkuh.prticl.Prticl;
import com.minkuh.prticl.data.database.entities.Node;
import com.minkuh.prticl.data.database.entities.Player;
import com.minkuh.prticl.data.database.entities.IPrticlEntity;
import com.minkuh.prticl.data.database.entities.Trigger;
import com.minkuh.prticl.data.database.functions.PrticlNodeFunctions;
import com.minkuh.prticl.data.database.functions.PrticlPlayerFunctions;
import com.minkuh.prticl.data.database.functions.PrticlTriggerFunctions;

import java.util.ArrayList;
import java.util.List;

public class PrticlDatabase {
    private final PrticlNodeFunctions nodeFunctions;
    private final PrticlTriggerFunctions triggerFunctions;
    private final PrticlPlayerFunctions playerFunctions;

    public PrticlDatabase(Prticl plugin) {
        this.nodeFunctions = new PrticlNodeFunctions();
        this.triggerFunctions = new PrticlTriggerFunctions();
        this.playerFunctions = new PrticlPlayerFunctions();
    }

    public PrticlNodeFunctions getNodeFunctions() {
        return nodeFunctions;
    }

    public PrticlTriggerFunctions getTriggerFunctions() {
        return triggerFunctions;
    }

    public PrticlPlayerFunctions getPlayerFunctions() {
        return playerFunctions;
    }

    /**
     * A constant list to share annotated JPA entities across Prticl.
     */
    public static final List<IPrticlEntity> PRTICL_DATABASE_ENTITIES = new ArrayList<>() {{
        add(new Player());
        add(new Node());
        add(new Trigger());
    }};
}