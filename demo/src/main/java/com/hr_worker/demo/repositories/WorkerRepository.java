package com.hr_worker.demo.repositories;

import com.hr_worker.demo.entities.Worker;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkerRepository extends JpaRepository <Worker, Long> {

}
