package pl.kskowronski.data.service.admin;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.kskowronski.data.entity.Report;


import java.math.BigDecimal;

public interface ReportRepo extends JpaRepository<Report, BigDecimal> {

}