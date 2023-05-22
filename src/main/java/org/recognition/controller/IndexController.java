package org.recognition.controller;

import net.sourceforge.tess4j.TesseractException;
import org.recognition.entity.DocumentEntity;
import org.recognition.entity.UserEntity;
import org.recognition.services.DocumentService;
import org.recognition.services.UserService;
import org.recognition.utils.Recognition;
import org.recognition.utils.Validation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
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


import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Controller
public class IndexController {
    @Autowired
    DocumentService documentService;
    @Autowired
    UserService userService;
    @Autowired
    private Environment env;

    @GetMapping("/")
    public String index(Model model, Principal principal) {
        UserEntity user = userService.loadUserByUsername(principal.getName());
        List<DocumentEntity> documents = documentService.findAllByUser(user);
        model.addAttribute("documents", documents);
        return "index";
    }
    @GetMapping(value = "/", params = {"id"})
    public String getDocument(@RequestParam("id") int id, Model model, Principal principal) {
        UserEntity user = userService.loadUserByUsername(principal.getName());
        Optional<DocumentEntity> document = documentService.getDocumentByUserAndId(user, id);
        List<DocumentEntity> documents = new ArrayList<>();
        document.ifPresent(documents::add);
        model.addAttribute("documents", documents);
        return "index";
    }

    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file, @RequestParam("name") String name,
                         @RequestParam("language") String language, Principal principal) {
        name = Validation.makeNameValid(name);
        byte[] binaryFile;
        String documentText = "";
        String keywords = "";
        try {
            binaryFile = file.getBytes();
            try {
                documentText = Recognition.recognize(binaryFile, language);
                keywords = Recognition.findKeyWords(documentText);
            }catch (IOException | TesseractException ignored) {}
        }
        catch (IOException  e) {
            binaryFile = null;
        }
        UserEntity user = userService.loadUserByUsername(principal.getName());
        documentService.uploadDocument(name, user.getUsername(), new java.sql.Date(new java.util.Date().getTime()),
                binaryFile, documentText, keywords, user);
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
            headers.add(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=" + URLEncoder.encode(document.get().getDocumentname(), StandardCharsets.UTF_8));
            headers.add(HttpHeaders.ACCEPT_CHARSET, "utf-8");
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
    public String update(@RequestParam("id") int id,
                         @RequestParam("name") String name, @RequestParam("file") MultipartFile file,
                         @RequestParam("language") String language, Principal principal) throws IOException {
        Optional<DocumentEntity> document = documentService.getDocumentById(id);
        String documentText = null;
        String keywords = null;
        if (document.isPresent()) {
            if(!name.equals("")) name = Validation.makeNameValid(name);
            byte[] binaryFile = file.getBytes();
            try {
                if (binaryFile.length != 0) {
                    if (language.equals("")) language = "rus";
                    documentText = Recognition.recognize(file.getBytes(), language);
                    keywords = Recognition.findKeyWords(documentText);
                }else if (!language.equals("")) {
                    documentText = Recognition.recognize(document.get().getBinarytext(), language);
                    keywords = Recognition.findKeyWords(documentText);
                }
            } catch (IOException | TesseractException ignored) {}
            UserEntity user = userService.loadUserByUsername(principal.getName());
            documentService.updateDocument(id, name, user.getUsername(), file, documentText, keywords);
        }
        return "redirect:/";
    }
}