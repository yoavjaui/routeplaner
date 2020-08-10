package de.templum.routeplaner.computing;

import java.util.List;

import de.templum.routeplaner.model.RoutePoint;
import io.reactivex.Observable;

public interface RouteCalculator {
    Observable<List<RoutePoint>> calculate(List<RoutePoint> initialRoute);
}
