package agh.ics.oop.maps;

import agh.ics.oop.map_elements.Vector2d;
import agh.ics.oop.conditions.MapConditions;


public class GrassField extends AbstractWorldMap {


    public GrassField(MapConditions mapConditions) {
        super(mapConditions);
    }

    @Override
    public boolean canMoveTo(Vector2d position) {
        return position.x <= width && position.x >= 0 && position.y <= height && position.y >= 0;
    }


}
