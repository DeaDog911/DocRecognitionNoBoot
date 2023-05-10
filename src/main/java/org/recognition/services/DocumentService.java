package org.recognition.services;

import jakarta.transaction.Transactional;
import org.recognition.entity.DocumentEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.recognition.repositories.DocumentRepository;

import java.io.IOException;
import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DocumentService implements IDocumentService{
    private final DocumentRepository documentRepository;
    @Autowired
    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    @Override
    public List<DocumentEntity> findAll() {
        return documentRepository.findAllByOrderByDocumentidAsc();
    }

    @Override
    public void uploadDocument(String documentName, String author, Date uploadDate, byte[] binaryFile, String documentText, String keywords) {
        DocumentEntity document = new DocumentEntity();
        document.setDocumentname(documentName);
        document.setAuthor(author);
        document.setUploaddate(uploadDate);
        document.setDocumenttext(documentText);
        document.setKeywords(keywords);
        document.setUpdatedate(null);
        document.setBinarytext(binaryFile);
        document.setUpdatedate(uploadDate);
        documentRepository.save(document);
    }

    @Override
    public void deleteDocument(int id) {
        documentRepository.deleteById((long) id);
    }
    @Override
    public Optional<DocumentEntity> getDocumentById(int id) {
        return documentRepository.findById((long) id);
    }

    @Override
    public void updateDocument(int id, String documentName, String author, MultipartFile file) {
        Optional<DocumentEntity> document = documentRepository.findById((long) id);
        boolean updated = false;
        if (document.isPresent()) {
            DocumentEntity docEntity = document.get();
            if (!documentName.equals("")) { docEntity.setDocumentname(documentName); updated = true ;}
            if (!author.equals("")) { docEntity.setAuthor(author); updated = true ;}
            try {
                if (file != null) { docEntity.setBinarytext(file.getBytes()); updated = true ;}
            } catch (IOException ignore) {}
            if (updated) docEntity.setUpdatedate(new Date(new java.util.Date().getTime()));
            documentRepository.save(docEntity);
        }
    }
}