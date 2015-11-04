/*
 * Copyright (C) 2015 Sandro Alejandro
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

import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import mx.org.inai.viajesclaros.model.BusquedaModel;
import mx.org.inai.viajesclaros.model.ElementoCatalogoModel;
import mx.org.inai.viajesclaros.model.FiltroBusquedaModel;
import mx.org.inai.viajesclaros.model.ValorListaModel;
import mx.org.inai.viajesclaros.model.ViajeResultModel;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.transform.BasicTransformerAdapter;

/**
 * Servicio para realizar operaciones del buscador en la base de datos. Todas las operaciones se
 * realizan por medio de stored procedures y funciones
 *
 * @author Sandro Alejandro
 */
@Stateless
public class BusquedaService {

    @PersistenceContext(unitName = "viajesclaros-PU")
    private EntityManager em;

    final static Logger log = Logger.getLogger(BusquedaService.class);

    public List<FiltroBusquedaModel> getFiltrosByDependencia(Integer idDependencia) {
        Session session = em.unwrap(Session.class);

        List<FiltroBusquedaModel> filtros = session.createSQLQuery("CALL get_filtros_por_dependencia(:idDep)")
                .setParameter("idDep", idDependencia)
                .setResultTransformer(new BasicTransformerAdapter() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public Object transformTuple(Object[] tuple, String[] aliases) {
                        Integer id = (Integer) tuple[0];
                        String campo = (String) tuple[2];
                        String descripcion = (String) tuple[3];
                        String tipoDato = (String) tuple[4];
                        String tipoControl = (String) tuple[5];
                        String comparador = (String) tuple[6];
                        Integer idCatalogo = (Integer) tuple[7];

                        return new FiltroBusquedaModel(id, campo, descripcion, tipoControl, tipoDato,
                                idCatalogo, comparador);
                    }
                })
                .list();

        /* Si el filtro es catálogo, se deben traer los elementos del catálogo */
        for (FiltroBusquedaModel f : filtros) {
            if (f.getIdCatalogo() != null && f.getIdCatalogo() > 0) {
                List<ValorListaModel> cat = session.createSQLQuery("CALL get_valores_dinamicos_por_id_lista(:idLista)")
                        .setParameter("idLista", f.getIdCatalogo())
                        .setResultTransformer(new BasicTransformerAdapter() {
                            private static final long serialVersionUID = 1L;

                            @Override
                            public Object transformTuple(Object[] tuple, String[] aliases) {
                                ValorListaModel valor = new ValorListaModel();
                                valor.setIdLista((Integer) tuple[0]);
                                valor.setCodigo((String) tuple[1]);
                                valor.setValor((String) tuple[2]);
                                return valor;
                            }
                        })
                        .list();
                f.setValoresLista(cat);
            }
        }

        return filtros;
    }

    /**
     * Llama al SP para obtener los campos parametrizados para mostrar en los resultados de búsqueda
     *
     * @param idDependencia
     * @return Encabezados (lista de objetos ElementoCatalogoModel)
     */
    public List<ElementoCatalogoModel> getEncabezadoViajes(Integer idDependencia) {
        Session session = em.unwrap(Session.class);

        List<ElementoCatalogoModel> encabezados = session.createSQLQuery("CALL get_headers_viajes(:idDep)")
                .setParameter("idDep", idDependencia)
                .setResultTransformer(new BasicTransformerAdapter() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public Object transformTuple(Object[] tuple, String[] aliases) {
                        Integer id = 0;
                        String descripcion = (String) tuple[2];

                        return new ElementoCatalogoModel(id, descripcion);
                    }
                })
                .list();

        return encabezados;
    }

    /**
     * Obtiene los datos de los viajes de la dependencia indicada (los campos son dinámicos)
     *
     * @param idDependencia
     * @return Lista de viajes
     */
    public List<ViajeResultModel> getViajesByDependencia(Integer idDependencia) {
        Session session = em.unwrap(Session.class);

        List<ViajeResultModel> viajes = session.createSQLQuery("CALL get_viajes_por_dependencia(:idDep)")
                .setParameter("idDep", idDependencia)
                .setResultTransformer(new BasicTransformerAdapter() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public Object transformTuple(Object[] tuple, String[] aliases) {
                        List<String> datos = new ArrayList<>();
                        for (Object o : tuple) {
                            datos.add((String) o);
                        }
                        return new ViajeResultModel(datos);
                    }
                })
                .list();

        return viajes;
    }

    /**
     * Realiza la búsqueda con los filtros dinámicos indicados
     * @param idDependencia     Id de la dependencia
     * @param busquedaModel     Filtros de búsqueda
     * @return viajes
     */
    public List<ViajeResultModel> getViajesByFiltros(Integer idDependencia, BusquedaModel busquedaModel) {
        Session session = em.unwrap(Session.class);
        String queryWhere = "";

        /* Crear la parte del WHERE del query */
        for (FiltroBusquedaModel p : busquedaModel.getParametros()) {
            if (p.getTipoControl().equals("SELECT") && p.getIdValor() != 0) {
//                queryWhere += " AND " + p.getDescripcion().replace(" ", "_") + "_id = " + p.getIdValor();
                queryWhere += " AND " + p.getDescripcion() + " = " + p.getIdValor();
            } else {
                if (p.getComparador() == null) {
                    p.setComparador("=");
                }
                log.info("tipo dato: " + p.getTipoDato());
                switch (p.getComparador()) {
                    case "LIKE":
                        queryWhere += " AND " + p.getDescripcion().replace(" ", "_") + " LIKE \"%" + p.getValor() + "%\"";
                        break;
                    case "=":
                        queryWhere += " AND " + p.getDescripcion().replace(" ", "_") + p.getComparador() + " \"" + p.getValor() + "\"";
                        break;
                    case "<":
                    case ">":
                    case "<=":
                    case ">=":
                        if (p.getTipoDato().equals("DATE")) {
                            queryWhere += " AND STR_TO_DATE(" + p.getDescripcion().replace(" ", "_") 
                                    + ", \"%d/%m/%Y\")" + p.getComparador() 
                                    + " STR_TO_DATE( \"" + p.getValor() + "\", \"%d/%m/%Y\")";
                        } else {
                            queryWhere += " AND " + p.getDescripcion().replace(" ", "_") + p.getComparador() + " \"" + p.getValor() + "\"";
                        }
                        break;
                    default: // same as =
                        queryWhere += " AND " + p.getDescripcion().replace(" ", "_") + p.getComparador() + " \"" + p.getValor() + "\"";
                        break;
                }
            }
        }
        log.info("query: " + queryWhere);
        
        /* Llamar al SP de búsqueda y enviar la parte WHERE como parámetro */
        List<ViajeResultModel> viajes = session.createSQLQuery("CALL get_viajes_por_filtros(:idDep, :queryWhere)")
                .setParameter("idDep", idDependencia)
                .setParameter("queryWhere", queryWhere)
                .setResultTransformer(new BasicTransformerAdapter() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public Object transformTuple(Object[] tuple, String[] aliases) {
                        List<String> datos = new ArrayList<>();
                        for (Object o : tuple) {
                            String val = o != null ? o.toString() : "";
                            datos.add(val);
                        }
                        return new ViajeResultModel(datos);
                    }
                })
                .list();
        log.info(viajes.size());
        return viajes;
    }

}
