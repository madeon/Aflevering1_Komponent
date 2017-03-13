package dk.sdu.mmmi.cbse.bullet;

import dk.sdu.mmmi.cbse.common.data.Entity;
import static dk.sdu.mmmi.cbse.common.data.EntityType.BULLET;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.events.Event;
import dk.sdu.mmmi.cbse.common.events.EventType;
import dk.sdu.mmmi.cbse.common.services.IEntityProcessingService;
import dk.sdu.mmmi.cbse.common.services.IGamePluginService;
import java.util.ArrayList;
import org.openide.util.lookup.ServiceProvider;
import org.openide.util.lookup.ServiceProviders;

@ServiceProviders(value = {
@ServiceProvider(service = IEntityProcessingService.class),
@ServiceProvider(service = IGamePluginService.class)})

public class BulletControlSystem implements IEntityProcessingService, IGamePluginService {

    private ArrayList<Entity> bullets;

    public void drawBullet(Entity e) {
        int size = 2;

        //Draw the bullet
        e.setShapeX(new float[]{
            e.getX() + (float) (Math.cos(e.getRadians()) * 8) / size,
            e.getX() + (float) (Math.cos(e.getRadians() - 4 * 3.1415f / 5) * 8) / size,
            e.getX() + (float) (Math.cos(e.getRadians() + 3.1415f) * 5) / size,
            e.getX() + (float) (Math.cos(e.getRadians() + 4 * 3.1415f / 5) * 8) / size});

        e.setShapeY(new float[]{
            e.getY() + (float) (Math.sin(e.getRadians()) * 8) / size,
            e.getY() + (float) (Math.sin(e.getRadians() - 4 * 3.1415f / 5) * 8) / size,
            e.getY() + (float) (Math.sin(e.getRadians() + 3.1415f) * 5) / size,
            e.getY() + (float) (Math.sin(e.getRadians() + 4 * 3.1415f / 5) * 8) / size});
    }

    public void moveBullet(Entity e, GameData gameData) {

        //make bullet move
        e.setDx((float) (Math.cos(e.getRadians()) * e.getMaxSpeed()));
        e.setDy((float) (Math.sin(e.getRadians()) * e.getMaxSpeed()));
        
        //Set position
        e.setX(e.getX() + e.getDx() * gameData.getDelta());
        e.setY(e.getY() + e.getDy() * gameData.getDelta());
    }

    private void addShootEvent(World world, GameData gameData, Event e) {
        world.addEntity(createBullet(gameData, e, world));
        gameData.removeEvent(e);
    }

    private Entity createBullet(GameData gameData, Event e, World world) {
        Entity bullet = new Entity();
        Entity entity = world.getEntity(e.getEntityID());
        bullets.add(bullet);
        bullet.setType(BULLET);
        bullet.setRadians(entity.getRadians());
        bullet.setPosition(entity.getX() + ((float) Math.cos(entity.getRadians()) * 20), entity.getY() + ((float) Math.sin(entity.getRadians()) * 20));
        bullet.setMaxSpeed(300);
        bullet.setWrap(false);
        return bullet;
    }

    @Override
    public void process(GameData gameData, World world) {
        for (Entity e : world.getEntities(BULLET)) {
            drawBullet(e);
            moveBullet(e, gameData);
        }

        for (Event e : gameData.getEvents()) {
            if (e.getType() == EventType.PLAYER_SHOOT) {
                addShootEvent(world, gameData, e);
                
            }

            if (e.getType() == EventType.ENEMY_SHOOT) {
                addShootEvent(world, gameData, e);
            }
        }

    }

    @Override
    public void start(GameData gameData, World world) {
        bullets = new ArrayList<>();
    }

    @Override
    public void stop(GameData gameData, World world) {
        for (Entity b : bullets) {
            world.removeEntity(b);
        }
    }
}
