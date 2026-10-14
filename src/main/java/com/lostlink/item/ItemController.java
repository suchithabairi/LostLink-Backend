package com.lostlink.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/items")
public class ItemController {
    private final ItemRepository items;
    public ItemController(ItemRepository items){this.items=items;}

    public record ItemRequest(@NotBlank String reportType, @NotBlank @Size(max=120) String itemName,
      @NotBlank @Size(max=80) String category, @Size(max=2000) String description,
      @NotBlank @Size(max=180) String location, LocalDate eventDate, @NotBlank @Email String contactEmail) {}

    @GetMapping public List<Item> all(@RequestParam(required=false) String type) {
        if(type==null || type.isBlank()) return items.findAll().stream().sorted(Comparator.comparing(Item::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder()))).collect(Collectors.toList());
        return items.findByReportTypeOrderByCreatedAtDesc(type.toUpperCase());
    }
    @GetMapping("/{id}") public ResponseEntity<Item> one(@PathVariable Long id){return items.findById(id).map(ResponseEntity::ok).orElseGet(()->ResponseEntity.notFound().build());}
    @PostMapping public ResponseEntity<?> create(@Valid @RequestBody ItemRequest r) {
        String type=r.reportType().trim().toUpperCase();
        if(!type.equals("LOST") && !type.equals("FOUND")) return ResponseEntity.badRequest().body(Map.of("message","reportType must be LOST or FOUND"));
        Item item=items.save(new Item(type,r.itemName().trim(),r.category().trim(),r.description(),r.location().trim(),r.eventDate(),r.contactEmail().trim().toLowerCase()));
        return ResponseEntity.status(HttpStatus.CREATED).body(item);
    }
    @GetMapping("/matches/{id}") public ResponseEntity<?> matches(@PathVariable Long id) {
        Optional<Item> source=items.findById(id);
        if(source.isEmpty()) return ResponseEntity.notFound().build();
        Item item=source.get();
        List<Item> found=items.findAll().stream()
          .filter(other -> !other.getId().equals(id))
          .filter(other -> !other.getReportType().equalsIgnoreCase(item.getReportType()))
          .filter(other -> other.getCategory().equalsIgnoreCase(item.getCategory()))
          .filter(other -> other.getItemName().toLowerCase().contains(item.getItemName().toLowerCase()) || item.getItemName().toLowerCase().contains(other.getItemName().toLowerCase()))
          .toList();
        return ResponseEntity.ok(found);
    }
}
