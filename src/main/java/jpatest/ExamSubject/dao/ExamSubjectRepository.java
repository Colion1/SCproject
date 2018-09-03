package jpatest.ExamSubject.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ExamSubjectRepository extends JpaRepository<ExamSubject,Long> {

	
	List<ExamSubject> findAll();
}
