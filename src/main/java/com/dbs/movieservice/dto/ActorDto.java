package com.dbs.movieservice.dto;

import com.dbs.movieservice.domain.movie.Actor;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "배우 정보를 담는 DTO")
public class ActorDto {

    @Schema(description = "배우 ID", example = "101")
    private final Long actorId;

    @Schema(description = "배우 이름", example = "정우성")
    private final String actorName;

    public ActorDto(Actor actor) {
        this.actorId = actor.getActorId();
        this.actorName = actor.getActorName();
    }
}
