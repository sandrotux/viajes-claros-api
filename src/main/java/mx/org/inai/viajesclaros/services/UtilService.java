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

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import mx.org.inai.viajesclaros.model.SimpleObjectModel;
import org.apache.log4j.Logger;
import org.hibernate.Session;

/**
 *
 * @author Sandro Alejandro
 */
@Stateless
public class UtilService {
    @PersistenceContext(unitName = "viajesclaros-PU")
    private EntityManager em;

    final static Logger log = Logger.getLogger(UtilService.class);
    
    public SimpleObjectModel getTotalViajesByDependencia(Integer idDependencia) {
        Session session = em.unwrap(Session.class);
        
        SimpleObjectModel model = new SimpleObjectModel();
        model.setId(0);
        model.setDescripcion(session.createSQLQuery("CALL get_total_viajes_por_dependencia(:idDep)")
                .setParameter("idDep", idDependencia)
                .uniqueResult().toString());
        
        return model;
    }
    
    public SimpleObjectModel getTotalGastoByDependencia(Integer idDependencia) {
        Session session = em.unwrap(Session.class);
        
        SimpleObjectModel model = new SimpleObjectModel();
        model.setId(0);
        try {
            model.setDescripcion(session.createSQLQuery("CALL get_total_gasto_por_dependencia(:idDep)")
                .setParameter("idDep", idDependencia)
                .uniqueResult().toString());
        } catch(Exception e) {
            log.info("NO EXISTE GASTO PARA LA DEPENDENCIA " + idDependencia);
            model.setDescripcion("0");
        }
        
        return model;
    }
}
