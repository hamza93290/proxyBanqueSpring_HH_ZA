package com.example.demo.controller;

import com.example.demo.dto.CompteBancaireDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.VirementDTO;
import com.example.demo.entity.Compte;
import com.example.demo.service.CompteService;
import com.example.demo.service.impl.VirementImpossibleException;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/comptes")
public class CompteController {

	@Autowired
	private CompteService compteService;



//	public CompteController(CompteService compteService) {
//		this.compteService = compteService;
//	}

	@GetMapping
	Iterable<Compte> getComptes() {
		return compteService.getAllCompte();
	}

	@PostMapping("/{id}")
	Compte postCompte(@Valid @RequestBody Compte c, @PathVariable Long id) {
		Compte newCompte = compteService.createCompte(c.getType(), c.getSolde());
		return compteService.saveCompte(newCompte, id);
	}



	@GetMapping("/comptesByIdClient")
	public ResponseEntity<List<CompteBancaireDto>> getAllCompteBancaireByIdClient(@RequestParam Long id){
		try{
			return new ResponseEntity<>(compteService.getByIdClientComptesBancaires(id), HttpStatus.OK);
		} catch (RuntimeException e){
			return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping("/virement")
	public ResponseEntity<String> virement(@RequestBody VirementDTO virementDTO) throws VirementImpossibleException {
		try {
			String messageReponse = compteService.virementComptes(virementDTO);
			if (messageReponse != "Virement effectué avec succès.") {
				throw new VirementImpossibleException(messageReponse);
			} else {
				return new ResponseEntity<>(messageReponse, HttpStatus.OK);
			}
		} catch (VirementImpossibleException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

}