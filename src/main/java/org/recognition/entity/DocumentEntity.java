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
    @Column(name = "documentname")
    private String documentname;
    @Basic
    @Column(name = "author")
    private String author;
    @Basic
    @Column(name = "uploaddate")
    private Date uploaddate;
    @Basic
    @Column(name = "updatedate")
    private Date updatedate;
    @Basic
    @Column(name = "binarytext")
    private byte[] binarytext;
    @Basic
    @Column(name = "documenttext")
    private String documenttext;
    @Basic
    @Column(name = "keywords")
    private String keywords;

    public int getDocumentid() {
        return documentid;
    }

    public void setDocumentid(int documentid) {
        this.documentid = documentid;
    }

    public String getDocumentname() {
        return documentname;
    }

    public void setDocumentname(String documentname) {
        this.documentname = documentname;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Date getUploaddate() {
        return uploaddate;
    }

    public void setUploaddate(Date uploaddate) {
        this.uploaddate = uploaddate;
    }

    public Date getUpdatedate() {
        return updatedate;
    }

    public void setUpdatedate(Date updatedate) {
        this.updatedate = updatedate;
    }

    public byte[] getBinarytext() {
        return binarytext;
    }

    public void setBinarytext(byte[] binarytext) {
        this.binarytext = binarytext;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DocumentEntity entity = (DocumentEntity) o;
        return documentid == entity.documentid && Objects.equals(documentname, entity.documentname) && Objects.equals(author, entity.author) && Objects.equals(uploaddate, entity.uploaddate) && Objects.equals(updatedate, entity.updatedate) && Arrays.equals(binarytext, entity.binarytext) && Objects.equals(documenttext, entity.documenttext) && Objects.equals(keywords, entity.keywords);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(documentid, documentname, author, uploaddate, updatedate, documenttext, keywords);
        result = 31 * result + Arrays.hashCode(binarytext);
        return result;
    }
}
