package com.qentelli.employeetrackingsystem.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.qentelli.employeetrackingsystem.entity.ViewReports;

@Repository
public interface ViewreportRepository extends JpaRepository<ViewReports, Integer> {

	@Query("SELECT v FROM ViewReports v WHERE v.softDelete = false AND v.weekRange.weekFromDate = :fromDate AND v.weekRange.weekToDate = :toDate")
	List<ViewReports> findByWeekRange(@Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);

	List<ViewReports> findBySoftDeleteFalse();
	
	Page<ViewReports> findBySoftDeleteFalse(Pageable pageable);

    Page<ViewReports> findBySoftDeleteFalseAndPerson_PersonId(Integer personId, Pageable pageable);

    Page<ViewReports> findBySoftDeleteFalseAndProject_ProjectId(Integer projectId, Pageable pageable);

    Page<ViewReports> findBySoftDeleteFalseAndPerson_PersonIdAndProject_ProjectId(Integer personId, Integer projectId, Pageable pageable);
}
