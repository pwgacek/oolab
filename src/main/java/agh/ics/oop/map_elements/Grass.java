package agh.ics.oop.map_elements;

public class Grass implements IMapElement{
    private final Vector2d position;

    public Grass(Vector2d position) {
        this.position = position;
    }

    public Vector2d getPosition() {
        return position;
    }

    @Override
    public String getImgPath() {
        return "src/main/resources/green.png";
    }

}
