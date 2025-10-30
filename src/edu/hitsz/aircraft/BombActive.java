package edu.hitsz.aircraft;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"unused"})
public class BombActive {
    private final List<ObserverAircraft> aircraftList = new ArrayList<>();

    public void addAircraft(ObserverAircraft aircraft){
        aircraftList.add(aircraft);
    }

    public void removeAircraft(ObserverAircraft aircraft){
        aircraftList.remove(aircraft);
    }

    public void notifyAircrafts(){
        for(ObserverAircraft aircraft : aircraftList){
            aircraft.update();
        }
    }

    public void catchBombSupply(){
        notifyAircrafts();
    }
}
