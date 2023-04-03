package ma.fstm.ilisi2.projects.biblio.gestionbibliospringboot.Model.service;


import ma.fstm.ilisi2.projects.biblio.gestionbibliospringboot.Model.bo.Adherent;
import ma.fstm.ilisi2.projects.biblio.gestionbibliospringboot.Model.bo.Emprunt;
import ma.fstm.ilisi2.projects.biblio.gestionbibliospringboot.Model.bo.Exemplaire;
import ma.fstm.ilisi2.projects.biblio.gestionbibliospringboot.Model.bo.Livre;
import ma.fstm.ilisi2.projects.biblio.gestionbibliospringboot.Model.dao.EmpruntRepo;
import ma.fstm.ilisi2.projects.biblio.gestionbibliospringboot.Model.dao.ExemplaireRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class ServiceEmprunt {
    @Autowired
    EmpruntRepo empruntRepo;

    @Autowired
    ServiceAdherent serviceAdherent;

    @Autowired
    ServiceLivre serviceLivre;

    @Autowired
    ExemplaireRepo exemplaireRepo;
    public List<Emprunt> getAllEmprunts() {
        return empruntRepo.findAll();
    }

    public void addEmprunt(String cin , String isbn) {
        if(cin.isEmpty() || isbn.isEmpty())
            return;


        Adherent adherent = serviceAdherent.getAdherentsByCin(cin).get(0);
        Exemplaire exemplaire = exemplaireRepo.getExemplaireDispoByIsbn(isbn).get(0);

        if(adherent == null || exemplaire == null)
            return;

        Livre L = serviceLivre.getLivre(isbn);
        if((L == null) || (L.getNbrexemp() == 0) )
            return;

        empruntRepo.save(new Emprunt(adherent,exemplaire));
        L.setNbrexemp(L.getNbrexemp() -1);
        serviceLivre.updateLivre(L);
    }

    public List<Emprunt> getEmpruntsByCin(String cin) {
        if(cin.isEmpty())
            return empruntRepo.findAll();
        return empruntRepo.getEmpruntsByAdherent_Cin(cin);
    }


    public void rendreEmprunt(Long idEmp, String isbn) {
        if(idEmp == null || isbn.isEmpty())
            return;

        Emprunt emprunt = empruntRepo.findById(idEmp).get();
        if(emprunt == null)
            return;

        if(emprunt.getStatus() == -1 || emprunt.getStatus() == 1)
            return;

        LocalDate currentDate = new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();;
        if(emprunt.getDateRetour().compareTo(currentDate) < 0)
            emprunt.setStatus(-1);
        else
            emprunt.setStatus(1);

        Livre L = serviceLivre.getLivre(isbn);
        if(L == null)
            return;

        empruntRepo.save(emprunt);
        L.setNbrexemp(L.getNbrexemp() +1);
        serviceLivre.updateLivre(L);
    }
}
