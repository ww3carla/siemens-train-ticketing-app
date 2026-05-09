package com.siemens.internship.optimizer.service;

import com.siemens.internship.optimizer.dto.OptimizedRouteRequest;
import com.siemens.internship.optimizer.dto.OptimizedRouteResponse;
import com.siemens.internship.optimizer.model.OptimizationCriterion;
import com.siemens.internship.optimizer.model.OptimizedRoute;
import com.siemens.internship.optimizer.model.TravelConnection;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

@Service
public class RouteOptimizerService {

    private final List<TravelConnection> connections = List.of(
            new TravelConnection("Cluj-Napoca", "Alba Iulia", 95, BigDecimal.valueOf(38), 100),
            new TravelConnection("Alba Iulia", "Sibiu", 80, BigDecimal.valueOf(32), 75),
            new TravelConnection("Sibiu", "Brasov", 135, BigDecimal.valueOf(55), 145),
            new TravelConnection("Brasov", "Bucharest", 160, BigDecimal.valueOf(70), 170),
            new TravelConnection("Brasov", "Iasi", 300, BigDecimal.valueOf(110), 310),
            new TravelConnection("Bucharest", "Iasi", 390, BigDecimal.valueOf(125), 390),
            new TravelConnection("Cluj-Napoca", "Brasov", 270, BigDecimal.valueOf(120), 280),
            new TravelConnection("Alba Iulia", "Brasov", 230, BigDecimal.valueOf(80), 220),
            new TravelConnection("Sibiu", "Bucharest", 280, BigDecimal.valueOf(95), 275)
    );

    public OptimizedRouteResponse findOptimalRoute(OptimizedRouteRequest request) {
        String from = normalizeCity(request.from());
        String to = normalizeCity(request.to());

        if (from.equalsIgnoreCase(to)) {
            throw new OptimizationException("Origin and destination must be different.");
        }

        validateCityExists(from);
        validateCityExists(to);

        OptimizedRoute route = calculateShortestPath(from, to, request.criterion());

        return new OptimizedRouteResponse(
                route.from(),
                route.to(),
                route.criterion(),
                route.path(),
                route.totalDurationMinutes(),
                route.totalCost(),
                route.totalDistanceKm()
        );
    }

    private OptimizedRoute calculateShortestPath(
            String from,
            String to,
            OptimizationCriterion criterion
    ) {
        Map<String, List<TravelConnection>> graph = buildGraph();

        Map<String, Integer> distances = new HashMap<>();
        Map<String, String> previousCity = new HashMap<>();

        PriorityQueue<RouteNode> queue = new PriorityQueue<>(
                Comparator.comparingInt(RouteNode::distance)
        );

        for (String city : graph.keySet()) {
            distances.put(city, Integer.MAX_VALUE);
        }

        distances.put(from, 0);
        queue.add(new RouteNode(from, 0));

        while (!queue.isEmpty()) {
            RouteNode currentNode = queue.poll();
            String currentCity = currentNode.city();

            if (currentNode.distance() > distances.get(currentCity)) {
                continue;
            }

            if (currentCity.equals(to)) {
                break;
            }

            for (TravelConnection connection : graph.getOrDefault(currentCity, List.of())) {
                String neighbor = connection.to();
                int newDistance = distances.get(currentCity) + connection.weightFor(criterion);

                if (newDistance < distances.getOrDefault(neighbor, Integer.MAX_VALUE)) {
                    distances.put(neighbor, newDistance);
                    previousCity.put(neighbor, currentCity);
                    queue.add(new RouteNode(neighbor, newDistance));
                }
            }
        }

        if (!previousCity.containsKey(to)) {
            throw new OptimizationException("No optimized route was found between the selected cities.");
        }

        List<String> path = reconstructPath(from, to, previousCity);
        RouteTotals totals = calculateTotals(path);

        return new OptimizedRoute(
                from,
                to,
                criterion,
                path,
                totals.durationMinutes(),
                totals.cost(),
                totals.distanceKm()
        );
    }

    private Map<String, List<TravelConnection>> buildGraph() {
        Map<String, List<TravelConnection>> graph = new HashMap<>();

        for (TravelConnection connection : connections) {
            graph.computeIfAbsent(connection.from(), ignored -> new ArrayList<>())
                    .add(connection);

            TravelConnection reverseConnection = new TravelConnection(
                    connection.to(),
                    connection.from(),
                    connection.durationMinutes(),
                    connection.cost(),
                    connection.distanceKm()
            );

            graph.computeIfAbsent(reverseConnection.from(), ignored -> new ArrayList<>())
                    .add(reverseConnection);
        }

        return graph;
    }

    private List<String> reconstructPath(
            String from,
            String to,
            Map<String, String> previousCity
    ) {
        LinkedList<String> path = new LinkedList<>();
        String current = to;

        path.addFirst(current);

        while (!current.equals(from)) {
            current = previousCity.get(current);

            if (current == null) {
                throw new OptimizationException("Could not reconstruct optimized route.");
            }

            path.addFirst(current);
        }

        return path;
    }

    private RouteTotals calculateTotals(List<String> path) {
        int totalDuration = 0;
        BigDecimal totalCost = BigDecimal.ZERO;
        int totalDistance = 0;

        for (int index = 0; index < path.size() - 1; index++) {
            String from = path.get(index);
            String to = path.get(index + 1);

            TravelConnection connection = findConnection(from, to);

            totalDuration += connection.durationMinutes();
            totalCost = totalCost.add(connection.cost());
            totalDistance += connection.distanceKm();
        }

        return new RouteTotals(totalDuration, totalCost, totalDistance);
    }

    private TravelConnection findConnection(String from, String to) {
        return connections.stream()
                .filter(connection -> connects(connection, from, to))
                .findFirst()
                .orElseThrow(() -> new OptimizationException(
                        "Connection between " + from + " and " + to + " was not found."
                ));
    }

    private boolean connects(TravelConnection connection, String from, String to) {
        return connection.from().equals(from) && connection.to().equals(to)
                || connection.from().equals(to) && connection.to().equals(from);
    }

    private void validateCityExists(String city) {
        boolean exists = connections.stream()
                .anyMatch(connection -> connection.from().equalsIgnoreCase(city)
                        || connection.to().equalsIgnoreCase(city));

        if (!exists) {
            throw new OptimizationException("City " + city + " does not exist in the optimizer network.");
        }
    }

    private String normalizeCity(String city) {
        if (city == null || city.isBlank()) {
            throw new OptimizationException("City name must not be blank.");
        }

        return connections.stream()
                .flatMap(connection -> List.of(connection.from(), connection.to()).stream())
                .filter(existingCity -> existingCity.equalsIgnoreCase(city.trim()))
                .findFirst()
                .orElse(city.trim());
    }

    private record RouteNode(String city, int distance) {
    }

    private record RouteTotals(int durationMinutes, BigDecimal cost, int distanceKm) {
    }
}