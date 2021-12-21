package agh.ics.oop;

import agh.ics.oop.gui.MapVisualizer;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class SimulationEngine  implements  Runnable{

    private final AbstractWorldMap map;
    private final ArrayList<Vector2d> positions = new ArrayList<>();
    private final MapVisualizer observer;
    private final int moveDelay;
    private AtomicBoolean stop;




    public SimulationEngine(AbstractWorldMap map, ArrayList<Vector2d> initialPositions, MapVisualizer mapVisualizer, int moveDelay,AtomicBoolean stop) {

        this.map = map;
        this.moveDelay = moveDelay;
        this.observer = mapVisualizer;
        this.stop = stop;

        for(Vector2d position : initialPositions){
            if(map.place(new Animal(this.map,position))) positions.add(position) ;
        }

    }



    @Override
    public void run() {

        Platform.runLater(observer::positionChanged);
        waitForRunLater();
        System.out.println(map);
        int dayCounter = 1;
        while(!map.animals.isEmpty()){

            System.out.println("********DZIEN NR " + dayCounter + "********");
            System.out.println("");


            ArrayList<Animal> animalsToRemove = new ArrayList<>();
            // usuwanie martwych zwierząt
            System.out.println("***USUWANIE MARTWYCH***");
            for(Animal animal : map.animals){
                if(animal.getEnergy() <= 0) animalsToRemove.add(animal);
            }
            for(Animal deadAnimal : animalsToRemove){

                System.out.println("USUWAM: " +deadAnimal.reports);
                deadAnimal.removeObserver(this.map);
                map.removeAnimal(deadAnimal);
            }

            // ruch albo skręt zwierzęcia
            System.out.println("***RUCH ZWIERZAT***");

            for(Animal animal : map.animals){
                animal.move(animal.getRandomGen());

            }
            for(Animal animal :map.animals){
                animal.changeEnergy(-1);
                System.out.println("ID: "+animal.myID+" nowa pozycja: "+ animal.getPosition()+ " nowy kierunek: "+ animal.getDirection() + " pozostało energii: " + (animal.getEnergy()));

            }

            //jedzenie roślin
            System.out.println("***JEDZENIE ROSLIN***");
            ArrayList<Grass> grassToRemove = new ArrayList<>();
            for(Vector2d position : map.animalsMap.keySet()){
                if(map.grassMap.containsKey(position)){

                    StringBuilder eatingReportBuilder = new StringBuilder();
                    eatingReportBuilder.append("jemy na pozycji: ").append(position).append(" Obecni: ");
                    for(Animal a :map.animalsMap.get(position)){
                        eatingReportBuilder.append(a.myID).append(", ");
                    }


                    int maxEnergy = map.animalsMap.get(position).get(0).getEnergy();
                    eatingReportBuilder.append(" potrzebna energia: ").append(maxEnergy);
                    List<Animal> banqueters = map.animalsMap.get(position).stream().filter(a -> a.getEnergy() == maxEnergy).collect(Collectors.toList());
                    eatingReportBuilder.append(" kto je: ");
                    for( Animal a :banqueters){
                        eatingReportBuilder.append(a.myID).append(", ");
                    }
                    for(Animal animal:banqueters){
                        animal.changeEnergy(10/banqueters.size());
                    }
                    eatingReportBuilder.append(" zyskują: ").append(10 / banqueters.size()).append(" energi");
                    System.out.println(eatingReportBuilder);
                    grassToRemove.add(map.grassMap.get(position));
                }
            }
            for(Grass grass : grassToRemove){
                map.grassMap.remove(grass.getPosition());
            }
            // rozmnażanie zwierząt
            System.out.println("***ROZMNAŻANIE***");
            for(Vector2d position : map.animalsMap.keySet()){
                if(map.animalsMap.get(position).size() > 1){
                    StringBuilder breedingReportBuilder = new StringBuilder();
                    breedingReportBuilder.append("Na pozycji: ").append(position).append("stoją zwierzęta: ");
                    for(Animal a :map.animalsMap.get(position)){
                        breedingReportBuilder.append(a.myID).append(" energia: ").append(a.getEnergy()).append(", ");
                    }
                    Animal strongerParent,weakerParent;
                    int maxEnergy = map.animalsMap.get(position).get(0).getEnergy();
                    ArrayList<Animal> candidats = (ArrayList<Animal>) map.animalsMap.get(position).stream().filter(a -> a.getEnergy() == maxEnergy).collect(Collectors.toList());
                    if (candidats.size() >= 2){
                        ArrayList<Animal> parents = drawParents(candidats,2);
                        strongerParent = parents.get(0);
                        weakerParent = parents.get(1);

                    }
                    else{
                        int secondMaxEnergy = map.animalsMap.get(position).get(1).getEnergy();
                        strongerParent = candidats.get(0);
                        candidats = (ArrayList<Animal>) map.animalsMap.get(position).stream().filter(a -> a.getEnergy() == secondMaxEnergy).collect(Collectors.toList());
                        weakerParent = drawParents(candidats,1).get(0);

                    }
                    breedingReportBuilder.append("WYBRANO: ").append(strongerParent.myID).append(", ").append(weakerParent.myID);

                    if(weakerParent.getEnergy() > 20){
                        breedingReportBuilder.append(" ZWIERZETA ROZMNAZAJA SIE! ");
                        Animal child = new Animal(map,position,strongerParent,weakerParent);
                        breedingReportBuilder.append("id dziecka: ").append(child.myID).append(" energia dziecka: ").append(child.getEnergy());
                        map.animals.add(child);
                        map.animalsMap.get(position).add(child);
                        Collections.sort(map.animalsMap.get(position));
                        child.addObserver(map);
                    }
                    else{
                        breedingReportBuilder.append(" NIE ROZMNAZAJA SIE!");
                    }
                    System.out.println(breedingReportBuilder);


                }
            }


            // dodanie nowych roślin

            this.map.addGrass(1);



            try {
                Thread.sleep(moveDelay);
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
            }

            Platform.runLater(observer::positionChanged);
            waitForRunLater();
            System.out.println(map);
            System.out.println("*******KONIEC DNIA NR: "+ dayCounter++ + "**********");
        }



    }
    private ArrayList<Animal> drawParents(ArrayList <Animal> candidats,int quantity){
        Random rd = new Random();
        ArrayList <Animal> result = new ArrayList<>();
        Animal chosenOne;
        for(int i =0;i<quantity;i++){
             chosenOne= candidats.remove(rd.nextInt(candidats.size()));
            result.add(chosenOne);
        }
        return result;
    }



    public static void waitForRunLater()  {
        Semaphore semaphore = new Semaphore(0);
        Platform.runLater(() -> semaphore.release());
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
