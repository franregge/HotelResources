package com.imatia.campusdual.grupoun_bootcampbackend.model.dao;

import com.imatia.campusdual.grupoun_bootcampbackend.model.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingDAO extends JpaRepository<Booking,Integer> {
}
