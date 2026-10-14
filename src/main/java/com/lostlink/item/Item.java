package com.lostlink.item;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name="items")
public class Item {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(name="report_type", nullable=false, length=10) private String reportType;
    @Column(name="item_name", nullable=false, length=120) private String itemName;
    @Column(nullable=false, length=80) private String category;
    @Column(columnDefinition="TEXT") private String description;
    @Column(nullable=false, length=180) private String location;
    private LocalDate eventDate;
    @Column(nullable=false, length=190) private String contactEmail;
    @Column(nullable=false, length=30) private String status="OPEN";
    private LocalDateTime createdAt=LocalDateTime.now();
    protected Item() {}
    public Item(String reportType,String itemName,String category,String description,String location,LocalDate eventDate,String contactEmail) {
        this.reportType=reportType; this.itemName=itemName; this.category=category; this.description=description;
        this.location=location; this.eventDate=eventDate; this.contactEmail=contactEmail; this.status="OPEN";
    }
    public Long getId(){return id;} public String getReportType(){return reportType;} public String getItemName(){return itemName;}
    public String getCategory(){return category;} public String getDescription(){return description;} public String getLocation(){return location;}
    public LocalDate getEventDate(){return eventDate;} public String getContactEmail(){return contactEmail;} public String getStatus(){return status;}
    public LocalDateTime getCreatedAt(){return createdAt;}
    public void setStatus(String status){this.status=status;}
}
