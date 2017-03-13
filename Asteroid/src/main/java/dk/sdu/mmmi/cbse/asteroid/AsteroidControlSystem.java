package dk.sdu.mmmi.cbse.asteroid;

import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.EntityType;
import static dk.sdu.mmmi.cbse.common.data.EntityType.ASTEROIDS;
import static dk.sdu.mmmi.cbse.common.data.EntityType.ENEMY;
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

public class AsteroidControlSystem implements IGamePluginService, IEntityProcessingService {

    private Entity asteroid;
    private Random rand = new Random();
    private float x, y, radiant;

    public AsteroidControlSystem() {

    }

    public AsteroidControlSystem(float x, float y, float radiant) {
        this.x = x;
        this.y = y;
        this.radiant = radiant;
    }

    private Entity createAsteroid(GameData gameData) {

        Entity asteroid = new Entity();
        asteroid.setType(ASTEROIDS);

        float randomNumber = rand.nextFloat();

        if (rand.nextFloat() < 0.25f) {
            asteroid.setPosition(gameData.getDisplayWidth() * rand.nextFloat(), 0);
        } else if (0.25f <= rand.nextFloat() && rand.nextFloat() < 0.49f) {
            asteroid.setPosition(0, gameData.getDisplayHeight() * rand.nextFloat());
        } else if (0.50f <= rand.nextFloat() && rand.nextFloat() < 0.74f) {
            asteroid.setPosition(gameData.getDisplayWidth(), gameData.getDisplayHeight() * rand.nextFloat());
        } else if (0.75f <= rand.nextFloat() && rand.nextFloat() < 1) {
            asteroid.setPosition(gameData.getDisplayWidth() * rand.nextFloat(), gameData.getDisplayHeight());
        }

        asteroid.setMaxSpeed(300);
        asteroid.setAcceleration(200);
        asteroid.setDeacceleration(10);
        asteroid.setRadius(rand.nextInt(25));
        asteroid.setRadians(3.1415f / 2);
        asteroid.setRotationSpeed(3);

        return asteroid;
    }

    public Entity createSplitAsteroid() {
        Entity asteroid = new Entity();
        asteroid.setType(ASTEROIDS);
        System.out.println("split asteroid entity is created");
        return asteroid;
    }

    @Override
    public void start(GameData gameData, World world) {

        for (int i = 0; i < 10; i++) {
            asteroid = createAsteroid(gameData);
            world.addEntity(asteroid);
        }

    }

    @Override
    public void stop(GameData gameData, World world) {
        world.removeEntity(asteroid);
    }

    public void drawAsteroid(Entity e) {
        int corners = 6;
        float[] xval = new float[corners];
        float[] yval = new float[corners];

        for (int i = 0; i < corners; i++) {
            xval[i] = e.getX() + (float) (e.getRadius() * Math.cos(i * 2 * Math.PI / 6.0));
            yval[i] = e.getY() + (float) (e.getRadius() * Math.sin(i * 2 * Math.PI / 6.0));
        }

        e.setShapeX(xval);
        e.setShapeY(yval);
    }

    public void deaccelerate(Entity e, GameData gameData) {
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

    public void moveAsteroid(Entity e, GameData gameData) {
        e.setDx((float) (e.getDx() + Math.cos(rand.nextInt() * e.getRadians()) * e.getAcceleration() * gameData.getDelta()));
        e.setDy((float) (e.getDy() + Math.sin(rand.nextInt() * e.getRadians()) * e.getAcceleration() * gameData.getDelta()));
        e.setX(e.getX() + e.getDx() * gameData.getDelta());
        e.setY(e.getY() + e.getDy() * gameData.getDelta());
    }

    private void addAsteroidEvent(World world, GameData gameData, Event e) {
        world.addEntity(createAsteroid(gameData));
    }

    @Override
    public void process(GameData gameData, World world) {

        for (Entity e : world.getEntities(ASTEROIDS)) {
            drawAsteroid(e);
            moveAsteroid(e, gameData);
            deaccelerate(e, gameData);
        }

        for (Event e : gameData.getEvents()) {
            if (e.getType() == EventType.ASTEROID_SPLIT) {
                addAsteroidEvent(world, gameData, e);
            }
        }
    }
}
