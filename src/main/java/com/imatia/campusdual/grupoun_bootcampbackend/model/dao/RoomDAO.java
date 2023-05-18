package com.imatia.campusdual.grupoun_bootcampbackend.model.dao;

import com.imatia.campusdual.grupoun_bootcampbackend.model.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomDAO extends JpaRepository<Room, Integer> {
}
