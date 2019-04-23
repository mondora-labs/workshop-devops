package com.mondoralabs.workshop04.Workshop;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * WorkshopRepository
 */
@Repository
public interface WorkshopRepository extends MongoRepository<Workshop, String> {

    List<Workshop> findByAuthor(String author);

}
