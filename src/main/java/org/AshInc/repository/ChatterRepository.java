package org.AshInc.repository;

import org.AshInc.model.Chatter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatterRepository extends JpaRepository<Chatter,Long> {

}
