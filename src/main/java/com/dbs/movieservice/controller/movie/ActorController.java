package com.dbs.movieservice.controller.movie;

import com.dbs.movieservice.domain.movie.Actor;
import com.dbs.movieservice.repository.movie.ActorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/actors")
@RequiredArgsConstructor
public class ActorController {

    private final ActorRepository actorRepository;

    @PostMapping
    public Actor addActor(@RequestBody Actor actor) {
        return actorRepository.save(actor);
    }

 /*   @PutMapping("/{id}")
    public Actor updateActor(@PathVariable Long id, @RequestBody Actor updated) {
        updated.setActorID(id);
        return actorRepository.save(updated);
    }*/

    @DeleteMapping("/{id}")
    public void deleteActor(@PathVariable Long id) {
        actorRepository.deleteById(id);
    }

    @GetMapping
    public List<Actor> getAllActors() {
        return actorRepository.findAll();
    }
}
