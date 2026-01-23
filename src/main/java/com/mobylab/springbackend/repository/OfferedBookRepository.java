package com.mobylab.springbackend.repository;

import com.mobylab.springbackend.entity.OfferedBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OfferedBookRepository extends JpaRepository<OfferedBook, UUID> {

}
