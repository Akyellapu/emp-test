package com.qentelli.employeetrackingsystem.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.qentelli.employeetrackingsystem.entity.Release;
import com.qentelli.employeetrackingsystem.entity.ReleaseCountProjection;

@Repository
public interface ReleaseRepository extends JpaRepository<Release, Long> {
	@Query(value = """
			SELECT p.project_name AS projectName,
			       COALESCE(SUM(r.major), 0) AS totalMajor,
			       COALESCE(SUM(r.minor), 0) AS totalMinor,
			       COALESCE(SUM(r.incident_created), 0) AS totalIncidents
			FROM project p
			LEFT JOIN release r ON p.project_id = r.project_id
			GROUP BY p.project_name
			""", nativeQuery = true)
	List<ReleaseCountProjection> getAggregatedReleaseCounts();

	List<Release> findByWeek_WeekId(int weekId);

	List<Release> findBySprint_SprintId(int sprintId);

}