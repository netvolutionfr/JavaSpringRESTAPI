package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@CrossOrigin("*")
@RequestMapping(path = "/users")
public class MainController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @GetMapping(path = "/")
    public @ResponseBody Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<User> read(@PathVariable Integer id) {
        User user;
        if (userRepository.findById(id).isPresent()) {
            user = userRepository.findById(id).get();
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<User> delete(@PathVariable Integer id) {
        if (userRepository.findById(id).isPresent()) {
            userRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(path = "/")
    public ResponseEntity<User> create(@RequestBody User user) {
        try {
            passwordEncoder = new SecurityConfig().passwordEncoder();
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            User newUser = userRepository.save(user);
            URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(newUser.getId())
                    .toUri();
            return ResponseEntity.created(uri).body(newUser);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<User> update(@PathVariable Integer id, @RequestBody User user) {
        User userToUpdate;
        if (userRepository.findById(id).isPresent()) {
            userToUpdate = userRepository.findById(id).get();
            userToUpdate.setName(user.getName());
            userToUpdate.setEmail(user.getEmail());
            userRepository.save(userToUpdate);

            return ResponseEntity.ok(userToUpdate);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<User> patch(@PathVariable Integer id, @RequestBody User user) {
        User userToUpdate;
        if (userRepository.findById(id).isPresent()) {
            userToUpdate = userRepository.findById(id).get();
            if (user.getName() != null) {
                userToUpdate.setName(user.getName());
            }
            if (user.getEmail() != null) {
                userToUpdate.setEmail(user.getEmail());
            }
            userRepository.save(userToUpdate);

            return ResponseEntity.ok(userToUpdate);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
