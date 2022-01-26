package pl.kskowronski.views.user;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import pl.kskowronski.data.entity.User;
import pl.kskowronski.data.entity.report.Report;
import pl.kskowronski.data.entity.report.ReportPermission;
import pl.kskowronski.data.service.UserService;
import pl.kskowronski.data.service.admin.report.ReportService;
import pl.kskowronski.data.service.admin.reportPermission.ReportPermService;
import pl.kskowronski.views.MainLayout;

import javax.annotation.security.RolesAllowed;
import java.util.ArrayList;
import java.util.List;


@PageTitle("Twoje raporty")
@Route(value = "user-reports", layout = MainLayout.class)
@RolesAllowed("user")
public class ReportUserView extends VerticalLayout {

    private ReportService reportService;
    private ReportPermService reportPermService;
    private User user;

    public ReportUserView(UserService userService,ReportPermService reportPermService, ReportService reportService) {
        this.reportPermService = reportPermService;
        this.reportService = reportService;
        var userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        user = userService.getByUsername(userDetails.getUsername());

        var comboListReport = getReportListForUser();

        add(comboListReport);
    }

    private ComboBox<Report> getReportListForUser() {
        ComboBox<Report> selectReport = new ComboBox<>();
        List<ReportPermission> listUserReports = reportPermService.findReportPermissionByPermUserId(user.getId());
        List<Report> listReport = new ArrayList<>();
        listUserReports.stream().forEach( item -> {
            listReport.add( reportService.getReportById(item.getPermRapId()) );
        });
        selectReport.setItems( listReport );
        selectReport.setItemLabelGenerator(Report::getRapName);
        selectReport.setLabel("Wybierz raport:");
        return selectReport;
    }

}
