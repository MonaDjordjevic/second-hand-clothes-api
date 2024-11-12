package com.marketplace.second_hand_clothes.repository;

import com.marketplace.second_hand_clothes.model.Garment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GarmentRepository extends JpaRepository<Garment, Long> {
}
