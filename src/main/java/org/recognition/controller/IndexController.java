package org.recognition.controller;

import net.sourceforge.tess4j.TesseractException;
import org.recognition.entity.DocumentEntity;
import org.apache.commons.io.FilenameUtils;
import org.recognition.utils.Recognition;
import org.recognition.utils.Validation;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.recognition.services.IDocumentService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Controller
public class IndexController {
    private final IDocumentService documentService;
    public IndexController(IDocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping("/")
    public String index(Model model) {
        List<DocumentEntity> documents = documentService.findAll();
        model.addAttribute("documents", documents);
        return "index";
    }
    @GetMapping(value = "/", params = {"id"})
    public String getDocument(@RequestParam("id") int id, Model model) {
        Optional<DocumentEntity> document = documentService.getDocumentById(id);
        List<DocumentEntity> documents = new ArrayList<>();
        document.ifPresent(documents::add);
        model.addAttribute("documents", documents);
        return "index";
    }

    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file, @RequestParam("name") String name,
                         @RequestParam("author") String author, @RequestParam("language") String language) {
        name = Validation.makeNameValid(name);
        byte[] binaryFile;
        String documentText = "";
        String keywords = "";
        try {
            binaryFile = file.getBytes();
            documentText = Recognition.recognize(binaryFile, language);
            keywords = Recognition.findKeyWords(documentText);
        }
        catch (IOException | TesseractException e) {
            binaryFile = null;
        }
        // Дата загрузки документа - текущая дата
        documentService.uploadDocument(name, author, new java.sql.Date(new java.util.Date().getTime()), binaryFile, documentText, keywords);
        return "redirect:/";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam("id") int id) {
        documentService.deleteDocument(id);
        return "redirect:/";
    }

    @PostMapping("/download")
    public ResponseEntity<Resource> download(@RequestParam("id") int id) {
        Optional<DocumentEntity> document = documentService.getDocumentById(id);
        if (document.isPresent() && document.get().getBinarytext() != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + document.get().getDocumentname());
            ByteArrayResource resource = new ByteArrayResource(document.get().getBinarytext());
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .headers(headers)
                    .body(resource);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    @GetMapping(value="/update", params={"id"})
    public String update(@RequestParam("id") int id, Model model) {
        Optional<DocumentEntity> document = documentService.getDocumentById(id);
        if (document.isPresent()) {
            model.addAttribute("document", document.get());
            return "update";
        } else {
            return "redirect:/";
        }
    }
    @PostMapping("/update")
    public String update(@RequestParam("id") int id, @RequestParam("author") String author,
                         @RequestParam("name") String name, @RequestParam("file") MultipartFile file) throws IOException {
        Optional<DocumentEntity> document = documentService.getDocumentById(id);
        if (document.isPresent()) {
            name = Validation.makeNameValid(name);
            documentService.updateDocument(id, name, author, file);
        }
        return "redirect:/";
    }
}