package ma.fstm.ilisi2.projects.biblio.gestionbibliospringboot.Model.dao;

import ma.fstm.ilisi2.projects.biblio.gestionbibliospringboot.Model.bo.Livre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LivreRepo extends JpaRepository<Livre, String> {
    @Query("SELECT l FROM Livre l WHERE l.auteur = :auteur")
    List<Livre> findByAuteur(@Param("auteur") String auteur);
}
