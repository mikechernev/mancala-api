package com.mikechernev.mancala.api.persistance;

import com.mikechernev.mancala.api.domain.Game;
import com.mikechernev.mancala.api.domain.Player;
import com.mongodb.MongoClient;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

/**
 * Created by mike on 23/04/16.
 */
public class Mongo {
    private static Datastore datastore;
    private static final String DATABASE_NAME = "Mancala";

    public static Datastore getDatastore() {
        if (datastore == null) {
            setDatastore();
        }

        return datastore;
    }

    private static void setDatastore() {
        MongoClient client = new MongoClient("mongodb");
        Morphia morphia = new Morphia();
        mapClasses(morphia);
        datastore = morphia.createDatastore(client, DATABASE_NAME);
    }

    private static void mapClasses(Morphia morphia) {
        morphia.map(Game.class);
        morphia.map(Player.class);
    }
}
