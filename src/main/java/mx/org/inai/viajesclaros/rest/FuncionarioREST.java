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
package mx.org.inai.viajesclaros.rest;

import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import mx.org.inai.viajesclaros.entities.Funcionario;
import mx.org.inai.viajesclaros.services.FuncionarioService;

/**
 *
 * @author Sandro Alejandro
 */
@Stateless
@Path("funcionario")
public class FuncionarioREST {
    @EJB
    FuncionarioService funcionarioService;
    
    @GET
    @Path("funcionariosByDependencia/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Funcionario> getFiltrosByDependencia(@PathParam("id") Integer id) throws Exception {
        return funcionarioService.getFuncionariosByDependencia(id); 
    }
}
