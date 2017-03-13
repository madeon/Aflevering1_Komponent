package dk.sdu.mmmi.cbse.wrap;

import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.services.IEntityProcessingService;
import dk.sdu.mmmi.cbse.common.services.IGamePluginService;
import org.openide.util.lookup.ServiceProvider;
import org.openide.util.lookup.ServiceProviders;


    @ServiceProvider(service = IEntityProcessingService.class)
    

public class WrapControlSystem implements IEntityProcessingService {

    @Override
    public void process(GameData gameData, World world) {

        for (Entity e : world.getEntities()) {

            if (e.getWrap()) {

                if (e.getX() < 0) {
                    e.setX(gameData.getDisplayWidth());
                }

                if (e.getX() > gameData.getDisplayWidth()) {
                    e.setX(0);
                }

                if (e.getY() < 0) {
                    e.setY(gameData.getDisplayHeight());
                }

                if (e.getY() > gameData.getDisplayHeight()) {
                    e.setY(0);
                }
            } else {

                if (e.getX() < 0) {
                    world.removeEntity(e);
                }

                if (e.getX() > gameData.getDisplayWidth()) {
                    world.removeEntity(e);
                }

                if (e.getY() < 0) {
                    world.removeEntity(e);
                }

                if (e.getY() > gameData.getDisplayHeight()) {
                    world.removeEntity(e);
                }

            }
        }
    }
}
