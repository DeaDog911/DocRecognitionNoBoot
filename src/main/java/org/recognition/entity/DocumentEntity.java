package org.recognition.entity;

import jakarta.persistence.*;

import java.sql.Date;
import java.util.Arrays;
import java.util.Objects;

@Entity
@Table(name = "document", schema = "public", catalog = "postgres")
public class DocumentEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "documentid")
    private int documentid;
    @Basic
    @Column(name = "author")
    private String author;
    @Basic
    @Column(name = "binarytext")
    private byte[] binarytext;
    @Basic
    @Column(name = "documentname")
    private String documentname;
    @Basic
    @Column(name = "documenttext", columnDefinition = "TEXT")
    private String documenttext;
    @Basic
    @Column(name = "keywords")
    private String keywords;
    @Basic
    @Column(name = "updatedate")
    private Date updatedate;
    @Basic
    @Column(name = "uploaddate")
    private Date uploaddate;

    @ManyToOne(fetch = FetchType.EAGER)
    private UserEntity user;

    public int getDocumentid() {
        return documentid;
    }

    public void setDocumentid(int documentid) {
        this.documentid = documentid;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public byte[] getBinarytext() {
        return binarytext;
    }

    public void setBinarytext(byte[] binarytext) {
        this.binarytext = binarytext;
    }

    public String getDocumentname() {
        return documentname;
    }

    public void setDocumentname(String documentname) {
        this.documentname = documentname;
    }

    public String getDocumenttext() {
        return documenttext;
    }

    public void setDocumenttext(String documenttext) {
        this.documenttext = documenttext;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public Date getUpdatedate() {
        return updatedate;
    }

    public void setUpdatedate(Date updatedate) {
        this.updatedate = updatedate;
    }

    public Date getUploaddate() {
        return uploaddate;
    }

    public void setUploaddate(Date uploaddate) {
        this.uploaddate = uploaddate;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DocumentEntity entity = (DocumentEntity) o;
        return documentid == entity.documentid && Objects.equals(author, entity.author) && Arrays.equals(binarytext, entity.binarytext) && Objects.equals(documentname, entity.documentname) && Objects.equals(documenttext, entity.documenttext) && Objects.equals(keywords, entity.keywords) && Objects.equals(updatedate, entity.updatedate) && Objects.equals(uploaddate, entity.uploaddate);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(documentid, author, documentname, documenttext, keywords, updatedate, uploaddate);
        result = 31 * result + Arrays.hashCode(binarytext);
        return result;
    }
}
