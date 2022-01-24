package pl.kskowronski.data.service.admin.reportPermission;

import com.vaadin.flow.component.crud.CrudFilter;
import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.SortDirection;
import pl.kskowronski.data.entity.report.ReportPermission;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;


public class ReportPermDataProvider extends AbstractBackEndDataProvider<ReportPermission, CrudFilter> {

    // A real app should hook up something like JPA
    List<ReportPermission> DATABASE;
    private ReportPermService reportPermService;
    private Consumer<Long> sizeChangeListener;


    public ReportPermDataProvider(ReportPermService reportPermService, BigDecimal idRap) {
        this.reportPermService = reportPermService;
        DATABASE = new ArrayList<>(reportPermService.findReportPermissionByPermRapIdOrderById(idRap));
    }

    private static Predicate<ReportPermission> predicate(CrudFilter filter) {
        // For RDBMS just generate a WHERE clause
        return filter.getConstraints().entrySet().stream()
                .map(constraint -> (Predicate<ReportPermission>) item -> {
                    try {
                        Object value = valueOf(constraint.getKey(), item);
                        return value != null && value.toString().toLowerCase()
                                .contains(constraint.getValue().toLowerCase());
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                })
                .reduce(Predicate::and)
                .orElse(e -> true);
    }

    private static Comparator<ReportPermission> comparator(CrudFilter filter) {
        // For RDBMS just generate an ORDER BY clause
        return filter.getSortOrders().entrySet().stream()
                .map(sortClause -> {
                    try {
                        Comparator<ReportPermission> comparator = Comparator.comparing(item ->
                                (Comparable) valueOf(sortClause.getKey(), item)
                        );

                        if (sortClause.getValue() == SortDirection.DESCENDING) {
                            comparator = comparator.reversed();
                        }

                        return comparator;

                    } catch (Exception ex) {
                        return (Comparator<ReportPermission>) (o1, o2) -> 0;
                    }
                })
                .reduce(Comparator::thenComparing)
                .orElse((o1, o2) -> 0);
    }

    private static Object valueOf(String fieldName, ReportPermission person) {
        try {
            Field field = ReportPermission.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(person);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    protected Stream<ReportPermission> fetchFromBackEnd(Query<ReportPermission, CrudFilter> query) {
        int offset = query.getOffset();
        int limit = query.getLimit();

        Stream<ReportPermission> stream = DATABASE.stream();

        if (query.getFilter().isPresent()) {
            stream = stream
                    .filter(predicate(query.getFilter().get()))
                    .sorted(comparator(query.getFilter().get()));
        }

        return stream.skip(offset).limit(limit);
    }

    @Override
    protected int sizeInBackEnd(Query<ReportPermission, CrudFilter> query) {
        // For RDBMS just execute a SELECT COUNT(*) ... WHERE query
        long count = fetchFromBackEnd(query).count();

        if (sizeChangeListener != null) {
            sizeChangeListener.accept(count);
        }

        return (int) count;
    }

    void setSizeChangeListener(Consumer<Long> listener) {
        sizeChangeListener = listener;
    }

    public void persist(ReportPermission item) {
        if (item.getPermId() == null) {
            Comparator<ReportPermission> comparator = Comparator.comparing( ReportPermission::getPermId );
            BigDecimal max = DATABASE.stream().max(comparator).get().getPermId();
            item.setPermId(max.add(BigDecimal.ONE));
        }

        final Optional<ReportPermission> existingItem = find(item.getPermId());
        if (existingItem.isPresent()) {
            int position = DATABASE.indexOf(existingItem.get());
            DATABASE.remove(existingItem.get());
            reportPermService.deleteById(item.getPermId());
            DATABASE.add(position, item);
            reportPermService.save(item);
        } else {
            DATABASE.add(item);
            reportPermService.save(item);
        }
    }

    Optional<ReportPermission> find(BigDecimal id) {
        return DATABASE
                .stream()
                .filter(entity -> entity.getPermId().equals(id))
                .findFirst();
    }

    public void delete(ReportPermission item) {
        reportPermService.deleteById(item.getPermId());
        DATABASE.removeIf(entity -> entity.getPermId().equals(item.getPermId()));
    }
}
