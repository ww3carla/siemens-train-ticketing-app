package com.siemens.internship.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(
        name = "route_stops",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_route_stop_order",
                        columnNames = {"route_id", "stop_order"}
                ),
                @UniqueConstraint(
                        name = "uk_route_station",
                        columnNames = {"route_id", "station_id"}
                )
        }
)
public class RouteStop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", nullable = false)
    private Station station;

    @Column(name = "stop_order", nullable = false)
    private int stopOrder;

    protected RouteStop() {
    }

    RouteStop(Route route, Station station, int stopOrder) {
        this.route = route;
        this.station = station;
        this.stopOrder = stopOrder;
    }

    public Long getId() {
        return id;
    }

    public Route getRoute() {
        return route;
    }

    public Station getStation() {
        return station;
    }

    public int getStopOrder() {
        return stopOrder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RouteStop routeStop)) return false;
        return id != null && Objects.equals(id, routeStop.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}