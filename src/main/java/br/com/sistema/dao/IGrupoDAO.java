package br.com.sistema.dao;

import br.com.sistema.model.entities.Grupo;
import java.util.List;

public interface IGrupoDAO {
    void salvarGrupo(Grupo g);
    void atualizarGrupo(Grupo g);
    void removerGrupoPorId(Long id);
    Grupo encontrarGrupoPorId(Long id);
    List<Grupo> listarGrupos();
}