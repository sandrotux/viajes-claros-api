/*
 * Copyright (C) 2015 INAI
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package mx.org.inai.viajesclaros.services;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import mx.org.inai.viajesclaros.entities.Funcionario;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.transform.BasicTransformerAdapter;

/**
 *
 * @author Sandro Alejandro
 */
@Stateless
public class FuncionarioService {
    @PersistenceContext(unitName = "viajesclaros-PU")
    private EntityManager em;

    final static Logger log = Logger.getLogger(FuncionarioService.class);
    
    public List<Funcionario> getFuncionariosByDependencia(Integer idDependencia) {
        Session session = em.unwrap(Session.class);
        
        List<Funcionario> funcionarios = session.createSQLQuery("CALL get_funcionarios_por_dependencia(:idDep)")
                .setParameter("idDep", idDependencia)
                .setResultTransformer(new BasicTransformerAdapter() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public Object transformTuple(Object[] tuple, String[] aliases) {
                        Funcionario funcionario = new Funcionario();
                        funcionario.setId((Integer) tuple[0]);
                        funcionario.setNombre((String) tuple[1]);
                        funcionario.setApellido1((String) tuple[2]);
                        funcionario.setApellido2((String) tuple[3]);
                        funcionario.setIdDependencia((Integer) tuple[4]);
                        funcionario.setEmail((String) tuple[5]);
                        return funcionario;
                    }
                })
                .list();
        
        return funcionarios;
    }
}
