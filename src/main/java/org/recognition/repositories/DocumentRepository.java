package org.recognition.repositories;

import org.recognition.entity.DocumentEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends CrudRepository<DocumentEntity, Long> {
    List<DocumentEntity> findAllByOrderByDocumentidAsc();
}
