package com.example.demo.service.impl;

import java.util.Optional;

import com.example.demo.service.ConseillerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dto.ConseillerDTO;
import com.example.demo.entity.Conseiller;
import com.example.demo.repository.ConseillerRepository;

@Service
public class ConseillerServiceimp implements ConseillerService {

	@Autowired
	private ConseillerRepository conseillerRepository;


	@Override
	public Iterable<Conseiller> getAllConseiller() {
		return conseillerRepository.findAll();
	}

	@Override
	public Conseiller saveConseiller(Conseiller conseiller) {
		return conseillerRepository.save(conseiller);
	}

	@Override
	public Optional<Conseiller> getConseillerById(Long id) {
		return conseillerRepository.findById(id);
	}

	@Override
	public void deleteConseillerById(Long id) {
		conseillerRepository.deleteById(id);

	}

	@Override
	public Conseiller updateConseiller(Conseiller conseiller) {

		return null;
	}

	@Override
	public ConseillerDTO getConseillerByLoginByPassword(String login, String password) {
		Conseiller conseiller = conseillerRepository.findByLoginAndPassword(login, password);
		ConseillerDTO conseillerDTO = new ConseillerDTO();

		// Copier les propriétés de l'entité Conseiller dans l'objet ConseillerDTO
		conseillerDTO.setId(conseiller.getId());
		conseillerDTO.setNom(conseiller.getNom());
		conseillerDTO.setPrenom(conseiller.getPrenom());
		conseillerDTO.setGradeGerant(conseiller.getGradeGerant());

		return conseillerDTO;
	}

}
