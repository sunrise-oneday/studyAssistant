package org.example.studyassistant.repository;

import org.example.studyassistant.pojo.entity.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Integer> {
    List<Resource> findByCourseId(Integer courseId);
}