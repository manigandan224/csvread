package com.example.CSVThreads.CSVRepository;

import com.example.CSVThreads.CSVModel.ThreadModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CSVThreadRepository extends JpaRepository<ThreadModel,Long> {
}
