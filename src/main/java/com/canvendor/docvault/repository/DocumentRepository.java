package com.canvendor.docvault.repository;

import com.canvendor.docvault.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface DocumentRepository extends JpaRepository<Document,String> {
}
