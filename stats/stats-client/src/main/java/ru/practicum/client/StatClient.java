package ru.practicum.client;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ru.practicum.client.exception.StatisticClientException;
import ru.practicum.dto.in.StatisticDto;
import ru.practicum.dto.output.GetStatisticDto;

import javax.management.ServiceNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class StatClient {
    private final DiscoveryClient discoveryClient;
    private RestClient restClient;
    private boolean initialized = false;

    @Autowired
    public StatClient(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    private void init() throws ServiceNotFoundException {
        this.restClient = RestClient.builder()
                .baseUrl("http://stats-server:" + getInstance().getPort())
                .defaultStatusHandler(
                        HttpStatusCode::isError,
                        (request, response) -> {
                            throw new StatisticClientException("Statistics service error: " + response.getStatusText());
                        })
                .build();
    }

    public void hit(StatisticDto statisticDto) {
        try {
            if (!initialized) {
                init();
            }
            log.info("Sending statistics hit request to client");
            restClient.post()
                    .uri("/hit")
                    .body(statisticDto)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.error("Error saving statistics in client: {}, {}", statisticDto, e.getMessage());
            throw new StatisticClientException("Error sending statistics", e);
        }
    }

    public List<GetStatisticDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        try {
            if (!initialized) {
                init();
            }
            log.info("Requesting statistics from client");
            return restClient
                    .get()
                    .uri(uriBuilder -> uriBuilder.path("/stats")
                            .queryParam("start", start.format(dateTimeFormat))
                            .queryParam("end", end.format(dateTimeFormat))
                            .queryParam("uris", uris != null ? uris : Collections.emptyList())
                            .queryParam("unique", unique)
                            .build())
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });
        } catch (Exception e) {
            log.error("Error retrieving statistics from client: {}", e.getMessage());
            throw new StatisticClientException("Error getting statistics", e);
        }
    }

    private ServiceInstance getInstance() throws ServiceNotFoundException {
        String statsServiceId = "stats-server";
        try {
            if (!initialized) {
                init();
            }
            return discoveryClient
                    .getInstances(statsServiceId)
                    .getFirst();
        } catch (Exception exception) {
            throw new ServiceNotFoundException(
                    "Ошибка обнаружения адреса сервиса статистики с id: " + statsServiceId +
                            " trace: " + exception.getMessage()
            );
        }
    }
}