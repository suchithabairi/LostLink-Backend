package com.lostlink.item;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface ItemRepository extends JpaRepository<Item,Long> {
    List<Item> findByReportTypeOrderByCreatedAtDesc(String reportType);
}
