package pl.kskowronski.data.service.admin.reportDetail;


import com.vaadin.flow.component.crud.CrudFilter;
import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import pl.kskowronski.data.entity.report.ReportDetail;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.SortDirection;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;



public class ReportDetailDataProvider extends AbstractBackEndDataProvider<ReportDetail, CrudFilter> {

    // A real app should hook up something like JPA
    List<ReportDetail> DATABASE;
    private ReportDetailService reportDetailService;
    private Consumer<Long> sizeChangeListener;


    public ReportDetailDataProvider(ReportDetailService reportDetailService, BigDecimal idRap) {
        this.reportDetailService = reportDetailService;
        DATABASE = new ArrayList<>(reportDetailService.findReportDetailBySrpRapId(idRap));
        DATABASE.stream().forEach( item -> {
           // item.setUser(userService.findById(item.getPrcId()).get());
           // item.setSk(skService.findBySkKod(item.getSkKod()));
        });
    }

    private static Predicate<ReportDetail> predicate(CrudFilter filter) {
        // For RDBMS just generate a WHERE clause
        return filter.getConstraints().entrySet().stream()
                .map(constraint -> (Predicate<ReportDetail>) person -> {
                    try {
                        Object value = valueOf(constraint.getKey(), person);
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

    private static Comparator<ReportDetail> comparator(CrudFilter filter) {
        // For RDBMS just generate an ORDER BY clause
        return filter.getSortOrders().entrySet().stream()
                .map(sortClause -> {
                    try {
                        Comparator<ReportDetail> comparator = Comparator.comparing(person ->
                                (Comparable) valueOf(sortClause.getKey(), person)
                        );

                        if (sortClause.getValue() == SortDirection.DESCENDING) {
                            comparator = comparator.reversed();
                        }

                        return comparator;

                    } catch (Exception ex) {
                        return (Comparator<ReportDetail>) (o1, o2) -> 0;
                    }
                })
                .reduce(Comparator::thenComparing)
                .orElse((o1, o2) -> 0);
    }

    private static Object valueOf(String fieldName, ReportDetail person) {
        try {
            Field field = ReportDetail.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(person);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    protected Stream<ReportDetail> fetchFromBackEnd(Query<ReportDetail, CrudFilter> query) {
        int offset = query.getOffset();
        int limit = query.getLimit();

        Stream<ReportDetail> stream = DATABASE.stream();

        if (query.getFilter().isPresent()) {
            stream = stream
                    .filter(predicate(query.getFilter().get()))
                    .sorted(comparator(query.getFilter().get()));
        }

        return stream.skip(offset).limit(limit);
    }

    @Override
    protected int sizeInBackEnd(Query<ReportDetail, CrudFilter> query) {
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

    public void persist(ReportDetail item) {
        if (item.getSrpId() == null) {
            BigDecimal max  = reportDetailService.findMaxId();
            item.setSrpId(max.add(BigDecimal.ONE));
        }

        final Optional<ReportDetail> existingItem = find(item.getSrpId());
        if (existingItem.isPresent()) {
            int position = DATABASE.indexOf(existingItem.get());
            DATABASE.remove(existingItem.get());
            reportDetailService.deleteById(item.getSrpId());
            DATABASE.add(position, item);
            reportDetailService.save(item);
        } else {
            DATABASE.add(item);
            reportDetailService.save(item);
        }
    }

    Optional<ReportDetail> find(BigDecimal id) {
        return DATABASE
                .stream()
                .filter(entity -> entity.getSrpId().equals(id))
                .findFirst();
    }

    public void delete(ReportDetail item) {
        reportDetailService.deleteById(item.getSrpId());
        DATABASE.removeIf(entity -> entity.getSrpId().equals(item.getSrpId()));
    }
}

