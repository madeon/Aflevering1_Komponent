package dk.sdu.mmmi.cbse.collission;

import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.EntityType;
import static dk.sdu.mmmi.cbse.common.data.EntityType.PLAYER;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.events.Event;
import dk.sdu.mmmi.cbse.common.events.EventType;
import dk.sdu.mmmi.cbse.common.services.IEntityProcessingService;
import dk.sdu.mmmi.cbse.common.services.IGamePluginService;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.Area;
import org.openide.util.lookup.ServiceProvider;
import org.openide.util.lookup.ServiceProviders;


    @ServiceProvider(service = IEntityProcessingService.class)
    

public class CollisionControlSystem implements IEntityProcessingService {

    private Polygon polyA, polyB;

    //Adds polygons to the 2 entities, to check if the areas collides
    public boolean collidesWith(Entity a, Entity b) {

        polyA = new Polygon();

        for (int i = 0; i < a.getShapeX().length; i++) {
            polyA.addPoint((int) a.getShapeX()[i], (int) a.getShapeY()[i]);
        }

        polyB = new Polygon();

        for (int i = 0; i < b.getShapeX().length; i++) {
            polyB.addPoint((int) b.getShapeX()[i], (int) b.getShapeY()[i]);

        }

        return polyA.getBounds2D().intersects(polyB.getBounds2D());
    }

    //Adds the split event to the asteroid if it is hit
    public void checkForAsteroidSplit(GameData gameData, World world) {
        for (Entity asteroid : world.getEntities(EntityType.ASTEROIDS)) {

            if (asteroid.getIsHit()) {
                gameData.addEvent(new Event(EventType.ASTEROID_SPLIT, asteroid.getID()));
                System.out.println("Asteroid is hit - event added");
            }

            for (Entity bullet : world.getEntities(EntityType.BULLET)) {
                if (collidesWith(asteroid, bullet)) {
                    asteroid.setIsHit(true);
                }
            }
        }
    }

    public void killEntityIfBulletCollision(World world) {
        //removes the player, enemy or ateroid if it is hit by a bullet
        for (Entity e : world.getEntities(EntityType.PLAYER, EntityType.ENEMY, EntityType.ASTEROIDS)) {

            if (e.getIsHit()) {
                world.removeEntity(e);
            }

            for (Entity bullet : world.getEntities(EntityType.BULLET)) {

                if (collidesWith(e, bullet)) {
                    e.setIsHit(true);
                    world.removeEntity(bullet);
                }
            }
        }
    }
    
    public void killPlayerIfAsteroidCollision(World world) {
        //Removes the player if it collides with an asteroid
        for (Entity e : world.getEntities(EntityType.PLAYER)) {
            if (e.getIsHit()) {
                world.removeEntity(e);
            }

            for (Entity player : world.getEntities(EntityType.ASTEROIDS)) {
                if (collidesWith(e, player)) {
                    e.setIsHit(true);
                }
            }
        }
    }

    @Override
    public void process(GameData gameData, World world) {
        
        checkForAsteroidSplit(gameData, world);
        
        killEntityIfBulletCollision(world);
        
        killPlayerIfAsteroidCollision(world);

        
    }
}
