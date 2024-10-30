package org.AshInc.repository;

import org.AshInc.model.Chatter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatterRepository extends JpaRepository<Chatter,Long> {
    @Query("SELECT ch FROM Chatter ch WHERE ch.login = :login")
    Chatter getChatterByLogin(@Param("login")String login);
}
