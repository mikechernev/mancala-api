package com.mikechernev.mancala.api.dao;

import com.mikechernev.mancala.api.domain.Player;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;

/**
 * Created by mike on 23/04/16.
 */
public class PlayerDaoImpl implements PlayerDao {

    private Datastore ds;

    public PlayerDaoImpl(Datastore ds) {
        this.ds = ds;
    }

    public boolean create(Player player) {
        return save(player);
    }

    public Player get(String id) {
        ObjectId objectId = new ObjectId(id);
        return ds.get(Player.class, objectId);
    }

    public boolean update(Player player) {
        return save(player);
    }

    private final boolean save(Player player) {
        ds.save(player);
        return true;
    }

}
