package com.example.demo.entity;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;

@Entity
public class CompteEpargne extends Compte {

	@JsonBackReference
	@OneToOne(mappedBy = "compte")
	private Client client;
	
	public CompteEpargne() {
		super();
	}

	public CompteEpargne(String type, String numCompte, Double solde, LocalDate creationDate) {
        super(type, numCompte, solde, creationDate);
    }

	
	
	
}
