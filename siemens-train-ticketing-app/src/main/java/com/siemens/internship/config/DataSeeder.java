package com.siemens.internship.config;

import com.siemens.internship.model.Route;
import com.siemens.internship.model.Station;
import com.siemens.internship.model.Train;
import com.siemens.internship.model.TrainSchedule;
import com.siemens.internship.repository.RouteRepository;
import com.siemens.internship.repository.StationRepository;
import com.siemens.internship.repository.TrainRepository;
import com.siemens.internship.repository.TrainScheduleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
public class DataSeeder {

    @Bean
    @Transactional
    CommandLineRunner seedData(
            StationRepository stationRepository,
            TrainRepository trainRepository,
            RouteRepository routeRepository,
            TrainScheduleRepository trainScheduleRepository
    ) {
        return args -> {
            if (stationRepository.count() > 0) {
                return;
            }

            Station cluj = stationRepository.save(new Station("Cluj-Napoca"));
            Station albaIulia = stationRepository.save(new Station("Alba Iulia"));
            Station sibiu = stationRepository.save(new Station("Sibiu"));
            Station brasov = stationRepository.save(new Station("Brasov"));
            Station bucharest = stationRepository.save(new Station("Bucharest"));
            Station iasi = stationRepository.save(new Station("Iasi"));

            Train interRegio = trainRepository.save(new Train("IR-101", "Transylvania Express", 120));
            Train regio = trainRepository.save(new Train("R-202", "Carpathian Connector", 80));
            Train moldovaExpress = trainRepository.save(new Train("IR-303", "Moldova Express", 100));

            Route clujToBucharest = new Route("Cluj-Napoca to Bucharest");
            clujToBucharest.addStop(cluj, 1);
            clujToBucharest.addStop(albaIulia, 2);
            clujToBucharest.addStop(sibiu, 3);
            clujToBucharest.addStop(brasov, 4);
            clujToBucharest.addStop(bucharest, 5);

            Route brasovToIasi = new Route("Brasov to Iasi");
            brasovToIasi.addStop(brasov, 1);
            brasovToIasi.addStop(bucharest, 2);
            brasovToIasi.addStop(iasi, 3);

            Route sibiuToBrasov = new Route("Sibiu to Brasov");
            sibiuToBrasov.addStop(sibiu, 1);
            sibiuToBrasov.addStop(brasov, 2);

            routeRepository.saveAll(List.of(clujToBucharest, brasovToIasi, sibiuToBrasov));

            trainScheduleRepository.save(new TrainSchedule(
                    interRegio,
                    clujToBucharest,
                    LocalDateTime.of(2026, 5, 1, 8, 0),
                    LocalDateTime.of(2026, 5, 1, 16, 30)
            ));

            trainScheduleRepository.save(new TrainSchedule(
                    regio,
                    sibiuToBrasov,
                    LocalDateTime.of(2026, 5, 1, 12, 0),
                    LocalDateTime.of(2026, 5, 1, 14, 15)
            ));

            trainScheduleRepository.save(new TrainSchedule(
                    moldovaExpress,
                    brasovToIasi,
                    LocalDateTime.of(2026, 5, 1, 17, 0),
                    LocalDateTime.of(2026, 5, 1, 23, 30)
            ));
        };
    }
}