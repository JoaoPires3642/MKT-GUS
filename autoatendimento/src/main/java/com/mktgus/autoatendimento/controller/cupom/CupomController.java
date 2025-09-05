package com.mktgus.autoatendimento.controller.cupom;

import com.mktgus.autoatendimento.Model.cupom.*;
import com.mktgus.autoatendimento.repository.cupom.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/cupons")
@CrossOrigin(origins = "http://localhost:3000")
public class CupomController {

    @Autowired
    private CupomRepository cupomRepository;

    @GetMapping
    public List<Cupom> listarCupons() {
        return cupomRepository.findAll();
    }
}