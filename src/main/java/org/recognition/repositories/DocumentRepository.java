package org.recognition.repositories;

import org.recognition.entity.DocumentEntity;
import org.recognition.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends CrudRepository<DocumentEntity, Long> {
    List<DocumentEntity> findAllByOrderByDocumentidAsc();
    List<DocumentEntity> findAllByUserOrderByDocumentidAsc(UserEntity user);
    Optional<DocumentEntity> findByUserAndDocumentid(UserEntity user, Long id);
}
