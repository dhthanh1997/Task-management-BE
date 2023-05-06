package com.ansv.taskmanagement.repository;

import com.ansv.taskmanagement.model.Section;
import com.ansv.taskmanagement.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface SectionRepository extends JpaRepository<Section, Long>, JpaSpecificationExecutor<Section>, SectionRepositoryCustom {

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM section WHERE id IN :listId", nativeQuery = true)
    Integer deleteByListId(@Param("listId") List<Long> listId);

    @Query(value = "SELECT sec.* FROM section AS sec INNER JOIN project_section AS ps ON sec.id = ps.section_id WHERE ps.project_id = :projectId", nativeQuery = true)
    List<Section> findBySectionId(@Param("projectId") Long projectId);

}
