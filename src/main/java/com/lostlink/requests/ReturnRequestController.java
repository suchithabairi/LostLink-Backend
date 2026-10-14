package com.lostlink.requests;
import com.lostlink.item.ItemRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestController
@RequestMapping("/api/return-requests")
public class ReturnRequestController {
 private final ReturnRequestRepository requests; private final ItemRepository items;
 public ReturnRequestController(ReturnRequestRepository requests,ItemRepository items){this.requests=requests;this.items=items;}
 public record CreateRequest(@NotBlank @Email String requesterEmail,@Size(max=500) String message){}
 @GetMapping public Object all(){return requests.findAll();}
 @PostMapping("/{itemId}") public ResponseEntity<?> create(@PathVariable Long itemId,@Valid @RequestBody CreateRequest r){
  if(items.findById(itemId).isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message","Item not found"));
  return ResponseEntity.status(HttpStatus.CREATED).body(requests.save(new ReturnRequest(itemId,r.requesterEmail().trim().toLowerCase(),r.message())));
 }
 @PatchMapping("/{id}/status") public ResponseEntity<?> status(@PathVariable Long id,@RequestParam String value){
  if(!value.equalsIgnoreCase("ACCEPTED")&&!value.equalsIgnoreCase("REJECTED")) return ResponseEntity.badRequest().body(Map.of("message","Status must be ACCEPTED or REJECTED"));
  return requests.findById(id).map(r->{r.setStatus(value.toUpperCase());return ResponseEntity.ok(requests.save(r));})
   .orElseGet(()->ResponseEntity.notFound().build());
 }
}
