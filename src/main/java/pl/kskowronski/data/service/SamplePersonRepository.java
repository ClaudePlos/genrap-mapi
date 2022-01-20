package pl.kskowronski.data.service;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.kskowronski.data.entity.SamplePerson;

public interface SamplePersonRepository extends JpaRepository<SamplePerson, Integer> {

}