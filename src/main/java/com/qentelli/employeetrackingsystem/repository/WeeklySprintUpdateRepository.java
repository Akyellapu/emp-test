package com.qentelli.employeetrackingsystem.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.qentelli.employeetrackingsystem.entity.WeeklySprintUpdate;

@Repository
public interface WeeklySprintUpdateRepository extends JpaRepository<WeeklySprintUpdate, Integer> {
	Page<WeeklySprintUpdate> findByWeeklySprintUpdateStatusTrue(Pageable pageable);

	List<WeeklySprintUpdate> findByWeeklySprintUpdateStatusTrue();

	@Query("""
			    SELECT wsu FROM WeeklySprintUpdate wsu
			    WHERE wsu.week.weekId = :weekId
			    AND wsu.weeklySprintUpdateStatus = true
			""")
	List<WeeklySprintUpdate> findActiveByWeekId(@Param("weekId") int weekId);

	@Query("""
			    SELECT wsu FROM WeeklySprintUpdate wsu
			    WHERE wsu.week.sprint.sprintId = :sprintId
			    AND wsu.weeklySprintUpdateStatus = true
			""")
	List<WeeklySprintUpdate> findActiveBySprintId(@Param("sprintId") Long sprintId);

//	@Query("""
//			    SELECT wsu FROM WeeklySprintUpdate wsu
//			    WHERE wsu.week.sprint.sprintNumber = :sprintNumber
//			    AND wsu.weeklySprintUpdateStatus = true
//			""")
//	List<WeeklySprintUpdate> findActiveBySprintNumber(@Param("sprintNumber") String sprintNumber);
}
