package com.canvendor.docvault.repository;

import com.canvendor.docvault.model.DocumentLedger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface DocumentLedgerRepository extends JpaRepository<DocumentLedger,Long> {
}
