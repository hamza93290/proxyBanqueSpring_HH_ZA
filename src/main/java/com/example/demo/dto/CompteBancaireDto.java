package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompteBancaireDto {
    private Long id;
    private String numerodecompte;
    private String nameClient;
    private String typeDeCompte;
    private Double decouvert;
    private Double tauxInteret;
    private Double solde;
}
