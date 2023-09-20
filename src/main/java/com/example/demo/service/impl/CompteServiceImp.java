package com.example.demo.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.example.demo.dto.CompteBancaireDto;
import com.example.demo.service.CompteService;
import com.example.demo.service.RandomCodeGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dto.VirementDTO;
import com.example.demo.entity.Client;
import com.example.demo.entity.Compte;
import com.example.demo.entity.CompteCourant;
import com.example.demo.entity.CompteEpargne;
import com.example.demo.repository.ClientRepository;
import com.example.demo.repository.CompteRepository;

@Service
public class CompteServiceImp implements CompteService {

	@Autowired
	private CompteRepository compteRepository;

	@Autowired
	private RandomCodeGeneratorService randomCodeGeneratorService;

	@Autowired
	private ClientRepository clientRepository;

//	public CompteServiceImp(CompteRepository compteRepository, RandomCodeGeneratorService randomCodeGeneratorService,
//			ClientRepository clientRepository) {
//		this.compteRepository = compteRepository;
//		this.randomCodeGeneratorService = randomCodeGeneratorService;
//		this.clientRepository = clientRepository;
//	}
public CompteBancaireDto compteToDto(Compte compte) {
	CompteBancaireDto compteBancaireDto = new CompteBancaireDto();
	compteBancaireDto.setId(compte.getId());
	compteBancaireDto.setNumerodecompte(compte.getNumCompte());
	compteBancaireDto.setTypeDeCompte(compte.getType() );
	compteBancaireDto.setSolde(compte.getSolde());
	compteBancaireDto.setNameClient(compte.getClient().getNom() + " " + compte.getClient().getPrenom());
	return compteBancaireDto;

}




	public Compte createCompte(String type, Double solde) {
		String numCompte = randomCodeGeneratorService.generateRandomCode();
		LocalDate creationDate = LocalDate.now();

		Compte compte;
		if ("Compte Courant".equalsIgnoreCase(type)) {
			compte = new CompteCourant(type, numCompte, solde, creationDate);
		} else if ("Compte Epargne".equalsIgnoreCase(type)) {
			compte = new CompteEpargne(type, numCompte, solde, creationDate);
		} else {
			throw new IllegalArgumentException("Invalid type: " + type);
		}

		return compte;
	}

	@Override
	public Iterable<Compte> getAllCompte() {
		return compteRepository.findAll();
	}

	@Override
	public Compte saveCompte(Compte compte, Long id) {
		Optional<Client> clientOptional = clientRepository.findById(id);
		if (clientOptional.isPresent()) {
			Client client = clientOptional.get();
			compte.setClient(client);
			if (compte instanceof CompteCourant) {
				client.setCompteCourant((CompteCourant) compte);
			} else if (compte instanceof CompteEpargne) {
				client.setCompteEpargne((CompteEpargne) compte);
			}
		}
		return compteRepository.save(compte);
	}

	@Override
	public Optional<Compte> getCompteById(Long id) {
		// TODO Auto-generated method stub
		return compteRepository.findById(id);
	}

	@Override
	public void deleteCompteById(Long id) {
		// TODO Auto-generated method stub

	}

	@Override
	public Compte updateCompte(Compte compte) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CompteBancaireDto> getByIdClientComptesBancaires(Long clientDto) {

		List<CompteBancaireDto> compteBancaireDtoList = new ArrayList<>();
		List<Compte> compteList = compteRepository.findByClient_Id(clientDto);
		//List<CompteEpargne> compteEpargneList = compteEpargneRepository.findByClient_Id(clientDto);

		if (!compteList.isEmpty() ){
			if (!compteList.isEmpty()){
				for(Compte  compte : compteList ){
					compteBancaireDtoList.add(compteToDto(compte)) ;
				}}

			return compteBancaireDtoList;
		}
		else throw new RuntimeException("Veuillez affilier des comptes bancaires à ce client");
	}


	public String virementComptes(VirementDTO virementDTO) throws VirementImpossibleException {
		try {
			String messageReponse = "";

			if (virementDTO.montant() > 0 && virementDTO.montant() <= 10000) {

				Optional<Compte> optionalCompteSource = compteRepository.findById(virementDTO.idSource());
				Optional<Compte> optionalCompteDestinataire = compteRepository.findById(virementDTO.idDestination());

				if (optionalCompteSource.isPresent() && optionalCompteDestinataire.isPresent()
						&& optionalCompteSource.get().getId() != optionalCompteDestinataire.get().getId()) {
					Compte compteSource = optionalCompteSource.get();
					Compte compteDestinataire = optionalCompteDestinataire.get();

					if (compteSource instanceof CompteCourant && compteDestinataire instanceof CompteCourant) {
						return virementExterne(virementDTO, messageReponse, compteSource, compteDestinataire);
					} else if (compteSource.getClient() == compteDestinataire.getClient()) {
						return virementInterne(virementDTO, messageReponse, compteSource, compteDestinataire);
					} else {
						messageReponse = "Seuls les virements externes de comptes courants à comptes courants sont autorisés.";
						throw new VirementImpossibleException(messageReponse);
					}
				} else {
					messageReponse = "ERREUR. Les ID des deux comptes doivent être valides et différents.";
					throw new VirementImpossibleException(messageReponse);

				}

			} else {
				messageReponse = "Le montant du virement doit être compris entre 1 et 10000 euros.";
				throw new VirementImpossibleException(messageReponse);

			}

		} catch (VirementImpossibleException e) {
			return e.getMessage();
		}

	}

	public String virementExterne(VirementDTO virementDTO, String messageReponse, Compte compteSource,
			Compte compteDestinataire) throws VirementImpossibleException {

		return saveTransaction(virementDTO, messageReponse, compteSource, compteDestinataire, -1000);

	}

	public String virementInterne(VirementDTO virementDTO, String messageReponse, Compte compteSource,
			Compte compteDestinataire) throws VirementImpossibleException {

		if (compteSource instanceof CompteCourant && compteDestinataire instanceof CompteEpargne) {
			return saveTransaction(virementDTO, messageReponse, compteSource, compteDestinataire, -1000);
		} else {
			return saveTransaction(virementDTO, messageReponse, compteSource, compteDestinataire, 0);
		}

	}

	public String saveTransaction(VirementDTO virementDTO, String messageReponse, Compte compteSource,
			Compte compteDestinataire, int limiteSolde) throws VirementImpossibleException {
		if (compteSource.getSolde() - virementDTO.montant() >= limiteSolde) {
			compteSource.setSolde(compteSource.getSolde() - virementDTO.montant());
			compteDestinataire.setSolde(compteDestinataire.getSolde() + virementDTO.montant());

			compteRepository.save(compteSource);
			compteRepository.save(compteDestinataire);

		} else {
			messageReponse = "Solde insuffisant.";
			throw new VirementImpossibleException(messageReponse);
		}

		messageReponse = "Virement effectué avec succès.";
		return messageReponse;

	}

}
