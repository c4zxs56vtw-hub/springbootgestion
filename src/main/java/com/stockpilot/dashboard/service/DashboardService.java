package com.stockpilot.dashboard.service;

import com.stockpilot.dashboard.dto.DailySeriesResponse;
import com.stockpilot.dashboard.dto.DashboardCountsResponse;
import com.stockpilot.dashboard.dto.DashboardPeriodResponse;
import com.stockpilot.dashboard.dto.DashboardResponse;
import com.stockpilot.dashboard.dto.MovementCountsResponse;
import com.stockpilot.product.model.Product;
import com.stockpilot.product.model.ProductStatus;
import com.stockpilot.product.model.StockStatus;
import com.stockpilot.product.repository.ProductRepository;
import com.stockpilot.stockmovement.dto.StockMovementResponse;
import com.stockpilot.stockmovement.model.MovementType;
import com.stockpilot.stockmovement.model.StockMovement;
import com.stockpilot.stockmovement.repository.StockMovementRepository;
import com.stockpilot.stockmovement.service.StockMovementService;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
public class DashboardService {

    private static final ZoneId ZONE =
            ZoneId.of("Africa/Douala");

    private final ProductRepository productRepository;
    private final StockMovementRepository stockMovementRepository;
    private final StockMovementService stockMovementService;

    public DashboardService(
            ProductRepository productRepository,
            StockMovementRepository stockMovementRepository,
            StockMovementService stockMovementService
    ) {
        this.productRepository = productRepository;
        this.stockMovementRepository = stockMovementRepository;
        this.stockMovementService = stockMovementService;
    }

    public DashboardResponse getDashboard(
            Instant from,
            Instant to
    ) {

        List<Product> products =
                productRepository.findAll();

        List<StockMovement> movements =
                stockMovementRepository.findAll();

        // 1. Filtrer les mouvements selon la période
        List<StockMovement> filteredMovements =
                movements.stream()
                        .filter(movement ->
                                from == null
                                        || !movement.getCreatedAt().isBefore(from)
                        )
                        .filter(movement ->
                                to == null
                                        || !movement.getCreatedAt().isAfter(to)
                        )
                        .toList();

        // 2. Compteurs produits
        long activeProducts =
                products.stream()
                        .filter(product ->
                                product.getStatus() == ProductStatus.ACTIVE
                        )
                        .count();

        long lowStockProducts =
                products.stream()
                        .filter(product ->
                                product.getStatus() == ProductStatus.ACTIVE
                                        && product.getStockStatus()
                                        == StockStatus.LOW_STOCK
                        )
                        .count();

        long outOfStockProducts =
                products.stream()
                        .filter(product ->
                                product.getStatus() == ProductStatus.ACTIVE
                                        && product.getStockStatus()
                                        == StockStatus.OUT_OF_STOCK
                        )
                        .count();

        DashboardCountsResponse counts =
                new DashboardCountsResponse(
                        activeProducts,
                        lowStockProducts,
                        outOfStockProducts
                );

        // 3. Valeur totale du stock
        BigInteger stockValue = BigInteger.ZERO;

        for (Product product : products) {

            if (product.getStatus() != ProductStatus.ACTIVE) {
                continue;
            }

            BigInteger purchasePrice =
                    new BigInteger(
                            product.getPurchasePrice()
                    );

            BigInteger quantity =
                    BigInteger.valueOf(
                            product.getQuantityInStock()
                    );

            stockValue =
                    stockValue.add(
                            purchasePrice.multiply(quantity)
                    );
        }

        // 4. Nombre d'entrées et sorties
        long inCount =
                filteredMovements.stream()
                        .filter(movement ->
                                movement.getType() == MovementType.IN
                        )
                        .count();

        long outCount =
                filteredMovements.stream()
                        .filter(movement ->
                                movement.getType() == MovementType.OUT
                        )
                        .count();

        MovementCountsResponse movementCounts =
                new MovementCountsResponse(
                        inCount,
                        outCount
                );

        // 5. Série journalière
        List<DailySeriesResponse> dailySeries =
                buildDailySeries(filteredMovements);

        // 6. Derniers mouvements
        List<StockMovementResponse> recentMovements =
                filteredMovements.stream()
                        .sorted(
                                Comparator.comparing(
                                        StockMovement::getCreatedAt
                                ).reversed()
                        )
                        .limit(5)
                        .map(stockMovementService::toResponse)
                        .toList();

        DashboardPeriodResponse period =
                new DashboardPeriodResponse(
                        from,
                        to,
                        "Africa/Douala"
                );

        return new DashboardResponse(
                "XAF",
                period,
                counts,
                stockValue.toString(),
                movementCounts,
                dailySeries,
                recentMovements
        );
    }

    private List<DailySeriesResponse> buildDailySeries(
            List<StockMovement> movements
    ) {

        Map<LocalDate, long[]> daily =
                new TreeMap<>();

        for (StockMovement movement : movements) {

            LocalDate date =
                    movement.getCreatedAt()
                            .atZone(ZONE)
                            .toLocalDate();

            long[] values =
                    daily.computeIfAbsent(
                            date,
                            ignored -> new long[3]
                    );

            if (movement.getType() == MovementType.IN) {
                values[0]++;
            }

            if (movement.getType() == MovementType.OUT) {
                values[1]++;
            }

            if (movement.getType() == MovementType.ADJUSTMENT) {
                values[2]++;
            }
        }

        List<DailySeriesResponse> result =
                new ArrayList<>();

        daily.forEach((date, values) ->
                result.add(
                        new DailySeriesResponse(
                                date,
                                values[0],
                                values[1],
                                values[2]
                        )
                )
        );

        return result;
    }
}