package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Compte;

import java.util.List;

public interface CompteRepository extends JpaRepository<Compte, Long> {

    List<Compte> findByClient_Id(Long idClient);
}
