package com.imatia.campusdual.grupoun_bootcampbackend.model.dao;

import com.imatia.campusdual.grupoun_bootcampbackend.model.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelDAO extends JpaRepository<Hotel, Integer> {
    boolean existsByNameIgnoreCase(String name);
}
