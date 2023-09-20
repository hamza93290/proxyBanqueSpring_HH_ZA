package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import com.example.demo.dto.CompteBancaireDto;
import com.example.demo.dto.VirementDTO;
import com.example.demo.entity.Compte;
import com.example.demo.service.impl.VirementImpossibleException;

public interface CompteService {

	CompteBancaireDto compteToDto(Compte compte);

	Compte createCompte(String type, Double solde);

	Iterable<Compte> getAllCompte();

	Compte saveCompte(Compte compte, Long id);

	Optional<Compte> getCompteById(Long id);

	void deleteCompteById(Long id);

	Compte updateCompte(Compte compte);

	List<CompteBancaireDto> getByIdClientComptesBancaires(Long clientDto);

	public String virementComptes(VirementDTO virementDTO) throws VirementImpossibleException;
}
