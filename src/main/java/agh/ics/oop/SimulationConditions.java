package agh.ics.oop;

import java.util.concurrent.atomic.AtomicBoolean;

public class SimulationConditions {

    private final int moveDelay;
    private boolean isRunning;
    private final int startEnergy;
    private final int moveEnergy;
    private final int plantEnergy;
    private final int animalQuantity;

    public SimulationConditions(int moveDelay, boolean isRunning, int startEnergy, int moveEnergy, int plantEnergy, int animalQuantity) {
        this.moveDelay = moveDelay;
        this.isRunning = isRunning;
        this.startEnergy = startEnergy;
        this.moveEnergy = moveEnergy;
        this.plantEnergy = plantEnergy;
        this.animalQuantity = animalQuantity;
    }

    public int getMoveDelay() {
        return moveDelay;
    }

    public boolean IsRunning() {
        return isRunning;
    }

    public void setIsRunning(boolean isRunning){
        this.isRunning=isRunning;
    }

    public int getStartEnergy() {
        return startEnergy;
    }

    public int getMoveEnergy() {
        return moveEnergy;
    }

    public int getPlantEnergy() {
        return plantEnergy;
    }

    public int getAnimalQuantity() {
        return animalQuantity;
    }
}
