package dk.sdu.mmmi.cbse.enemy;

import dk.sdu.mmmi.cbse.common.data.Entity;
import static dk.sdu.mmmi.cbse.common.data.EntityType.ENEMY;
import static dk.sdu.mmmi.cbse.common.data.EntityType.PLAYER;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.events.Event;
import dk.sdu.mmmi.cbse.common.events.EventType;
import dk.sdu.mmmi.cbse.common.services.IEntityProcessingService;
import dk.sdu.mmmi.cbse.common.services.IGamePluginService;
import java.util.Random;
import org.openide.util.lookup.ServiceProvider;
import org.openide.util.lookup.ServiceProviders;

@ServiceProviders(value = {
    @ServiceProvider(service = IEntityProcessingService.class),
    @ServiceProvider(service = IGamePluginService.class)})

public class EnemyControlSystem implements IEntityProcessingService, IGamePluginService {

    private Entity enemy;
    private Random rand = new Random();

    public void shoot(GameData gameData, Entity e) {

        if (e.getShootTimer() > 200 - rand.nextInt(50)) {
            gameData.addEvent(new Event(EventType.ENEMY_SHOOT, e.getID()));
            e.setShootTimer(0);
        }
    }

    public void followPlayer(Entity e, World world, GameData gameData) {

        for (Entity player : world.getEntities(PLAYER)) {

            float distanceX = player.getX() - e.getX();
            float distanceY = player.getY() - e.getY();
            
            //Follow
            e.setRadians((float) Math.atan2(distanceY, distanceX));
            e.setDx(distanceX);
            e.setDy(distanceY);
        }
    }

    public void deacceleration(Entity e, GameData gameData) {
        //Deacceleration
        float vec = (float) Math.sqrt(e.getDx() * e.getDx() + e.getDy() * e.getDy());
        if (vec > 0) {
            e.setDx(e.getDx() - (e.getDx() / vec) * e.getDeacceleration() * gameData.getDelta());
            e.setDy(e.getDy() - (e.getDy() / vec) * e.getDeacceleration() * gameData.getDelta());
        }

        if (vec > e.getMaxSpeed()) {
            e.setDx((e.getDx() / vec) * e.getMaxSpeed());
            e.setDy((e.getDy() / vec) * e.getMaxSpeed());
        }
    }

    public void drawEnemy(Entity e) {
        //Draw the enemy
        e.setShapeX(new float[]{
            e.getX() + (float) (Math.cos(e.getRadians()) * 8),
            e.getX() + (float) (Math.cos(e.getRadians() - 4 * 3.1415f / 5) * 8),
            e.getX() + (float) (Math.cos(e.getRadians() + 3.1415f) * 5),
            e.getX() + (float) (Math.cos(e.getRadians() + 4 * 3.1415f / 5) * 8)});

        e.setShapeY(new float[]{
            e.getY() + (float) (Math.sin(e.getRadians()) * 8),
            e.getY() + (float) (Math.sin(e.getRadians() - 4 * 3.1415f / 5) * 8),
            e.getY() + (float) (Math.sin(e.getRadians() + 3.1415f) * 5),
            e.getY() + (float) (Math.sin(e.getRadians() + 4 * 3.1415f / 5) * 8)});
    }

    @Override
    public void process(GameData gameData, World world) {
        
        for (Entity e : world.getEntities(ENEMY)) {

            e.setShootTimer(e.getShootTimer() + 1);
            followPlayer(e, world, gameData);
            deacceleration(e, gameData);

            //Set position
            e.setX(e.getX() + e.getDx() * gameData.getDelta());
            e.setY(e.getY() + e.getDy() * gameData.getDelta());

            drawEnemy(e);
            shoot(gameData, e);
            

        }
    }
    
    private Entity createEnemyShip(GameData gameData) {
        Random rand = new Random();
        Entity enemyShip = new Entity();
        enemyShip.setType(ENEMY);
        enemyShip.setPosition(rand.nextInt(gameData.getDisplayWidth()), rand.nextInt(gameData.getDisplayHeight()));
        enemyShip.setMaxSpeed(30);
        enemyShip.setAcceleration(10);
        enemyShip.setDeacceleration(10);

        enemyShip.setRadians(3.1415f / 2);
        enemyShip.setRotationSpeed(3);

        return enemyShip;
    }

    @Override
    public void start(GameData gameData, World world) {
        enemy = createEnemyShip(gameData);
        world.addEntity(enemy);
    }

    @Override
    public void stop(GameData gameData, World world) {
        world.removeEntity(enemy);
    }
}
