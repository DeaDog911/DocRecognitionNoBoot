package org.recognition.services;

import jakarta.transaction.Transactional;
import org.recognition.entity.DocumentEntity;
import org.recognition.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.recognition.repositories.DocumentRepository;

import java.io.IOException;
import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Service
public class DocumentService{
    @Autowired
    DocumentRepository documentRepository;
    public List<DocumentEntity> findAll() {
        return documentRepository.findAllByOrderByDocumentidAsc();
    }

    public void uploadDocument(String documentName, String author, Date uploadDate,
                               byte[] binaryFile, String documentText, String keywords, UserEntity user) {
        DocumentEntity document = new DocumentEntity();
        document.setDocumentname(documentName);
        document.setAuthor(author);
        document.setUploaddate(uploadDate);
        document.setDocumenttext(documentText);
        document.setKeywords(keywords);
        document.setUpdatedate(uploadDate);
        document.setBinarytext(binaryFile);
        document.setUpdatedate(uploadDate);
        document.setUser(user);
        documentRepository.save(document);
    }


    public void deleteDocument(int id) {
        documentRepository.deleteById((long) id);
    }
    public Optional<DocumentEntity> getDocumentByUserAndId(UserEntity user, int id) {
        return documentRepository.findByUserAndDocumentid(user, (long) id);
    }

    public Optional<DocumentEntity> getDocumentById(int id) {
        return documentRepository.findById((long) id);
    }

    public void updateDocument(int id, String documentName, String author, MultipartFile file, String documentText, String keywords) {
        Optional<DocumentEntity> document = documentRepository.findById((long) id);
        boolean updated = false;
        if (document.isPresent()) {
            DocumentEntity docEntity = document.get();
            if (!documentName.equals("")) { docEntity.setDocumentname(documentName); updated = true ;}
            if (!author.equals("")) {docEntity.setAuthor(author); updated = true ;}
            try {
                if (file.getBytes().length != 0) { docEntity.setBinarytext(file.getBytes()); updated = true ;}
            } catch (IOException ignore) {}
            if (documentText != null) { docEntity.setDocumenttext(documentText); updated = true;}
            if (keywords != null) { docEntity.setKeywords(keywords); updated = true;}
            if (updated) docEntity.setUpdatedate(new Date(new java.util.Date().getTime()));
            documentRepository.save(docEntity);
        }
    }

    public List<DocumentEntity> findAllByUser(UserEntity user) {
        return documentRepository.findAllByUserOrderByDocumentidAsc(user);
    }
}