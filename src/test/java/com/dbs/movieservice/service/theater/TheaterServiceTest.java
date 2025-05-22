package com.dbs.movieservice.service.theater;

import com.dbs.movieservice.domain.theater.Theater;
import com.dbs.movieservice.repository.theater.TheaterRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class TheaterServiceTest {
    @Autowired
    private TheaterService theaterService;

    @Autowired
    private TheaterRepository theaterRepository;
    @Test
    void createTheater_shouldPersistEntity() {
        // given
        Theater theater = new Theater();
        theater.setTheaterName("1번상영관");
        theater.setTotalSeats(150);

        // when
        Theater saved = theaterService.createTheater(theater);

        // then
        assertThat(saved.getTheaterId()).isNotNull(); // 자동 생성된 ID
        assertThat(saved.getTheaterName()).isEqualTo("1번상영관");
        assertThat(saved.getTotalSeats()).isEqualTo(150);
        assertThat(theaterRepository.findById(saved.getTheaterId())).isPresent(); // 실제 DB에 저장됨
    }
}
