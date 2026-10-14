package com.lostlink.requests;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity
@Table(name="return_requests")
public class ReturnRequest {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(name="item_id",nullable=false) private Long itemId;
 @Column(name="requester_email",nullable=false,length=190) private String requesterEmail;
 @Column(length=500) private String message;
 @Column(nullable=false,length=30) private String status="PENDING";
 private LocalDateTime createdAt=LocalDateTime.now();
 protected ReturnRequest(){}
 public ReturnRequest(Long itemId,String requesterEmail,String message){this.itemId=itemId;this.requesterEmail=requesterEmail;this.message=message;this.status="PENDING";}
 public Long getId(){return id;} public Long getItemId(){return itemId;} public String getRequesterEmail(){return requesterEmail;}
 public String getMessage(){return message;} public String getStatus(){return status;} public LocalDateTime getCreatedAt(){return createdAt;}
 public void setStatus(String status){this.status=status;}
}
